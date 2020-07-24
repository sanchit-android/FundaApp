package com.sanchit.funda.content.file;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public abstract class AbstractFileParser<T> {

    protected static final int PERMISSION_REQUEST_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public abstract List<T> parse(Activity activity, Uri uri) throws IOException;

    protected boolean checkPermission(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    protected void requestPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, "Write External Storage permission allows us to read files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, PERMISSION_REQUEST_CODE);
        }
    }

    protected boolean isNumeric(String string) {
        try {
            new BigDecimal(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
