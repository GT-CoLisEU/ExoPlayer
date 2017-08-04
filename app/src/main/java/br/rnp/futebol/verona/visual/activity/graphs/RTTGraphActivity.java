package br.rnp.futebol.verona.visual.activity.graphs;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.androidplot.Region;
import com.androidplot.ui.Anchor;
import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.SeriesBundle;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.TextOrientation;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.util.SeriesUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;
import com.androidplot.xy.XYStepCalculator;
import com.google.android.exoplayer2.demo.R;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * An example of a Bar Plot.
 */
public class RTTGraphActivity extends Activity {

    private String NO_SELECTION_TXT = "Touch bar to select.";
    private XYPlot plot;
    //    private Number[] series1Numbers = {0, 1, 2, 3, 4, 5, 6, 7};
    private Number[] series1Numbers = randons();
    private String[] names = names();
    //    private Number[] series2Numbers = {4, -1};
    private MyBarFormatter formatter1;
    private MyBarFormatter formatter2;
    private MyBarFormatter selectionFormatter;
    private TextLabelWidget selectionWidget;
    private Pair<Integer, XYSeries> selection;
    int cont = 0;

    private Number[] randons() {
        Number[] n;
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(10) + 1;
        n = new Number[randomInt];
        for (int i = 0; i < randomInt; i++) {
            n[i] = randomGenerator.nextInt(10) + 1;
            Log.i("nabc", n[i]+"");
        }
        return n;
    }

    private String[] names() {
        String[] s = new String[series1Numbers.length + 1];
        s[0] = "";
        int cont = 1;
        for (int i = 1; i < series1Numbers.length + 1; i++) {
            s[i] = "" + cont++;
        }
        return s;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_view_activity);

        plot = (XYPlot) findViewById(R.id.plot);
        plot.setTitle("Providers");

        formatter1 = new MyBarFormatter(Color.rgb(63, 81, 181), Color.rgb(63, 81, 181));
        formatter1.setMarginLeft(PixelUtils.dpToPix(1));
        formatter1.setMarginRight(PixelUtils.dpToPix(1));
        formatter2 = new MyBarFormatter(Color.rgb(100, 100, 150), Color.rgb(100, 100, 150));
        formatter2.setMarginLeft(PixelUtils.dpToPix(1));
        formatter2.setMarginRight(PixelUtils.dpToPix(1));

        selectionFormatter = new MyBarFormatter(Color.rgb(100, 100, 150), Color.rgb(100, 100, 150));

        selectionWidget = new TextLabelWidget(plot.getLayoutManager(), NO_SELECTION_TXT,
                new Size(PixelUtils.dpToPix(100), SizeMode.ABSOLUTE,
                        PixelUtils.dpToPix(100), SizeMode.ABSOLUTE),
                TextOrientation.HORIZONTAL);

        Paint p = new Paint();
        p.setARGB(100, 0, 0, 0);
        selectionWidget.setBackgroundPaint(p);

        selectionWidget.getLabelPaint().setTextSize(PixelUtils.dpToPix(16));

        selectionWidget.position(
                0, HorizontalPositioning.RELATIVE_TO_CENTER,
                PixelUtils.dpToPix(45), VerticalPositioning.ABSOLUTE_FROM_TOP,
                Anchor.TOP_MIDDLE);
        selectionWidget.pack();

//        plot.setLinesPerRangeLabel(5);
//        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);
//
//        plot.setLinesPerDomainLabel(5);

        plot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    onPlotClicked(new PointF(motionEvent.getX(), motionEvent.getY()));
                }
                return true;
            }
        });

        updatePlot();


