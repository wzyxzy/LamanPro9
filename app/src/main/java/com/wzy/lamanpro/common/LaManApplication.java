package com.wzy.lamanpro.common;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.wzy.lamanpro.BuildConfig;
import com.wzy.lamanpro.utils.CrashHandler;
import com.wzy.lamanpro.utils.MatClassifier;

public class LaManApplication extends Application {

    private static LaManApplication appContext;
    public static boolean canUseUsb;
    public static boolean isManager;
    // Modified by X.F
    //删除easyMode这个全局变量，需要的时候直接访问preferences
    //public static boolean easyMode;
    public static final int dataLength = 2095;
    public static MatClassifier matClassifier;
    //测试开关
    public static boolean testMode = false;

    public static LaManApplication getInstance() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        initOkgo();
        initStetho();
        matClassifier = new MatClassifier(this);
        if (BuildConfig.DEBUG) {
            Logger.addLogAdapter(new AndroidLogAdapter());
        }
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    private void initStetho() {
        Stetho.initializeWithDefaults(this);
    }

    private void initOkgo() {
        OkGo.getInstance().init(this);
    }
}
