package com.sanchit.funda.async;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanchit.funda.R;
import com.sanchit.funda.content.NSDL_CASContentParser;
import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.utils.NumberUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NSDL_CASAsyncLoader extends AsyncTask<Uri, Void, List<MFPosition>> {

    private final Activity activity;
    private final List<MFPosition> positions;

    public NSDL_CASAsyncLoader(Activity activity, List<MFPosition> positions) {
        this.activity = activity;
        this.positions = positions;
    }

    @Override
    protected List<MFPosition> doInBackground(Uri... args) {
        try {
            return new NSDL_CASContentParser().parse(activity, args[0]);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<MFPosition> data) {
        final LinearLayout layout = activity.findViewById(R.id.layout_home_summary);
        final Button loadButton = null;//activity.findViewById(R.id.button_main_load_data);

        loadButton.setEnabled(true);
        loadButton.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.VISIBLE);

        positions.addAll(data);

        BigDecimal investment = BigDecimal.ZERO;
        BigDecimal valuation = BigDecimal.ZERO;
        int fundCount = 0;
        Set<String> categories = new HashSet<>();
        for (MFPosition position : positions) {
            investment = investment.add(position.getCost());
            valuation = valuation.add(position.getCurrentValue());
            fundCount++;
            categories.add(position.getFund().getAppDefinedCategory());
        }

        ((TextView) activity.findViewById(R.id.home_summary_investment)).setText(NumberUtils.formatMoney(investment));
        ((TextView) activity.findViewById(R.id.home_summary_valuation)).setText(NumberUtils.formatMoney(valuation));
        ((TextView) activity.findViewById(R.id.home_summary_fund_count)).setText(String.valueOf(fundCount));
        ((TextView) activity.findViewById(R.id.home_summary_category_count)).setText(String.valueOf(categories.size()));
    }
}
