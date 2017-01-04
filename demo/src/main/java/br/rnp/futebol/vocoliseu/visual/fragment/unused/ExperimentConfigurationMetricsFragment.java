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
package br.rnp.futebol.vocoliseu.visual.fragment.unused;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import br.rnp.futebol.vocoliseu.pojo.unused.Script;
import br.rnp.futebol.vocoliseu.pojo.Metric;
import br.rnp.futebol.vocoliseu.util.adapter.MetricAdapter;
import com.google.android.exoplayer2.demo.R;

import br.rnp.futebol.vocoliseu.visual.activity.unused.ExperimentConfigurationControllerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * An activity for selecting from a list of samples.
 */
public class ExperimentConfigurationMetricsFragment extends Fragment {

    private static final String TAG = "ExperimentConfiguration";
    private View view;
    private Script script;
    private ExperimentConfigurationControllerActivity activity;
    private ListView lvMetrics;
    private List<Metric> metrics;
    private MetricAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.f_experiment_configuration_metrics, container, false);
        lvMetrics = (ListView) view.findViewById(R.id.lv_metrics);
        activity = (ExperimentConfigurationControllerActivity) getActivity();
        script = activity.getScript();
        refreshList();
//        lvMetrics.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvMetrics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(), metrics.get(position).getName(), Toast.LENGTH_SHORT).show();
                script.getMetrics().get(position).setUsed(!script.getMetrics().get(position).isUsed());
                refreshList();
            }
        });
//        lvMetrics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(), metrics.get(position).getName(), Toast.LENGTH_SHORT).show();
//                metrics.get(position).setUsed(!metrics.get(position).isUsed());
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
        return view;
    }

    private void refreshList() {
        metrics = script.getMetrics();
        if (metrics != null) {
            adapter = new MetricAdapter(getContext(), metrics);
            lvMetrics.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //    private void checkPerm() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            }
//        }
//    }

    private String prepareMsg(String name, String info) {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("info", info);
            return json.toString();
        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
            return "";
        }
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

    private String read(String file) {
        try {
            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/".concat(file.concat(".txt"));
            BufferedReader reader = new BufferedReader(new FileReader(csv));
            String text = "", line;
            while ((line = reader.readLine()) != null) {
                text += line.concat(" ");
            }
            return text;
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            return "";
        }
    }


}
