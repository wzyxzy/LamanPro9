package com.wzy.lamanpro.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

public class PermissionGetting {
    private static PermissionListener permissionListener;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static String[] myPermissions;
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;


    public static void setPermissionListener(PermissionListener permissionListener, Context context, String... myPermissions) {
        PermissionGetting.permissionListener = permissionListener;
        PermissionGetting.context = context;
        PermissionGetting.myPermissions = myPermissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission();
        } else {
            permissionListener.onPermissionGranted();
        }
    }


    private static void permission() {
        boolean isAllGranted = checkPermissionAllGranted(
                myPermissions);
        if (isAllGranted) {
            permissionListener.onPermissionGranted();
            return;
        }
        ActivityCompat.requestPermissions(
                (Activity) context,
                myPermissions,
                MY_PERMISSION_REQUEST_CODE
        );


    }

    private static boolean checkPermissionAllGranted(String[] myPermissions) {
        for (String permission : myPermissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }


    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionListener permissionListener) {
        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                permissionListener.onPermissionGranted();

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                permissionListener.onPermissionDenied();
            }
        }
    }


    /**
     * 跳转到权限设置界面
     */
    private static void toAppSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }


    public static void showToAppSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("我们需要" + Arrays.toString(myPermissions) + "权限，请到 “设置 -> 应用权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toAppSetting(context);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }


}
