package com.sanchit.funda.async;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;

import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.content.file.NSDL_CASContentParser;
import com.sanchit.funda.model.MFPosition;

import java.io.IOException;
import java.util.List;

public class NSDL_CASAsyncLoader extends AsyncTask<Uri, Void, List<MFPosition>> {

    private final Activity activity;
    private final OnEnrichmentCompleted<List<MFPosition>> callback;
    private final String PAN;

    public NSDL_CASAsyncLoader(Activity activity, OnEnrichmentCompleted<List<MFPosition>> callback, String PAN) {
        this.activity = activity;
        this.callback = callback;
        this.PAN = PAN;
    }

    @Override
    protected List<MFPosition> doInBackground(Uri... args) {
        try {
            return new NSDL_CASContentParser(PAN).parse(activity, args[0]);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<MFPosition> data) {
        callback.updateView(data);
    }
}
