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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.exoplayer2.demo.R;

import br.rnp.futebol.vocoliseu.pojo.TExperiment;

public class ExperimentGeneralActivity extends AppCompatActivity {

    private static final String TAG = "ExperimentMetrics";
    private TExperiment experiment;
    private EditText etName, etFilename, etInstructions;
    private CheckBox cbAskInfo;
    private ImageButton ibPt2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_general_activity);
        init();
        ibPt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPart2();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle extras = null;
        if (getIntent() != null)
            extras = getIntent().getExtras();
        experiment = (extras != null) ? (TExperiment) extras.getSerializable("experiment") : null;
        if (experiment != null) {
            etFilename.setText(experiment.getFilename());
            etName.setText(experiment.getName());
            etInstructions.setText(experiment.getInstruction());
            cbAskInfo.setChecked(experiment.isAskInfo());
        } else
            experiment = new TExperiment();
    }

    private void goToPart2() {
        if (checkField(etFilename) && checkField(etName)) {
            experiment.setFilename(etFilename.getText().toString());
            experiment.setName(etName.getText().toString());
            if (etInstructions.getText() != null)
                experiment.setInstruction(etInstructions.getText().toString());
            experiment.setAskInfo(cbAskInfo.isChecked());
            Bundle extras = new Bundle();
            extras.putSerializable("experiment", experiment);
            Intent intent = new Intent(this, ExperimentMetricsActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "All fields should be filled", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkField(EditText et) {
        if (et != null)
            if (et.getText() != null)
                if (!et.getText().toString().trim().equals(""))
                    return true;
        return false;
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_exp_general_ac);
//        toolbar.setNavigationIcon(R.drawable.ic_temp_icon_color_2);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("New experiment");
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleAppearance);
        toolbar.setSubtitle("Provide the necessary information");
        toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleAppearance);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        etName = (EditText) findViewById(R.id.et_exp_name_ac);
        etFilename = (EditText) findViewById(R.id.et_file_name_ac);
        etInstructions = (EditText) findViewById(R.id.et_instructions_ac);
        cbAskInfo = (CheckBox) findViewById(R.id.cb_ask_info_ac);
        ibPt2 = (ImageButton) findViewById(R.id.ib_experiment_pt2);
    }


}
