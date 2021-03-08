package com.wzy.lamanpro.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.wzy.lamanpro.bean.ProductData;
import com.wzy.lamanpro.common.LaManApplication;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.raman.utility.lib.RamanUtility;
import com.wzy.lamanpro.dao.ProductDataDaoUtils;

/*
 * Created by X.F
 * 物质识别；目前样本不多，所以采用最简单的相关值+最近邻方法；分类器为外部生成的一个Json文件打包进apk
 * 2020.12 增加预处理算法，去除本底并根据乙腈的尖峰对曲线横坐标进行校正，这样再使用相关值就比较稳妥了；分类时使用建库的样本数据
 */
public class MatClassifier {

    private static final String TAG = "MatClassifier";
    private Context context;
    private String classifier_filename = "Classifier.json";
    private boolean classifier_loaded = false;

    // 以下变量均从json文件中加载，用于物质分类
    private int start_pos;
    private int end_pos;
    private String classifier_type;
    private double classifier_threshold;
    private int classifier_vector_num;
    private double[][] classifier_vectors;
    private double[][] calib_classifier_vectors;
    private ArrayList<String> classifier_object_names;

    private RamanUtility raman_utility = new RamanUtility();
    public static final int laser_wavelength = 785;     //固定的激光波长，如果今后发生变化则需要增加界面让用户选择
    public double calibration_residual_error_threshold = 0.3;       //拟合残差阈值；如果过高则需要用户确认后才能写入数据库

    public static final double default_classifier_threshold = 0.97;
    public static final long   default_classifier_priority = 5L;

    //调试用，产生虚拟数据的索引
    private static int virtual_data_index = 0;

    //物质分类返回值
    public class classify_result {
        // 前三项只是最近邻给出的结果；还需要根据阈值来判断是否成功识别
        // 分类结果：名称
        public String object_name;
        // 分类结果得分，[0-1]之间的一个浮点数，一般来说需要超过0.95才比较有把握
        public double score;
        // 最近邻的索引；用于分析数据，对结果无影响
        public int nearest_index;

        // 正式的结果（低于阈值则显示无法识别）
        public String formal_message;
        // 是否正确识别
        public boolean bSuccess;
        // 调试信息（识别结果及索引）
        public String debug_message;

        public classify_result() {
            this.object_name = "无";
            this.score = -1;
            this.nearest_index = -1;

            this.formal_message = "";
            this.bSuccess = false;
            this.debug_message = "";
        }
    }

    //内部使用的临时结果；分类时进行排序
    private class classify_temp_result {
        public String object_name;
        public double score;
        public int nearest_index;
        public double threshold;
        public long priority;

        public classify_temp_result() {
            object_name = "";
            score = -1;
            nearest_index = -1;
            threshold = -1;
            priority = -1;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format("Name: %s, score: %.4f, nearest_index: %d, threshold: %.2f, priority: %d",
                    object_name, score, nearest_index, threshold, priority);
        }

        public String toDebugString() {
            return "识别结果：" + object_name + "，相关值：" + String.format("%.4f", score) + "，索引：" + nearest_index;
        }
    }

    public MatClassifier(Context context) {
        // 初始化，加载分类器
        this.context = context;
        loadClassifierFromeFile();
    }

    public RamanUtility getRamanUtility() {
        return raman_utility;
    }

    public void loadClassifierFromeFile() {
        if (classifier_loaded)
            return;

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(classifier_filename));
            start_pos = obj.getInt("start_pos");
            end_pos = obj.getInt("end_pos");
            classifier_type = obj.getString("classifier_type");
            classifier_threshold = obj.getDouble("classifier_threshold");
            assert (end_pos > start_pos);

            JSONArray classifier_info_arr = obj.getJSONArray("classifier_info");
            classifier_vector_num = classifier_info_arr.length();
            classifier_vectors = new double[classifier_vector_num][end_pos - start_pos + 1];
            classifier_object_names = new ArrayList<>();

