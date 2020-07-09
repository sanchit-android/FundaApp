package com.sanchit.funda;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.adapter.HomeSummary1Adapter;
import com.sanchit.funda.adapter.HomeSummary2Adapter;
import com.sanchit.funda.adapter.HomeSummary3Adapter;
import com.sanchit.funda.async.NSDL_CASAsyncLoader;
import com.sanchit.funda.model.HomeSummary1Model;
import com.sanchit.funda.model.HomeSummary2Model;
import com.sanchit.funda.model.HomeSummary3Model;
import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.utils.DummyDataGenerator;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Request code for selecting a PDF document.
    private static final int PICK_PDF_FILE = 2;

    private final List<MFPosition> positions = new ArrayList<>();

    private RecyclerView recyclerViewHomeSummary1;
    private List<HomeSummary1Model> homeSummary1Model;
    private HomeSummary1Adapter homeSummary1Adapter;

    private RecyclerView recyclerViewHomeSummary2;
    private List<HomeSummary2Model> homeSummary2Model;
    private HomeSummary2Adapter homeSummary2Adapter;

    private RecyclerView recyclerViewHomeSummary3;
    private List<HomeSummary3Model> homeSummary3Model;
    private HomeSummary3Adapter homeSummary3Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getSupportActionBar().hide();
        getSupportActionBar().setElevation(0);
        setContentView(R.layout.activity_main);

        //findViewById(R.id.layout_home_summary).setVisibility(View.INVISIBLE);

        PDFBoxResourceLoader.init(getApplicationContext());

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

    public void onClickButtonLoadData(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        if (requestCode == PICK_PDF_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                //final Button loadButton = findViewById(R.id.button_main_load_data);
                new NSDL_CASAsyncLoader(this, positions).execute(uri);

                //loadButton.setText("Loading...");
                //loadButton.setBackgroundColor(getResources().getColor(R.color.colorDisabled, null));
                //loadButton.setEnabled(false);
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }
}
