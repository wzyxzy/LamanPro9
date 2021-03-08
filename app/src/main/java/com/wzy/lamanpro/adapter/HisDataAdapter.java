package com.wzy.lamanpro.adapter;

import android.content.Context;
import android.widget.TextView;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.HisData;
import com.wzy.lamanpro.bean.ListBean;
import com.wzy.lamanpro.utils.WZYBaseAdapter;

import java.util.List;

public class HisDataAdapter extends WZYBaseAdapter<ListBean> {
    public HisDataAdapter(List<ListBean> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
    }

    @Override
    public void bindData(ViewHolder holder, ListBean listBean, int indexPostion) {
        TextView name = (TextView) holder.getView(R.id.name);
        TextView account = (TextView) holder.getView(R.id.account);
//        TextView email = (TextView) holder.getView(R.id.email);
        name.setText(listBean.getName());
        account.setText(listBean.getValue());
//        email.setText(users.getEmail());

    }
}
