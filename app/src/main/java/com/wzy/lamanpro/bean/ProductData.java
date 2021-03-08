package com.wzy.lamanpro.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ProductData {
    @Id(autoincrement = true)
    private Long id;//编号
    private String proName;//样品名称
    private String data;//数据
    private String date;//日期
    private String userName;//用户名
    private String userCompany;//公司
    private String proHSCode;//HS码
    private String proCASCode;//CAS码
    private String proNFPA704Code;//NFPA704标志
    private String proDangerLevel;//危险等级
    private String proDangerClass;//危险性符号
    private String proDangerTransportCode;//危险运输编码
    private String proMDLNumber;//MDL号
    private String proEINECSNumber;//EINECS号
    private String proRTECSNumber;//RTECS号
    private String proBRNNumber;//BRN号
    private String proDetail;//样品信息
    //Modified by X.F
    private Double ProductThreshold;    //分类器阈值
    private Long ProductPriority;       //分类器优先级
    private String calibratedData;      //校正后的数据

    public ProductData(String proName, String data, String date, String userName, String userCompany, String proHSCode, String proCASCode,
                       String proNFPA704Code, String proDangerLevel, String proDangerClass, String proDangerTransportCode,
                       String proMDLNumber, String proEINECSNumber, String proRTECSNumber, String proBRNNumber, String proDetail,
                       Double ProductThreshold, Long ProductPriority, String calibratedData) {
        this.proName = proName;
        this.data = data;
        this.date = date;
        this.userName = userName;
        this.userCompany = userCompany;
        this.proHSCode = proHSCode;
        this.proCASCode = proCASCode;
        this.proNFPA704Code = proNFPA704Code;
        this.proDangerLevel = proDangerLevel;
        this.proDangerClass = proDangerClass;
        this.proDangerTransportCode = proDangerTransportCode;
        this.proMDLNumber = proMDLNumber;
        this.proEINECSNumber = proEINECSNumber;
        this.proRTECSNumber = proRTECSNumber;
        this.proBRNNumber = proBRNNumber;
        this.proDetail = proDetail;
        this.ProductThreshold = ProductThreshold;
        this.ProductPriority = ProductPriority;
        this.calibratedData = calibratedData;
    }

    @Generated(hash = 1446039799)
    public ProductData(Long id, String proName, String data, String date, String userName, String userCompany, String proHSCode,
            String proCASCode, String proNFPA704Code, String proDangerLevel, String proDangerClass, String proDangerTransportCode,
            String proMDLNumber, String proEINECSNumber, String proRTECSNumber, String proBRNNumber, String proDetail, Double ProductThreshold,
            Long ProductPriority, String calibratedData) {
        this.id = id;
        this.proName = proName;
        this.data = data;
        this.date = date;
        this.userName = userName;
        this.userCompany = userCompany;
        this.proHSCode = proHSCode;
        this.proCASCode = proCASCode;
        this.proNFPA704Code = proNFPA704Code;
        this.proDangerLevel = proDangerLevel;
        this.proDangerClass = proDangerClass;
        this.proDangerTransportCode = proDangerTransportCode;
        this.proMDLNumber = proMDLNumber;
        this.proEINECSNumber = proEINECSNumber;
        this.proRTECSNumber = proRTECSNumber;
        this.proBRNNumber = proBRNNumber;
        this.proDetail = proDetail;
        this.ProductThreshold = ProductThreshold;
        this.ProductPriority = ProductPriority;
        this.calibratedData = calibratedData;
    }

    @Generated(hash = 1035286127)
    public ProductData() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProName() {
        return this.proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
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

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCompany() {
        return this.userCompany;
    }

    public void setUserCompany(String userCompany) {
        this.userCompany = userCompany;
    }

    public String getProHSCode() {
        return this.proHSCode;
    }

    public void setProHSCode(String proHSCode) {
        this.proHSCode = proHSCode;
    }

    public String getProCASCode() {
        return this.proCASCode;
    }

    public void setProCASCode(String proCASCode) {
        this.proCASCode = proCASCode;
    }

    public String getProNFPA704Code() {
        return this.proNFPA704Code;
    }

    public void setProNFPA704Code(String proNFPA704Code) {
        this.proNFPA704Code = proNFPA704Code;
    }

    public String getProDangerLevel() {
        return this.proDangerLevel;
    }

    public void setProDangerLevel(String proDangerLevel) {
        this.proDangerLevel = proDangerLevel;
    }

    public String getProDangerClass() {
        return this.proDangerClass;
    }

    public void setProDangerClass(String proDangerClass) {
        this.proDangerClass = proDangerClass;
    }

    public String getProDangerTransportCode() {
        return this.proDangerTransportCode;
    }

    public void setProDangerTransportCode(String proDangerTransportCode) {
        this.proDangerTransportCode = proDangerTransportCode;
    }

    public String getProMDLNumber() {
        return this.proMDLNumber;
    }

    public void setProMDLNumber(String proMDLNumber) {
        this.proMDLNumber = proMDLNumber;
    }

    public String getProEINECSNumber() {
        return this.proEINECSNumber;
    }

    public void setProEINECSNumber(String proEINECSNumber) {
        this.proEINECSNumber = proEINECSNumber;
    }

    public String getProRTECSNumber() {
        return this.proRTECSNumber;
    }

    public void setProRTECSNumber(String proRTECSNumber) {
        this.proRTECSNumber = proRTECSNumber;
    }

    public String getProBRNNumber() {
        return this.proBRNNumber;
    }

    public void setProBRNNumber(String proBRNNumber) {
        this.proBRNNumber = proBRNNumber;
    }

    public String getProDetail() {
        return this.proDetail;
    }

    public void setProDetail(String proDetail) {
        this.proDetail = proDetail;
    }

    public Double getProductThreshold() {
        return this.ProductThreshold;
    }

    public void setProductThreshold(Double ProductThreshold) {
        this.ProductThreshold = ProductThreshold;
    }

    public Long getProductPriority() {
        return this.ProductPriority;
    }

    public void setProductPriority(Long ProductPriority) {
        this.ProductPriority = ProductPriority;
    }

    public String getCalibratedData() {
        return this.calibratedData;
    }

    public void setCalibratedData(String calibratedData) {
        this.calibratedData = calibratedData;
    }

}
