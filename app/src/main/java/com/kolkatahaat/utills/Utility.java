package com.kolkatahaat.utills;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;

import com.kolkatahaat.R;

import java.util.regex.Pattern;

public class Utility {
    public static String TAG = Utility.class.getSimpleName();

    public static void hideSoftKeyboard(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }


    public static void displayDialog(final Context context, final String msg, final boolean needToFinished) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.str_alert);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (needToFinished) {
                    ((Activity) context).finish();
                }
            }
        });
        alertDialog.show();
    }


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
