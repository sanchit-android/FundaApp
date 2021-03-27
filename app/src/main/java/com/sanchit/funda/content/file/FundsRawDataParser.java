package com.sanchit.funda.content.file;

import android.app.Activity;
import android.net.Uri;

import com.sanchit.funda.R;
import com.sanchit.funda.model.MutualFund;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FundsRawDataParser extends AbstractFileParser<MutualFund> {

    @Override
    public List<MutualFund> parse(Activity activity, Uri uri) throws IOException {
        List<MutualFund> resultList = new ArrayList<>();

        InputStream inputStream = activity.getResources().openRawResource(R.raw.funds_data_dump);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                boolean direct = row[3].equals("Direct");
                if (!direct) {
                    continue;
                }

                MutualFund fund = new MutualFund();
                fund.setAmfiID(row[0]);
                fund.setFundHouse(row[1]);
                fund.setDirect(direct);
                fund.setFundName(row[2]);
                fund.setCategory(row[6]);
                fund.setSubCategory(row[7]);
                fund.setAppDefinedCategory(row[9]);
                fund.setIsin(row[10]);
                resultList.add(fund);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: " + e);
            }
        }
        return resultList;
    }

}
