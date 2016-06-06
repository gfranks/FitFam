package com.github.gfranks.workoutcompanion.data.api;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.manager.AccountManager;
import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.concurrent.Executor;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ErrorHandlingExecutorCallAdapterFactory extends CallAdapter.Factory {

    private final Application mApplication;
    private final Executor mCallbackExecutor;
    private AccountManager mAccountManager;

    public ErrorHandlingExecutorCallAdapterFactory(Application application, AccountManager accountManager) {
        mApplication = application;
        mAccountManager = accountManager;
        mCallbackExecutor = new MainThreadExecutor();
    }

    public ErrorHandlingExecutorCallAdapterFactory(Application application, AccountManager accountManager, Executor callbackExecutor) {
        mApplication = application;
        mAccountManager = accountManager;
        mCallbackExecutor = callbackExecutor;
    }

    @Override
    public CallAdapter<Call<?>> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if ($Gson$Types.getRawType(returnType) != Call.class) {
            return null;
        }
        final Type responseType = getCallResponseType(returnType);
        return new CallAdapter<Call<?>>() {
            @Override
            public Type responseType() {
                return responseType;
            }

            @Override
            public <R> Call<R> adapt(Call<R> call) {
                return new ExecutorCallbackCall<>(mCallbackExecutor, call);
            }
        };
    }

    private Type getCallResponseType(Type returnType) {
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException(
                    "Call return type must be parameterized as Call<Foo> or Call<? extends Foo>");
        }
        final Type responseType = getSingleParameterUpperBound((ParameterizedType) returnType);

        // Ensure the Call response type is not Response, we automatically deliver the Response object.
        if ($Gson$Types.getRawType(responseType) == Response.class) {
            throw new IllegalArgumentException(
                    "Call<T> cannot use Response as its generic parameter. "
                            + "Specify the response body type only (e.g., Call<TweetResponse>).");
        }
        return responseType;
    }

    private Type getSingleParameterUpperBound(ParameterizedType type) {
        Type[] types = type.getActualTypeArguments();
        if (types.length != 1) {
            throw new IllegalArgumentException(
                    "Expected one type argument but got: " + Arrays.toString(types));
        }
        Type paramType = types[0];
        if (paramType instanceof WildcardType) {
            return ((WildcardType) paramType).getUpperBounds()[0];
        }
        return paramType;
    }

    public static class MainThreadExecutor implements Executor {

        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable r) {
            handler.post(r);
        }
    }

    private final class ExecutorCallbackCall<T> implements Call<T> {

        private final Executor callbackExecutor;
        private final Call<T> delegate;

        ExecutorCallbackCall(Executor callbackExecutor, Call<T> delegate) {
            this.callbackExecutor = callbackExecutor;
            this.delegate = delegate;
        }

        @Override
        public Request request() {
            return null;
        }

        @Override
        public void enqueue(Callback<T> callback) {
            delegate.enqueue(new ExecutorCallback<>(callbackExecutor, callback));
        }

        @Override
        public boolean isExecuted() {
            return false;
        }

        @Override
        public Response<T> execute() throws IOException {
            return delegate.execute();
        }

        @Override
        public void cancel() {
            delegate.cancel();
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @SuppressWarnings("CloneDoesntCallSuperClone") // Performing deep clone.
        @Override
        public Call<T> clone() {
            return new ExecutorCallbackCall<>(callbackExecutor, delegate.clone());
        }
    }

    private class ExecutorCallback<T> implements Callback<T> {

        private final Executor mCallbackExecutor;
        private final Callback<T> mDelegate;

        ExecutorCallback(Executor callbackExecutor, Callback<T> delegate) {
            this.mCallbackExecutor = callbackExecutor;
            this.mDelegate = delegate;
        }

        @Override
        public void onResponse(final Call<T> call, final Response<T> response) {
            if (response.isSuccessful()) {
                mCallbackExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mDelegate.onResponse(call, response);
                    }
                });
            } else {
                if (response.code() == HttpURLConnection.HTTP_FORBIDDEN && mAccountManager.isLoggedIn()) {
                    mAccountManager.logout();
                    return;
                }

                mCallbackExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mDelegate.onFailure(call, new Throwable(mApplication.getString(R.string.error_network_no_response)));
                    }
                });
            }
        }

        @Override
        public void onFailure(final Call<T> call, Throwable t) {
            String errorResponse;
            if (t instanceof IOException) {
                errorResponse = mApplication.getString(R.string.error_network);
            } else {
                errorResponse = mApplication.getString(R.string.error_network_http);
            }
            final String finalErrorResponse = errorResponse;
            mCallbackExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mDelegate.onFailure(call, new Throwable(finalErrorResponse));
                }
            });
        }
    }
}