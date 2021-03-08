package com.wzy.lamanpro.adapter;

import android.content.Context;
import android.widget.TextView;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.ProductData;
import com.wzy.lamanpro.utils.WZYBaseAdapter;

import java.util.List;

public class ProductChooseAdapter extends WZYBaseAdapter<ProductData> {

    public ProductChooseAdapter(List<ProductData> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
    }

    @Override
    public void bindData(ViewHolder holder, ProductData productData, int indexPostion) {
        TextView product_name = (TextView) holder.getView(R.id.product_name);
        product_name.setText(productData.getProName());
    }
}
