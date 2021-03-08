package com.wzy.lamanpro.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.wzy.lamanpro.R;

/**
 * 常用的Dialog 工具类
 */

public class CommonBaseDialog extends Dialog implements View.OnClickListener {
    private int itemLayoutId;
    private OnCloseListener mListener;
    private boolean mIsDismiss = false;

    public interface OnCloseListener {
        void onClick(Dialog dialog, int viewId);
    }

    private CommonBaseDialog(Context context, int theme, int itemLayoutId) {
        super(context, theme);
        this.itemLayoutId = itemLayoutId;
    }

    public static CommonBaseDialog showDialog(Context mContext, int itemLayoutId) {
        CommonBaseDialog dialog = new CommonBaseDialog(mContext, R.style.common_dialog, itemLayoutId);
        dialog.show();
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(itemLayoutId);
        setCanceledOnTouchOutside(mIsDismiss);
    }

    public CommonBaseDialog setDialogLocation(int gravity, int left, int top, int right, int bottom) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = gravity;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(left, top, right, bottom);
        getWindow().setAttributes(layoutParams);
        return this;
    }

    /**
     * 设置点击Dialog其他位置是否消失 默认false
     */
    public CommonBaseDialog setOnTouchOutside(boolean isDismiss) {
        this.mIsDismiss = isDismiss;
        setCanceledOnTouchOutside(isDismiss);
        return this;
    }

    /**
     * 设置点击事件
     *
     * @param viewIds 需要设置的点击事件控件id
     */
    public CommonBaseDialog setViewListener(OnCloseListener listener, int... viewIds) {
        this.mListener = listener;
        for (int viewId : viewIds) {
            findViewById(viewId).setOnClickListener(this);
        }
        return this;
    }

    public <T extends View> T getView(int viewId) {
        return (T) findViewById(viewId);
    }


    public CommonBaseDialog setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onClick(this, v.getId());
        }
    }

}
