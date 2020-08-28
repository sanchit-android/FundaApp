package com.sanchit.funda.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.sanchit.funda.MainActivity;
import com.sanchit.funda.R;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.model.MutualFund;
import com.sanchit.funda.utils.DateUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MFTradeEntryActivity extends AppCompatActivity {

    private List<MutualFund> funds;

    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mf_trade_entry);

        funds = (List<MutualFund>) CacheManager.get(Caches.FUNDS);

        setupEditTexBoxes();
        setupTradeDatePicker();
        setupFundSelector();
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

        MutualFund fund = ((CacheManager.Cache<String, MutualFund>) CacheManager.get(Caches.FUNDS_BY_NAME)).get(fundName);

        trade.setFund(fund);
        trade.setCostPrice(price);
        trade.setQuantity(quantity);
        trade.setCost(quantity.multiply(price));
        trade.setInvestmentDate(DateUtils.parseDate(date));

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