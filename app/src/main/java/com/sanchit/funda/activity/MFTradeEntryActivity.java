package com.sanchit.funda.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.sanchit.funda.MainActivity;
import com.sanchit.funda.R;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.model.MutualFund;
import com.sanchit.funda.model.cashflow.Taxlot;
import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.DateUtils;
import com.sanchit.funda.utils.ViewUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MFTradeEntryActivity extends AppCompatActivity {

    private List<MutualFund> funds;
    private Taxlot taxlot;

    private DatePickerDialog picker;
    private String sideSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Trade Entry");
        getSupportActionBar().setElevation(0);
        ViewUtils.setActionBarColor(this, R.color.colorPrimaryDark);
        setTitleColor(getResources().getColor(R.color.colorTextLight));
        setContentView(R.layout.activity_mf_trade_entry);

        funds = (List<MutualFund>) CacheManager.get(Caches.FUNDS);
        taxlot = (Taxlot) getIntent().getSerializableExtra("taxlot");

        setupEditTexBoxes();
        setupTradeDatePicker();
        setupFundSelector();
        setupData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_mf_trade_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_trade_delete:
                attemptDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void attemptDelete() {
        List<MFTrade> trades = (List<MFTrade>) CacheManager.get(Caches.TRADES);
        CacheManager.Cache<String, List> tradesByAMFIId = CacheManager.get(Caches.TRADES_BY_AMFI_ID, List.class);
        List<MFPosition> positions = (List<MFPosition>) CacheManager.get(Caches.POSITIONS);

        MFTrade t = new MFTrade();
        t.setUID(taxlot.getUID());
        boolean deleteSuccess = trades.remove(t);

        if (!deleteSuccess) {
            MFPosition p = new MFPosition();
            p.setUID(taxlot.getUID());
            deleteSuccess = trades.remove(t);
        } else {
            deleteSuccess = tradesByAMFIId.get(taxlot.getFund().getAmfiID()).remove(t);
        }
    }

    private void setupData() {
        if (taxlot == null) {
            return;
        }

        ViewUtils.setTextViewData(this, R.id.mf_trade_entry_quantity, taxlot.getQuantity().toPlainString());
        ViewUtils.setTextViewData(this, R.id.mf_trade_entry_price, taxlot.getCostPrice().toPlainString());
        ViewUtils.setTextViewData(this, R.id.mf_trade_entry_date, DateUtils.formatDate(taxlot.getInvestmentDate(), "d/M/yyyy"));
        ViewUtils.setTextViewData(this, R.id.mf_trade_entry_fund_name, taxlot.getFund().getFundName());
        sideSelected = taxlot.getSide();

        RadioButton b = "buy".equals(sideSelected.toLowerCase()) ? findViewById(R.id.side_buy) : findViewById(R.id.side_sell);
        b.setChecked(true);
    }

    private void setupEditTexBoxes() {
        final EditText et_Units = (EditText) findViewById(R.id.mf_trade_entry_quantity);
        final EditText et_Price = (EditText) findViewById(R.id.mf_trade_entry_price);
        final EditText et_Inv = (EditText) findViewById(R.id.mf_trade_entry_cost);

        MyEditTextListener listener = new MyEditTextListener(et_Units, et_Price, et_Inv);

        et_Price.addTextChangedListener(listener);
        et_Units.addTextChangedListener(listener);
        et_Inv.setEnabled(false);
    }

    private void setupTradeDatePicker() {
        final EditText eText = (EditText) findViewById(R.id.mf_trade_entry_date);
        eText.setInputType(InputType.TYPE_NULL);
    }

    private void setupFundSelector() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getFundsName());
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.mf_trade_entry_fund_name);
        textView.setAdapter(adapter);
    }

    private String[] getFundsName() {
        List<String> result = new ArrayList<>();
        for (MutualFund fund : funds) {
            result.add(fund.getFundName());
        }
        String[] array = new String[result.size()];
        return result.toArray(array);
    }

    public void onClickSubmit(View view) throws ParseException {
        MFTrade trade = new MFTrade();

        String fundName = ((EditText) findViewById(R.id.mf_trade_entry_fund_name)).getText().toString();
        BigDecimal quantity = new BigDecimal(((EditText) findViewById(R.id.mf_trade_entry_quantity)).getText().toString());
        BigDecimal price = new BigDecimal(((EditText) findViewById(R.id.mf_trade_entry_price)).getText().toString());
        String date = ((EditText) findViewById(R.id.mf_trade_entry_date)).getText().toString();

        MutualFund fund = ((CacheManager.Cache<String, MutualFund>) CacheManager.get(Caches.FUNDS_BY_NAME)).get(fundName.toLowerCase());

        trade.setFund(fund);
        trade.setCostPrice(price);
        trade.setQuantity(quantity);
        trade.setCost(quantity.multiply(price));
        trade.setInvestmentDate(DateUtils.parseDate(date));
        trade.setSide(sideSelected);

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("trade", trade);
        startActivity(i);
    }

    public void onClickEditTextEntryDate(View view) {
        final EditText editText = (EditText) view;

        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(MFTradeEntryActivity.this, R.style.MyTimePickerDialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        picker.show();
    }

    public void onSideSelectionClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.side_buy:
                if (checked) {
                    sideSelected = Constants.Side.BUY;
                }
                break;
            case R.id.side_sell:
                if (checked) {
                    sideSelected = Constants.Side.SELL;
                }
                break;
        }
    }

    private static final class MyEditTextListener implements TextWatcher {
        private final EditText et_units;
        private final EditText et_price;
        private final EditText et_Inv;

        public MyEditTextListener(EditText et_units, EditText et_price, EditText et_Inv) {
            this.et_units = et_units;
            this.et_price = et_price;
            this.et_Inv = et_Inv;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                BigDecimal units = new BigDecimal(et_units.getText().toString());
                BigDecimal price = new BigDecimal(et_price.getText().toString());
                et_Inv.setText(units.multiply(price).toPlainString());
            } catch (Exception e) {
                et_Inv.setText("0");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}