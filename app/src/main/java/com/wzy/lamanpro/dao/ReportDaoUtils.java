package com.wzy.lamanpro.dao;

import android.content.Context;
import android.widget.Toast;

import com.wzy.lamanpro.bean.ReportData;
import com.wzy.lamanpro.bean.ReportDataDao;
import com.wzy.lamanpro.bean.ReportDataDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class ReportDaoUtils {
    private ReportDataDao reportDataDao;
    private Context context;


    public ReportDaoUtils(Context context) {
        this.context = context;
//        SQLiteDatabase writableDatabase = DBManager.getInstance(context).getWritableDatabase();
//        DaoMaster daoMaster = new DaoMaster(writableDatabase);
//        DaoSession daoSession = daoMaster.newSession();
        reportDataDao = DBManager.getInstance(context).getDaoSession().getReportDataDao();
    }


    /**
     * 插入用户
     *
     * @param reportData 账户
     */
    public boolean insertReportDataList(ReportData reportData) {
        if (reportData == null) {
            Toast.makeText(context, "信息有错，不能保存", Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (queryUserSize(ReportData.getName()) > 0) {
//            Toast.makeText(context, "已有同名记录，建议修改名称", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        reportDataDao.insert(reportData);
        return true;
    }

    /**
     * 更新账户
     *
     * @param reportData 用户账户
     */
    public void updateData(ReportData reportData) {
        if (reportData == null) {
            return;
        }
        reportDataDao.update(reportData);
    }


//    /**
//     * 查询用户密码
//     */
//    public String queryUserPass(String account) {
//
//        QueryBuilder<Users> qb = usersDao.queryBuilder();
//        qb.where(UsersDao.Properties.Account.eq(account));
//        if (qb.list() == null || qb.list().size() == 0)
//            return "";
//        return qb.list().get(0).getPassword();
//    }

//    /**
//     * 查询用户密码
//     */
//    public String queryUserName(String account) {
//
//        QueryBuilder<Users> qb = usersDao.queryBuilder();
//        qb.where(UsersDao.Properties.Account.eq(account));
//        if (qb.list() == null || qb.list().size() == 0)
//            return "";
//        return qb.list().get(0).getName();
//    }

//    /**
//     * 查询用户列表个数
//     */
//    public int queryAccountSize() {
//        QueryBuilder<Users> qb = usersDao.queryBuilder();
//        return qb.list().size();
//    }

    /**
     * 查询所有记录列表
     */
    public List<ReportData> queryAllData() {
        QueryBuilder<ReportData> qb = reportDataDao.queryBuilder();
        return qb.list();
    }

    /**
     * 查询用户所有记录列表
     */
    public List<ReportData> queryAllDataByAccount(String account) {
        QueryBuilder<ReportData> qb = reportDataDao.queryBuilder();
        qb.where(ReportDataDao.Properties.TestAccount.eq(account));
        return qb.list();
    }

    /**
     * 查询指定name的记录数量
     */
    public int queryDataSize(String name) {
        QueryBuilder<ReportData> qb = reportDataDao.queryBuilder();
        qb.where(ReportDataDao.Properties.FileName.eq(name));
        return qb.list().size();
    }

    /**
     * 根据id查询数据
     */
    public ReportData queryData(Long id) {
        QueryBuilder<ReportData> qb = reportDataDao.queryBuilder();
        qb.where(ReportDataDao.Properties.Id.eq(id));
        return qb.list().get(0);
    }

    /**
     * 删除数据
     */
    public void deleteData(Long id) {
        QueryBuilder<ReportData> qb = reportDataDao.queryBuilder();
        qb.where(ReportDataDao.Properties.Id.eq(id)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 删除数据
     */
    public void deleteData(ReportData reportData) {
        reportDataDao.delete(reportData);
    }
}