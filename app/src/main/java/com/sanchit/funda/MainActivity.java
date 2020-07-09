package com.sanchit.funda;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.activity.PositionsViewActivity;
import com.sanchit.funda.adapter.HomeSummary1Adapter;
import com.sanchit.funda.adapter.HomeSummary2Adapter;
import com.sanchit.funda.adapter.HomeSummary3Adapter;
import com.sanchit.funda.async.FundsRawDataAsyncLoader;
import com.sanchit.funda.async.NSDL_CASAsyncLoader;
import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.model.HomeSummary1Model;
import com.sanchit.funda.model.HomeSummary2Model;
import com.sanchit.funda.model.HomeSummary3Model;
import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MutualFund;
import com.sanchit.funda.utils.DummyDataGenerator;
import com.sanchit.funda.utils.NumberUtils;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // Request code for selecting a PDF document.
    private static final int PICK_PDF_FILE = 2;

    private final List<MFPosition> positions = new ArrayList<>();
    private final List<MutualFund> funds = new ArrayList<>();

    private RecyclerView recyclerViewHomeSummary1;
    private List<HomeSummary1Model> homeSummary1Model;
    private HomeSummary1Adapter homeSummary1Adapter;

    private RecyclerView recyclerViewHomeSummary2;
    private List<HomeSummary2Model> homeSummary2Model;
    private HomeSummary2Adapter homeSummary2Adapter;

    private RecyclerView recyclerViewHomeSummary3;
    private List<HomeSummary3Model> homeSummary3Model;
    private HomeSummary3Adapter homeSummary3Adapter;

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        setContentView(R.layout.activity_main);

        spinner = (ProgressBar) findViewById(R.id.progressBar_home_summary);
        spinner.setVisibility(View.VISIBLE);

        Uri ecasFilePath = (Uri) getIntent().getParcelableExtra("uri");

        PDFBoxResourceLoader.init(getApplicationContext());
        new NSDL_CASAsyncLoader(this, new OnECASFileLoadedHandler()).execute(ecasFilePath);
        new FundsRawDataAsyncLoader(this, new FundsRawDataLoadedHandler()).execute(Uri.EMPTY);

        recyclerViewHomeSummary1 = findViewById(R.id.recycler_view_home_summary_1);
        recyclerViewHomeSummary1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        homeSummary1Model = DummyDataGenerator.generateHomeSumary1Model();
        homeSummary1Adapter = new HomeSummary1Adapter(this, homeSummary1Model);
        recyclerViewHomeSummary1.setAdapter(homeSummary1Adapter);

        recyclerViewHomeSummary2 = findViewById(R.id.recycler_view_home_summary_2);
        recyclerViewHomeSummary2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        homeSummary2Model = DummyDataGenerator.generateHomeSumary2Model();
        homeSummary2Adapter = new HomeSummary2Adapter(this, homeSummary2Model);
        recyclerViewHomeSummary2.setAdapter(homeSummary2Adapter);

        recyclerViewHomeSummary3 = findViewById(R.id.recycler_view_home_summary_3);
        recyclerViewHomeSummary3.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        homeSummary3Model = DummyDataGenerator.generateHomeSumary3Model();
        homeSummary3Adapter = new HomeSummary3Adapter(this, homeSummary3Model);
        recyclerViewHomeSummary3.setAdapter(homeSummary3Adapter);
    }

    public void onClickHomeSummary(View view) {
        Intent i = new Intent(this, PositionsViewActivity.class);
        i.putExtra("positions", (ArrayList) positions);
        startActivity(i);
    }

    private class OnECASFileLoadedHandler implements OnEnrichmentCompleted<List<MFPosition>> {

        @Override
        public void updateView(List<MFPosition> data) {
            positions.addAll(data);
        }
    }

    private class FundsRawDataLoadedHandler implements OnEnrichmentCompleted<List<MutualFund>> {
        @Override
        public void updateView(List<MutualFund> data) {
            funds.addAll(data);
            Map<String, MutualFund> isinToFundMap = toISINMap(funds);

            for (MFPosition position : positions) {
                position.setFund(isinToFundMap.get(position.getFund().getIsin()));
            }

            updateSummary();
        }

        private void updateSummary() {
            BigDecimal investment = BigDecimal.ZERO;
            BigDecimal valuation = BigDecimal.ZERO;
            Set<String> funds = new HashSet<>();
            Set<String> categories = new HashSet<>();
            for (MFPosition position : positions) {
                investment = investment.add(position.getCost());
                valuation = valuation.add(position.getCurrentValue());
                funds.add(position.getFund().getFundName());
                categories.add(position.getFund().getAppDefinedCategory());
            }
            BigDecimal pnl = (valuation.subtract(investment)).divide(investment, 4, BigDecimal.ROUND_HALF_UP);

            ((TextView) findViewById(R.id.home_summary_investment)).setText(NumberUtils.formatMoney(investment));
            ((TextView) findViewById(R.id.home_summary_valuation)).setText(NumberUtils.formatMoney(valuation));
            ((TextView) findViewById(R.id.home_summary_fund_count)).setText(String.valueOf(funds.size()));
            ((TextView) findViewById(R.id.home_summary_category_count)).setText(String.valueOf(categories.size()));
            ((TextView) findViewById(R.id.home_summary_pnl)).setText(NumberUtils.toPercentage(pnl, 2));

            spinner.setVisibility(View.GONE);
        }

        private Map<String, MutualFund> toISINMap(List<MutualFund> funds) {
            Map<String, MutualFund> isinToFundMap = new HashMap<>();
            for (MutualFund fund : funds) {
                isinToFundMap.put(fund.getIsin(), fund);
            }
            return isinToFundMap;
        }
    }
}
