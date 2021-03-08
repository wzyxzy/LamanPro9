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
    List<String> xDataList = new ArrayList<>();// x轴数据源
    List<Entry> yDataList = new ArrayList<Entry>();// y轴数据数据源
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
        listBeans.add(new ListBean("名字:", hisData.getName()));
        listBeans.add(new ListBean("日期:", hisData.getDate()));
        listBeans.add(new ListBean("测试者姓名:", hisData.getTestName()));
        listBeans.add(new ListBean("测试者账号:", hisData.getTestAccount()));
        listBeans.add(new ListBean("测试地点:", hisData.getTestLocal()));
        listBeans.add(new ListBean("测试积分时间:", hisData.getTestTime()));
        listBeans.add(new ListBean("测试积分功率:", hisData.getTestPower()));
        hisDataAdapter = new HisDataAdapter(listBeans, this, R.layout.item_his);
        allData.setAdapter(hisDataAdapter);
        String[] strings = hisData.getData().split(",");
        // Modified by X.F
        double[] curves = new double[LaManApplication.dataLength];
        for (int i = 0; i < strings.length; i++) {
            // 兼容性，测试旧版数据库（2100长度）
            if (i >= LaManApplication.dataLength)
                break;
            xDataList.add(String.valueOf(i));
            curves[i] = Float.valueOf(strings[i]);
            yDataList.add(new Entry((float) curves[i], i));
        }
        // 兼容性
        if (strings.length < LaManApplication.dataLength)
            for (int i = strings.length; i < LaManApplication.dataLength; i++)
                curves[i] = 0;
        ChartUtil.showChart(this, lineChart, xDataList, yDataList, "光谱图", "波长/时间", "");
        // 物质分类，需要先进行标定
        CalibrationDaoUtils calibUtil = new CalibrationDaoUtils(this);
        double[] current_params = calibUtil.getCalibrationParams();
        if (current_params.length == 0 || current_params[0] == 0) {
            showCalibrationAlert();
        } else {
            // 物质分类
            // 保险起见设置一遍
            // matClassifier.getRamanUtility().SetCalibrationParams(current_params);
            long startTime = SystemClock.elapsedRealtime();
            MatClassifier.classify_result result = matClassifier.classify(curves);
            long endTime = SystemClock.elapsedRealtime();
            long elapsedMilliSeconds = endTime - startTime;
            Log.d(TAG, String.format("物质识别耗时：%.2f秒", elapsedMilliSeconds / 1000.0));
            text_report.setText(result.formal_message);
            if (!result.bSuccess)
                text_report.setTextColor(android.graphics.Color.RED);
            debug_message.setText(result.debug_message);
            //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showCalibrationAlert() {
        text_report.setText("请先用乙腈样品进行标定！");
        text_report.setTextColor(Color.RED);
        debug_message.setText("经过标定之后才能进行物质识别");
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
                Snackbar.make(v, "您确定要生成测试报告吗？", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    long timestamp = new Date().getTime();
                                    String time = TimeUtils.stampToDate(timestamp);
                                    String fileName = "拉曼测试报告-" + timestamp + ".pdf";
                                    String path = Environment.getExternalStorageDirectory() + File.separator + "拉曼测试报告" + File.separator + fileName;
                                    FileUtils.makeFolders(path);
                                    PdfManager.makeViewEveryPdf(new View[]{header_data, allData}, path);
                                    new ReportDaoUtils(HisDetails.this).insertReportDataList(new ReportData(fileName, time, hisData.getTestAccount(), hisData.getTestName(), hisData.getTestAccount()));
                                    Toast.makeText(HisDetails.this, "文件已经保存在:" + path, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
                break;
        }
    }
}
