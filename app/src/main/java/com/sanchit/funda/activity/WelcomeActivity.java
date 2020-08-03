package com.sanchit.funda.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sanchit.funda.R;

public class WelcomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Welcome...");
        setContentView(R.layout.activity_welcome);

        sharedPref = getSharedPreferences(getString(R.string.main_preference_file_key), Context.MODE_PRIVATE);
    }

    public void onClickProceedDataCapture(View view) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.preference_terms_accepted), "Y");
        editor.commit();

        Intent i = new Intent(this, UserDataCaptureActivity.class);
        startActivity(i);
    }
}