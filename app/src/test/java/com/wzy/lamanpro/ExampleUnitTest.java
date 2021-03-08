package com.wzy.lamanpro;

import android.widget.Toast;

import com.wzy.lamanpro.bean.Calibration;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() {
        setCalibrationParams(new double[]{1.2,3.4,5.6,3.2,3,2,1});
    }

    @Test
    public void test3(){
        int a=0,c=0;
        do{
            --c;
            a=a-1;
        }while(a>0);
        System.out.println(c);
    }


    public void setCalibrationParams(double[] params){
        Calibration calibration=new Calibration();
        if (params.length>10){
            System.out.println(">>>>>>10");
            return;
        }
        switch (params.length){

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
        System.out.println(calibration.toString());
    }


}
