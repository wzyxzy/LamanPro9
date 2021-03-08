package com.wzy.lamanpro.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wzy.lamanpro.bean.DaoMaster;
import com.wzy.lamanpro.bean.DaoSession;

public class DBManager {
    private final static String dbName = "laman";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
        openHelper = new MyGreenDaoDbHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    public SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    public SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    /**
     * 关闭可写数据库
     */
    public void closeDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.close();
    }

    public DaoSession getDaoSession() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        return daoMaster.newSession();
    }
}