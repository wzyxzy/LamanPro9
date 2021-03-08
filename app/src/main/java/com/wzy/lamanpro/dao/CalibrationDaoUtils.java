package com.wzy.lamanpro.dao;

import android.content.Context;
import android.widget.Toast;

import com.wzy.lamanpro.bean.Calibration;
import com.wzy.lamanpro.bean.CalibrationDao;

import org.greenrobot.greendao.query.QueryBuilder;

public class CalibrationDaoUtils {
    private CalibrationDao calibrationDao;
    private Context context;


    public CalibrationDaoUtils(Context context) {
        this.context = context;
//        SQLiteDatabase writableDatabase = DBManager.getInstance(context).getWritableDatabase();
//        DaoMaster daoMaster = new DaoMaster(writableDatabase);
//        DaoSession daoSession = daoMaster.newSession();
        calibrationDao = DBManager.getInstance(context).getDaoSession().getCalibrationDao();
    }

    /**
     * 查询数据
     */
    public Calibration queryData(Long id) {
        QueryBuilder<Calibration> qb = calibrationDao.queryBuilder();
        qb.where(CalibrationDao.Properties.Id.eq(id));
        return qb.list()==null||qb.list().size()==0?null:qb.list().get(0);
    }


    public double[] getCalibrationParams() {
        Calibration calibration = queryData(1L);
        if (calibration == null) {
            Calibration calibration1 = new Calibration();
            calibration1.setParam1(0);
            calibrationDao.insert(calibration1);
            calibration = queryData(1L);
        }
        double[] d = new double[calibration.param_num];
        d[0] = calibration.getParam1();
        d[1] = calibration.getParam2();
        d[2] = calibration.getParam3();
        d[3] = calibration.getParam4();
        d[4] = calibration.getParam5();
        d[5] = calibration.getParam6();
        d[6] = calibration.getParam7();
        d[7] = calibration.getParam8();
        d[8] = calibration.getParam9();
        d[9] = calibration.getParam10();
        return d;
    }


    /**
     * 修改
     *
     * @param calibration 数据
     */
    public void updateCalibration(Calibration calibration) {
        if (calibration == null) {
            Toast.makeText(context, "数据有错，无法记录", Toast.LENGTH_SHORT).show();
        }
        Calibration calibration1 = queryData(1L);
        if (calibration1 == null)
            calibrationDao.insert(calibration);
        else
            calibrationDao.update(calibration);
    }

    public void setCalibrationParams(double[] params) {
        Calibration calibration = new Calibration();
        if (params.length > calibration.param_num) {
            Toast.makeText(context, "超过长度部分无法记录", Toast.LENGTH_SHORT).show();
        }
        calibration.setId(1L);
        switch (params.length) {

            case 10:
                calibration.setParam10(params[9]);
            case 9:
                calibration.setParam9(params[8]);
            case 8:
                calibration.setParam8(params[7]);
            case 7:
                calibration.setParam7(params[6]);
            case 6:
                calibration.setParam6(params[5]);
            case 5:
                calibration.setParam5(params[4]);
            case 4:
                calibration.setParam4(params[3]);
            case 3:
                calibration.setParam3(params[2]);
            case 2:
                calibration.setParam2(params[1]);
            case 1:
                calibration.setParam1(params[0]);
            case 0:
                break;
        }
        updateCalibration(calibration);
    }

//    /**
//     * 更新账户
//     *
//     * @param productData 用户账户
//     */
//    public boolean updateData(ProductData productData) {
//        if (productData == null) {
//            return false;
//        }
//        productDataDao.update(productData);
//        return true;
//    }
//
//
////    /**
////     * 查询用户密码
////     */
////    public String queryUserPass(String account) {
////
////        QueryBuilder<Users> qb = usersDao.queryBuilder();
////        qb.where(UsersDao.Properties.Account.eq(account));
////        if (qb.list() == null || qb.list().size() == 0)
////            return "";
////        return qb.list().get(0).getPassword();
////    }
//
////    /**
////     * 查询用户密码
////     */
////    public String queryUserName(String account) {
////
////        QueryBuilder<Users> qb = usersDao.queryBuilder();
////        qb.where(UsersDao.Properties.Account.eq(account));
////        if (qb.list() == null || qb.list().size() == 0)
////            return "";
////        return qb.list().get(0).getName();
////    }
//
////    /**
////     * 查询用户列表个数
////     */
////    public int queryAccountSize() {
////        QueryBuilder<Users> qb = usersDao.queryBuilder();
////        return qb.list().size();
////    }
//
//    /**
//     * 查询所有用户列表
//     */
//    public List<ProductData> queryAllData() {
//        QueryBuilder<ProductData> qb = productDataDao.queryBuilder();
//        return qb.list();
//    }
//
//    /**
//     * 查询用户个数
//     */
//    public int queryUserSize(String name) {
//        QueryBuilder<ProductData> qb = productDataDao.queryBuilder();
//        qb.where(ProductDataDao.Properties.ProName.eq(name));
//        return qb.list().size();
//    }
//

//
//    /**
//     * 删除数据
//     */
//    public void deleteData(ProductData productData) {
//        productDataDao.delete(productData);
//    }
//
//    /**
//     * 删除数据
//     */
//    public void deleteData(Long id) {
//        QueryBuilder<ProductData> qb = productDataDao.queryBuilder();
//        qb.where(ProductDataDao.Properties.Id.eq(id)).buildDelete().executeDeleteWithoutDetachingEntities();
//    }
}