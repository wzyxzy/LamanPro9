package com.wzy.lamanpro.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wzy.lamanpro.R;

/**
 * 自定义Dialog
 */
public class CommonDialog extends Dialog {

    private TextView titleTv;
    private TextView messageTv;
    private Button leftBtn;
    private Button rightBtn;

    private String title;
    private String message;
    private String leftBtnText;
    private String rightBtnText;
    private LeftButtonClickListener leftButtonClickListener;
    private RightButtonClickListener rightButtonClickListener;

    public void setLeftButtonClickListener(LeftButtonClickListener leftButtonClickListener) {
        this.leftButtonClickListener = leftButtonClickListener;
    }

    public void setRightButtonClickListener(RightButtonClickListener rightButtonClickListener) {
        this.rightButtonClickListener = rightButtonClickListener;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setLeftBtnText(String leftBtnText) {
        this.leftBtnText = leftBtnText;
    }

    public void setRightBtnText(String rightBtnText) {
        this.rightBtnText = rightBtnText;
    }

    public CommonDialog(@NonNull Context context) {
        super(context, R.style.CommonDialog);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_dialog);

        setCanceledOnTouchOutside(false);
        initView();
        initEvent();
        initData();
    }

    private void initData() {
        if (title != null) titleTv.setText(title);
        if (message != null) messageTv.setText(message);
        if (leftBtnText != null) leftBtn.setText(leftBtnText);
        if (rightBtnText != null) rightBtn.setText(rightBtnText);
    }

    private void initEvent() {
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (leftButtonClickListener != null) {
                    leftButtonClickListener.onLeftButtonClick();
                } else {
                    dismiss();
                }
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rightButtonClickListener != null) {
                    rightButtonClickListener.onRightButtonClick();
                } else {
                    dismiss();
                }
            }
        });
    }

    private void initView() {
        titleTv = findViewById(R.id.title);
        messageTv = findViewById(R.id.message);
        leftBtn = findViewById(R.id.no);
        rightBtn = findViewById(R.id.yes);

//        titleTv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    public interface LeftButtonClickListener {
        void onLeftButtonClick();
    }
    public interface RightButtonClickListener {
        void onRightButtonClick();
    }
}