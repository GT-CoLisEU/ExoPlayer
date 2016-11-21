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
package br.rnp.futebol.vocoliseu.visual.fragment;

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

import br.rnp.futebol.vocoliseu.pojo.Metric;
import br.rnp.futebol.vocoliseu.pojo.TExperiment;
import br.rnp.futebol.vocoliseu.pojo.TScript;
import br.rnp.futebol.vocoliseu.util.ReadyMetrics;
import br.rnp.futebol.vocoliseu.util.adapter.MetricAdapter;
import br.rnp.futebol.vocoliseu.visual.activity.ExperimentControllerActivity;
import br.rnp.futebol.vocoliseu.visual.activity.ScriptControllerActivity;

/**
 * An activity for selecting from a list of samples.
 */
public class ScriptMetricsFragment extends Fragment {

    private static final String TAG = "ExperimentMetrics";
    private View view;
    private ListView lvMetrics;
    private List<Metric> metrics;
    private MetricAdapter adapter;
    private TScript script;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.script_metrics_fragment, container, false);
        init();
        lvMetrics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (script.getSubjectiveQoeMetrics() == null)
                    script.setSubjectiveQoeMetrics(new ArrayList<Integer>());
                operateMetricsList(script.getSubjectiveQoeMetrics(), position);
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
        script = ((ScriptControllerActivity) getActivity()).getScript();
        lvMetrics = (ListView) view.findViewById(R.id.lv_script_metrics);
        metrics = new ArrayList<>();
    }

    private void refreshList() {
        ReadyMetrics.init();
        metrics.clear();
        ArrayList<Integer> ids = new ArrayList<>();
        if (script.getSubjectiveQoeMetrics() != null)
            ids.addAll(script.getSubjectiveQoeMetrics());
        if (ReadyMetrics.S_QOE_METRICS != null)
            metrics.addAll(ReadyMetrics.S_QOE_METRICS);
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
