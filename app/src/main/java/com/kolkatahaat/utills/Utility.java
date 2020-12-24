package com.kolkatahaat.utills;

import android.app.ProgressDialog;
import android.content.Context;

import com.kolkatahaat.R;

import java.util.regex.Pattern;

public class Utility {
    public static String TAG = Utility.class.getSimpleName();

    public static ProgressDialog showProgressDialog(Context context) {
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_ProgressDialog_Theme);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        return progressDialog;
    }

    public static boolean checkEmail(String email) {
        final Pattern EMAIL_ADDRESS_PATTERN = Pattern
                .compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
}
