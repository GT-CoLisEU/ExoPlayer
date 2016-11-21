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
package br.rnp.futebol.vocoliseu.visual.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.exoplayer2.demo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import br.rnp.futebol.vocoliseu.dao.ScriptDAO;
import br.rnp.futebol.vocoliseu.dao.TExpForListDAO;
import br.rnp.futebol.vocoliseu.pojo.Metric;
import br.rnp.futebol.vocoliseu.pojo.Script;
import br.rnp.futebol.vocoliseu.pojo.TExperiment;
import br.rnp.futebol.vocoliseu.util.ReadyMetrics;
import br.rnp.futebol.vocoliseu.util.adapter.ExpFragmentAdapter;
import br.rnp.futebol.vocoliseu.util.adapter.FragmentAdapter;

public class ExperimentControllerActivity extends AppCompatActivity {

    private static final String TAG = "ExperimentController";
    private TExperiment experiment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_fragment_controller);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_experiment);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tl_experiment);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_experiment);

        experiment = new TExperiment();
//        ArrayList<Metric> metrics = new ArrayList<>();

//        ReadyMetrics.init();
//        metrics.addAll(ReadyMetrics.QOS_METRICS);
//        metrics.addAll(ReadyMetrics.S_QOE_METRICS);
//        metrics.addAll(ReadyMetrics.O_QOE_METRICS);

        setSupportActionBar(toolbar);
//        toolbar.setLogo(R.drawable.ic_temp_icon_white_2);

        tabLayout.addTab(tabLayout.newTab().setText("EXPERIMENT"));
        tabLayout.addTab(tabLayout.newTab().setText("METRICS"));
        tabLayout.addTab(tabLayout.newTab().setText("VIDEOS"));
//        tabLayout.addTab(tabLayout.newTab().setText("Metrics"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ExpFragmentAdapter adapter = new ExpFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.experiment_menu, menu);
        return true;
    }

    private boolean isFieldOk(String field) {
        return (field != null && !field.trim().equals(""));
    }

    private boolean areFieldsOk() {
        return ((isFieldOk(experiment.getName())) && (isFieldOk(experiment.getFilename())));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (areFieldsOk()) {
                write(experiment.getFilename(), experiment.toJson().toString());
                TExpForListDAO dao = new TExpForListDAO(getBaseContext());
                dao.insert(experiment);
                dao.close();
                Toast.makeText(getBaseContext(), "Script saved as " + experiment.getFilename() + ".txt", Toast.LENGTH_SHORT).show();
//                Log.i(TAG, read(script.getFileName()));
//                new ScriptDAO(getBaseContext()).insert(script);
                finish();
//                try {
//                    Script exp = new Script().fromJson(new JSONObject(read(script.getFileName())));
//                } catch (JSONException e) {
//                }
            } else {
                Toast.makeText(getBaseContext(), "Name and filename cannot be empty", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public TExperiment getExperiment() {
        return experiment;
    }

    public void setExperiment(TExperiment experiment) {
        this.experiment = experiment;
    }
}

