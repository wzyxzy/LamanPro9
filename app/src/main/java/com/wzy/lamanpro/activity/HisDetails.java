package com.wzy.lamanpro.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.wzy.lamanpro.R;
import com.wzy.lamanpro.adapter.HisDataAdapter;
import com.wzy.lamanpro.bean.HisData;
import com.wzy.lamanpro.bean.ListBean;
import com.wzy.lamanpro.bean.ReportData;
import com.wzy.lamanpro.common.LaManApplication;
import com.wzy.lamanpro.dao.CalibrationDaoUtils;
import com.wzy.lamanpro.dao.HisDaoUtils;
import com.wzy.lamanpro.dao.ReportDaoUtils;
import com.wzy.lamanpro.ui.ListViewForScrollView;
import com.wzy.lamanpro.utils.ChartUtil;
import com.wzy.lamanpro.utils.FileUtils;
import com.wzy.lamanpro.utils.MatClassifier;
import com.wzy.lamanpro.utils.PdfManager;
import com.wzy.lamanpro.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.wzy.lamanpro.common.LaManApplication.matClassifier;

public class HisDetails extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = HisDetails.class.getSimpleName();

    private FloatingActionButton fab;
    private TextView title_name;
    private ListViewForScrollView allData;
    private Long id;
    private HisData hisData;
    private HisDataAdapter hisDataAdapter;
    private List<ListBean> listBeans;
    private LineChart lineChart;
    List<String> xDataList = new ArrayList<>();// x????????????
    List<Entry> yDataList = new ArrayList<Entry>();// y??????????????????
    private TextView text_report;
    private TextView debug_message;
    private View header_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_details);
        initView();
        initData();
    }

    private void initData() {

        id = getIntent().getLongExtra("id", 0);
        hisData = new HisDaoUtils(this).queryData(id);
        title_name.setText(hisData.getName());
        listBeans = new ArrayList<>();
        listBeans.add(new ListBean("??????:", hisData.getName()));
        listBeans.add(new ListBean("??????:", hisData.getDate()));
        listBeans.add(new ListBean("???????????????:", hisData.getTestName()));
        listBeans.add(new ListBean("???????????????:", hisData.getTestAccount()));
        listBeans.add(new ListBean("????????????:", hisData.getTestLocal()));
        listBeans.add(new ListBean("??????????????????:", hisData.getTestTime()));
        listBeans.add(new ListBean("??????????????????:", hisData.getTestPower()));
        hisDataAdapter = new HisDataAdapter(listBeans, this, R.layout.item_his);
        allData.setAdapter(hisDataAdapter);
        String[] strings = hisData.getData().split(",");
        // Modified by X.F
        double[] curves = new double[LaManApplication.dataLength];
        for (int i = 0; i < strings.length; i++) {
            // ????????????????????????????????????2100?????????
            if (i >= LaManApplication.dataLength)
                break;
            xDataList.add(String.valueOf(i));
            curves[i] = Float.valueOf(strings[i]);
            yDataList.add(new Entry((float) curves[i], i));
        }
        // ?????????
        if (strings.length < LaManApplication.dataLength)
            for (int i = strings.length; i < LaManApplication.dataLength; i++)
                curves[i] = 0;
        ChartUtil.showChart(this, lineChart, xDataList, yDataList, "?????????", "??????/??????", "");
        // ????????????????????????????????????
        CalibrationDaoUtils calibUtil = new CalibrationDaoUtils(this);
        double[] current_params = calibUtil.getCalibrationParams();
        if (current_params.length == 0 || current_params[0] == 0) {
            showCalibrationAlert();
        } else {
            // ????????????
            // ????????????????????????
            // matClassifier.getRamanUtility().SetCalibrationParams(current_params);
            long startTime = SystemClock.elapsedRealtime();
            MatClassifier.classify_result result = matClassifier.classify(curves);
            long endTime = SystemClock.elapsedRealtime();
            long elapsedMilliSeconds = endTime - startTime;
            Log.d(TAG, String.format("?????????????????????%.2f???", elapsedMilliSeconds / 1000.0));
            text_report.setText(result.formal_message);
            if (!result.bSuccess)
                text_report.setTextColor(android.graphics.Color.RED);
            debug_message.setText(result.debug_message);
            //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showCalibrationAlert() {
        text_report.setText("????????????????????????????????????");
        text_report.setTextColor(Color.RED);
        debug_message.setText("??????????????????????????????????????????");
    }

    protected void checkCalibrationTable() {
        CalibrationDaoUtils calibUtil = new CalibrationDaoUtils(this);
        double[] current_params = calibUtil.getCalibrationParams();
        if (current_params.length == 0 || current_params[0] == 0) {
            showCalibrationAlert();
        }
    }

    private void initView() {
        header_data = findViewById(R.id.header_data);
        allData = findViewById(R.id.allData);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        title_name = findViewById(R.id.title_name);
        lineChart = findViewById(R.id.lineChart);
        text_report = findViewById(R.id.text_report);
        debug_message = findViewById(R.id.debug_message);
        checkCalibrationTable();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Snackbar.make(v, "????????????????????????????????????", Snackbar.LENGTH_LONG)
                        .setAction("??????", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    long timestamp = new Date().getTime();
                                    String time = TimeUtils.stampToDate(timestamp);
                                    String fileName = "??????????????????-" + timestamp + ".pdf";
                                    String path = Environment.getExternalStorageDirectory() + File.separator + "??????????????????" + File.separator + fileName;
                                    FileUtils.makeFolders(path);
                                    PdfManager.makeViewEveryPdf(new View[]{header_data, allData}, path);
                                    new ReportDaoUtils(HisDetails.this).insertReportDataList(new ReportData(fileName, time, hisData.getTestAccount(), hisData.getTestName(), hisData.getTestAccount()));
                                    Toast.makeText(HisDetails.this, "?????????????????????:" + path, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
                break;
        }
    }
}
