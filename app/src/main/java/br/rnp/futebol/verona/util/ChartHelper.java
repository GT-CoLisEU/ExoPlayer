package br.rnp.futebol.verona.util;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartHelper {


    public BarData barPlot(ArrayList<Float> values, float barWidth, int[] colors, String about) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int entryIndex = 0;
        for (Float f : values)
            barEntries.add(new BarEntry(++entryIndex, f));
        BarDataSet barDataSet = new BarDataSet(barEntries, about);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(barWidth);
        barDataSet.setColors(colors);
        return barData;
    }

    public BarData barPlot(ArrayList<Float> values, ArrayList<Float> values2,
                           float barWidth, int[] colors, String about, String about2) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries2 = new ArrayList<>();

        int entryIndex = 0;
        for (Float f : values)
            barEntries.add(new BarEntry((++entryIndex) + barWidth + 0.05f, f));
        entryIndex = 0;
        for (Float g : values2)
            barEntries2.add(new BarEntry(++entryIndex, g));

        BarDataSet barDataSet = new BarDataSet(barEntries, about);
        barDataSet.setColors(colors);
        BarDataSet barDataSet2 = new BarDataSet(barEntries2, about2);
        barDataSet2.setColors(colors);
        List<IBarDataSet> datas = new ArrayList<>();
        datas.add(barDataSet);
        datas.add(barDataSet2);
        BarData barData = new BarData(datas);
        barData.setBarWidth(barWidth);
        return barData;
    }

    public void barChartDefaultConfig(BarChart chart, Float max, Float min) {
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter());
//        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setEnabled(false);
        chart.animateY(1000);
        chart.setFitBars(true);
        if (max != null)
            chart.getAxisLeft().setAxisMaximum(max);
        if (min != null)
            chart.getAxisLeft().setAxisMaximum(min);
    }

    public void barChartDefaultConfig(BarChart chart) {
        barChartDefaultConfig(chart, null, null);
    }


    public static final int[] VERONA_COLORS = {Color.rgb(63, 81, 181), Color.rgb(100, 100, 150),
            Color.rgb(136, 180, 187), Color.rgb(148, 212, 212),
            Color.rgb(118, 174, 175), Color.rgb(42, 109, 130), Color.rgb(207, 248, 246)};


}
