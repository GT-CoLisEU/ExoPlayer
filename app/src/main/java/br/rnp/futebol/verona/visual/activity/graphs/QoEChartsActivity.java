package br.rnp.futebol.verona.visual.activity.graphs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.google.android.exoplayer2.demo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.rnp.futebol.verona.util.ChartHelper;
import br.rnp.futebol.verona.util.QOEStatisticHelper;
import br.rnp.futebol.verona.util.adapter.LegendAdapter;

public class QoEChartsActivity extends AppCompatActivity {

    private BarChart chartRTT;
    private HorizontalBarChart chartLoss;
    private ListView lvLegends;
    private TextView tvRTT, tvLoss;
    private CheckBox showPst, showFrozenTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_view_activity_selectable);
        init();

        showPst.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                manageBoxes(b, showFrozenTime.isChecked(), true);
            }
        });

        showFrozenTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                manageBoxes(b, showPst.isChecked(), false);
            }
        });

        customText(tvLoss, "Packet Loss", "% per Provider");

        ChartHelper helper = populateChart(true, true);
        helper.barChartDefaultConfig(chartRTT);
        helper.barChartDefaultConfig(chartLoss, 0F, 100F);

        ArrayList<String> labelsList = new ArrayList<>();
        for (QOEStatisticHelper s : getData())
            labelsList.add(s.getProvider());
        LegendAdapter adapter = new LegendAdapter(getBaseContext(), labelsList, getColors(ChartHelper.VERONA_COLORS));
        lvLegends.setAdapter(adapter);
    }

    private ChartHelper populateChart(boolean showPst, boolean showFrzTime) {
        ArrayList<QOEStatisticHelper> qshs = getData();

        ChartHelper helper = new ChartHelper();
        ArrayList<Float> psts = new ArrayList<>();
        ArrayList<Float> frozen = new ArrayList<>();

        for (QOEStatisticHelper s : qshs) {
            psts.add((float) s.getPST());
            frozen.add((float) s.getFrzDuration());
            Log.i("INFODEBUG", "PST = " + s.getPST() + " / FRZ = " + s.getFrzDuration());
        }
        BarData barData;
        if (showPst && showFrzTime) {
            barData = helper.barPlot(psts, frozen, 0.3F, ChartHelper.VERONA_COLORS, "Playback Start Time", "Frozen Time");
            Log.i("INFODEBUG", "AMBOS");
        } else if (showPst) {
            barData = helper.barPlot(psts, 0.3F, ChartHelper.VERONA_COLORS, "Playback Start Time");
            Log.i("INFODEBUG", "PSTS");
        } else {
            barData = helper.barPlot(frozen, 0.3F, ChartHelper.VERONA_COLORS, "Frozen Time");
            Log.i("INFODEBUG", "FROZEN");
        }
        chartRTT.setData(barData);

        customText(tvRTT, "Playback Start Time | Frozen Time", "Milliseconds per Video");
        chartRTT.invalidate();
        return helper;
    }

    private void manageBoxes(boolean first, boolean second, boolean isPst) {
        if (!first && !second) {
            if (!isPst) showPst.setChecked(true);
            else showFrozenTime.setChecked(true);
            customText(tvRTT, !isPst ? "Playback Start Time" : "Frozen Time", "Milliseconds per Video");
            Log.i("INFODEBUG", "1");
        } else if (first)
            if (second) {
                customText(tvRTT, "Playback Start Time | Frozen Time", "Milliseconds per Video");
                Log.i("INFODEBUG", "2");
            } else {
                customText(tvRTT, isPst ? "Playback Start Time" : "Frozen Time", "Milliseconds per Video");
                Log.i("INFODEBUG", "3");
            }
        else {
            customText(tvRTT, !isPst ? "Playback Start Time" : "Frozen Time", "Milliseconds per Video");
            Log.i("INFODEBUG", "4");
        }
        populateChart(first, second);
    }


    private void customText(TextView tv, String title, String description) {
        String desc = "\n".concat(description);
        final SpannableStringBuilder sb = new SpannableStringBuilder(title.concat(desc));
        final StyleSpan bss = new StyleSpan(Typeface.BOLD);
        final StyleSpan iss = new StyleSpan(Typeface.NORMAL);
        sb.setSpan(bss, 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(iss, title.length(), title.length() + desc.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(new ForegroundColorSpan(Color.GRAY), title.length(), title.length() + desc.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tv.setText(sb);
    }


    private ArrayList<Integer> getColors(int[] colors) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int c : colors)
            list.add(c);
        return list;
    }

    private void init() {
        chartRTT = (BarChart) findViewById(R.id.chart_rtt_qos);
        chartLoss = (HorizontalBarChart) findViewById(R.id.chart_packetloss_qos);
        lvLegends = (ListView) findViewById(R.id.lv_qos_legend_qos);
        tvRTT = (TextView) findViewById(R.id.tv_qos_results_rtt_qos);
        tvLoss = (TextView) findViewById(R.id.tv_qos_results_loss_qos);
        showPst = (CheckBox) findViewById(R.id.cb_pst_selectable_qos);
        showPst.setChecked(true);
        showFrozenTime = (CheckBox) findViewById(R.id.cb_frozen_time_selectable_qos);
        showFrozenTime.setChecked(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_qos_charts_qos);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("QoS Charts");
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleAppearance);
        toolbar.setSubtitle("Analyzed by provider");
        toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleAppearance);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private ArrayList<QOEStatisticHelper> getData() {

        ArrayList<QOEStatisticHelper> qshs = new ArrayList<>();
        Intent intent = getIntent();
        Bundle extras = null;
        if (intent != null)
            extras = intent.getExtras();
        if (extras != null) {
            try {
                JSONArray array = new JSONObject(extras.getString("qshs")).getJSONArray("array");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);
                    QOEStatisticHelper qsh = new QOEStatisticHelper().fromJSON(new JSONObject(json.getString("qsh")));
                    qshs.add(qsh);
                }
            } catch (JSONException e) {
                Log.i("error", e.getMessage());
            }
        }

        return qshs;
    }

}