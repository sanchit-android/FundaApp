package com.sanchit.funda.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SecurityUtils {

    protected static final int PERMISSION_REQUEST_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void setupPermissions(Activity activity) {
        if (!SecurityUtils.checkPermission(activity)) {
            SecurityUtils.requestPermission(activity);
        } else {
            SecurityUtils.requestPermission(activity);
        }

        if (!SecurityUtils.checkPermission(activity)) {
            Toast.makeText(activity, "Permissions Missing", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean checkPermission(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static void requestPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, "Write External Storage permission allows us to read files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, PERMISSION_REQUEST_CODE);
        }
    }

}
