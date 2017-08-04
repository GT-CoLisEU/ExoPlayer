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
package br.rnp.futebol.verona.visual.activity.unused;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.exoplayer2.demo.R;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.rnp.futebol.verona.pojo.BinaryQuestion;
import br.rnp.futebol.verona.pojo.Metric;
import br.rnp.futebol.verona.pojo.TScript;
import br.rnp.futebol.verona.util.ReadyMetrics;
import br.rnp.futebol.verona.util.adapter.MetricAdapter;

/**
 * An activity for selecting from a list of samples.
 */
public class ScriptMetricsActivity extends AppCompatActivity {

    //    private static final String TAG = "ExperimentMetrics";
//    private View view;
    private ListView lvMetrics;
    private List<Metric> metrics;
    private TScript script;
    private int pos;
    private ImageButton ibSave;
    private BinaryQuestion question = null;
    private EditText etRep;
    private boolean deleted, ok = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subjective_metric_selection_popup);
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

        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!exists(script.getProvider()))
                    makeContinueDialog();
                if (ok) {
                    int rep = (etRep.getText() != null && !etRep.getText().toString().trim().equals("")) ?
                            Integer.parseInt(etRep.getText().toString()) : 1;
                    script.setLoop(rep);
                    Intent intent = new Intent();
                    intent.putExtra("modScript", script);
                    intent.putExtra("pos", pos);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }



    private void operateMetricsList(ArrayList<Integer> metricsIds, int position) {
        boolean triedToDelete = false;
        if (ReadyMetrics.S_QOE_METRICS.get(position).getId() == ReadyMetrics.BINARY_QUESTION_ID)
            if (!metricsIds.contains(ReadyMetrics.BINARY_QUESTION_ID))
                insertBinaryQuestion(metricsIds, position);
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

    private boolean exists(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void makeContinueDialog() {
        ok = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        String text = "The file ".concat(script.getVideo()).concat(script.getExtension()).concat(" in the address ").concat(script.getAddress()).concat(" was not found. Contine anyway?");
        builder.setMessage(text);
        builder.setCancelable(false);
        builder.setTitle("Warning");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ok = true;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean deleteBinaryQuestion(final ArrayList<Integer> metricsIds, final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
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

    private void insertBinaryQuestion(final ArrayList<Integer> metricsIds, final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.binary_question_form, null);
        final EditText etQuestion = (EditText) view.findViewById(R.id.et_binary_question);
        final EditText etAnswer1 = (EditText) view.findViewById(R.id.et_binary_answer_1);
        final EditText etAnswer2 = (EditText) view.findViewById(R.id.et_binary_answer_2);

        alert.setView(view);
        alert.setCancelable(false);
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
                operateAux(metricsIds, position);
            }
        });
        alert.show();

    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_script_metrics_popup);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("New video");
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
        etRep = (EditText) findViewById(R.id.et_popup_rep);
        lvMetrics = (ListView) findViewById(R.id.sm_popup_list);
        ibSave = (ImageButton) findViewById(R.id.ib_script_final_popup);
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
            MetricAdapter adapter = new MetricAdapter(this, metrics, false);
            lvMetrics.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle extras = null;
        if (getIntent() != null)
            extras = getIntent().getExtras();
        script = (extras != null) ? (TScript) extras.getSerializable("script") : null;
        pos = (extras != null) ? extras.getInt("pos") : -1;
        if (script == null)
            finish();
        else
            refreshList();
    }

}