            for (int i = 0; i < classifier_vector_num; i++) {
                JSONObject classifier_info_item = classifier_info_arr.getJSONObject(i);
                classifier_object_names.add(classifier_info_item.getString("object"));
                JSONArray classifier_vector = classifier_info_item.getJSONArray("vector");
                assert (classifier_vector.length() == end_pos - start_pos + 1);
                for (int j = 0; j < classifier_vector.length(); j++)
                    classifier_vectors[i][j] = classifier_vector.getDouble(j);
            }
            classifier_loaded = true;
            Log.d(TAG, "分类器正常加载完毕，共" + classifier_vector_num + "个向量，长度：" + (end_pos - start_pos + 1));
        } catch (JSONException e) {
            Log.w(TAG, "解析分类器JSON文件出错！");
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void writeClassifierVectorToTable(int index) {
        String product_name = classifier_object_names.get(index);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
        boolean retval = new ProductDataDaoUtils(context).insertProductList(new ProductData(product_name, convertDataArrayToString(classifier_vectors[index]),
                format.format(new Date()), "", "", "", "", "", "", "", "", "", "", "", "", "",
                default_classifier_threshold, default_classifier_priority, convertDataArrayToString(calib_classifier_vectors[index])));
        if (!retval)
            Log.w(TAG, "写入PRODUCT_DATA表失败！");
    }

    //这里为了避免小数点后无用的位数过多，所以转为float；
    //对于采集的数据来说都是整数/n取平均，所以一般来说float已经足够了，即使从字符串中恢复数据误差也很小
    public String convertDataArrayToString(double[] data) {
        StringBuffer stringBuffer = new StringBuffer();
        if (data != null) {
            for (double item : data) {
                stringBuffer.append((float)item + ",");
            }
            if (stringBuffer.length() > 0)
                stringBuffer.setLength(stringBuffer.length() - 1);
        } else
            stringBuffer.append("Null array");
        return stringBuffer.toString();
    }

    //调试用，输出数据到文件以便后续分析
    public void saveDataToTempFile(double[] data) {
        Date current_date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        final String fileName = Environment.getExternalStorageDirectory() + File.separator + "RamanTest" +
                File.separator + "RawTestData-" + sdf.format(current_date) + ".txt";
        FileUtils.writeFile(fileName, convertDataArrayToString(data), false);
    }

    //调试用，生成虚拟数据
    public double[] getSampleData() {
        virtual_data_index ++;
        if (virtual_data_index % 2 == 0) {
            return recoverOriginalVector(classifier_vectors[0]);
        }
        return recoverOriginalVector(classifier_vectors[5]);
    }

    //历史原因，json中的向量切掉了两端少量数据，所以要先恢复原始长度
    private double[] recoverOriginalVector(double[] input_vector) {
        double[] org_vector = new double[LaManApplication.dataLength];
        for (int i = 0; i < start_pos; i ++)
            org_vector[i] = input_vector[0];
        for (int i = start_pos; i <= end_pos; i ++)
            org_vector[i] = input_vector[i - start_pos];
        for (int i = end_pos + 1; i < LaManApplication.dataLength; i ++)
            org_vector[i] = input_vector[end_pos - start_pos];
        return org_vector;
    }

    //初始化
    public void initializeClassifierTable() {
        //如果PRODUCT_DATA表为空，将json中的数据校正之后有选择的直接写入Product表；目前只写0（酒精）和5（乙腈）两条记录
        if (new ProductDataDaoUtils(context).queryAllData().size() == 0) {
            if (!classifier_loaded) {
                loadClassifierFromeFile();
            }
            Log.d(TAG, "PRODUCT_DATA表为空，将训练好的分类器写入PRODUCT_DATA表。");
            //Log的输出一行最多4k，所以显示不完整；还是得输出到文件中
            //StringBuffer stringBuf = new StringBuffer();
            calib_classifier_vectors = new double[classifier_vector_num][raman_utility.GetMaxCalibratedWavenumber()];
            //json中的数据校正参数不同，需要重新计算；
            double[] best_params = null;
            for (int i = 0; i < classifier_vector_num; i ++) {
                try {
                    double[] params = raman_utility.CalcCalibrationParameter(recoverOriginalVector(classifier_vectors[i]), 785);
                    Log.d(TAG, String.format("重新标定：No. %3d, 物质：%s, 参数：%s", i, classifier_object_names.get(i), convertDataArrayToString(params)));
                    if (best_params == null || best_params[5] > params[5])
                        best_params = params.clone();
                } catch (IndexOutOfBoundsException e) {
                    Log.d(TAG, String.format("No. %3d, 标定失败，自动检测的尖峰数不足", i));
                } catch (Exception e) {
                    Log.d(TAG, String.format("No. %3d, 标定失败，原因不明", i));
                }
            }
            Log.d(TAG, String.format("最佳参数：%s", convertDataArrayToString(best_params)));
            for (int i = 0; i < classifier_vector_num; i ++) {
                calib_classifier_vectors[i] = raman_utility.CalibrateSpectrumWithParams(recoverOriginalVector(classifier_vectors[i]), best_params);
                //stringBuf.append(convertDataArrayToString(calib_classifier_vectors[i]) + "\n");
            }
            //写入文件
            //final String fileName = Environment.getExternalStorageDirectory() + File.separator + "RamanTest" +
            //        File.separator + "RawClassifierData.txt";
            //FileUtils.writeFile(fileName, stringBuf.toString(), false);

            writeClassifierVectorToTable(0);
            //writeClassifierVectorToTable(1);
            writeClassifierVectorToTable(5);
            //writeClassifierVectorToTable(6);
        }
        //批量读取测试标定与分类功能
//        batchTestCalibrationTasks();
    }

    //旧版的代码，使用JSON读入的分类向量进行分类
    public classify_result classify_old(double[] input_vector) {
        if (!classifier_loaded) {
            loadClassifierFromeFile();
        }
        assert (input_vector.length >= end_pos);

        // 暂时修改逻辑为：输入的vector和从json读取的数据均要经过校正，然后再计算相关值；暂时不做优化，就是为了看一下速度如何
        // 以后会改进为使用建库的数据，那时可以考虑安装后就从将json数据校正后导入数据库中
        // 调用者保证了数据库中标定参数的有效性

        // 截取中间有效区域；正好转成double数组
        double[] vector_slice = new double[end_pos - start_pos + 1];
        for (int i = 0; i < end_pos - start_pos + 1; i++)
            vector_slice[i] = input_vector[start_pos + i];

        double[] calib_vector_slice = raman_utility.CalibrateSpectrum(recoverOriginalVector(vector_slice));

        classify_result result = new classify_result();
        if (classifier_type.equals("NN")) {
            // 取相关值最高的作为识别结果
            result.score = -1;
            for (int i = 0; i < classifier_vector_num; i++) {
                //double corr = new PearsonsCorrelation().correlation(vector_slice, classifier_vectors[i]);
                double corr = new PearsonsCorrelation().correlation(calib_vector_slice, calib_classifier_vectors[i]);
                if (corr > result.score) {
                    result.score = corr;
                    result.object_name = classifier_object_names.get(i);
                    result.nearest_index = i;
                }
            }
            String score_str = new DecimalFormat("0.0000").format(result.score);
            result.debug_message = "[Debug]识别结果：" + result.object_name + "，相关值：" + score_str + "，索引：" + result.nearest_index;
            if (result.score < classifier_threshold) {
                result.formal_message = "无法识别，相关值低于阈值！";
            } else {
                result.formal_message = "识别结果：" + result.object_name + "，相关值：" + score_str;
                result.bSuccess = true;
            }
            Log.d(TAG, result.debug_message);
        } else {
            result.object_name = "";
            result.score = -1;
            result.formal_message = "未支持的分类器类型，请检查！";
            Log.d(TAG, result.formal_message);
        }
        return result;
    }

    private double[][] readCSVData(String filename) {
        List<String> file_content = FileUtils.readFileToList(filename, "UTF-8");
        double[][] data = new double[file_content.size()][LaManApplication.dataLength];
        for (int index = 0; index < file_content.size(); index ++) {
            String line = file_content.get(index);
            String[] strings = line.split(",");
            assert(strings.length == LaManApplication.dataLength);
            for (int i = 0; i < strings.length; i++)
                data[index][i] = Double.valueOf(strings[i]);
        }
        Log.d(TAG, String.format("Load data (%d, %d) from file: %s", data.length, data[0].length, filename));
        return data;
    }

    //测试用
    private void batchTestCalibration(String filename, boolean inside_sample) {
        //in表示乙腈样品，out表示非乙腈样品；
        double[][] data = readCSVData(filename);
        double[] org_params = raman_utility.GetCalibrationParams();
        classify_result result;
        for (int i = 0; i < data.length; i ++) {
            try {
                //Log.d(TAG, String.format("No. %3d, 数据：%s", i, convertDataArrayToString(data[i])));
                double[] params = raman_utility.CalcCalibrationParameter(data[i], 785);
                String result_str;
                if (inside_sample)
                    result_str = params[5] < calibration_residual_error_threshold ? "通过" : "不通过";
                else
                    result_str = params[5] >= calibration_residual_error_threshold ? "通过" : "不通过";
                Log.d(TAG, String.format("No. %3d, 标定结果：%4s, 残差: %.4f, 参数：%s", i, result_str, params[5], convertDataArrayToString(params)));
                //如果是乙腈样品并且通过，那么就进行后续的物质分类；非乙腈类样本则无法估计正确的参数，就算了
                if (inside_sample && params[5] < calibration_residual_error_threshold) {
                    raman_utility.SetCalibrationParams(params);
                    result = classify(data[i]);
                    Log.d(TAG, String.format("\t%s, %s", result.formal_message, result.debug_message));
                }
            } catch (IndexOutOfBoundsException e) {
                Log.d(TAG, String.format("No. %3d, 标定失败，自动检测的尖峰数不足", i));
            } catch (Exception e) {
                Log.d(TAG, String.format("No. %3d, 标定失败，原因不明", i));
            }
        }
        raman_utility.SetCalibrationParams(org_params);
    }

    //测试用；批量读取导入的csv，每行一个采样数据，测试标定功能
    private void batchTestCalibrationTasks() {
        //in表示乙腈样品，out表示非乙腈样品；
        batchTestCalibration(Environment.getExternalStorageDirectory() + File.separator + "RamanTest" +
                File.separator + "device1_in.csv", true);
        batchTestCalibration( Environment.getExternalStorageDirectory() + File.separator + "RamanTest" +
                File.separator + "device2_in.csv", true);
        batchTestCalibration( Environment.getExternalStorageDirectory() + File.separator + "RamanTest" +
                File.separator + "device1_out.csv", false);
        batchTestCalibration( Environment.getExternalStorageDirectory() + File.separator + "RamanTest" +
                File.separator + "device2_out.csv", false);
    }

    //新版物质识别；从PRODUCT_DATA表获取数据计算相关值并且排序
    public classify_result classify(double[] input_vector) {
        double[] calib_vector = raman_utility.CalibrateSpectrum(input_vector);
        List<ProductData> productData = new ProductDataDaoUtils(context).queryAllData();
        //对相关值超过阈值的每种物质均保留一份最大值
        Map<String, classify_temp_result> maps = new HashMap<>();
        for (ProductData product : productData) {
            String vector_str = product.getCalibratedData();
            String[] strings = vector_str.split(",");
            assert(strings.length == raman_utility.GetMaxCalibratedWavenumber());
            double[] calibrated_data = new double[strings.length];
            for (int i = 0; i < strings.length; i++)
                calibrated_data[i] = Double.valueOf(strings[i]);
            double corr = new PearsonsCorrelation().correlation(calib_vector, calibrated_data);
            String productName = product.getProName();
            classify_temp_result temp_result;
            //调试时将阈值降低一些；便于多输出一些信息
            if (corr >= 0 /* product.getProductThreshold() */) {
                temp_result = maps.get(productName);
                if (temp_result == null)
                    temp_result = new classify_temp_result();
                if (temp_result.score < corr) {
                    temp_result.object_name = productName;
                    temp_result.score = corr;
                    temp_result.nearest_index = product.getId().intValue();
                    temp_result.threshold = product.getProductThreshold();
                    temp_result.priority = product.getProductPriority();
                    maps.put(productName, temp_result);
                }
            }
        }
        //对maps中的所有结果进行排序
        ArrayList<classify_temp_result> resultList = new ArrayList<>();
        for (String key: maps.keySet())
            resultList.add(maps.get(key));
        Collections.sort(resultList, new Comparator<classify_temp_result>() {
            double ERR = 1e-6;
            @Override
            public int compare(classify_temp_result lhs, classify_temp_result rhs) {
                //TODO: 未来可能需要结合优先级priority进行判断；目前先简单取最佳相关值
                if (Math.abs(lhs.score - rhs.score) < ERR) return 0;
                return Double.compare(rhs.score, lhs.score);
            }
        });
//        for (classify_temp_result temp_result: resultList) {
//            Log.d(TAG, temp_result.toString());
//        }
        classify_result result = new classify_result();
        if (resultList.size() > 0) {
            classify_temp_result best_result = resultList.get(0);
            result.score = best_result.score;
            result.object_name = best_result.object_name;
            result.bSuccess = result.score >= best_result.threshold;
            String score_str = String.format("%.4f", result.score);
            //debug_message输出前三名的结果
            result.debug_message = "[Debug]最佳" + best_result.toDebugString();
            if (resultList.size() > 1)
                result.debug_message += "\n第二" + resultList.get(1).toDebugString();
            if (resultList.size() > 2)
                result.debug_message += "\n第三" + resultList.get(2).toDebugString();
            if (result.score < best_result.threshold) {
                result.formal_message = "无法识别，相关值低于阈值！";
            } else {
                result.formal_message = "识别结果：" + result.object_name + "，相关值：" + score_str;
            }
        } else {
            result.object_name = "";
            result.score = -1;
            result.formal_message = "结果列表为空，请检查！";
            Log.w(TAG, result.formal_message);
        }
        return result;
    }
}
