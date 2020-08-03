package com.sanchit.funda.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sanchit.funda.MainActivity;
import com.sanchit.funda.R;

public class UserDataCaptureActivity extends AppCompatActivity {

    // Request code for selecting a PDF document.
    private static final int PICK_PDF_FILE = 2;

    private Button submitButton;
    private TextView ecasPathTextView;
    private EditText PANEditText;
    private EditText nameEditText;

    private Uri uri;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Setup...");
        setContentView(R.layout.activity_user_data_capture);

        submitButton = findViewById(R.id.user_data_submit);
        ecasPathTextView = findViewById(R.id.user_data_ecas_path);
        PANEditText = findViewById(R.id.user_data_pan);
        nameEditText = findViewById(R.id.user_data_name);

        sharedPref = getSharedPreferences(getString(R.string.main_preference_file_key), Context.MODE_PRIVATE);

        if (savedInstanceState != null) {
            extractSavedState(savedInstanceState);
        } else {
            extractSavedState();
        }
    }

    private void extractSavedState() {
        String termsAccepted = sharedPref.getString(getString(R.string.preference_terms_accepted), "N");
        String name = sharedPref.getString(getString(R.string.investor_name), null);
        String pan = sharedPref.getString(getString(R.string.investor_PAN), null);
        String ecasFilePath = sharedPref.getString(getString(R.string.investor_ecas_file_path), null);
        if ("Y".equals(termsAccepted)) {
            if (ecasFilePath != null) {
                ecasPathTextView.setText(ecasFilePath);
                uri = Uri.parse(ecasFilePath);
            }
            if (name != null) {
                nameEditText.setText(name);
            }
            if (pan != null) {
                PANEditText.setText(pan);
            }
        }
    }

    private void extractSavedState(Bundle savedInstanceState) {
        uri = savedInstanceState.getParcelable("ecas");
        if (uri != null) {
            ecasPathTextView.setText(uri.toString());
        }
        String name = savedInstanceState.getString("name");
        if (name != null) {
            nameEditText.setText(name);
        }
        String pan = savedInstanceState.getString("pan");
        if (pan != null) {
            PANEditText.setText(pan);
        }
    }

    public void onClickECASFileSelectButton(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        if (requestCode == PICK_PDF_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that the user selected.
            if (resultData != null) {
                uri = resultData.getData();
                if (uri != null) {
                    ecasPathTextView.setText(uri.toString());
                    submitButton.setEnabled(true);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    public void onClickSubmit(View view) {
        String PAN = PANEditText.getText().toString();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.investor_name), "Sanchit Srivastava");
        editor.putString(getString(R.string.investor_PAN), PAN);
        editor.putString(getString(R.string.investor_ecas_file_path), uri.toString());
        editor.commit();

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("uri", (Uri) uri);
        i.putExtra("PAN", PAN);
        startActivity(i);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ecas", uri);
        outState.putString("name", nameEditText.getText().toString());
        outState.putString("pan", PANEditText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        uri = savedInstanceState.getParcelable("ecas");
        if (uri != null) {
            ecasPathTextView.setText(uri.toString());
        }
    }
}