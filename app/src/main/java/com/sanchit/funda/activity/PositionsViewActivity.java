package com.sanchit.funda.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.adapter.PositionsViewTableAdapter;
import com.sanchit.funda.model.MFPosition;

import java.util.List;

public class PositionsViewActivity extends AppCompatActivity {

    private List<MFPosition> positions;

    private RecyclerView recyclerViewPositionsView;
    private PositionsViewTableAdapter positionsViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Positions...");
        setContentView(R.layout.activity_positions_view);

        positions = (List<MFPosition>) getIntent().getSerializableExtra("positions");

        recyclerViewPositionsView = findViewById(R.id.recycler_view_positions_view_table);
        recyclerViewPositionsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        positionsViewAdapter = new PositionsViewTableAdapter(this, positions);
        recyclerViewPositionsView.setAdapter(positionsViewAdapter);
    }
}