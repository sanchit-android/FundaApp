package com.sanchit.funda.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.adapter.MFDetailAdapter;
import com.sanchit.funda.async.MFAPI_NAVAsyncLoader;
import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.MFDetailModel;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.model.MutualFund;

import java.util.ArrayList;
import java.util.List;

public class PositionLineItemDetailActivity extends AppCompatActivity {

    private String item;
    private String grouping;
    private List<MutualFund> funds;

    private RecyclerView recyclerViewOtherFunds;
    private List<MFDetailModel> fundDetailModels;
    private MFDetailAdapter mfDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_line_item_detail);

        item = getIntent().getStringExtra("itemName");
        grouping = getIntent().getStringExtra("grouping");

        recyclerViewOtherFunds = findViewById(R.id.recycler_view_position_line_item_other_funds);
        recyclerViewOtherFunds.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        fundDetailModels = generateDataModel();
        mfDetailAdapter = new MFDetailAdapter(this, fundDetailModels);
        recyclerViewOtherFunds.setAdapter(mfDetailAdapter);

    }

    private List<MFDetailModel> generateDataModel() {
        funds = (List<MutualFund>) CacheManager.get(Caches.FUNDS);
        List<MFDetailModel> fundDetailModels = new ArrayList<>();

        for (MutualFund fund : funds) {
            if (included(fund)) {
                MFDetailModel mfDetailModel = new MFDetailModel();
                mfDetailModel.setFund(fund);
                fundDetailModels.add(mfDetailModel);
            }
        }
        return fundDetailModels;
    }

    private boolean included(MutualFund fund) {
        if (getResources().getStringArray(R.array.positions_view_grouping)[0].equals(grouping)) {
            return fund.getFundName().equals(item);
        } else if (getResources().getStringArray(R.array.positions_view_grouping)[1].equals(grouping)) {
            return fund.getCategory().equals(item);
        } else if (getResources().getStringArray(R.array.positions_view_grouping)[2].equals(grouping)) {
            return fund.getSubCategory().equals(item);
        } else if (getResources().getStringArray(R.array.positions_view_grouping)[3].equals(grouping)) {
            return fund.getAppDefinedCategory().equals(item);
        }
        return fund.getFundName().equals(item);
    }

    private class OnMFAPI_PriceLoadedHandler implements OnEnrichmentCompleted<MFPriceModel> {

        @Override
        public void updateView(MFPriceModel data) {

        }
    }
}