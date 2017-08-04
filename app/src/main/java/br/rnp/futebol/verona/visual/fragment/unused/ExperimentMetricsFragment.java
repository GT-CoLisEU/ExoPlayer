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
package br.rnp.futebol.verona.visual.fragment.unused;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.exoplayer2.demo.R;

import java.util.ArrayList;
import java.util.List;

import br.rnp.futebol.verona.pojo.Metric;
import br.rnp.futebol.verona.pojo.TExperiment;
import br.rnp.futebol.verona.util.ReadyMetrics;
import br.rnp.futebol.verona.util.adapter.MetricAdapter;
import br.rnp.futebol.verona.visual.activity.unused.ExperimentControllerActivity;

/**
 * An activity for selecting from a list of samples.
 */
public class ExperimentMetricsFragment extends Fragment {

    private static final String TAG = "ExperimentMetrics";
    private View view;
    private ListView lvMetrics;
    private List<Metric> metrics;
    private MetricAdapter adapter;
    private TExperiment experiment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.experiment_metrics_fragment, container, false);
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
        return view;
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
        experiment = ((ExperimentControllerActivity) getActivity()).getExperiment();
        lvMetrics = (ListView) view.findViewById(R.id.lv_exp_metrics);
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
            adapter = new MetricAdapter(getContext(), metrics);
            lvMetrics.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

}
