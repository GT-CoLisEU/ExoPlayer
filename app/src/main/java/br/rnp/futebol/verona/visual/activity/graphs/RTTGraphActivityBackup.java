package br.rnp.futebol.verona.visual.activity.graphs;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.exoplayer2.demo.R;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;


public class RTTGraphActivityBackup extends AppCompatActivity {


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private static final String NO_SELECTION_TXT = "Touch bar to select.";
    private XYPlot plot;

//    private CheckBox series1CheckBox;
//    private CheckBox series2CheckBox;
//    private Spinner spRenderStyle, spWidthStyle, spSeriesSize;
//    private SeekBar sbFixedWidth, sbVariableWidth;

    private XYSeries series1;
    private XYSeries series2;

    private enum SeriesSize {
        TEN,
        TWENTY,
        SIXTY
    }

    // Create a couple arrays of y-values to plot:
    Number[] series1Numbers10 = {2, null, 5, 2, 7, 4, 3, 7, 4, 5};
    Number[] series2Numbers10 = {4, 6, 3, null, 2, 0, 7, 4, 5, 4};
    Number[] series1Numbers20 = {2, null, 5, 2, 7, 4, 3, 7, 4, 5, 7, 4, 5, 8, 5, 3, 6, 3, 9, 3};
    Number[] series2Numbers20 = {4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9};
    Number[] series1Numbers60 = {2, null, 5, 2, 7, 4, 3, 7, 4, 5, 7, 4, 5, 8, 5, 3, 6, 3, 9, 3, 2, null, 5, 2, 7, 4, 3, 7, 4, 5, 7, 4, 5, 8, 5, 3, 6, 3, 9, 3, 2, null, 5, 2, 7, 4, 3, 7, 4, 5, 7, 4, 5, 8, 5, 3, 6, 3, 9, 3};
    Number[] series2Numbers60 = {4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9, 4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9, 4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9};
    Number[] series1Numbers = series1Numbers10;
    Number[] series2Numbers = series2Numbers10;

    private MyBarFormatter formatter1;

    private MyBarFormatter formatter2;

    private MyBarFormatter selectionFormatter;

    private TextLabelWidget selectionWidget;

    private Pair<Integer, XYSeries> selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_view_activity);
//        GraphView graph = (GraphView) findViewById(R.id.graph_view_graph1);
        /*Intent intent = getIntent();
        Bundle extras = null;
        ArrayList<QOSStatisticHelper> qshs = new ArrayList<>();
        ArrayList<Float> rtts = new ArrayList<>();
        if (intent != null)
            extras = intent.getExtras();
        if (extras != null) {
            try {
                JSONArray array = new JSONObject(extras.getString("qshs")).getJSONArray("array");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);
                    QOSStatisticHelper qsh = new QOSStatisticHelper().fromJSON(new JSONObject(json.getString("qsh")));
                    qshs.add(qsh);
                }
            } catch (JSONException e) {
                Log.i("error", e.getMessage());
            }
        }
        if (!qshs.isEmpty())
            for (QOSStatisticHelper qsh : qshs) {
                rtts.add(qsh.getValue());
            }
        else
            finish();

        DataPoint points[] = new DataPoint[rtts.size()];
        String names[] = new String[rtts.size() * 2];
        for (int j = 0; j < names.length; j++)
            names[j] = "";
        for (int i = 0; i < rtts.size(); i++) {
            points[i] = new DataPoint(2 * i + 1, rtts.get(i));
            names[2 * i] = qshs.get(i).getProvider();
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);

      int maior = (int) Math.round(series.getHighestValueY());
        int x = maior - (maior % 20) / 20;
        int limite = 20 * (x + 1);
        String values[] = new String[(limite / 20) + 1];
        int counter = 0;
        for (int i = 0; i < limite + 20; i += 20) {
            values[counter++] = (i + "");
        }

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(names);
        staticLabelsFormatter.setVerticalLabels(values);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb(63, 81, 181);
            }
        });

        Log.i("x123456: high", series.getHighestValueX() + "");
        Log.i("x123456: low", series.getLowestValueX() + "");*/

//        double xInterval = 1.0;
//        graph.getViewport().setXAxisBoundsManual(true);
//        graph.getViewport().setMinX(series.getLowestValueX() - (xInterval / 2.0));
//        graph.getViewport().setMaxX(series.getHighestValueX() + (xInterval / 2.0));
//
//        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
//        series.setDataWidth(0.8);
//        series.setSpacing(10);
//        series.setDrawValuesOnTop(true);
//        series.setValuesOnTopColor(Color.rgb(0, 0, 0));
//
//        graph.setTitle("RTT");
//        graph.addSeries(series);
//

        plot = (XYPlot) findViewById(R.id.plot);

        // create a couple arrays of y-values to plot:
