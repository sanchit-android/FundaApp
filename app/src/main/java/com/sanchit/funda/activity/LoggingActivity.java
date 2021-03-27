package com.sanchit.funda.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.adapter.LoggingAdapter;
import com.sanchit.funda.log.LogManager;
import com.sanchit.funda.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class LoggingActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLogs;
    private LoggingAdapter adapter;
    private List<String> logs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("LOGS...");
        getSupportActionBar().setElevation(0);
        ViewUtils.setActionBarColor(this, R.color.colorPrimaryDark);
        setTitleColor(getResources().getColor(R.color.colorTextLight));
        setContentView(R.layout.activity_logging);

        setupLogs();
        recyclerViewLogs = findViewById(R.id.recycler_view_logs);
        recyclerViewLogs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new LoggingAdapter(this, logs);
        recyclerViewLogs.setAdapter(adapter);
    }

    private void setupLogs() {
        logs = new ArrayList<>(LogManager.getLogs());
        logs.add("---END OF LOGS---");
    }
}