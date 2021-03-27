package com.sanchit.funda.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.MainActivity;
import com.sanchit.funda.R;
import com.sanchit.funda.adapter.UserDataListAdapter;
import com.sanchit.funda.dao.UserDataDao;
import com.sanchit.funda.dao.entity.UserDataModel;
import com.sanchit.funda.dao.task.SelectActionTask;
import com.sanchit.funda.database.AppDatabase;
import com.sanchit.funda.database.DatabaseHelper;
import com.sanchit.funda.utils.ViewUtils;

import java.util.List;

public class UserDataCaptureActivity extends AppCompatActivity {

    // Request code for selecting a PDF document.
    private static final int PICK_PDF_FILE = 2;
    boolean disableSavedState = true;
    private Button submitButton;
    private TextView ecasPathTextView;
    private EditText PANEditText;
    private EditText nameEditText;
    private RecyclerView userDataRecyclerView;
    private List<UserDataModel> userDataList;
    private UserDataListAdapter userDataListAdapter;
    private Spinner fileTypeSpinner;
    private ProgressBar progressBarUserData;
    private Uri uri;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Setup...");
        ViewUtils.setActionBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_user_data_capture);

        submitButton = findViewById(R.id.user_data_submit);
        ecasPathTextView = findViewById(R.id.user_data_ecas_path);
        PANEditText = findViewById(R.id.user_data_pan);
        nameEditText = findViewById(R.id.user_data_name);

        fileTypeSpinner = findViewById(R.id.user_data_file_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.file_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fileTypeSpinner.setAdapter(adapter);

        progressBarUserData = findViewById(R.id.progressBar_user_data_existing_profiles);
        progressBarUserData.setVisibility(View.VISIBLE);

        userDataRecyclerView = findViewById(R.id.recycler_view_user_data_entries);
        userDataRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        UserDataDao dao = AppDatabase.getInstance(this).userDataDao();
        SelectActionTask.SelectActionCallback<UserDataModel> selectActionCallback = resultSet -> {
            userDataList = resultSet;
            userDataListAdapter = new UserDataListAdapter(this, userDataList);
            userDataRecyclerView.setAdapter(userDataListAdapter);

            progressBarUserData.setVisibility(View.INVISIBLE);
        };
        DatabaseHelper.selectAll(dao, selectActionCallback);

        sharedPref = getSharedPreferences(getString(R.string.main_preference_file_key), Context.MODE_PRIVATE);

        if (savedInstanceState != null) {
            extractSavedState(savedInstanceState);
        } else {
            extractSavedState();
        }
    }

    public void loadSavedProfiles() {
        UserDataDao dao = AppDatabase.getInstance(this).userDataDao();
        SelectActionTask.SelectActionCallback<UserDataModel> selectActionCallback = resultSet -> {
            userDataList = resultSet;
            userDataListAdapter = new UserDataListAdapter(this, userDataList);
            userDataRecyclerView.setAdapter(userDataListAdapter);

            progressBarUserData.setVisibility(View.INVISIBLE);
        };
        DatabaseHelper.selectAll(dao, selectActionCallback);
    }

    private void extractSavedState() {
        if (disableSavedState) {
            return;
        }

        String termsAccepted = sharedPref.getString(getString(R.string.preference_terms_accepted), "N");
        String name = sharedPref.getString(getString(R.string.investor_name), null);
        String pan = sharedPref.getString(getString(R.string.investor_PAN), null);
        String ecasFilePath = sharedPref.getString(getString(R.string.investor_ecas_file_path), null);
        String fileType = sharedPref.getString(getString(R.string.investor_ecas_file_type), null);
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
            if (fileType != null) {
                fileTypeSpinner.setSelection("NSDL eCAS".equals(fileType) ? 0 : 1, true);
            }
        }
    }

    private void extractSavedState(Bundle savedInstanceState) {
        if (disableSavedState) {
            return;
        }

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
        String fileType = savedInstanceState.getString("fileType");
        if (fileType != null) {
            fileTypeSpinner.setSelection("NSDL eCAS".equals(fileType) ? 0 : 1, true);
        }
    }

    public void onClickECASFileSelectButton(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        if (requestCode == PICK_PDF_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that the user selected.
            if (resultData != null) {
                uri = resultData.getData();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    this.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                if (uri != null) {
                    ecasPathTextView.setText(uri.toString());
                    submitButton.setEnabled(true);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    public void onClickSubmit(View view) {
        String name = nameEditText.getText().toString();
        String PAN = PANEditText.getText().toString();
        String fileType = fileTypeSpinner.getSelectedItem().toString();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.investor_name), name);
        editor.putString(getString(R.string.investor_PAN), PAN);
        editor.putString(getString(R.string.investor_ecas_file_path), uri.toString());
        editor.putString(getString(R.string.investor_ecas_file_type), fileType);
        editor.commit();

        DatabaseHelper.insert(AppDatabase.getInstance(this).userDataDao(), new UserDataModel(name, PAN, uri.toString(), fileType));

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("uri", (Uri) uri);
        i.putExtra("PAN", PAN);
        i.putExtra("name", name);
        i.putExtra("fileType", fileType);
        startActivity(i);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ecas", uri);
        outState.putString("name", nameEditText.getText().toString());
        outState.putString("pan", PANEditText.getText().toString());
        outState.putString("fileType", fileTypeSpinner.getSelectedItem().toString());
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