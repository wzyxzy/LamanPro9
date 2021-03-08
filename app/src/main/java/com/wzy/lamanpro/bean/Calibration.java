package com.wzy.lamanpro.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Calibration {

    // Modified by X.F
    public static final int param_num = 10;

    @Id(autoincrement = true)
    private Long id;

    private double param1;
    private double param2;
    private double param3;
    private double param4;
    private double param5;
    private double param6;
    private double param7;
    private double param8;
    private double param9;
    private double param10;
    @Generated(hash = 1045235553)
    public Calibration(Long id, double param1, double param2, double param3,
            double param4, double param5, double param6, double param7,
            double param8, double param9, double param10) {
        this.id = id;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.param4 = param4;
        this.param5 = param5;
        this.param6 = param6;
        this.param7 = param7;
        this.param8 = param8;
        this.param9 = param9;
        this.param10 = param10;
    }
    @Generated(hash = 1851984659)
    public Calibration() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public double getParam1() {
        return this.param1;
    }
    public void setParam1(double param1) {
        this.param1 = param1;
    }
    public double getParam2() {
        return this.param2;
    }
    public void setParam2(double param2) {
        this.param2 = param2;
    }
    public double getParam3() {
        return this.param3;
    }
    public void setParam3(double param3) {
        this.param3 = param3;
    }
    public double getParam4() {
        return this.param4;
    }
    public void setParam4(double param4) {
        this.param4 = param4;
    }
    public double getParam5() {
        return this.param5;
    }
    public void setParam5(double param5) {
        this.param5 = param5;
    }
    public double getParam6() {
        return this.param6;
    }
    public void setParam6(double param6) {
        this.param6 = param6;
    }
    public double getParam7() {
        return this.param7;
    }
    public void setParam7(double param7) {
        this.param7 = param7;
    }
    public double getParam8() {
        return this.param8;
    }
    public void setParam8(double param8) {
        this.param8 = param8;
    }
    public double getParam9() {
        return this.param9;
    }
    public void setParam9(double param9) {
        this.param9 = param9;
    }
    public double getParam10() {
        return this.param10;
    }
    public void setParam10(double param10) {
        this.param10 = param10;
    }

    @Override
    public String toString() {
        return "Calibration{" +
                "id=" + id +
                ", param1=" + param1 +
                ", param2=" + param2 +
                ", param3=" + param3 +
                ", param4=" + param4 +
                ", param5=" + param5 +
                ", param6=" + param6 +
                ", param7=" + param7 +
                ", param8=" + param8 +
                ", param9=" + param9 +
                ", param10=" + param10 +
                '}';
    }
}
