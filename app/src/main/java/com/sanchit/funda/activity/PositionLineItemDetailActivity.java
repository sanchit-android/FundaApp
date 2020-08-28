package com.sanchit.funda.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.adapter.MFListingAdapter;
import com.sanchit.funda.async.MFAPI_NAVAsyncLoader;
import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.dialog.SortingOptionDialog;
import com.sanchit.funda.model.MFDetailModel;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.model.MutualFund;
import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PositionLineItemDetailActivity extends AppCompatActivity implements SortingOptionDialog.SortingDialogListener {

    int priceEnrichmentReqs = 0;
    private String item;
    private String grouping;
    private List<MutualFund> funds;
    private RecyclerView recyclerViewOtherFunds;
    private List<MFDetailModel> fundDetailModels;
    private MFListingAdapter mfDetailAdapter;

    private String currentSortOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        item = getIntent().getStringExtra("itemName");
        grouping = getIntent().getStringExtra("grouping");

        String trimmedTitle = trim(item);

        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        setTitle(trimmedTitle);
        ViewUtils.setActionBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_position_line_item_detail);

        recyclerViewOtherFunds = findViewById(R.id.recycler_view_position_line_item_other_funds);
        recyclerViewOtherFunds.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        fundDetailModels = generateDataModel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_position_line_item_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_position_detail_sort_option:
                showNoticeDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String trim(String item) {
        int trimLength = 10;
        if (item != null && item.length() > trimLength) {
            return item.substring(0, trimLength) + "...";
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

    private void initAdapter() {
        Collections.sort(fundDetailModels, (o1, o2) -> o1.getPriceModel().get1YearReturnComparable().compareTo(o2.getPriceModel().get1YearReturnComparable()) * -1);

        mfDetailAdapter = new MFListingAdapter(this, fundDetailModels);
        recyclerViewOtherFunds.setAdapter(mfDetailAdapter);

        mfDetailAdapter.notifyDataSetChanged();
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new SortingOptionDialog(currentSortOption);
        dialog.show(getSupportFragmentManager(), "Sort Funds!");
    }

    @Override
    public void onDialogSortClick(DialogFragment dialog, String sortOption) {
        currentSortOption = sortOption;
        Collections.sort(fundDetailModels, Constants.getPositionDetailComparator(sortOption));
        mfDetailAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog) {

    }

    private class OnMFAPI_PriceLoadedHandler implements OnEnrichmentCompleted<MFPriceModel> {
        private final MFDetailModel model;

        public OnMFAPI_PriceLoadedHandler(Context context, MFDetailModel model) {
            this.model = model;
        }

        @Override
        public void updateView(MFPriceModel data) {
            model.setPriceModel(data);
            --priceEnrichmentReqs;

            if (priceEnrichmentReqs == 0) {
                initAdapter();
            }
        }
    }
}