//        final Number[] domainLabels = {1, 2, 3, 6, 7, 8, 9, 10, 13, 14};
//        Number[] series1Numbers = {0, 10, 3, 10, 3, 10, 3, 10, 3, 10, 3, 0};
//        Number[] series2Numbers = {1, 8, 2, 8, 3, 8, 4, 8, 5, 8};

        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
//        XYSeries series1 = new SimpleXYSeries(
//                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
//        XYSeries series2 = new SimpleXYSeries(
//                Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
//        BarFormatter series1Format = new BarFormatter(Color.RED, Color.WHITE);
//        plot.getGraphWidget().getGridBox().setPaddingLeft(PixelUtils.dpToPix(70));
//        plot.getGraphWidget().getGridBox().setPaddingRight(PixelUtils.dpToPix(70));
//        MyBarRenderer renderer = ((MyBarRenderer) plot.getRenderer(MyBarRenderer.class));
//        renderer.setBarWidthStyle(BarRenderer.BarWidthStyle.FIXED_WIDTH);
//        renderer.setBarWidth(20);
//        BarRenderer renderer = plot.getRenderer(BarRenderer.class);
//        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, 10);
//        BarFormatter series2Format = new BarFormatter(Color.BLUE, Color.WHITE);
//        LineAndPointFormatter series1Format =
//                new LineAndPointFormatter(Color.YELLOW, Color.WHITE, Color.TRANSPARENT, null);
//
//        LineAndPointFormatter series2Format =
//                new LineAndPointFormatter(Color.RED, Color.GREEN, Color.TRANSPARENT, null);

//        series1Format.getLinePaint().setPathEffect(new DashPathEffect(new float[]{

        // always use DP when specifying pixel sizes, to keep things consistent across devices:
//                PixelUtils.dpToPix(20),
//                PixelUtils.dpToPix(15)}, 100));

        // add an "dash" effect to the series2 line:
//        series2Format.getLinePaint().setPathEffect(new DashPathEffect(new float[]{

        // always use DP when specifying pixel sizes, to keep things consistent across devices:
//                PixelUtils.dpToPix(20),
//                PixelUtils.dpToPix(15)}, 100));

        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
//        series1Format.setInterpolationParams(
//                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

//        series2Format.setInterpolationParams(
//                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        // add a new series' to the xyplot:
//        plot.getGraph().getDomainGridLinePaint().setColor(Color.TRANSPARENT);
//        plot.getGraph().getRangeSubGridLinePaint().setColor(Color.TRANSPARENT);
//        plot.getGraph().getRangeGridLinePaint().setColor(Color.LTGRAY);
//        plot.getGraph().getDomainSubGridLinePaint().setColor(Color.TRANSPARENT);
//        plot.getGraph().getGridBackgroundPaint().setColor(Color.WHITE);
//
//        plot.addSeries(series1, series1Format);
//        plot.addSeries(series2, series2Format);

