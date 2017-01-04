/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.rnp.futebol.vocoliseu.visual.activity.experiment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.exoplayer2.demo.R;

import java.util.ArrayList;
import java.util.List;

import br.rnp.futebol.vocoliseu.pojo.Metric;
import br.rnp.futebol.vocoliseu.pojo.TExperiment;
import br.rnp.futebol.vocoliseu.util.ReadyMetrics;
import br.rnp.futebol.vocoliseu.util.adapter.MetricAdapter;

/**
 * An activity for selecting from a list of samples.
 */
public class ExperimentMetricsActivity extends AppCompatActivity {

    private static final String TAG = "ExperimentMetrics";
    private ListView lvMetrics;
    private List<Metric> metrics;
    private MetricAdapter adapter;
    private ImageButton ibPt3;
    private TExperiment experiment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_metrics_activity);
        init();
        lvMetrics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (metrics.get(position).getType() == Metric.O_QOE) {
                    if (experiment.getObjectiveQoeMetrics() == null)
                        experiment.setObjectiveQoeMetrics(new ArrayList<Integer>());
                    operateMetricsList(experiment.getObjectiveQoeMetrics(), position);
                } else {
                    if (experiment.getQosMetrics() == null)
                        experiment.setQosMetrics(new ArrayList<Integer>());
                    operateMetricsList(experiment.getQosMetrics(), position);
                }
                refreshList();
            }
        });
        ibPt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putSerializable("experiment", experiment);
                Intent intent = new Intent(getBaseContext(), ExperimentScriptsActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    private void operateMetricsList(ArrayList<Integer> metricsIds, int position) {
        if (metricsIds.contains(metrics.get(position).getId())) {
            for (int i = 0; i < metricsIds.size(); i++)
                if (metricsIds.get(i) == metrics.get(position).getId())
                    metricsIds.remove(i);
        } else
            metricsIds.add(metrics.get(position).getId());
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_exp_metrics_ac);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("New experiment");
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleAppearance);
        toolbar.setSubtitle("Select the metrics you want");
        toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleAppearance);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ibPt3 = (ImageButton) findViewById(R.id.ib_exp_pt3);
        lvMetrics = (ListView) findViewById(R.id.lv_exp_metrics_ac);
        metrics = new ArrayList<>();
    }

    private void refreshList() {
        ReadyMetrics.init();
        ArrayList<Integer> ids = new ArrayList<>();
        metrics.clear();
        if (experiment.getObjectiveQoeMetrics() != null)
            ids.addAll(experiment.getObjectiveQoeMetrics());
        if (experiment.getQosMetrics() != null)
            ids.addAll(experiment.getQosMetrics());
        if (ReadyMetrics.O_QOE_METRICS != null)
            metrics.addAll(ReadyMetrics.O_QOE_METRICS);
        if (ReadyMetrics.QOS_METRICS != null)
            metrics.addAll(ReadyMetrics.QOS_METRICS);
        if (!ids.isEmpty())
            for (Metric m : metrics)
                for (Integer id : ids)
                    if (id == m.getId())
                        m.setUsed(true);
        if (metrics != null) {
            adapter = new MetricAdapter(this, metrics);
            lvMetrics.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle extras = null;
        if (getIntent() != null)
            extras = getIntent().getExtras();
        experiment = (extras != null) ? (TExperiment) extras.getSerializable("experiment") : null;
        if (experiment == null)
            finish();
        else
            refreshList();
    }

}
