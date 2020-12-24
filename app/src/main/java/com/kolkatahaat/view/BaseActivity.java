package com.kolkatahaat.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.HashMap;

public class BaseActivity extends AppCompatActivity {

    private Dialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

   /* public void showProgressBar(boolean isShow) {
        if (isShow) {
            showProgressDialog();
        } else {
            hideProgressDialog();
        }
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this);
        } else {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.app_loading_dialog,null, false);
        ImageView imageView1 = view.findViewById(R.id.imageView2);
        Animation a1 = AnimationUtils.loadAnimation(this, R.anim.progress_anim);
        a1.setDuration(1500);
        imageView1.startAnimation(a1);

        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(view);
        Window window = progressDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(ContextCompat.getDrawable(this, android.R.color.transparent));
        }
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public final void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }*/


    public void onClick(View view) {
       /* switch (view.getId()) {
            case R.id.ivToolbarLeft:
                finish();
                break;
        }*/
    }

    public HashMap<String, String> getMap() {
        return new HashMap<String, String>();
    }
}
