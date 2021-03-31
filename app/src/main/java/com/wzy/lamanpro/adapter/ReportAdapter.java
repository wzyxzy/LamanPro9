package com.wzy.lamanpro.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.ProductData;
import com.wzy.lamanpro.bean.ReportData;
import com.wzy.lamanpro.utils.WZYBaseAdapter;

import java.util.List;

public class ReportAdapter extends WZYBaseAdapter<ReportData> {


    private boolean isCheck;

    public ReportAdapter(List<ReportData> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
    }

    @Override
    public void bindData(ViewHolder holder, ReportData reportData, int indexPostion) {
        CheckBox check_report = (CheckBox) holder.getView(R.id.check_report);
        check_report.setVisibility(isCheck ? View.VISIBLE : View.GONE);
        TextView num = (TextView) holder.getView(R.id.num);
        TextView test_user = (TextView) holder.getView(R.id.test_user);
        TextView test_time = (TextView) holder.getView(R.id.test_time);
        num.setText(String.format("%d", indexPostion + 1));
        test_user.setText(reportData.getTestName());
        test_time.setText(reportData.getTime());
    }


    public void setCheck(boolean check) {
        isCheck = check;
    }
}
