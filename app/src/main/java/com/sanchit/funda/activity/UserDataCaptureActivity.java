package com.sanchit.funda.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Setup...");
        setContentView(R.layout.activity_user_data_capture);

        submitButton = findViewById(R.id.user_data_submit);
        ecasPathTextView = findViewById(R.id.user_data_ecas_path);

        if (savedInstanceState != null) {
            uri = savedInstanceState.getParcelable("ecas");
            if (uri != null) {
                ecasPathTextView.setText(uri.toString());
            }
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
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("uri", (Uri) uri);
        startActivity(i);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ecas", uri);
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