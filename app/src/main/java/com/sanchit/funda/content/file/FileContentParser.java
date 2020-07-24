package com.sanchit.funda.content.file;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FileContentParser extends AbstractFileParser {

    public List<String> parse(Activity activity, Uri uri) throws IOException {
        if (!checkPermission(activity)) {
            requestPermission(activity);
        }

        if (!checkPermission(activity)) {
            Toast.makeText(activity, "Permissions Missing", Toast.LENGTH_LONG).show();
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     activity.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        return Arrays.asList(new String[]{stringBuilder.toString()});
    }

}
