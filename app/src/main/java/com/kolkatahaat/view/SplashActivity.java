package com.kolkatahaat.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.kolkatahaat.R;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.SharedPrefsUtils;

public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener {

    private static final String TAG = SplashActivity.class.getName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    protected boolean _active = true;
    protected int _splashTime = 3000; // time to display the splash screen in ms
    private String FCMToken;
    // private TextView txtVersionName;
    private TextView txtCopyRight;

    private FirebaseAuth fireAuth;
    ImageView cl_logo;
    Animation animMoveToTop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //txtVersionName = (TextView)findViewById(R.id.txt_version_number);
        cl_logo = findViewById(R.id.cl_logo);

        //txtVersionName.setText(getString(R.string.app_name)+" "+versionName);

        fireAuth = FirebaseAuth.getInstance();
        // load the animation
        animMoveToTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);

        // set animation listener
        animMoveToTop.setAnimationListener(this);
        // start the animation
        cl_logo.startAnimation(animMoveToTop);
        animMoveToTop.setAnimationListener(this);



        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                        }
                    }
                } catch (Exception e) {

                } finally {
                    Gson gson = new Gson();
                    Object userDetial = SharedPrefsUtils.getFromPrefs(SplashActivity.this, SharedPrefsUtils.USER_DETAIL, "");
                    Users obj = gson.fromJson(String.valueOf(userDetial), Users.class);
                    if(obj != null && fireAuth.getCurrentUser() != null) {
                        if (obj.getUserId() != "" && obj.getUserEmail() != "" && obj.getUserType() == 1) {
                            Intent intent = new Intent(SplashActivity.this, HomeAdminActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);                            startActivity(intent);
                            finish();
                        } else if (obj.getUserId() != "" && obj.getUserEmail() != "" && obj.getUserType() == 2) {
                            Intent intent = new Intent(SplashActivity.this, HomeCustomerActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent m_intent = new Intent(SplashActivity.this, LoginActivity.class);
                        //m_intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        m_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        m_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);                        startActivity(m_intent);
                        finish();
                    }
                }
            }
        };
        splashTread.start();
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }
}