//        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new GraphXLabelFormat(names));
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new NumberFormat() {
                    @Override
                    public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
                        StringBuffer strBuffer = new StringBuffer();
                        strBuffer.append(cont < names.length ? names[cont] : "");
                        cont++;
//                        strBuffer.append("");
                        return strBuffer;
                    }

                    @Override
                    public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
                        throw new UnsupportedOperationException("Not yet implemented.");
                    }

                    @Override
                    public Number parse(String string, ParsePosition position) {
                        throw new UnsupportedOperationException("Not yet implemented.");
                    }
                });
        updatePlot();
    }

    private void updatePlot() {

        plot.clear();

//        ArrayList<XYSeries> seriesList = new ArrayList<>();
//
//        for (int i = 0; i < series1Numbers.length; i++) {
//            Number temp[] = new Number[2];
//            temp[1] = series1Numbers[i];
//            temp[0] = 0;
//            seriesList.add(new SimpleXYSeries(Arrays.asList(temp),
//                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, names[i]));
//        }

        XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Providers");
//        plot.calculateMinMaxVals();
//        Number maxX = SeriesUtils.minMax(series1).getMaxX();
        plot.setDomainStep(StepMode.SUBDIVIDE, series1Numbers.length + 1);
//        plot.setLinesPerDomainLabel(100);
//        plot.setTicksPerDomainLabel(1);
//        XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers),
//                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Them");

        plot.setDomainBoundaries(-1, series1Numbers.length, BoundaryMode.FIXED);
        plot.setRangeUpperBoundary(SeriesUtils.minMax(series1).getMaxY().doubleValue() + 1, BoundaryMode.FIXED);

//        for (XYSeries xy : seriesList)
        plot.addSeries(series1, formatter1);
//        plot.addSeries(series2, formatter2);
//        plot.getLayoutManager().remove(plot.getLegend());
        plot.setPlotMarginBottom(0);
        plot.setPlotMarginTop(0);
        plot.setPlotMarginLeft(0);
        plot.setPlotMarginRight(0);
//        plot.getGraph().getDomainGridLinePaint().setTextAlign(Paint.Align.CENTER);
//        plot.getBackgroundPaint().setColor(Color.TRANSPARENT);
//        plot.getGraph().getBackgroundPaint().setColor(Color.TRANSPARENT);
//        plot.getBorderPaint().setColor(Color.TRANSPARENT);
//        plot.addSeries(series2, formatter2);

        MyBarRenderer renderer = plot.getRenderer(MyBarRenderer.class);
        renderer.setBarOrientation(BarRenderer.BarOrientation.SIDE_BY_SIDE);

        final BarRenderer.BarGroupWidthMode barGroupWidthMode
                = (BarRenderer.BarGroupWidthMode.FIXED_WIDTH);
        renderer.setBarGroupWidth(barGroupWidthMode, 80);

//        plot.getInnerLimits().setMaxY(10);
        plot.redraw();

    }

    @SuppressWarnings("deprecation")
    private void onPlotClicked(PointF point) {
        cont = 1;

        if (plot.containsPoint(point.x, point.y)) {
            Number x = plot.getXVal(point);
            Number y = plot.getYVal(point);

            selection = null;
            double xDistance = 0;
            double yDistance = 0;

            for (SeriesBundle<XYSeries, ? extends XYSeriesFormatter> sfPair : plot
                    .getRegistry().getSeriesAndFormatterList()) {
                XYSeries series = sfPair.getSeries();
                for (int i = 0; i < series.size(); i++) {
                    Number thisX = series.getX(i);
                    Number thisY = series.getY(i);
                    if (thisX != null && thisY != null) {
                        double thisXDistance =
                                Region.measure(x, thisX).doubleValue();
                        double thisYDistance =
                                Region.measure(y, thisY).doubleValue();
                        if (selection == null) {
                            selection = new Pair<>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance < xDistance) {
                            selection = new Pair<>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance == xDistance &&
                                thisYDistance < yDistance &&
                                thisY.doubleValue() >= y.doubleValue()) {
                            selection = new Pair<>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        }
                    }
                }
            }

        } else {
            selection = null;
        }

        if (selection == null) {
            selectionWidget.setText(NO_SELECTION_TXT);
        } else {
//            selectionWidget.setText("Selected: " + selection.second.getTitle() +
//                    " Value: " + selection.second.getY(selection.first));
            selectionWidget.setText("RTT: " + selection.second.getY(selection.first) + "ms");
        }
        plot.redraw();
    }

    private class GraphXLabelFormat extends Format {

        private String labels[];

        public GraphXLabelFormat(String[] labels) {
            this.labels = labels;
        }

        @Override
        public StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
//            int parsedInt = Math.round(Float.parseFloat(object.toString()));
            String labelString = labels[0];
            buffer.append(labelString);
            return buffer;
        }

        @Override
        public Object parseObject(String string, ParsePosition position) {
            return java.util.Arrays.asList(labels).indexOf(string);
        }
    }

    class MyBarFormatter extends BarFormatter {

        public MyBarFormatter(int fillColor, int borderColor) {
            super(fillColor, borderColor);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MyBarRenderer.class;
        }

        @Override
        public SeriesRenderer doGetRendererInstance(XYPlot plot) {
            return new MyBarRenderer(plot);
        }
    }

    class MyBarRenderer extends BarRenderer<MyBarFormatter> {

        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }

        @Override
        public MyBarFormatter getFormatter(int index, XYSeries series) {
            if (selection != null &&
                    selection.second == series &&
                    selection.first == index) {
                return selectionFormatter;
            } else {
                return getFormatter(series);
            }
        }
    }
}