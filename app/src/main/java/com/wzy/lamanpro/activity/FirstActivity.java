package com.wzy.lamanpro.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.common.CommonActivity;
import com.wzy.lamanpro.utils.PermissionGetting;
import com.wzy.lamanpro.utils.PermissionListener;

import java.util.Arrays;

public class FirstActivity extends CommonActivity {
    private String[] myPermissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        PermissionGetting.setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                initData();
                mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 2000);//3秒跳转

            }

            @Override
            public void onPermissionDenied() {

            }
        }, this, myPermissions);


        if (Build.VERSION.SDK_INT > 15 && Build.VERSION.SDK_INT < 19) {
            View view = this.getWindow().getDecorView();
            view.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGetting.onRequestPermissionsResult(requestCode, permissions, grantResults, new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 1000);//3秒跳转

            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(FirstActivity.this, "我们需要" + Arrays.toString(permissions) + "权限", Toast.LENGTH_SHORT).show();
                PermissionGetting.showToAppSettingDialog();
            }
        });

    }

    private static final int GOTO_MAIN_ACTIVITY = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case GOTO_MAIN_ACTIVITY:
                    Intent intent = new Intent();
                    intent.setClass(FirstActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                default:
                    break;
            }
        }

        ;
    };

}
