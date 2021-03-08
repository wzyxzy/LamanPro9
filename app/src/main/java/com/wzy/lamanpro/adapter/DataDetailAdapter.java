package com.wzy.lamanpro.adapter;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.ListBean;
import com.wzy.lamanpro.utils.WZYBaseAdapter;

import java.util.List;

public class DataDetailAdapter extends WZYBaseAdapter<ListBean> {
    public DataDetailAdapter(List<ListBean> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
    }

    @Override
    public void bindData(ViewHolder holder, ListBean listBean, int indexPostion) {
        EditText name = (EditText) holder.getView(R.id.account);
        name.setHint(listBean.getName());
        name.setText(listBean.getValue());
    }
}
