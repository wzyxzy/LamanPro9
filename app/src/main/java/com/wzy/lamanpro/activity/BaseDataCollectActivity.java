package com.wzy.lamanpro.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.HisData;
import com.wzy.lamanpro.common.LaManApplication;
import com.wzy.lamanpro.dao.CalibrationDaoUtils;
import com.wzy.lamanpro.dao.HisDaoUtils;
import com.wzy.lamanpro.dao.UserDaoUtils;
import com.wzy.lamanpro.utils.ChartUtil;
import com.wzy.lamanpro.utils.FileUtils;
import com.wzy.lamanpro.utils.MatClassifier;
import com.wzy.lamanpro.utils.SPUtility;
import com.wzy.lamanpro.utils.UsbUtils;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.wzy.lamanpro.common.LaManApplication.matClassifier;
import static com.wzy.lamanpro.utils.UsbUtils.readFromUsb;

//鉴于主界面和AddLibrary中都会有通过USB口连接硬件采集数据的步骤，所以将这部分功能提取出来放到一个BaseActivity中，方便维护
public class BaseDataCollectActivity extends AppCompatActivity
        implements View.OnClickListener {

    protected static final String TAG = BaseDataCollectActivity.class.getSimpleName();
    //区分主界面和AddLibrary两个界面；主界面上控件更多
    protected static final int modeMainActivity = 0;
    protected static final int modeLibraryActivity = 1;
    protected int modeCurrentActivity = modeLibraryActivity;

    private static final byte[] SET_INIT_TIME = {0x09, 0x4F, 0x69, 0x74};//设置积分时间
    private static final byte[] SET_POWER = {0x09, 0x4F, 0x70, 0x77, 0x00, 0x00, 0x00, 0x00, (byte) 0xE8, 0x03, 0x00, 0x00};//设置能量大小
    private static final byte[] OPEN_PORT = {0x09, 0x4f, 0x65, 0x70, 0x02, 0x00, 0x00, 0x00};//打开激光
    private static final byte[] GET_DATA = {0x09, 0x4F, 0x53, 0x4F};//获取波形
    private static final byte[] CLOSE_PORT = {0x09, 0x4f, 0x65, 0x70, 0x00, 0x00, 0x00, 0x00};//关闭激光
    //快检的缺省参数：积分时间、次数和功率
    private static final int LASER_DEFAULT_TIME  = 500;
    private static final int LASER_DEFAULT_COUNT = 10;
    private static final int LASER_DEFAULT_POWER = 300;

    //控件部分将在派生类中进行初始化，所以需要用protected
    protected LineChart lineChart;
    protected Button button_start;
    protected Button button_calibrate;
    protected TextView text_report;
    protected TextView debug_message;
    protected TextView text_location;
    protected FloatingActionButton fab;
    protected ProgressBar progress_bar;
    protected TextView state;

    protected String button_start_normal_text;
    protected String button_start_pressed_text;

    private String stateText = "";
    protected String locationName;
    //定位都要通过LocationManager这个类实现
    protected LocationManager locationManager;
    protected Context context = BaseDataCollectActivity.this;

    private List<String> xDataList = new ArrayList<>();// x轴数据源
    private List<Entry> yDataList = new ArrayList<Entry>();// y轴数据数据源
    private int once;
    private int time;
    private int power;
    private int testCount = 0;
    protected String provider;

    // Modified by X.F
    private byte[] raw_data;
    private int[][] results;
    private double[] finalsResults;
    //标定参数
    private double[] calibrate_params;

    //测试用；不必连接设备就可以采集到一组虚拟的曲线
    private boolean useVirtualData = false;
    //调试用，激光的警告对话框仅显示一次
    private static boolean ShowLaserWarningDialog = true;

    //派生类需要调用该函数，设置mode
    protected void setMode(int mode) {
        if (mode == modeMainActivity || mode == modeLibraryActivity)
            modeCurrentActivity = mode;
    }

    @SuppressLint("HandlerLeak")
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (!useVirtualData)
                        UsbUtils.sendToUsb(CLOSE_PORT);
                    for (int i = 0; i < finalsResults.length; i++) {
                        xDataList.add(String.valueOf(i));
                        yDataList.add(new Entry((float)finalsResults[i], i));
                    }
                    button_start.setEnabled(true);
                    button_start.setText(button_start_normal_text);
                    fab.setEnabled(true);
                    if (modeCurrentActivity == modeMainActivity)
                        button_calibrate.setEnabled(true);
                    if (!useVirtualData)
                        stateText = "获取波形完成\n";
                    testCount = 0;
                    handler.sendEmptyMessage(2);
                    handler.sendEmptyMessage(3);
                    progress_bar.setProgress(0);
                    break;
                case 1:
                    Toast.makeText(context, "请输入合理范围内的设置", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    state.setText(stateText);
                    lineChart.notifyDataSetChanged();
                    lineChart.invalidate();
                    break;
                case 3:
                    ChartUtil.showChart(context, lineChart, xDataList, yDataList, "光谱图", "波长/时间", "");
                    //建库时不需要进行物质分类
                    if (modeCurrentActivity == modeMainActivity) {
                        // Modified by X.F
                        if (finalsResults != null) {
                            // 物质分类，需要先进行标定
                            CalibrationDaoUtils calibUtil = new CalibrationDaoUtils(context);
                            double[] current_params = calibUtil.getCalibrationParams();
                            if (current_params.length == 0 || current_params[0] == 0) {
                                showCalibrationAlert();
                            } else {
                                // 保险起见设置一遍
                                // matClassifier.getRamanUtility().SetCalibrationParams(current_params);
                                long startTime = SystemClock.elapsedRealtime();
                                MatClassifier.classify_result result = matClassifier.classify(finalsResults);
                                long endTime = SystemClock.elapsedRealtime();
                                long elapsedMilliSeconds = endTime - startTime;
                                Log.d(TAG, String.format("物质识别耗时：%.2f秒", elapsedMilliSeconds / 1000.0));
                                text_report.setText(result.formal_message);
                                if (!result.bSuccess)
                                    text_report.setTextColor(Color.RED);
                                debug_message.setText(result.debug_message);
                            }
                        }
                    }
                    break;
                case 4:
                    if (modeCurrentActivity == modeMainActivity)
                        text_location.setText("位置是：" + locationName);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //音量键，唤起开始测试
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//            Toast.makeText(Main2Activity.this, testCount + "", Toast.LENGTH_SHORT).show();
            switch (testCount) {
                case 0:
                    test();
//                    testCount++;
                    return true;
                default:
                    return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void test() {
        //使用虚拟数据时警告对话框仅第一次显示
        if (useVirtualData && !ShowLaserWarningDialog)
            testNow();
        else {
            ShowLaserWarningDialog = false;
            if (LaManApplication.canUseUsb || useVirtualData) {
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.adangerous);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(imageView)
                        //                        .setView(textView)
                        .setMessage("当心激光辐射\n执行扫描时请勿将眼睛对着出射窗口！")
                        .setTitle("温 馨 提 示 :")
                        .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                testNow();
                                testCount++;
                            }
                        });
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(300); // 休眠1秒
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        /**
                         * 延时执行的代码
                         */
                        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                                if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                                    testNow();
                                    alertDialog.dismiss();
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                }).start();
            } else {
                Toast.makeText(context, "请先连接光谱仪设备！", Toast.LENGTH_SHORT).show();
                testCount = 0;
            }
        }
    }

    private void testNow() {
        if (!LaManApplication.canUseUsb && !useVirtualData) {
            Toast.makeText(context, "请先连接光谱仪设备！", Toast.LENGTH_SHORT).show();
            return;
        }
        lineChart.clear();
        xDataList.clear();
        yDataList.clear();
        if (modeCurrentActivity == modeMainActivity) {
            text_report.setText("");
            debug_message.setText("");
        }
        if (useVirtualData) {
            int index = new Random().nextInt(10);
            finalsResults = matClassifier.getSampleData().clone();
            stateText = "使用虚拟数据进行测试";
            Log.d(TAG, stateText);
            handler.sendEmptyMessage(2);
            handler.sendEmptyMessage(0);
            return;
        }

        final int[] count = {0, 0};
        boolean easyMode = !SPUtility.getSPBoolean(context, "use_mode");
        once = easyMode || TextUtils.isEmpty(SPUtility.getSPString(context, "once")) ? LASER_DEFAULT_COUNT : Integer.valueOf(SPUtility.getSPString(context, "once"));
        time = easyMode || TextUtils.isEmpty(SPUtility.getSPString(context, "time")) ? LASER_DEFAULT_TIME : Integer.valueOf(SPUtility.getSPString(context, "time"));
        power = TextUtils.isEmpty(SPUtility.getSPString(context, "power")) ? LASER_DEFAULT_POWER : Integer.valueOf(SPUtility.getSPString(context, "power"));
        Log.d(TAG, String.format("当前采集模式：%s，功率：%d，时间：%d，次数：%d", easyMode ? "快检" : "精检", power, time, once));
//        stateText = new StringBuffer();
        Log.i(TAG, "[Raman]开始采集数据，积分次数：" + once + ", 时间：" + time + "，功率：" + power);
        stateText = "开始采集数据，积分次数为" + once + "次\n";
        button_start.setEnabled(false);
        fab.setEnabled(false);
        if (modeCurrentActivity == modeMainActivity)
            button_calibrate.setEnabled(false);
        button_start.setText(button_start_pressed_text);
        handler.sendEmptyMessage(2);
        testCount = 2;

        // Modified by X.F
        if (finalsResults == null) {
            raw_data = new byte[LaManApplication.dataLength * 2];
            finalsResults = new double[LaManApplication.dataLength];
        }
        // 采样次数可能不同，所以干脆重新分配空间
        results = new int[once][LaManApplication.dataLength];
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                switch (count[0]) {
                    case 0:
                        try {
                            UsbUtils.sendToUsb(UsbUtils.addBytes(SET_INIT_TIME, UsbUtils.intTobyteLH(time * 1000)));
                            stateText = "第" + (count[1] + 1) + "次积分：  积分时间为" + time + "毫秒。  功率为：" + power + "。";
//                            stateText.append("积分时间设置完毕，积分时间为" + time + "毫秒，发送的内容是：" + Arrays.toString(UsbUtils.addBytes(SET_INIT_TIME, UsbUtils.intTobyteLH(time * 1000))) + "\n");
                            handler.sendEmptyMessage(2);
//                            Log.i(TAG, "[Raman]第" + (count[1]+1) + "次设置积分时间完成");
                        } catch (NumberFormatException e) {
                            handler.sendEmptyMessage(1);
                        }
                        break;
                    case 1:
                        try {
                            UsbUtils.sendToUsb(UsbUtils.addBytes(SET_POWER, UsbUtils.intTobyteLH(power)));
//                            stateText = "功率为：" + power + "。";
//                              stateText.append("功率设置发送完毕，发送的内容是：" + Arrays.toString(SET_POWER) + "\n");
//                            handler.sendEmptyMessage(2);
//                            Log.i(TAG, "[Raman]第" + (count[1]+1) + "次设置功率完成");
                        } catch (NumberFormatException e) {
                            handler.sendEmptyMessage(1);
                        }
                        break;
                    case 2:
                        UsbUtils.sendToUsb(OPEN_PORT);
//                        stateText.append("打开激光发送完毕。\n");
//                        stateText.append("打开激光发送完毕，发送的内容是：" + Arrays.toString(OPEN_PORT) + "\n");
//                        handler.sendEmptyMessage(2);
//                        Log.i(TAG, "[Raman]第" + (count[1]+1) + "次打开激光完成");
                        break;
                    case 3:
                        UsbUtils.sendToUsb(GET_DATA);
//                        stateText.append("获取波形发送完毕。\n");
//                        stateText.append("获取波形发送完毕，发送的内容是：" + Arrays.toString(GET_DATA) + "\n");
//                        handler.sendEmptyMessage(2);
//                        Log.i(TAG, "[Raman]第" + (count[1]+1) + "次获取波形完成");
                        break;
                    case 4:
                        raw_data = readFromUsb();
                        // Modified by X.F
                        // 每次采样完毕直接转换成int保存
                        for (int i = 0; i < LaManApplication.dataLength; i ++) {
                            results[count[1]][i] = UsbUtils.twoByteToUnsignedInt(raw_data[2 * i + 1], raw_data[2 * i]);
                        }
                        count[1] ++;
                        progress_bar.setProgress(count[1] * 100 / once);
//                        Log.i(TAG, "[Raman]第" + count[1] + "次获取数据完成");
                        break;
                    case 5:
                        // Modified by X.F
                        // 加完再取平均，而不是每一次都直接累加/2，那样会导致最后采样的数据权重最大
                        for (int i = 0; i < LaManApplication.dataLength; i++) {
                            finalsResults[i] = 0;
                            for (int j = 0; j < results.length; j++)
                                finalsResults[i] += results[j][i];
                            finalsResults[i] /= results.length;
                        }
                        handler.sendEmptyMessage(0);
                        timer.cancel();
                        Log.i(TAG, "[Raman]第" + count[1] + "次采集完成。");
                        break;
                }
                count[0]++;
                // 仅当count[1] == once时count[0]才会变成5，计算平均值后结束
                if (count[1] < once && count[0] == 5) {
                    count[0] = 0;
                    Log.i(TAG, "[Raman]第" + count[1] + "次采集完成。");
                }
                if (count[1] > 0 && count[0] == 1) {
                    // 除了第一次之外直接从3开始，也就是不必重复设置相同的参数和打开激光指令；
                    count[0] = 3;
                }
            }
        };
        timer.schedule(timerTask, 5, 10);
    }

    private void showCalibrationAlert() {
        text_report.setText("请先用乙腈样品进行标定！");
        text_report.setTextColor(Color.RED);
        debug_message.setText("经过标定之后才能进行建库和物质识别。");
    }

    //初始检查；如果校正参数表数据为空或者非法则提示需要先校正；否则设置校正参数到内部数据
    protected boolean checkCalibrationTable() {
        CalibrationDaoUtils calibUtil = new CalibrationDaoUtils(context);
        double[] current_params = calibUtil.getCalibrationParams();
        Log.d(TAG, "Current calibration params: " + matClassifier.convertDataArrayToString(current_params));
        double[] inner_params = matClassifier.getRamanUtility().GetCalibrationParams();
        Log.d(TAG, "Inner calibration params: " + matClassifier.convertDataArrayToString(inner_params));
        if (current_params.length == 0 || current_params[0] == 0) {
            showCalibrationAlert();
            return false;
        } else
            matClassifier.getRamanUtility().SetCalibrationParams(current_params);
        return true;
    }

    private void calibrate() {
        String calibration_result;
        int calibration_state = -1;
        double[] params = null;
        try {
            //此时的finalsResults应该已经采集到了数据
            params = matClassifier.getRamanUtility().CalcCalibrationParameter(finalsResults, matClassifier.laser_wavelength);
            if (params[5] < matClassifier.calibration_residual_error_threshold) {
                calibration_result = "标定成功，残差小于阈值";
                calibration_state = 0;
            } else {
                calibration_result = "标定完成，但残差大于阈值";
                calibration_state = 1;
            }
        } catch (IndexOutOfBoundsException e) {
            calibration_result = "标定失败，自动检测的尖峰数不足";
        } catch (Exception e) {
            calibration_result = "标定失败，原因不明";
        }
        //同时访问数据库获取当前标定表中的数据，允许为空
        CalibrationDaoUtils calibUtil = new CalibrationDaoUtils(context);
        double[] current_params = calibUtil.getCalibrationParams();
        //显示
        text_report.setText(calibration_result);
        switch (calibration_state) {
            case 0:
                text_report.setTextColor(Color.GREEN);
                break;
            case 1:
                text_report.setTextColor(Color.YELLOW);
                break;
            default:
                text_report.setTextColor(Color.RED);
                break;
        }
        if (calibration_state < 0)
            return;
        Timestamp timestamp = new Timestamp((long)params[1]);
        SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
        String debug_msg = String.format("标定结果：%.2f, %.4f, %.6f", params[2], params[3], params[4]);
        debug_msg += String.format("\n标定时间：%s  残差：%.4f", sdf.format(timestamp), params[5]);
        String debug_msg_current_table_time = "";
        String debug_msg_current_table_residual = "";
        if (current_params.length > 5) {
            debug_msg_current_table_time = sdf.format((new Timestamp((long)current_params[1])));
            debug_msg_current_table_residual = String.format("%.4f", current_params[5]);
            debug_msg = debug_msg + String.format("\n前一次记录：标定时间：%s  残差：%s", debug_msg_current_table_time, debug_msg_current_table_residual);
        }
        debug_message.setText(debug_msg);
        //弹出对话框，所以需要将params保存到类成员变量
        calibrate_params = params;
        if (params[5] >= matClassifier.calibration_residual_error_threshold) {
            //标定时拟合残差过大，需要用户确认才能写入数据库
            String alert_msg = String.format("拟合残差：%.4f大于阈值，", params[5]);
            if (current_params.length > 5)
                alert_msg = alert_msg + String.format("当前数据库中标定时间%s，残差%s，确认要覆盖吗？", debug_msg_current_table_time, debug_msg_current_table_residual);
            else
                alert_msg = alert_msg + "确认要写入数据库吗？";
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(alert_msg);
            builder.setTitle("温 馨 提 示 :");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateCalibrationTable();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } else
            updateCalibrationTable();
    }

    private void updateCalibrationTable() {
        CalibrationDaoUtils calibUtil = new CalibrationDaoUtils(context);
        calibUtil.setCalibrationParams(calibrate_params);
        // 同步设置到raman_utility中
        matClassifier.getRamanUtility().SetCalibrationParams(calibrate_params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start:
                test();
                break;
            case R.id.button_calibrate:
                calibrate();
                break;
            case R.id.fab:
                String alertMsg;
                if (modeCurrentActivity == modeMainActivity)
                    alertMsg = "是否要将此数据保存为测试数据？";
                else
                    alertMsg = "是否要将此数据保存到数据库，用于训练物质识别？";
                Snackbar.make(v, alertMsg, Snackbar.LENGTH_LONG)
                        .setAction("保存", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (finalsResults == null || finalsResults.length == 0) {
                                    Toast.makeText(context, "还没有测试数据，请先测试！", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (modeCurrentActivity == modeMainActivity) {
                                    final EditText et = new EditText(context);
                                    new AlertDialog.Builder(context)
                                            .setIcon(android.R.drawable.ic_dialog_info)
                                            .setView(et)
                                            .setTitle("请输入要保存的名字")
                                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    ProgressDialog pd2 = ProgressDialog.show(context, "温馨提示", "正在保存...");
                                                    saveRawDataToStorage();
                                                    String input = et.getText().toString();
                                                    String userid = SPUtility.getUserId(context);
                                                    boolean canSave = saveDataToHistoryTable(input, userid);
                                                    pd2.dismiss();
                                                    if (canSave)
                                                        dialog.dismiss();
                                                }
                                            }).setNegativeButton("取消", null).create().show();
                                } else {
                                    Intent intent = new Intent(context, DataDetails.class);
                                    intent.putExtra("id", -1L);
                                    intent.putExtra("results", matClassifier.convertDataArrayToString(finalsResults));
                                    startActivity(intent);
                                }
                            }
                        }).show();
                break;
        }
    }

    private void saveRawDataToStorage() {
        //使用虚拟数据无需保存原始数据
        if (useVirtualData)
            return;
        Date current_date = new Date();
        // Modified by X.F
        // 原始数据写入txt
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        final String fileName = Environment.getExternalStorageDirectory() + File.separator + "RamanTest" +
                File.separator + "RawSampleData-" + sdf.format(current_date) + ".txt";
        StringBuffer raw_data_buffer = new StringBuffer();
        String sep = "";
        for (int j = 0; j < results.length; j++) {
            for (int i = 0; i < LaManApplication.dataLength; i ++) {
                if (i < LaManApplication.dataLength - 1)
                    sep = ",";
                else
                    sep = "\n";
                raw_data_buffer.append(results[j][i] + sep);
            }
        }
        FileUtils.writeFile(fileName, raw_data_buffer.toString(), false);
    }

    //保存数据到历史数据表
    private boolean saveDataToHistoryTable(String input, String userid) {
        // 平均后的值写入数据库
        boolean canSave = new HisDaoUtils(context).insertHisDataList(new HisData(
                matClassifier.convertDataArrayToString(finalsResults),
                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()), input,
                new UserDaoUtils(context).queryUserName(userid),
                userid, String.valueOf(time), String.valueOf(power), locationName));
        return canSave;
    }
}
