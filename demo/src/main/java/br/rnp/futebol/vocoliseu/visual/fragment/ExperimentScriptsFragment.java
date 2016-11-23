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

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.exoplayer2.demo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.rnp.futebol.vocoliseu.dao.TScriptDAO;
import br.rnp.futebol.vocoliseu.pojo.Metric;
import br.rnp.futebol.vocoliseu.pojo.Script;
import br.rnp.futebol.vocoliseu.pojo.TExperiment;
import br.rnp.futebol.vocoliseu.pojo.TScript;
import br.rnp.futebol.vocoliseu.util.adapter.MetricAdapter;
import br.rnp.futebol.vocoliseu.util.adapter.ScriptAdapter;
import br.rnp.futebol.vocoliseu.visual.activity.ExperimentConfigurationControllerActivity;
import br.rnp.futebol.vocoliseu.visual.activity.ExperimentControllerActivity;
import br.rnp.futebol.vocoliseu.visual.activity.ScriptControllerActivity;

/**
 * An activity for selecting from a list of samples.
 */
public class ExperimentScriptsFragment extends Fragment {

    private static final String TAG = "ExperimentMetrics";
    private View view;
    private ListView lvScripts;
    private ScriptAdapter adapter;
    private TExperiment experiment;
//    private ImageButton ibNewScript;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.experiment_scripts_fragment, container, false);
        init();
        lvScripts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (experiment.getScripts() == null)
                    experiment.setScripts(new ArrayList<TScript>());
                List<TScript> scripts = new TScriptDAO(getContext()).getScripts();
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
//        ibNewScript.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity().getBaseContext(), ScriptControllerActivity.class);
//                Bundle extras = new Bundle();
//                extras.putSerializable("experiment", experiment);
//                intent.putExtras(extras);
//                startActivity(intent);
//            }
//        });
        return view;
    }

    private void init() {
        experiment = ((ExperimentControllerActivity) getActivity()).getExperiment();
        lvScripts = (ListView) view.findViewById(R.id.lv_exp_scripts);
//        ibNewScript = (ImageButton) view.findViewById(R.id.ib_new_script);
    }

    private void refreshList() {
        List<TScript> aux = null;
        List<TScript> scripts = new TScriptDAO(getContext()).getScripts();
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
            adapter = new ScriptAdapter(getContext(), aux);
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
        refreshList();
    }


}
