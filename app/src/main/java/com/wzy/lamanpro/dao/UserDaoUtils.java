package com.wzy.lamanpro.dao;

import android.content.Context;
import android.widget.Toast;

import com.wzy.lamanpro.bean.Users;
import com.wzy.lamanpro.bean.UsersDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class UserDaoUtils {
    private UsersDao usersDao;
    private Context context;


    public UserDaoUtils(Context context) {
        this.context = context;
//        SQLiteDatabase writableDatabase = DBManager.getInstance(context).getWritableDatabase();
//        DaoMaster daoMaster = new DaoMaster(writableDatabase);
//        DaoSession daoSession = daoMaster.newSession();
        usersDao = DBManager.getInstance(context).getDaoSession().getUsersDao();
    }


    /**
     * 插入用户
     *
     * @param users 账户
     */
    public boolean insertUserList(Users users) {
        if (users == null) {
            Toast.makeText(context, "账户信息有错，不能注册", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (queryUserSize(users.getAccount()) > 0) {
            Toast.makeText(context, "账户已存在，不能注册", Toast.LENGTH_SHORT).show();
            return false;
        }
        usersDao.insert(users);
        return true;
    }

    /**
     * 更新账户
     *
     * @param users 用户账户
     */
    public void updateUser(Users users) {
        if (users == null) {
            return;
        }
        usersDao.update(users);
    }


    /**
     * 查询用户密码
     */
    public String queryUserPass(String account) {

        QueryBuilder<Users> qb = usersDao.queryBuilder();
        qb.where(UsersDao.Properties.Account.eq(account));
        if (qb.list() == null || qb.list().size() == 0)
            return "";
        return qb.list().get(0).getPassword();
    }

    /**
     * 查询用户密码
     */
    public String queryUserName(String account) {

        QueryBuilder<Users> qb = usersDao.queryBuilder();
        qb.where(UsersDao.Properties.Account.eq(account));
        if (qb.list() == null || qb.list().size() == 0)
            return "";
        return qb.list().get(0).getName();
    }

    /**
     * 查询用户列表个数
     */
    public int queryAccountSize() {
        QueryBuilder<Users> qb = usersDao.queryBuilder();
        return qb.list().size();
    }

    /**
     * 查询所有用户列表
     */
    public List<Users> queryAllUsers() {
        QueryBuilder<Users> qb = usersDao.queryBuilder();
        return qb.list();
    }

    /**
     * 查询用户个数
     */
    public int queryUserSize(String account) {
        QueryBuilder<Users> qb = usersDao.queryBuilder();
        qb.where(UsersDao.Properties.Account.eq(account));
        return qb.list().size();
    }

    /**
     * 查询用户个数
     */
    public Users queryUser(String account) {
        QueryBuilder<Users> qb = usersDao.queryBuilder();
        qb.where(UsersDao.Properties.Account.eq(account));
        return qb.list().get(0);
    }

    /**
     * 查询用户个数
     */
    public void deleteUser(String account) {
        QueryBuilder<Users> qb = usersDao.queryBuilder();
        qb.where(UsersDao.Properties.Account.eq(account)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
}