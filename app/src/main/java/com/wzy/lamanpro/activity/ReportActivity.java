package com.wzy.lamanpro.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.wzy.lamanpro.R;
import com.wzy.lamanpro.adapter.ReportAdapter;
import com.wzy.lamanpro.bean.ReportData;
import com.wzy.lamanpro.dao.ProductDataDaoUtils;
import com.wzy.lamanpro.dao.ReportDaoUtils;
import com.wzy.lamanpro.utils.FileUtils;
import com.wzy.lamanpro.utils.PdfManager;
import com.wzy.lamanpro.utils.SPUtility;
import com.wzy.lamanpro.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.wzy.lamanpro.common.LaManApplication.isManager;
import static com.wzy.lamanpro.common.LaManApplication.matClassifier;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView reportList;
    private FloatingActionButton menu_report;
    private FloatingActionButton cancel_report;
    private ReportAdapter reportAdapter;
    private List<ReportData> reportData;
    private CheckBox check_report;
    private boolean isCheck;


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
        cancel_report = (FloatingActionButton) findViewById(R.id.cancel_report);

        menu_report.setOnClickListener(this);
        cancel_report.setOnClickListener(this);
        check_report = (CheckBox) findViewById(R.id.check_report);
        reportData = new ArrayList<>();
        if (isManager)
            reportData = new ReportDaoUtils(this).queryAllData();
        else
            reportData = new ReportDaoUtils(this).queryAllDataByAccount(SPUtility.getUserId(this));

        reportAdapter = new ReportAdapter(reportData, this, R.layout.item_report);
        reportList.setAdapter(reportAdapter);
        reportList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isCheck){//选择

                }else {//打开

                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_report://导出按钮
                if (isCheck) {
                    new AlertDialog.Builder(this).setMessage("您要导出所选数据吗？").setTitle("特别提示").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isCheck = false;

                        }
                    }).setNegativeButton("取消", null).create().show();
                } else {
                    isCheck = true;
                }
                reportAdapter.setCheck(isCheck);
                reportAdapter.notifyDataSetChanged();
                check_report.setVisibility(isCheck ? View.VISIBLE : View.GONE);
                cancel_report.setVisibility(isCheck ? View.VISIBLE : View.GONE);

                break;
            case R.id.cancel_report:
                isCheck = false;
                reportAdapter.setCheck(isCheck);
                reportAdapter.notifyDataSetChanged();
                check_report.setVisibility(isCheck ? View.VISIBLE : View.GONE);
                cancel_report.setVisibility(isCheck ? View.VISIBLE : View.GONE);
                break;
        }
    }
}