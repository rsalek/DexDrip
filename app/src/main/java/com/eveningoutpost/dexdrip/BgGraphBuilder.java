package com.eveningoutpost.dexdrip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Created by stephenblack on 11/15/14.
 */
public class BgGraphBuilder {
    public double  end_time = new Date().getTime() + (60000 * 20);
    public double  start_time = end_time - (60000 * 60 * 24);

    //TODO: Make these editable via settings
    public final int highMark = 175;
    public final int lowMark = 70;
    public final int defaultMaxY = 40;
    public final int defaultMinY = 200;
    private double endHour;
    private final int numValues =(60/5)*24;
    private final List<BgReading> bgReadings = BgReading.latestForGraph( numValues, start_time);
    private List<PointValue> inRangeValues = new ArrayList<PointValue>();
    private List<PointValue> highValues = new ArrayList<PointValue>();
    private List<PointValue> lowValues = new ArrayList<PointValue>();
    public Viewport tempViewport;


    public LineChartData lineData() {
        LineChartData lineData = new LineChartData(defaultLines());
        lineData.setAxisYLeft(yAxis());
        lineData.setAxisXBottom(xAxis());
        return lineData;
    }

    public LineChartData previewLineData() {
        LineChartData previewLineData = new LineChartData(lineData());
        previewLineData.setAxisYLeft(yAxis());
        previewLineData.setAxisXBottom(previewXAxis());
//        previewLineData.getLines().get(0).setColor(Utils.DEFAULT_DARKEN_COLOR);
//        previewLineData.getLines().get(1).setColor(Utils.DEFAULT_DARKEN_COLOR);
//        previewLineData.getLines().get(2).setColor(Utils.DEFAULT_DARKEN_COLOR);
//        previewLineData.getLines().get(3).setColor(Utils.DEFAULT_DARKEN_COLOR);
        return previewLineData;
    }

    public List<Line> defaultLines() {
        addBgReadingValues();
        List<Line> lines = new ArrayList<Line>();
        lines.add(minShowLine());
        lines.add(maxShowLine());
        lines.add(highLine());
        lines.add(lowLine());
        lines.add(inRangeValuesLine());
        lines.add(lowValuesLine());
        lines.add(highValuesLine());
        return lines;
    }

    public Line highValuesLine() {
        Line highValuesLine = new Line(highValues);
        highValuesLine.setColor(Utils.COLOR_ORANGE);
        highValuesLine.setHasLines(false);
        highValuesLine.setPointRadius(2);
        highValuesLine.setHasPoints(true);
        return highValuesLine;
    }

    public Line lowValuesLine() {
        Line lowValuesLine = new Line(lowValues);
        lowValuesLine.setColor(Utils.COLOR_RED);
        lowValuesLine.setHasLines(false);
        lowValuesLine.setPointRadius(2);
        lowValuesLine.setHasPoints(true);
        return lowValuesLine;
    }

    public Line inRangeValuesLine() {
        Line inRangeValuesLine = new Line(inRangeValues);
        inRangeValuesLine.setColor(Utils.COLOR_BLUE);
        inRangeValuesLine.setHasLines(false);
        inRangeValuesLine.setPointRadius(2);
        inRangeValuesLine.setHasPoints(true);
        return inRangeValuesLine;
    }

    private void addBgReadingValues() {
        for (BgReading bgReading : bgReadings) {
            if (bgReading.calculated_value >= highMark) {
                highValues.add(new PointValue((float)bgReading.timestamp, (float)bgReading.calculated_value));
            } else if (bgReading.calculated_value <= lowMark) {
                lowValues.add(new PointValue((float)bgReading.timestamp, (float)bgReading.calculated_value));
            } else {
                inRangeValues.add(new PointValue((float)bgReading.timestamp, (float)bgReading.calculated_value));
            }
        }
    }

    public Line highLine() {
        List<PointValue> highLineValues = new ArrayList<PointValue>();
        highLineValues.add(new PointValue((float)start_time, highMark));
        highLineValues.add(new PointValue((float)end_time, highMark));
        Line highLine = new Line(highLineValues);
        highLine.setHasPoints(false);
        highLine.setStrokeWidth(1);
        highLine.setColor(Utils.COLOR_ORANGE);
        return highLine;
    }

