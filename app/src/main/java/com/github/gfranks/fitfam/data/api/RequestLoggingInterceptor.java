package com.github.gfranks.fitfam.data.api;

import android.util.Log;

import com.github.gfranks.fitfam.application.FitFamApplication;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpEngine;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;

public class RequestLoggingInterceptor implements Interceptor {

    private static final String TAG = FitFamApplication.TAG + "/Req";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private HttpLoggingInterceptor mLoggingInterceptor;

    @Inject
    public RequestLoggingInterceptor(HttpLoggingInterceptor loggingInterceptor) {
        mLoggingInterceptor = loggingInterceptor;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (mLoggingInterceptor.getLevel() == HttpLoggingInterceptor.Level.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = mLoggingInterceptor.getLevel() == HttpLoggingInterceptor.Level.BODY;
        boolean logHeaders = logBody || mLoggingInterceptor.getLevel() == HttpLoggingInterceptor.Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage =
                "--> " + request.method() + ' ' + request.url() + ' ' + protocol(protocol);
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        log(requestStartMessage);

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    log("Content-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    log("Content-Length: " + requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    log(name + ": " + headers.value(i));
                }
            }

            if (!logBody || !hasRequestBody) {
                log("--> END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                log("--> END " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    contentType.charset(UTF8);
                }

                log("");
                log(buffer.readString(charset));

                log("--> END " + request.method()
                        + " (" + requestBody.contentLength() + "-byte body)");
            }
        }

        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        log("<-- " + response.code() + ' ' + response.message() + ' '
                + response.request().url() + " (" + tookMs + "ms" + (!logHeaders ? ", "
                + responseBody.contentLength() + "-byte body" : "") + ')');

        if (logHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                log(headers.name(i) + ": " + headers.value(i));
            }

            if (!logBody || !HttpEngine.hasBody(response)) {
                log("<-- END HTTP");
            } else if (bodyEncoded(response.headers())) {
                log("<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (responseBody.contentLength() != 0) {
                    log("");
                    log(buffer.clone().readString(charset));
                }

                log("<-- END HTTP (" + buffer.size() + "-byte body)");
            }
        }

        return response;
    }

    boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    String protocol(Protocol protocol) {
        return protocol == Protocol.HTTP_1_0 ? "HTTP/1.0" : "HTTP/1.1";
    }

    void log(String message) {
        Log.v(TAG, message);
    }
}