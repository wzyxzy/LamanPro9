package com.wzy.lamanpro.adapter;

import android.content.Context;
import android.widget.TextView;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.ProductData;
import com.wzy.lamanpro.bean.Users;
import com.wzy.lamanpro.utils.WZYBaseAdapter;

import java.util.List;

public class DataAdapter extends WZYBaseAdapter<ProductData> {
    public DataAdapter(List<ProductData> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
    }

    @Override
    public void bindData(ViewHolder holder, ProductData productData, int indexPostion) {
        TextView name = (TextView) holder.getView(R.id.name);
        TextView account = (TextView) holder.getView(R.id.account);
        TextView email = (TextView) holder.getView(R.id.email);
        name.setText(productData.getProName());
        account.setText(productData.getUserName());
//        email.setText(users.getEmail());

    }
}
