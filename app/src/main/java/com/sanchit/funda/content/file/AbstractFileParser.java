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



    public abstract List<T> parse(Activity activity, Uri uri) throws IOException;

    protected boolean isNumeric(String string) {
        try {
            new BigDecimal(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