//        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.TOP).setFormat(new Format() {
//            @Override
//            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
//                int i = Math.round(((Number) obj).floatValue());
//                return toAppendTo.append(domainLabels[i]);
//            }
//
//            @Override
//            public Object parseObject(String source, ParsePosition pos) {
//                return null;
//            }
//        });
        series1 = new SimpleXYSeries(Arrays.asList(series1Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Us");
        series2 = new SimpleXYSeries(Arrays.asList(series2Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Them");

        formatter1 = new MyBarFormatter(Color.rgb(100, 150, 100), Color.LTGRAY);
        formatter1.setMarginLeft(PixelUtils.dpToPix(1));
        formatter1.setMarginRight(PixelUtils.dpToPix(1));
        formatter2 = new MyBarFormatter(Color.rgb(100, 100, 150), Color.LTGRAY);
        formatter2.setMarginLeft(PixelUtils.dpToPix(1));
        formatter2.setMarginRight(PixelUtils.dpToPix(1));
        selectionFormatter = new MyBarFormatter(Color.YELLOW, Color.WHITE);

        selectionWidget = new TextLabelWidget(plot.getLayoutManager(), NO_SELECTION_TXT,
                new Size(
                        PixelUtils.dpToPix(100), SizeMode.ABSOLUTE,
                        PixelUtils.dpToPix(100), SizeMode.ABSOLUTE),
                TextOrientation.HORIZONTAL);

        selectionWidget.getLabelPaint().setTextSize(PixelUtils.dpToPix(16));

        // add a dark, semi-transparent background to the selection label widget:
        Paint p = new Paint();
        p.setARGB(100, 0, 0, 0);
        selectionWidget.setBackgroundPaint(p);

        selectionWidget.position(
                0, HorizontalPositioning.RELATIVE_TO_CENTER,
                PixelUtils.dpToPix(45), VerticalPositioning.ABSOLUTE_FROM_TOP,
                Anchor.TOP_MIDDLE);
        selectionWidget.pack();

        // reduce the number of range labels
        plot.setLinesPerRangeLabel(3);
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);

        plot.setLinesPerDomainLabel(2);

        // setup checkbox listers:
//        series1CheckBox = (CheckBox) findViewById(R.id.s1CheckBox);
//        series1CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                onS1CheckBoxClicked(b);
//            }
//        });
//
//        series2CheckBox = (CheckBox) findViewById(R.id.s2CheckBox);
//        series2CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                onS2CheckBoxClicked(b);
//            }
//        });

        plot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    onPlotClicked(new PointF(motionEvent.getX(), motionEvent.getY()));
                }
                return true;
            }
        });

