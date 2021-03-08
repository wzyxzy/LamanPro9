package com.wzy.lamanpro.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ReportData {
    @Id(autoincrement = true)
    private Long id;
    private String fileName;//文件名
    private String time;//文件导出时间
    private String testAccount;//测试者登录名
    private String testName;//测试者姓名
    private String testLocal;//测试地点
    @Generated(hash = 886810506)
    public ReportData(Long id, String fileName, String time, String testAccount,
            String testName, String testLocal) {
        this.id = id;
        this.fileName = fileName;
        this.time = time;
        this.testAccount = testAccount;
        this.testName = testName;
        this.testLocal = testLocal;
    }

    public ReportData(String fileName, String time, String testAccount, String testName, String testLocal) {
        this.fileName = fileName;
        this.time = time;
        this.testAccount = testAccount;
        this.testName = testName;
        this.testLocal = testLocal;
    }

    @Generated(hash = 789822999)
    public ReportData() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getTestAccount() {
        return this.testAccount;
    }
    public void setTestAccount(String testAccount) {
        this.testAccount = testAccount;
    }
    public String getTestLocal() {
        return this.testLocal;
    }
    public void setTestLocal(String testLocal) {
        this.testLocal = testLocal;
    }

    public String getTestName() {
        return this.testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

}
