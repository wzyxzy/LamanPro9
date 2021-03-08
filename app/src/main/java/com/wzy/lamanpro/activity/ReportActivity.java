package com.wzy.lamanpro.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wzy.lamanpro.R;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView reportList;
    private FloatingActionButton menu_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        initView();
    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        reportList = (ListView) findViewById(R.id.reportList);
        menu_report = (FloatingActionButton) findViewById(R.id.menu_report);

        menu_report.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_report:

                break;
        }
    }
}