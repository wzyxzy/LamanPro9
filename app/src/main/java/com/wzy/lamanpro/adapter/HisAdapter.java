package com.wzy.lamanpro.adapter;

import android.content.Context;
import android.widget.TextView;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.HisData;
import com.wzy.lamanpro.bean.ProductData;
import com.wzy.lamanpro.utils.WZYBaseAdapter;

import java.util.List;

public class HisAdapter extends WZYBaseAdapter<HisData> {
    public HisAdapter(List<HisData> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
    }

    @Override
    public void bindData(ViewHolder holder, HisData hisData, int indexPostion) {
        TextView name = (TextView) holder.getView(R.id.name);
        TextView account = (TextView) holder.getView(R.id.account);
//        TextView email = (TextView) holder.getView(R.id.email);
        name.setText(hisData.getName());
        account.setText(hisData.getDate());
//        email.setText(users.getEmail());

    }
}
