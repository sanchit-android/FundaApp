package com.sanchit.funda.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.adapter.PositionsViewTableAdapter;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.model.MutualFund;
import com.sanchit.funda.model.PositionViewModel;
import com.sanchit.funda.model.cashflow.CashflowPosition;
import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.NumberUtils;
import com.sanchit.funda.utils.ViewUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PositionsViewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private List<MFPosition> originalPositions;
    private Map<String, MFPriceModel> priceMap;
    private List<MFTrade> trades;

    private Spinner spinner;

    private RecyclerView recyclerViewPositionsView;
    private PositionsViewTableAdapter positionsViewAdapter;
    private List<PositionViewModel> positions = new ArrayList<>();
    private List<CashflowPosition> cashflowPositions;

    private Integer lastSortID;
    private String lastSortType;
    private MutualFund fund;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        ViewUtils.setActionBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_positions_view);

        originalPositions = (List<MFPosition>) getIntent().getSerializableExtra("positions");
        priceMap = (Map<String, MFPriceModel>) getIntent().getSerializableExtra("prices");
        trades = (List<MFTrade>) CacheManager.get(Caches.TRADES);
        cashflowPositions = (List<CashflowPosition>) CacheManager.get(Caches.CASHFLOW_POSITION_RAW);

        setupCustomActionBar();
        setupPositions("Custom Categories");

        recyclerViewPositionsView = findViewById(R.id.recycler_view_positions_view_table);
        recyclerViewPositionsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        positionsViewAdapter = new PositionsViewTableAdapter(this, positions);
        recyclerViewPositionsView.setAdapter(positionsViewAdapter);
    }

    private void setupPositions(String key) {
        LinearLayout layout = findViewById(R.id.positions_view_header_fund_category_wrapper);
        if (getResources().getStringArray(R.array.positions_view_grouping)[0].equals(key)) {
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
        positions.clear();
        Map<String, PositionViewModel> data = new HashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CashflowPosition pos : cashflowPositions) {
            if (pos.getUnclosedTaxlotGroup().getUnrealizedQuantity() == null || NumberUtils.equals(BigDecimal.ZERO, pos.getUnclosedTaxlotGroup().getUnrealizedQuantity())) {
                continue;
            }
            total = aggregateMFObject(key, data, total, pos);
        }
        BigDecimal maxCost = BigDecimal.ZERO;
        for (Map.Entry<String, PositionViewModel> entry : data.entrySet()) {
            entry.getValue().setTotalCost(total);
            if (entry.getValue().getInvestment().compareTo(maxCost) > 0) {
                maxCost = entry.getValue().getInvestment();
            }
        }
        for (Map.Entry<String, PositionViewModel> entry : data.entrySet()) {
            entry.getValue().setMaxCost(maxCost);
        }
        positions.addAll(data.values());
    }

    private BigDecimal aggregateMFObject(String key, Map<String, PositionViewModel> data, BigDecimal total, CashflowPosition object) {
        MutualFund fund = object.getFund();
        String keyValue = getKeyValue(fund, key);
        if (!data.containsKey(keyValue)) {
            data.put(keyValue, new PositionViewModel());
            data.get(keyValue).setHead(keyValue);
            if (getResources().getStringArray(R.array.positions_view_grouping)[0].equals(key)) {
                data.get(keyValue).setAmfiId(fund.getAmfiID());
                data.get(keyValue).setFundCategory(fund.getAppDefinedCategory());
            }
        }

        BigDecimal quantity = object.getUnclosedTaxlotGroup().getUnrealizedQuantity();
        BigDecimal cost = object.getUnclosedTaxlotGroup().getCostOfUnrealizedInvestments();

        MFPriceModel priceModel = priceMap.get(fund.getAmfiID());
        BigDecimal priceLatest = priceModel.getPrice(Constants.Duration.T);
        BigDecimal priceLast = priceModel.getPrice(Constants.Duration.T_1d);
        BigDecimal valuation = quantity.multiply(priceLatest);
        BigDecimal valuationLast = quantity.multiply(priceLast);

        data.get(keyValue).addPreviousValuation(valuationLast);
        data.get(keyValue).addValuation(valuation);
        data.get(keyValue).addInvestment(cost);

        total = total.add(cost);
        return total;
    }

    private String getKeyValue(MutualFund fund, String key) {
        if (getResources().getStringArray(R.array.positions_view_grouping)[0].equals(key)) {
            return fund.getFundName();
        } else if (getResources().getStringArray(R.array.positions_view_grouping)[1].equals(key)) {
            return fund.getCategory();
        } else if (getResources().getStringArray(R.array.positions_view_grouping)[2].equals(key)) {
            return fund.getSubCategory();
        } else if (getResources().getStringArray(R.array.positions_view_grouping)[3].equals(key)) {
            return fund.getAppDefinedCategory();
        }
        return fund.getFundName();
    }

    private void setupCustomActionBar() {
        spinner = findViewById(R.id.positions_view_menu_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.positions_view_grouping, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        setupPositions(item);
        recyclerViewPositionsView.invalidate();

        positionsViewAdapter.setGrouping(item);
        positionsViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Not watching
    }

    public void onClickPositionsViewDataSort(View view) {
        String sortType = Constants.SortType.Ascending;
        // If resorting on same column
        if (lastSortID != null && lastSortID == view.getId()) {
            sortType = Constants.SortType.Ascending.equals(lastSortType) ? Constants.SortType.Descending : Constants.SortType.Ascending;
        }

        Comparator<PositionViewModel> cmp = Constants.getComparator(view.getId(), sortType);
        Collections.sort(positions, cmp);
        recyclerViewPositionsView.invalidate();
        positionsViewAdapter.notifyDataSetChanged();

        resetAllSortImages();
        setSortImage(view, sortType);

        lastSortID = view.getId();
        lastSortType = sortType;
    }

    private void resetAllSortImages() {
        for (Integer imageViewId : Constants.getAllSortImages()) {
            ImageView imageView = findViewById(imageViewId);
            imageView.setImageResource(R.drawable.ic_outline_sort_919191_24);
        }
    }

    private void setSortImage(View view, String sortType) {
        Integer imageId = Constants.SortType.Ascending.equals(sortType) ? R.drawable.ic_baseline_arrow_upward_white_24 : R.drawable.ic_baseline_arrow_downward_24;
        Integer imageViewId = Constants.getSortImage(view.getId());
        ImageView imageView = findViewById(imageViewId);
        imageView.setImageResource(imageId);
    }
}