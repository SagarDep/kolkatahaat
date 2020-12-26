package com.kolkatahaat.view;

import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kolkatahaat.R;

public class AboutActivity extends AppCompatActivity {

    TextView txtAboutDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        txtAboutDetails = (TextView) findViewById(R.id.txtAboutDetails);
        //txtAboutDetails.setText(someContent);
        Linkify.addLinks(txtAboutDetails, Linkify.ALL);
    }
}