    public Line lowLine() {
        List<PointValue> lowLineValues = new ArrayList<PointValue>();
        lowLineValues.add(new PointValue((float)start_time, lowMark));
        lowLineValues.add(new PointValue((float)end_time, lowMark));
        Line lowLine = new Line(lowLineValues);
        lowLine.setHasPoints(false);
        lowLine.setAreaTransparency(30);
        lowLine.setColor(Utils.COLOR_RED);
        lowLine.setStrokeWidth(1);
        lowLine.setFilled(true);
        return lowLine;
    }

    public Line maxShowLine() {
        List<PointValue> maxShowValues = new ArrayList<PointValue>();
        maxShowValues.add(new PointValue((float)start_time, defaultMaxY));
        maxShowValues.add(new PointValue((float)end_time, defaultMaxY));
        Line maxShowLine = new Line(maxShowValues);
        maxShowLine.setHasLines(false);
        maxShowLine.setHasPoints(false);
        return maxShowLine;
    }

    public Line minShowLine() {
        List<PointValue> minShowValues = new ArrayList<PointValue>();
        minShowValues.add(new PointValue((float)start_time, defaultMinY));
        minShowValues.add(new PointValue((float)end_time, defaultMinY));
        Line minShowLine = new Line(minShowValues);
        minShowLine.setHasPoints(false);
        minShowLine.setHasLines(false);
        return minShowLine;
    }

    /////////AXIS RELATED//////////////
    public Axis yAxis() {
        Axis yAxis = new Axis();
        yAxis.setAutoGenerated(false);
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for(int j = 1; j <= 12; j += 1) {
            axisValues.add(new AxisValue(j * 50));
        }
        yAxis.setValues(axisValues);
        yAxis.setHasLines(true);
        yAxis.setMaxLabelChars(5);
        return yAxis;
    }
    public Axis xAxis() {
        Axis xAxis = new Axis();
        xAxis.setAutoGenerated(false);
        List<AxisValue> xAxisValues = new ArrayList<AxisValue>();
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar today = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat timeFormat = new SimpleDateFormat("h a");
        timeFormat.setTimeZone(TimeZone.getDefault());
        double start_hour = today.getTime().getTime();
        double timeNow = new Date().getTime();
        for(int l=0; l<=24; l++) {
            if ((start_hour + (60000 * 60 * (l))) <  timeNow) {
                if((start_hour + (60000 * 60 * (l + 1))) >=  timeNow) {
                    endHour = start_hour + (60000 * 60 * (l));
                    l=25;
                }
            }
        }
        for(int l=0; l<=24; l++) {
            double timestamp = endHour - (60000 * 60 * l);
            xAxisValues.add(new AxisValue((long)(timestamp), (timeFormat.format(timestamp)).toCharArray()));
        }
        xAxis.setValues(xAxisValues);
        xAxis.setHasLines(true);
        return xAxis;
    }

    public Axis previewXAxis(){
        List<AxisValue> previewXaxisValues = new ArrayList<AxisValue>();
        SimpleDateFormat timeFormat = new SimpleDateFormat("h a");
        timeFormat.setTimeZone(TimeZone.getDefault());
        for(int l=0; l<=24; l++) {
            double timestamp = endHour - (60000 * 60 * l);
            previewXaxisValues.add(new AxisValue((long)(timestamp), (timeFormat.format(timestamp)).toCharArray()));
        }
        Axis previewXaxis = new Axis();
        previewXaxis.setValues(previewXaxisValues);
        previewXaxis.setHasLines(true);
        previewXaxis.setTextSize(5);
        return previewXaxis;
    }

    /////////VIEWPORT RELATED//////////////
    public Viewport advanceViewport(Chart chart, Chart previewChart) {
        tempViewport = new Viewport(chart.getMaximumViewport());
        tempViewport.inset((float)(86400000 / 2.5), 0);
        double distance_to_move = (new Date().getTime()) - tempViewport.left - (((tempViewport.right - tempViewport.left) /2));
        tempViewport.offset((float) distance_to_move, 0);
        return tempViewport;
    }
}