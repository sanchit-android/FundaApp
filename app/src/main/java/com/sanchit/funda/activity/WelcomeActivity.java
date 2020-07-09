package com.sanchit.funda.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sanchit.funda.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Welcome...");
        setContentView(R.layout.activity_welcome);
    }

    public void onClickProceedDataCapture(View view) {
        Intent i = new Intent(this, UserDataCaptureActivity.class);
        startActivity(i);
    }
}