package com.wzy.lamanpro.utils;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.wzy.lamanpro.R;

import java.util.ArrayList;
import java.util.List;

public class ChartUtil {
    /**
     * 显示图表
     *
     * @param context    上下文
     * @param lineChart  图表对象
     * @param xDataList  X轴数据
     * @param yDataList  Y轴数据
     * @param title      图表标题（如：XXX趋势图）
     * @param curveLable 曲线图例名称（如：--用电量/时间）
     * @param unitName
     */
    static int[] mColors = new int[]{Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED, Color.YELLOW, Color.GREEN};


    public static void showChart(Context context, LineChart lineChart, List<String> xDataList,
                                 List<Entry> yDataList, String title, String curveLable, String unitName) {
        // 设置数据
        lineChart.setData(setLineData(context, xDataList, yDataList, curveLable));
        CustomMarkerView mv = new CustomMarkerView(context, R.layout.chart_marker_view, unitName);
        // set the marker to the chart
        lineChart.setMarkerView(mv);
        // 是否在折线图上添加边框
        lineChart.setDrawBorders(true);
        // 曲线描述 -标题
        lineChart.setDescription(title);
        // 标题字体大小
        lineChart.setDescriptionTextSize(16f);
        // 标题字体颜色
        lineChart.setDescriptionColor(context.getApplicationContext().getResources()
                .getColor(R.color.txt_black));
        // 如果没有数据的时候，会显示这个，类似文本框的placeholder
        lineChart.setNoDataTextDescription("暂无数据");
        // 是否显示表格颜色
        lineChart.setDrawGridBackground(false);
        // 禁止绘制图表边框的线
        lineChart.setDrawBorders(false);
        // 表格的的颜色，在这里是是给颜色设置一个透明度
        // lineChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF);
        // 设置是否启动触摸响应
        lineChart.setTouchEnabled(true);
        // 是否可以拖拽
        lineChart.setDragEnabled(true);
        // 是否可以缩放
        lineChart.setScaleEnabled(true);
        // 如果禁用，可以在x和y轴上分别进行缩放
        lineChart.setPinchZoom(false);
        // lineChart.setMarkerView(mv);
        // 设置背景色
        // lineChart.setBackgroundColor(getResources().getColor(R.color.bg_white));
        // 图例对象
        Legend mLegend = lineChart.getLegend();
        // mLegend.setPosition(LegendPosition.BELOW_CHART_CENTER);
        // 图例样式 (CIRCLE圆形；LINE线性；SQUARE是方块）
        mLegend.setForm(LegendForm.SQUARE);
        // 图例大小
        mLegend.setFormSize(8f);
        // 图例上的字体颜色
        mLegend.setTextColor(context.getApplicationContext().getResources().getColor(R.color.bg_blue));
        mLegend.setTextSize(12f);
        // 图例字体
        // mLegend.setTypeface(mTf);
        // 图例的显示和隐藏
        mLegend.setEnabled(true);
        // 隐藏右侧Y轴（只在左侧的Y轴显示刻度）
        lineChart.getAxisRight().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        // 显示X轴上的刻度值
        xAxis.setDrawLabels(true);

        // 设置X轴的数据显示在报表的下方
        xAxis.setPosition(XAxisPosition.BOTTOM);
        // 轴线
        // xAxis.setDrawAxisLine(false);
        // 设置不从X轴发出纵向直线
        xAxis.setDrawGridLines(false);
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMaxValue(60000);
        yAxis.setAxisMinValue(0);
        // 执行的动画,x轴（动画持续时间）；从2500改为1500，提高速度
        lineChart.animateX(1500);
        lineChart.notifyDataSetChanged();
    }

    /**
     * 曲线赋值与设置
     *
     * @param context   上下文
     * @param xDataList x轴数据
     * @param yDataList y轴数据
     * @return LineData
     */
    private static LineData setLineData(Context context, List<String> xDataList, List<Entry> yDataList,
                                        String curveLable) {
        // LineDataSet表示一条曲线数据对象
        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yDataList, curveLable);
        // mLineDataSet.setFillAlpha(110);
        // mLineDataSet.setFillColor(Color.RED);
        lineDataSet.setCircleColors(mColors);
        // 显示颜色
        lineDataSet.setColor(mColors[3]);
        // 圆形的颜色
        lineDataSet.setCircleColor(mColors[3]);
        // 设置坐标点的颜色
        lineDataSet.setFillColor(mColors[3]);
        // 用y轴的集合来设置参数
        // 不显示坐标点的数据
        lineDataSet.setDrawValues(false);
        // 显示坐标点的小圆点
        lineDataSet.setDrawCircles(true);
        // 定位线
        lineDataSet.setHighlightEnabled(true);
        // 线宽
        lineDataSet.setLineWidth(1f);
        // 显示的圆形大小
        lineDataSet.setCircleSize(1f);
        // 显示颜色
//        lineDataSet.setColor(context.getApplicationContext().getResources().getColor(R.color.bg_blue));
        // 圆形的颜色
//        lineDataSet.setCircleColor(context.getApplicationContext().getResources().getColor(R.color.bg_blue));
        // 高亮的线的颜色
        lineDataSet.setHighLightColor(context.getApplicationContext().getResources()
                .getColor(R.color.text_yellow));
        // 设置坐标点的颜色
//        lineDataSet.setFillColor(context.getApplicationContext().getResources().getColor(R.color.bg_blue));
        // 设置坐标点为空心环状
        lineDataSet.setDrawCircleHole(false);
        // lineDataSet.setValueTextSize(9f);
        lineDataSet.setFillAlpha(65);
        // 设置显示曲线和X轴围成的区域阴影
        lineDataSet.setDrawFilled(true);
        // 坐标轴在左侧
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        // 设置每条曲线图例标签名
        // lineDataSet.setLabel("标签");
        lineDataSet.setValueTextSize(10f);
        // 曲线弧度（区间0.05f-1f，默认0.2f）
//        lineDataSet.setCubicIntensity(0.2f);
        // 设置为曲线显示,false为折线
        lineDataSet.setDrawCubic(false);
        lineDataSets.add(lineDataSet);
        // y轴的数据
        LineData lineData = new LineData(xDataList, lineDataSets);
        return lineData;
    }


}

/**
 * 自定义图表的MarkerView(点击坐标点，弹出提示框)
 */
class CustomMarkerView extends MarkerView {

    private TextView tvContent;
    private String unitName;

    /**
     * @param context        上下文
     * @param layoutResource 资源文件
     * @param unitName       Y轴数值计量单位名称
     */
    public CustomMarkerView(Context context, int layoutResource, final String unitName) {
        super(context, layoutResource);
        // 显示布局中的文本框
        tvContent = (TextView) findViewById(R.id.txt_tips);
        this.unitName = unitName;
    }

    // 每次markerview回调重绘，可以用来更新内容
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // 设置Y周数据源对象Entry的value值为显示的文本内容
        tvContent.setText("" + e.getVal() + unitName);
    }

    @Override
    public int getXOffset(float xpos) {
        // 水平居中
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        // 提示框在坐标点上方显示
        return -getHeight();
    }
}
