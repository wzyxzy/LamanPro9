package com.wzy.lamanpro.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.wzy.lamanpro.R;
import com.wzy.lamanpro.adapter.HisAdapter;
import com.wzy.lamanpro.bean.HisData;
import com.wzy.lamanpro.dao.HisDaoUtils;
import com.wzy.lamanpro.dao.ProductDataDaoUtils;
import com.wzy.lamanpro.utils.SPUtility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.wzy.lamanpro.common.LaManApplication.isManager;

public class ManageHis extends AppCompatActivity implements View.OnClickListener {

    private ListView hisList;
    private List<HisData> hisData;
    private HisAdapter hisAdapter;
    private FloatingActionButton menu_search;
    private List<HisData> hisDatas;
    private TabLayout tab_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_his);
        initView();
        initData();
    }

    private void initData() {
        if (isManager)
            hisData = new HisDaoUtils(this).queryAllData();
        else
            hisData = new HisDaoUtils(this).queryAllDataByAccount(SPUtility.getUserId(ManageHis.this));
        hisDatas = hisData;
        hisAdapter = new HisAdapter(hisDatas, this, R.layout.item_his);
        hisList.setAdapter(hisAdapter);
        hisList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ManageHis.this, HisDetails.class);
                intent.putExtra("id", hisDatas.get(position).getId());
                startActivity(intent);
            }
        });

        if (!isManager) {
            return;
        }
        hisList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                new AlertDialog.Builder(ManageHis.this)
                        .setTitle("温馨提示")
                        .setMessage("确定要删除吗？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new HisDaoUtils(ManageHis.this).deleteData(hisDatas.get(position).getId());
                                if (isManager)
                                    hisData = new HisDaoUtils(ManageHis.this).queryAllData();
                                else
                                    hisData = new HisDaoUtils(ManageHis.this).queryAllDataByAccount(SPUtility.getUserId(ManageHis.this));

                                hisDatas = hisData;
                                hisAdapter.updateRes(hisDatas);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).create().show();
                return true;
            }
        });
    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hisList = findViewById(R.id.hisList);
        menu_search = findViewById(R.id.menu_search);
        menu_search.setOnClickListener(this);
        Toast.makeText(this, "长按条目即可删除", Toast.LENGTH_LONG).show();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isManager)
            hisData = new HisDaoUtils(this).queryAllData();
        else
            hisData = new HisDaoUtils(this).queryAllDataByAccount(SPUtility.getUserId(ManageHis.this));

        hisDatas = hisData;
        hisAdapter.updateRes(hisDatas);
    }

    //选择日期
    private void showCalendar(final TextView editText, final int type) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        int month = monthOfYear + 1;
                        if (month < 10 && dayOfMonth < 10) {
                            editText.setText(year + "/0" + month + "/0" + dayOfMonth);
                        } else if (month < 10 && dayOfMonth >= 10) {
                            editText.setText(year + "/0" + month + "/" + dayOfMonth);
                        } else if (month >= 10 && dayOfMonth < 10) {
                            editText.setText(year + "/" + month + "/0" + dayOfMonth);
                        } else {
                            editText.setText(year + "/" + month + "/" + dayOfMonth);
                        }
                        if (type == 0)
                            editText.append(" 00:00:00");
                        else
                            editText.append(" 23:59:59");
                    }
                }
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                .get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_search:
                Snackbar.make(v, "点击可以进行条件查询，确定查询吗？", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                View view = LayoutInflater.from(ManageHis.this).inflate(R.layout.content_search_data, null, false);
                                final EditText editText = view.findViewById(R.id.name);
                                final TextView timeFrom = view.findViewById(R.id.time_from);
                                final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
                                timeFrom.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showCalendar(timeFrom, 0);
                                    }
                                });
                                final TextView timeTo = view.findViewById(R.id.time_to);
                                timeTo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showCalendar(timeTo, 1);
                                    }
                                });
                                new AlertDialog.Builder(ManageHis.this).setView(view).setTitle("条件查询").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ProgressDialog pd2 = ProgressDialog.show(ManageHis.this, "温馨提示", "正在搜索...");
                                        String name = editText.getText().toString();
                                        String timeFromText = timeFrom.getText().toString().isEmpty() ? "1800/00/00 00:00:00" : timeFrom.getText().toString();
                                        String timeToText = timeTo.getText().toString().isEmpty() ? "2999/12/31 00:00:00" : timeTo.getText().toString();
                                        hisDatas = new ArrayList<>();
                                        for (HisData hisData : hisData) {
                                            try {
                                                long datePro = format.parse(hisData.getDate()).getTime();
                                                if (hisData.getName().contains(name) && format.parse(timeFromText).getTime() <= datePro && datePro <= format.parse(timeToText).getTime()) {
                                                    hisDatas.add(hisData);
                                                }

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        hisAdapter.removeAll();
                                        hisAdapter.updateRes(hisDatas);
                                        pd2.dismiss();
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("取消", null).create().show();

//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                                    String path = Environment.getExternalStorageDirectory() + File.separator + new Date().getTime() + ".pdf";
//                                    PdfManager.makeViewEveryPdf(new View[]{view_pdf}, path);
//                                    Toast.makeText(HisDetails.this, "文件已经保存在:" + path, Toast.LENGTH_SHORT).show();
//                                }
                            }
                        }).show();
                break;
        }
    }
}
