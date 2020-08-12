package com.sanchit.funda.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.sanchit.funda.MainActivity;
import com.sanchit.funda.R;

public class IntroLogoActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_intro_logo);

        sharedPref = getSharedPreferences(getString(R.string.main_preference_file_key), Context.MODE_PRIVATE);
        new Forwarder().execute();
    }

    public void proceedToWelcome() {
        Intent i = new Intent(this, WelcomeActivity.class);
        startActivity(i);
    }

    public void proceedToMain() {
        Intent i = new Intent(this, MainActivity.class);
        String name = sharedPref.getString(getString(R.string.investor_name), null);
        String PAN = sharedPref.getString(getString(R.string.investor_PAN), null);
        String ecasFilePath = sharedPref.getString(getString(R.string.investor_ecas_file_path), null);
        if (PAN != null && ecasFilePath != null) {
            i.putExtra("uri", (Uri) Uri.parse(ecasFilePath));
            i.putExtra("PAN", PAN);
            i.putExtra("name", name);
        }
        startActivity(i);
    }

    public class Forwarder extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void noData) {
            String termsAccepted = sharedPref.getString(getString(R.string.preference_terms_accepted), "N");
            String PAN = sharedPref.getString(getString(R.string.investor_PAN), null);
            String ecasFilePath = sharedPref.getString(getString(R.string.investor_ecas_file_path), null);
            if ("Y".equals(termsAccepted) && PAN != null && ecasFilePath != null) {
                proceedToMain();
            } else {
                proceedToWelcome();
            }
        }
    }
}