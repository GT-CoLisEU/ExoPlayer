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

import com.google.android.exoplayer2.demo.R;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import br.rnp.futebol.vocoliseu.dao.TScriptDAO;
import br.rnp.futebol.vocoliseu.pojo.Experiment;
import br.rnp.futebol.vocoliseu.pojo.TExperiment;
import br.rnp.futebol.vocoliseu.pojo.TScript;
import br.rnp.futebol.vocoliseu.util.ReadyMetrics;
import br.rnp.futebol.vocoliseu.util.adapter.ExpFragmentAdapter;
import br.rnp.futebol.vocoliseu.util.adapter.ScriptFragmentAdapter;
import br.rnp.futebol.vocoliseu.visual.fragment.ExperimentScriptsFragment;
public class ScriptControllerActivity extends AppCompatActivity {

    private TScript script;
//    private TExperiment experiment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.script_fragment_controller);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_script);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tl_script);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_script);

//        if (getIntent() != null)
//            if (getIntent().getExtras() != null)
//                experiment = (TExperiment) getIntent().getExtras().getSerializable("experiment");
        script = new TScript();
//        experiment = ExperimentScriptsFragment.getExperiment();
        setSupportActionBar(toolbar);

        tabLayout.addTab(tabLayout.newTab().setText("VIDEO"));
        tabLayout.addTab(tabLayout.newTab().setText("METRICS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ScriptFragmentAdapter adapter = new ScriptFragmentAdapter(getSupportFragmentManager());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.experiment_menu, menu);
        return true;
    }

    private boolean isFieldOk(String field) {
        return (field != null && !field.trim().equals(""));
    }

    private boolean areFieldsOk() {
        return ((isFieldOk(script.getAddress())) && (isFieldOk(script.getVideo())) &&
                (isFieldOk(script.getExtension())));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (areFieldsOk()) {
//                if (experiment.getScripts() == null)
//                    experiment.setScripts(new ArrayList<TScript>());
//                experiment.getScripts().add(script);
                TScriptDAO dao = new TScriptDAO(getBaseContext());
                dao.insert(script);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public TScript getScript() {
        return script;
    }

    public void setScript(TScript script) {
        this.script = script;
    }
}

