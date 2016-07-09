package com.github.gfranks.fitfam.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Patterns;

import com.github.gfranks.fitfam.R;
import com.rengwuxian.materialedittext.validation.METValidator;

public class ValidatorUtils {

    public static METValidator getEmailValidator(Context context) {
        return new METValidator(context.getString(R.string.error_not_an_email)) {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return Patterns.EMAIL_ADDRESS.matcher(text).matches();
            }
        };
    }

    public static METValidator getNonEmptyValidator(String errorMessage) {
        return new METValidator(errorMessage) {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return !isEmpty;
            }
        };
    }

    public static METValidator getPhoneNumberValidator(Context context) {
        return new METValidator(context.getString(R.string.error_no_phone_number)) {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return Utils.isPhoneNumber(text.toString());
            }
        };
    }

    public static METValidator getBirthdayValidator(Context context) {
        return new METValidator(context.getString(R.string.error_invalid_birthday)) {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return Utils.isBirthday(text.toString());
            }
        };
    }
}
