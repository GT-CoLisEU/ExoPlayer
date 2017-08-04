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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.exoplayer2.demo.R;

import java.util.ArrayList;
import java.util.List;

import br.rnp.futebol.verona.pojo.BinaryQuestion;
import br.rnp.futebol.verona.pojo.Metric;
import br.rnp.futebol.verona.pojo.TScript;
import br.rnp.futebol.verona.util.ReadyMetrics;
import br.rnp.futebol.verona.util.adapter.MetricAdapter;
import br.rnp.futebol.verona.visual.activity.unused.ScriptControllerActivity;

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
    private BinaryQuestion question = null;
    boolean deleted;

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
        boolean triedToDelete = false;
        if (ReadyMetrics.S_QOE_METRICS.get(position).getId() == ReadyMetrics.BINARY_QUESTION_ID)
            if (!metricsIds.contains(ReadyMetrics.BINARY_QUESTION_ID))
                insertBinaryQuestion();
            else {
                deleteBinaryQuestion(metricsIds, position);
                triedToDelete = true;
            }
        if ((!triedToDelete) || (deleted))
            operateAux(metricsIds, position);

    }

    private void operateAux(ArrayList<Integer> metricsIds, int position) {
        if (metricsIds.contains(metrics.get(position).getId())) {
            for (int i = 0; i < metricsIds.size(); i++)
                if (metricsIds.get(i) == metrics.get(position).getId())
                    metricsIds.remove(i);
        } else
            metricsIds.add(metrics.get(position).getId());
        refreshList();
    }

    private boolean deleteBinaryQuestion(final ArrayList<Integer> metricsIds, final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        deleted = false;
        alert.setTitle("Be careful!");
        alert.setMessage("Unchecking this option will delete the previous inserted question. Are you sure?");
        alert.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                question = null;
                script.setQuestion(null);
                deleted = true;
                dialog.dismiss();
                operateAux(metricsIds, position);
            }
        });
        alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
        return deleted;
    }

    private void insertBinaryQuestion() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.binary_question_form, null);
        final EditText etQuestion = (EditText) view.findViewById(R.id.et_binary_question);
        final EditText etAnswer1 = (EditText) view.findViewById(R.id.et_binary_answer_1);
        final EditText etAnswer2 = (EditText) view.findViewById(R.id.et_binary_answer_2);

        alert.setView(view);
        alert.setTitle("Type your question and answers");
        alert.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                question = new BinaryQuestion();
                question.setQuestion(etQuestion.getText().toString());
                question.setAnswer1(etAnswer1.getText().toString());
                question.setAnswer2(etAnswer2.getText().toString());
                script.setQuestion(question);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();

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
