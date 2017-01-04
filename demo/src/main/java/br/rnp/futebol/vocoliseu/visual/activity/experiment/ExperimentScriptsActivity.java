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
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.exoplayer2.demo.R;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.rnp.futebol.vocoliseu.dao.TExpForListDAO;
import br.rnp.futebol.vocoliseu.dao.TScriptDAO;
import br.rnp.futebol.vocoliseu.pojo.TExperiment;
import br.rnp.futebol.vocoliseu.pojo.TScript;
import br.rnp.futebol.vocoliseu.util.adapter.ScriptAdapter;
import br.rnp.futebol.vocoliseu.visual.activity.MainActivity;

/**
 * An activity for selecting from a list of samples.
 */
public class ExperimentScriptsActivity extends AppCompatActivity {

    private static final String TAG = "ExperimentMetrics";
    private ListView lvScripts;
    private Toolbar toolbar;
    private ScriptAdapter adapter;
    private TExperiment experiment;
    private ImageButton ibFinal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_scripts_activity);
        init();
        lvScripts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (experiment.getScripts() == null)
                    experiment.setScripts(new ArrayList<TScript>());
                List<TScript> scripts = new TScriptDAO(getBaseContext()).getScripts();
                if (experiment.getScripts().isEmpty())
                    experiment.getScripts().add(scripts.get(position));
                else {
                    boolean aux = false;
                    int cont = 0, pos = -1;
                    for (TScript s : experiment.getScripts()) {
                        if (compareScripts(s, scripts.get(position))) {
                            pos = cont;
                            aux = true;
                        }
                        cont++;
                    }
                    if (!aux)
                        experiment.getScripts().add(scripts.get(position));
                    else
                        experiment.getScripts().remove(pos);
                }
                refreshList();
            }
        });
        ibFinal.setOnClickListener(new View.OnClickListener() {
            String msg;

            @Override
            public void onClick(View v) {
                if (experiment.getScripts() != null && !experiment.getScripts().isEmpty()) {
                    write(experiment.getFilename(), experiment.toJson().toString());
                    TExpForListDAO dao = new TExpForListDAO(getBaseContext());
                    dao.insert(experiment);
                    dao.close();
                    msg = "Experiment configured!";
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else
                    msg = "At least one video should be selected";
                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void write(String file, String msg) {
        try {
            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/".concat(file.concat(".txt"));
            BufferedWriter output;
            output = new BufferedWriter(new FileWriter(csv, true));
            output.append(msg);
            output.newLine();
            output.close();
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void init() {
        lvScripts = (ListView) findViewById(R.id.lv_exp_scripts_ac);
        ibFinal = (ImageButton) findViewById(R.id.ib_exp_final);
        toolbar = (Toolbar) findViewById(R.id.tb_exp_scripts_ac);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("New experiment");
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleAppearance);
        toolbar.setSubtitle("Select which videos will be displayed");
        toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleAppearance);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void refreshList() {
        List<TScript> aux = null;
        List<TScript> scripts = new TScriptDAO(this).getScripts();
        if (scripts != null) {
            aux = scripts;
            if (experiment.getScripts() != null)
                if (!experiment.getScripts().isEmpty()) {
                    for (TScript s : experiment.getScripts()) {
                        for (TScript sc : aux) {
                            if (compareScripts(s, sc))
                                sc.setUsedAux(true);
                        }
                    }
                }
        }
        if (aux != null && !aux.isEmpty()) {
            adapter = new ScriptAdapter(this, aux);
            lvScripts.setAdapter(adapter);
        }
    }

    private boolean compareScripts(TScript sc1, TScript sc2) {
        return (sc1.getAddress().compareTo(sc2.getAddress()) == 0
                && sc1.getVideo().compareTo(sc2.getVideo()) == 0
                && sc1.getExtension().compareTo(sc2.getExtension()) == 0);
    }

//    public static TExperiment getExperiment() {
//        return experiment;
//    }

//    public static void setExperiment(TExperiment experiment) {
//        this.experiment = experiment;
//    }

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
