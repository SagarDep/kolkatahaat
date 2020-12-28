package com.kolkatahaat.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kolkatahaat.MainActivity;
import com.kolkatahaat.R;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    protected boolean _active = true;
    protected int _splashTime = 3000; // time to display the splash screen in ms
    private String FCMToken;
    // private TextView txtVersionName;
    private TextView txtCopyRight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //txtVersionName = (TextView)findViewById(R.id.txt_version_number);
        String versionName = "1.2.3";
        //txtVersionName.setText(getString(R.string.app_name)+" "+versionName);

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
                    Intent m_intent = new Intent(SplashActivity.this, RegisterActivity.class);
                    m_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(m_intent);
                    finish();
                }
            }
        };
        splashTread.start();
    }
}
