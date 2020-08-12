package com.sanchit.funda.activity;

import android.content.Context;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PositionLineItemDetailActivity extends AppCompatActivity {

    int priceEnrichmentReqs = 0;
    private String item;
    private String grouping;
    private List<MutualFund> funds;
    private RecyclerView recyclerViewOtherFunds;
    private List<MFDetailModel> fundDetailModels;
    private MFDetailAdapter mfDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        item = getIntent().getStringExtra("itemName");
        grouping = getIntent().getStringExtra("grouping");

        String trimmedTitle = trim(item);

        super.onCreate(savedInstanceState);
        setTitle(trimmedTitle);
        setContentView(R.layout.activity_position_line_item_detail);

        recyclerViewOtherFunds = findViewById(R.id.recycler_view_position_line_item_other_funds);
        recyclerViewOtherFunds.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        fundDetailModels = generateDataModel();
    }

    private String trim(String item) {
        if (item != null && item.length() > 7) {
            return item.substring(0, 7) + "...";
        }
        return item;
    }

    private List<MFDetailModel> generateDataModel() {
        funds = (List<MutualFund>) CacheManager.get(Caches.FUNDS);
        List<MFDetailModel> fundDetailModels = new ArrayList<>();

        for (MutualFund fund : funds) {
            if (included(fund)) {
                MFDetailModel mfDetailModel = new MFDetailModel();
                mfDetailModel.setFund(fund);
                fundDetailModels.add(mfDetailModel);

                new MFAPI_NAVAsyncLoader(this, new OnMFAPI_PriceLoadedHandler(this, mfDetailModel), MFAPI_NAVAsyncLoader.EnrichmentModel.Default)
                        .execute(mfDetailModel.getFund().getAmfiID());
                ++priceEnrichmentReqs;
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

    private void updateAdapter() {
        Collections.sort(fundDetailModels, new Comparator<MFDetailModel>() {
            @Override
            public int compare(MFDetailModel o1, MFDetailModel o2) {
                return o1.getPriceModel().get1YearReturnComparable().compareTo(o2.getPriceModel().get1YearReturnComparable()) * -1;
            }
        });

        mfDetailAdapter = new MFDetailAdapter(this, fundDetailModels);
        recyclerViewOtherFunds.setAdapter(mfDetailAdapter);

        mfDetailAdapter.notifyDataSetChanged();
    }

    private class OnMFAPI_PriceLoadedHandler implements OnEnrichmentCompleted<MFPriceModel> {
        private final Context context;
        private final MFDetailModel model;

        public OnMFAPI_PriceLoadedHandler(Context context, MFDetailModel model) {
            this.context = context;
            this.model = model;
        }

        @Override
        public void updateView(MFPriceModel data) {
            model.setPriceModel(data);
            --priceEnrichmentReqs;

            if (priceEnrichmentReqs == 0) {
                updateAdapter();
            }
        }
    }
}