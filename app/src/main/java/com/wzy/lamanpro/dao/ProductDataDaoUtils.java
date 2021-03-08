package com.wzy.lamanpro.dao;

import android.content.Context;
import android.widget.Toast;

import com.wzy.lamanpro.bean.ProductData;
import com.wzy.lamanpro.bean.ProductDataDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class ProductDataDaoUtils {
    private ProductDataDao productDataDao;
    private Context context;

    public ProductDataDaoUtils(Context context) {
        this.context = context;
//        SQLiteDatabase writableDatabase = DBManager.getInstance(context).getWritableDatabase();
//        DaoMaster daoMaster = new DaoMaster(writableDatabase);
//        DaoSession daoSession = daoMaster.newSession();
        productDataDao = DBManager.getInstance(context).getDaoSession().getProductDataDao();
    }

    /**
     * 插入用户
     *
     * @param productData 账户
     */
    public boolean insertProductList(ProductData productData) {
        if (productData == null) {
            Toast.makeText(context, "信息有错，不能添加", Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (queryUserSize(productData.getProName()) > 0) {
//            Toast.makeText(context, "已有同名记录，建议修改名称", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        productDataDao.insert(productData);
        return true;
    }

    /**
     * 更新账户
     *
     * @param productData 用户账户
     */
    public boolean updateData(ProductData productData) {
        if (productData == null) {
            return false;
        }
        productDataDao.update(productData);
        return true;
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
     * 查询所有用户列表
     */
    public List<ProductData> queryAllData() {
        QueryBuilder<ProductData> qb = productDataDao.queryBuilder();
        return qb.list();
    }

    /**
     * 条件查询
     */
    public List<ProductData> queryByName(String proName) {
        QueryBuilder<ProductData> qb = productDataDao.queryBuilder();
        qb.where(ProductDataDao.Properties.ProName.eq(proName));
        return qb.list();
    }

//    /**
//     * 查询用户个数
//     */
//    public int queryDataSize(String proName) {
//        QueryBuilder<ProductData> qb = productDataDao.queryBuilder();
//        qb.where(ProductDataDao.Properties.ProName.eq(proName));
//        return qb.list().size();
//    }

    /**
     * 查询数据
     */
    public ProductData queryData(Long id) {
        QueryBuilder<ProductData> qb = productDataDao.queryBuilder();
        qb.where(ProductDataDao.Properties.Id.eq(id));
        return qb.list().get(0);
    }

    /**
     * 删除数据
     */
    public void deleteData(ProductData productData) {
        productDataDao.delete(productData);
    }

    /**
     * 删除数据
     */
    public void deleteData(Long id) {
        QueryBuilder<ProductData> qb = productDataDao.queryBuilder();
        qb.where(ProductDataDao.Properties.Id.eq(id)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
}