package com.wzy.lamanpro.dao;

import android.content.Context;
import android.widget.Toast;

import com.wzy.lamanpro.bean.HisData;
import com.wzy.lamanpro.bean.HisDataDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class HisDaoUtils {
    private HisDataDao hisDataDao;
    private Context context;


    public HisDaoUtils(Context context) {
        this.context = context;
//        SQLiteDatabase writableDatabase = DBManager.getInstance(context).getWritableDatabase();
//        DaoMaster daoMaster = new DaoMaster(writableDatabase);
//        DaoSession daoSession = daoMaster.newSession();
        hisDataDao = DBManager.getInstance(context).getDaoSession().getHisDataDao();
    }


    /**
     * 插入用户
     *
     * @param hisData 账户
     */
    public boolean insertHisDataList(HisData hisData) {
        if (hisData == null) {
            Toast.makeText(context, "信息有错，不能保存", Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (queryUserSize(hisData.getName()) > 0) {
//            Toast.makeText(context, "已有同名记录，建议修改名称", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        hisDataDao.insert(hisData);
        return true;
    }

    /**
     * 更新账户
     *
     * @param hisData 用户账户
     */
    public void updateData(HisData hisData) {
        if (hisData == null) {
            return;
        }
        hisDataDao.update(hisData);
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
    public List<HisData> queryAllData() {
        QueryBuilder<HisData> qb = hisDataDao.queryBuilder();
        return qb.list();
    }

    /**
     * 查询用户所有记录列表
     */
    public List<HisData> queryAllDataByAccount(String account) {
        QueryBuilder<HisData> qb = hisDataDao.queryBuilder();
        qb.where(HisDataDao.Properties.TestAccount.eq(account));
        return qb.list();
    }

    /**
     * 查询指定name的记录数量
     */
    public int queryDataSize(String name) {
        QueryBuilder<HisData> qb = hisDataDao.queryBuilder();
        qb.where(HisDataDao.Properties.Name.eq(name));
        return qb.list().size();
    }

    /**
     * 根据id查询数据
     */
    public HisData queryData(Long id) {
        QueryBuilder<HisData> qb = hisDataDao.queryBuilder();
        qb.where(HisDataDao.Properties.Id.eq(id));
        return qb.list().get(0);
    }

    /**
     * 删除数据
     */
    public void deleteData(Long id) {
        QueryBuilder<HisData> qb = hisDataDao.queryBuilder();
        qb.where(HisDataDao.Properties.Id.eq(id)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 删除数据
     */
    public void deleteData(HisData hisData) {
        hisDataDao.delete(hisData);
    }
}