//        spRenderStyle = (Spinner) findViewById(R.id.spRenderStyle);
//        ArrayAdapter<BarRenderer.BarOrientation> adapter = new ArrayAdapter<BarRenderer.BarOrientation>(this,
//                android.R.layout.simple_spinner_item, BarRenderer.BarOrientation
//                .values());
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spRenderStyle.setAdapter(adapter);
//        spRenderStyle.setSelection(BarRenderer.BarOrientation.OVERLAID.ordinal());
//        spRenderStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                updatePlot();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//        });
//
//        spWidthStyle = (Spinner) findViewById(R.id.spWidthStyle);
//        ArrayAdapter<BarRenderer.BarGroupWidthMode> adapter1 = new ArrayAdapter<BarRenderer.BarGroupWidthMode>(
//                this, android.R.layout.simple_spinner_item, BarRenderer.BarGroupWidthMode
//                .values());
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spWidthStyle.setAdapter(adapter1);
//        spWidthStyle.setSelection(BarRenderer.BarGroupWidthMode.FIXED_WIDTH.ordinal());
//        spWidthStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                if (BarRenderer.BarGroupWidthMode.FIXED_WIDTH.equals(spWidthStyle.getSelectedItem())) {
//                    sbFixedWidth.setVisibility(View.VISIBLE);
//                    sbVariableWidth.setVisibility(View.INVISIBLE);
//                } else {
//                    sbFixedWidth.setVisibility(View.INVISIBLE);
//                    sbVariableWidth.setVisibility(View.VISIBLE);
//                }
//                updatePlot();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//        });
//
//        spSeriesSize = (Spinner) findViewById(R.id.spSeriesSize);
//        ArrayAdapter<SeriesSize> adapter11 = new ArrayAdapter<SeriesSize>(this,
//                android.R.layout.simple_spinner_item, SeriesSize.values());
//        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spSeriesSize.setAdapter(adapter11);
//        spSeriesSize.setSelection(SeriesSize.TEN.ordinal());
//        spSeriesSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                final SeriesSize selectedSize = (SeriesSize) arg0.getSelectedItem();
//                switch (selectedSize) {
//                    case TEN:
//                        series1Numbers = series1Numbers10;
//                        series2Numbers = series2Numbers10;
//                        break;
//                    case TWENTY:
//                        series1Numbers = series1Numbers20;
//                        series2Numbers = series2Numbers20;
//                        break;
//                    case SIXTY:
//                        series1Numbers = series1Numbers60;
//                        series2Numbers = series2Numbers60;
//                        break;
//                    default:
//                        break;
//                }
//                updatePlot(selectedSize);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//        });
//
//        sbFixedWidth = (SeekBar) findViewById(R.id.sbFixed);
//        sbFixedWidth.setProgress(50);
//        sbFixedWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                updatePlot();
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
//
//        sbVariableWidth = (SeekBar) findViewById(R.id.sbVariable);
//        sbVariableWidth.setProgress(1);
//        sbVariableWidth.setVisibility(View.INVISIBLE);
//        sbVariableWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                updatePlot();
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
        plot.addSeries(series1, formatter1);
        plot.addSeries(series2, formatter2);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new NumberFormat() {
                    @Override
                    public StringBuffer format(double value, StringBuffer buffer,
                                               FieldPosition field) {
                        int year = (int) (value + 0.5d) / 12;
                        int month = (int) ((value + 0.5d) % 12);
                        return new StringBuffer(DateFormatSymbols.getInstance()
                                .getShortMonths()[month] + " '0" + year);
                    }

                    @Override
                    public StringBuffer format(long value, StringBuffer buffer,
                                               FieldPosition field) {
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
        updatePlot(null);
    }

    private void updatePlot(SeriesSize seriesSize) {

        // Remove all current series from each plot
        plot.clear();

        // Setup our Series with the selected number of elements
        series1 = new SimpleXYSeries(Arrays.asList(series1Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Us");
        series2 = new SimpleXYSeries(Arrays.asList(series2Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Them");

        plot.setDomainBoundaries(-1, series1.size(), BoundaryMode.FIXED);
        plot.setRangeUpperBoundary(
                SeriesUtils.minMax(series1, series2).
                        getMaxY().doubleValue() + 1, BoundaryMode.FIXED);

        if (seriesSize != null) {
            switch (seriesSize) {
                case TEN:
                    plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 2);
                    break;
                case TWENTY:
                    plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 4);
                    break;
                case SIXTY:
                    plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 6);
                    break;
            }
        }

        // add a new series' to the xyplot:
//        if (series1CheckBox.isChecked()) plot.addSeries(series1, formatter1);
//        if (series2CheckBox.isChecked()) plot.addSeries(series2, formatter2);

        // Setup the BarRenderer with our selected options
        MyBarRenderer renderer = plot.getRenderer(MyBarRenderer.class);
//        renderer.setBarOrientation((BarRenderer.BarOrientation.OVERLAID));
        final BarRenderer.BarGroupWidthMode barGroupWidthMode
                = (BarRenderer.BarGroupWidthMode.FIXED_WIDTH);
//        renderer.setBarGroupWidth(barGroupWidthMode,
//                barGroupWidthMode == BarRenderer.BarGroupWidthMode.FIXED_WIDTH
//                        ? sbFixedWidth.getProgress() : sbVariableWidth.getProgress());

        //renderer.setBarGroupWidth(barGroupWidthMode, 70);
//        if (BarRenderer.BarOrientation.STACKED.equals(spRenderStyle.getSelectedItem())) {
        plot.getInnerLimits().setMaxY(15);
//        } else {
//            plot.getInnerLimits().setMaxY(0);
//        }

        plot.redraw();

    }

    private void onPlotClicked(PointF point) {

        // make sure the point lies within the graph area.  we use gridrect
        // because it accounts for margins and padding as well.
        if (plot.containsPoint(point.x, point.y)) {
            Number x = plot.getXVal(point);
            Number y = plot.getYVal(point);

            selection = null;
            double xDistance = 0;
            double yDistance = 0;

            // find the closest value to the selection:
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
            // if the press was outside the graph area, deselect:
            selection = null;
        }

        if (selection == null) {
            selectionWidget.setText(NO_SELECTION_TXT);
        } else {
            selectionWidget.setText("Selected: " + selection.second.getTitle() +
                    " Value: " + selection.second.getY(selection.first));
        }
        plot.redraw();
    }

    private void onS1CheckBoxClicked(boolean checked) {
        if (checked) {
            plot.addSeries(series1, formatter1);
        } else {
            plot.removeSeries(series1);
        }
        plot.redraw();
    }

    private void onS2CheckBoxClicked(boolean checked) {
        if (checked) {
            plot.addSeries(series2, formatter2);
        } else {
            plot.removeSeries(series2);
        }
        plot.redraw();
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

        /**
         * Implementing this method to allow us to inject our
         * special selection getFormatter.
         *
         * @param index  index of the point being rendered.
         * @param series XYSeries to which the point being rendered belongs.
         * @return
         */
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*private class MyBarFormatter extends BarFormatter {
        public MyBarFormatter(int fillColor, int borderColor) {
            super(fillColor, borderColor);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MyBarRenderer.class;
        }

        @Override
        public SeriesRenderer getRendererInstance(XYPlot plot) {
            return new MyBarRenderer(plot);
        }
    }

    class MyBarRenderer extends BarRenderer<MyBarFormatter> {

        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }

        public MyBarFormatter getFormatter(int index, XYSeries series) {
            return getFormatter(series);
        }
    }*/

}