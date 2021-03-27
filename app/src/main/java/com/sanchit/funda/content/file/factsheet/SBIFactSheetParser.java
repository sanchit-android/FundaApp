package com.sanchit.funda.content.file.factsheet;

import android.app.Activity;
import android.net.Uri;

import com.sanchit.funda.R;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.MutualFund;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class SBIFactSheetParser extends AbstractFactSheetParser {

    private final CacheManager.Cache<String, MutualFund> funds;

    public SBIFactSheetParser() {
        funds = CacheManager.get(Caches.FUNDS_BY_NAME, MutualFund.class);
    }

    @Override
    public List<Void> parse(Activity activity, Uri uri) throws IOException {
        InputStream is = activity.getResources().openRawResource(R.raw.factsheet_sbi);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");

                String fundName = row[0];
                MutualFund fund = funds.get(fundName.toLowerCase());

                if(fund == null) {
                    continue;
                }

                fund.setBenchmark(row[1]);
                fund.setAaum(row[2]);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: " + e);
            }
        }
        return null;
    }
}
