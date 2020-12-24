package com.kolkatahaat.utills;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kolkatahaat.R;

public class AppUtils {
    public static void showAlert(Context context, String message) {
        // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        final Dialog dialog = showAlertDialogSingleText(context, message, R.drawable.ic_info);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 2500);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static Dialog showAlertDialogSingleText(Context context, final String dialogText, int icon) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        // lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        TextView txtDialogText = dialog.findViewById(R.id.txtDialogText);
        ImageView ivIcon = dialog.findViewById(R.id.ivIcon);
        ivIcon.setImageResource(icon);
        txtDialogText.setText(dialogText);

        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }

    public static void showAlert(Context context, String title, String mess) {
        AlertDialog.Builder oBuilder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        oBuilder.setTitle(title);
        oBuilder.setMessage(mess);
        oBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        oBuilder.show();
    }
}
