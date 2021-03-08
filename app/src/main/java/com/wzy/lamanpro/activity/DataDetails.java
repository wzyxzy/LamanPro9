package com.wzy.lamanpro.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.ProductData;
import com.wzy.lamanpro.common.LaManApplication;
import com.wzy.lamanpro.dao.ProductDataDaoUtils;
import com.wzy.lamanpro.utils.ChartUtil;
import com.wzy.lamanpro.utils.MatClassifier;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.wzy.lamanpro.common.LaManApplication.isManager;
import static com.wzy.lamanpro.common.LaManApplication.matClassifier;

public class DataDetails extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DataDetails.class.getSimpleName();

    private FloatingActionButton fab;
    private TextView title_name;
    private LineChart lineChart;
    private double[] original_data;
//    private ListView allData;
    private Long id;
    private Long id2;
    private String results;
    private ProductData productData;
//    private DataDetailAdapter dataDetailAdapter;
//    private List<ListBean> listBeans;
    List<String> xDataList = new ArrayList<>();// x轴数据源
    List<Entry> yDataList = new ArrayList<Entry>();// y轴数据数据源
    private EditText product_name;
    private EditText user_account;
    private EditText user_company;
    private EditText product_hs;
    private EditText product_cas;
    private EditText product_nfpa704;
    private EditText dangerous_level;
    private EditText dangerous_sign;
    private EditText dangerous_transport;
    private EditText product_mdl;
    private EditText product_einecs;
    private EditText product_rtecs;
    private EditText product_brn;
    private EditText product_detail;

    private EditText classifier_threshold;
    private EditText classifier_priority;

    //Modified by X.F 逻辑修改为从建库或者从数据库管理页面进来，都可以进行修改
    private boolean canEdit = true;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_details);
        initView();
        initData();
    }

    private void initData() {
        id = getIntent().getLongExtra("id", -1L);
        if (id == -1) {
            //从AddLibrary跳转；此时需要将校正后的数据和原始数据一起写入PRODUCT_DATA表
            results = getIntent().getStringExtra("results");
            title_name.setText("建库");
            productData = new ProductData("", "", format.format(new Date()), "", "", "", "", "", "", "", "", "", "", "", "", "", 0.97, 5L, "");
            setTextAllNull();
            //canEdit = true;
            checkEnabled();
        } else {
            setTextAll(id);
            //canEdit = false;
            checkEnabled();
        }

        String[] strings = results.split(",");
        assert(strings.length == LaManApplication.dataLength);
        original_data = new double[strings.length];
        for (int i = 0; i < strings.length; i++) {
            original_data[i] = Double.valueOf(strings[i]);
            xDataList.add(String.valueOf(i));
            yDataList.add(new Entry((float)original_data[i], i));
        }
        ChartUtil.showChart(this, lineChart, xDataList, yDataList, "光谱图", "波长/时间", "");
    }

    private void setTextAll(long id) {
        productData = new ProductDataDaoUtils(this).queryData(id);
        title_name.setText(productData.getProName());
        if (TextUtils.isEmpty(results))
            results = productData.getData();
        product_name.setText(productData.getProName());
        user_account.setText(productData.getUserName());
        user_company.setText(productData.getUserCompany());
        product_hs.setText(productData.getProHSCode());
        product_cas.setText(productData.getProCASCode());
        product_nfpa704.setText(productData.getProNFPA704Code());
        dangerous_level.setText(productData.getProDangerLevel());
        dangerous_sign.setText(productData.getProDangerClass());
        dangerous_transport.setText(productData.getProDangerTransportCode());
        product_mdl.setText(productData.getProMDLNumber());
        product_einecs.setText(productData.getProEINECSNumber());
        product_rtecs.setText(productData.getProRTECSNumber());
        product_brn.setText(productData.getProBRNNumber());
        product_detail.setText(productData.getProDetail());
        classifier_threshold.setText(productData.getProductThreshold().toString());
        classifier_priority.setText(productData.getProductPriority().toString());
    }

    private void setTextAllNull() {
        productData.setId(null);
        title_name.setText("");
        user_account.setText("");
        user_company.setText("");
        product_hs.setText("");
        product_cas.setText("");
        product_nfpa704.setText("");
        dangerous_level.setText("");
        dangerous_sign.setText("");
        dangerous_transport.setText("");
        product_mdl.setText("");
        product_einecs.setText("");
        product_rtecs.setText("");
        product_brn.setText("");
        product_detail.setText("");
        classifier_threshold.setText(String.format("%.2f", MatClassifier.default_classifier_threshold));
        classifier_priority.setText(String.format("%d", MatClassifier.default_classifier_priority));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && canEdit) {
            saveSubmit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveSubmit() {
        if (!checkDataValid())
            return;
        new AlertDialog.Builder(this).setMessage("您要保存修改的数据吗？").setTitle("特别提示").setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (submit()) {
                    boolean canSave;
                    if (id == -1) {
                        productData.setData(results);
                        productData.setCalibratedData(matClassifier.convertDataArrayToString(matClassifier.getRamanUtility().CalibrateSpectrum(original_data)));
                        //逻辑简化为从建库进来的一律新增记录；不再有覆盖功能
//                        if (id2 == -1 || id2 == -2)
                        canSave = new ProductDataDaoUtils(DataDetails.this).insertProductList(productData);
//                        else
//                            canSave = new DataDaoUtils(DataDetails.this).updateData(productData);
                        Log.d(TAG, "id2: " + id2);
                        if (canSave)
                            finish();
                        else
                            Log.w(TAG, "写入PRODUCT_DATA表失败！");
                    } else {
                        canSave = new ProductDataDaoUtils(DataDetails.this).updateData(productData);
                        if (canSave)
                            finish();
                        else
                            Log.w(TAG, "写入PRODUCT_DATA表失败！");
                    }
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).create().show();
    }

    private void checkEnabled() {
        if (canEdit && id != -1) {
            product_name.setFocusable(canEdit);
            product_name.setFocusableInTouchMode(canEdit);
        }
        user_account.setEnabled(canEdit);
        user_company.setEnabled(canEdit);
        product_hs.setEnabled(canEdit);
        product_cas.setEnabled(canEdit);
        product_nfpa704.setEnabled(canEdit);
        dangerous_level.setEnabled(canEdit);
        dangerous_sign.setEnabled(canEdit);
        dangerous_transport.setEnabled(canEdit);
        product_mdl.setEnabled(canEdit);
        product_einecs.setEnabled(canEdit);
        product_rtecs.setEnabled(canEdit);
        product_brn.setEnabled(canEdit);
        product_detail.setEnabled(canEdit);
        classifier_threshold.setEnabled(canEdit);
        classifier_priority.setEnabled(canEdit);
    }

    @SuppressLint("RestrictedApi")
    private void initView() {
//        getSupportActionBar().
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        if (!isManager) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.GONE);
        }
        title_name = findViewById(R.id.title_name);
        title_name.setOnClickListener(this);
        lineChart = findViewById(R.id.lineChart);
        lineChart.setOnClickListener(this);
        product_name = findViewById(R.id.product_name);
        product_name.setOnClickListener(this);
        user_account = findViewById(R.id.user_account);
        user_account.setOnClickListener(this);
        user_company = findViewById(R.id.user_company);
        user_company.setOnClickListener(this);
        product_hs = findViewById(R.id.product_hs);
        product_hs.setOnClickListener(this);
        product_cas = findViewById(R.id.product_cas);
        product_cas.setOnClickListener(this);
        product_nfpa704 = findViewById(R.id.product_nfpa704);
        product_nfpa704.setOnClickListener(this);
        dangerous_level = findViewById(R.id.dangerous_level);
        dangerous_level.setOnClickListener(this);
        dangerous_sign = findViewById(R.id.dangerous_sign);
        dangerous_sign.setOnClickListener(this);
        dangerous_transport = findViewById(R.id.dangerous_transport);
        dangerous_transport.setOnClickListener(this);
        product_mdl = findViewById(R.id.product_mdl);
        product_mdl.setOnClickListener(this);
        product_einecs = findViewById(R.id.product_einecs);
        product_einecs.setOnClickListener(this);
        product_rtecs = findViewById(R.id.product_rtecs);
        product_rtecs.setOnClickListener(this);
        product_brn = findViewById(R.id.product_brn);
        product_brn.setOnClickListener(this);
        product_detail = findViewById(R.id.product_detail);
        product_detail.setOnClickListener(this);

        classifier_threshold = findViewById(R.id.classifier_threshold);
        classifier_threshold.setOnClickListener(this);
        classifier_priority = findViewById(R.id.classifier_priority);
        classifier_priority.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Snackbar.make(v, "点击可以对库数据进行修改，确认要修改吗", Snackbar.LENGTH_LONG)
                        .setAction("确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                canEdit = true;
                                checkEnabled();
                            }
                        }).show();
                break;
            case R.id.product_name:
                if (id == -1)
                    startActivityForResult(new Intent(this, ChooseNameActivity.class), 222);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 222 && resultCode == 222) {
            assert data != null;
            id2 = data.getLongExtra("productId", -2L);
            if (id2 == -2) {
                Toast.makeText(DataDetails.this, "没有选择任何物质", Toast.LENGTH_SHORT).show();
            } else {
                product_name.setText(data.getStringExtra("name"));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (canEdit) {
            saveSubmit();
        } else {
            super.onBackPressed();
        }
    }

    //先确认数据完整性再提示是否保存
    private boolean checkDataValid() {
        String name = product_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "样品名称不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        productData.setProName(name);

        String threshold_text = classifier_threshold.getText().toString().trim();
        Double threshold = -1.0;
        if (!threshold_text.isEmpty()) {
            try {
                threshold = Double.parseDouble(threshold_text);
            } catch (Exception e) {
                Log.d(TAG, "分类器阈值解析出错：" + threshold_text);
            }
        }
        if (threshold < 0 || threshold > 1) {
            Toast.makeText(this, "分类器阈值范围为0到1之间", Toast.LENGTH_SHORT).show();
            return false;
        }
        productData.setProductThreshold(threshold);

        String priority_text = classifier_priority.getText().toString().trim();
        Long priority = 0L;
        if (!priority_text.isEmpty()) {
            try {
                priority = Long.parseLong(priority_text);
            } catch (Exception e) {
                Log.d(TAG, "分类器优先级解析出错：" + priority_text);
            }
        }
        if (priority < 1 || priority > 9) {
            Toast.makeText(this, "分类器优先级范围为1到9之间", Toast.LENGTH_SHORT).show();
            return false;
        }
        productData.setProductPriority(priority);
        return true;
    }

    private boolean submit() {

        productData.setUserName(user_account.getText().toString().trim());
//        if (TextUtils.isEmpty(account)) {
//            Toast.makeText(this, "用户名", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setUserCompany(user_company.getText().toString().trim());
//        if (TextUtils.isEmpty(company)) {
//            Toast.makeText(this, "公司", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProHSCode(product_hs.getText().toString().trim());
//        if (TextUtils.isEmpty(hs)) {
//            Toast.makeText(this, "HS码", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProCASCode(product_cas.getText().toString().trim());
//        if (TextUtils.isEmpty(cas)) {
//            Toast.makeText(this, "CAS码", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProNFPA704Code(product_nfpa704.getText().toString().trim());
//        if (TextUtils.isEmpty(nfpa704)) {
//            Toast.makeText(this, "NFPA704标志", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProDangerLevel(dangerous_level.getText().toString().trim());
//        if (TextUtils.isEmpty(level)) {
//            Toast.makeText(this, "危险等级", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProDangerClass(dangerous_sign.getText().toString().trim());
//        if (TextUtils.isEmpty(sign)) {
//            Toast.makeText(this, "危险性符号", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProDangerTransportCode(dangerous_transport.getText().toString().trim());
//        if (TextUtils.isEmpty(transport)) {
//            Toast.makeText(this, "危险运输编码", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProMDLNumber(product_mdl.getText().toString().trim());
//        if (TextUtils.isEmpty(mdl)) {
//            Toast.makeText(this, "MDL号", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProEINECSNumber(product_einecs.getText().toString().trim());
//        if (TextUtils.isEmpty(einecs)) {
//            Toast.makeText(this, "EINECS号", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProRTECSNumber(product_rtecs.getText().toString().trim());
//        if (TextUtils.isEmpty(rtecs)) {
//            Toast.makeText(this, "RTECS号", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProBRNNumber(product_brn.getText().toString().trim());
//        if (TextUtils.isEmpty(brn)) {
//            Toast.makeText(this, "BRN号", Toast.LENGTH_SHORT).show();
//            return;
//        }

        productData.setProDetail(product_detail.getText().toString().trim());
//        if (TextUtils.isEmpty(detail)) {
//            Toast.makeText(this, "样品信息", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // TODO validate success, do something
        return true;
    }
}
