package com.wzy.lamanpro.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class HisData {
    @Id(autoincrement = true)
    private Long id;
    private String data;//数据
    private String date;//日期
    private String name;//保存名字
    private String testName;//测试者姓名
    private String testAccount;//测试者登录名
    private String testTime;//积分时间
    private String testPower;//功率
    private String testLocal;//测试地点
    @Generated(hash = 1834219352)
    public HisData(Long id, String data, String date, String name, String testName,
            String testAccount, String testTime, String testPower,
            String testLocal) {
        this.id = id;
        this.data = data;
        this.date = date;
        this.name = name;
        this.testName = testName;
        this.testAccount = testAccount;
        this.testTime = testTime;
        this.testPower = testPower;
        this.testLocal = testLocal;
    }

    public HisData(String data, String date, String name, String testName, String testAccount, String testTime, String testPower, String testLocal) {
        this.data = data;
        this.date = date;
        this.name = name;
        this.testName = testName;
        this.testAccount = testAccount;
        this.testTime = testTime;
        this.testPower = testPower;
        this.testLocal = testLocal;
    }

    @Generated(hash = 2089447536)
    public HisData() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getData() {
        return this.data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTestName() {
        return this.testName;
    }
    public void setTestName(String testName) {
        this.testName = testName;
    }
    public String getTestAccount() {
        return this.testAccount;
    }
    public void setTestAccount(String testAccount) {
        this.testAccount = testAccount;
    }
    public String getTestTime() {
        return this.testTime;
    }
    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }
    public String getTestPower() {
        return this.testPower;
    }
    public void setTestPower(String testPower) {
        this.testPower = testPower;
    }
    public String getTestLocal() {
        return this.testLocal;
    }
    public void setTestLocal(String testLocal) {
        this.testLocal = testLocal;
    }

    

}
