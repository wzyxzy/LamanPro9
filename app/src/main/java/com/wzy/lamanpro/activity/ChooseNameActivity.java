package com.wzy.lamanpro.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.adapter.ProductChooseAdapter;
import com.wzy.lamanpro.bean.ProductData;
import com.wzy.lamanpro.dao.ProductDataDaoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  ChooseNameActivity extends AppCompatActivity {

    private ListView choose_list;
    private List<ProductData> productData;
    private ProductChooseAdapter productChooseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_name);
        initData();
        initView();
    }

    private void initData() {
        productData = new ProductDataDaoUtils(this).queryAllData();
    }

    private void initView() {
        choose_list = findViewById(R.id.choose_list);
        View inflate = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_product, choose_list, false);
        final TextView textView = inflate.findViewById(R.id.product_name);
        textView.setText("+  新增物质名称");
        choose_list.addHeaderView(inflate);

        if (productData != null && productData.size() > 0) {
            //过滤重复名称
            ArrayList<ProductData> filteredList = new ArrayList<>();
            Map<String, Integer> filteredNames = new HashMap<>();
            for (ProductData item : productData) {
                String name = item.getProName();
                Integer count = filteredNames.get(name);
                if (count == null) {
                    count = 1;
                    filteredNames.put(name, count);
                    filteredList.add(item);
                }
            }
            productChooseAdapter = new ProductChooseAdapter(filteredList /*productData*/, this, R.layout.item_product);
        } else
            productData = new ArrayList<>();
//            Toast.makeText(ChooseNameActivity.this, "没有物品信息，请添加", Toast.LENGTH_SHORT).show();
//        if (productChooseAdapter != null)
        choose_list.setAdapter(productChooseAdapter);

        choose_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    final EditText et = new EditText(ChooseNameActivity.this);
                    new AlertDialog.Builder(ChooseNameActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(et)
                            .setTitle("请输入物质名称")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setResult(222, new Intent().putExtra("productId", -1L).putExtra("name", et.getText().toString()));
                                    finish();
                                }
                            }).setNegativeButton("取消", null).create().show();
                } else {
                    setResult(222, new Intent().putExtra("productId", productData.get(i - 1).getId()).putExtra(
                            "name", productData.get(i - 1).getProName() ));
                    finish();
                }
            }
        });
    }
}