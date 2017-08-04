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
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.google.android.exoplayer2.demo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.rnp.futebol.verona.util.ChartHelper;
import br.rnp.futebol.verona.util.QOSStatisticHelper;
import br.rnp.futebol.verona.util.adapter.LegendAdapter;

public class QoSChartsActivity extends AppCompatActivity {

    private BarChart chartRTT;
    private HorizontalBarChart chartLoss;
    private ListView lvLegends;
    private TextView tvRTT, tvLoss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_view_activity);
        init();

        ArrayList<QOSStatisticHelper> qshs = getData();

        ChartHelper helper = new ChartHelper();
        ArrayList<Float> rtts = new ArrayList<>();
        ArrayList<Float> pckLosses = new ArrayList<>();
        ArrayList<String> labelsList = new ArrayList<>();

        for (QOSStatisticHelper s : qshs) {
            rtts.add(s.getValue());
            pckLosses.add(s.getLoss());
            labelsList.add(s.getProvider());
        }

        chartRTT.setData(helper.barPlot(rtts, 0.9F, ChartHelper.VERONA_COLORS, "Provider"));
        chartLoss.setData(helper.barPlot(pckLosses, 0.9F, ChartHelper.VERONA_COLORS, "Provider"));

        customText(tvRTT, "RTT", "Milliseconds per Provider");
        customText(tvLoss, "Packet Loss", "% per Provider");

        helper.barChartDefaultConfig(chartRTT);
        helper.barChartDefaultConfig(chartLoss, 0F, 100F);

        LegendAdapter adapter = new LegendAdapter(getBaseContext(), labelsList, getColors(ChartHelper.VERONA_COLORS));
        lvLegends.setAdapter(adapter);
    }

    private void customText(TextView tv, String title, String description) {
        String desc = "\n".concat(description);
        final SpannableStringBuilder sb = new SpannableStringBuilder(title.concat(desc));
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
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
        chartRTT = (BarChart) findViewById(R.id.chart_rtt);
        chartLoss = (HorizontalBarChart) findViewById(R.id.chart_packetloss);
        lvLegends = (ListView) findViewById(R.id.lv_qos_legend);
        tvRTT = (TextView) findViewById(R.id.tv_qos_results_rtt);
        tvLoss = (TextView) findViewById(R.id.tv_qos_results_loss);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_qos_charts);
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

    private ArrayList<QOSStatisticHelper> getData() {

        ArrayList<QOSStatisticHelper> qshs = new ArrayList<>();
        Intent intent = getIntent();
        Bundle extras = null;
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

        return qshs;
    }

}