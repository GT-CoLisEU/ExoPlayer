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
package br.rnp.futebol.verona.visual.activity.experiment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.exoplayer2.demo.R;

import org.json.JSONException;
import org.json.JSONObject;

import br.rnp.futebol.verona.pojo.InfoHelper;
import br.rnp.futebol.verona.pojo.TExperiment;

public class ExperimentGeneralActivity extends AppCompatActivity {

    private static final String TAG = "ExperimentMetrics";
    private TExperiment experiment, experimentAux;
    private EditText etName, etFilename, etInstructions;
    private CheckBox cbAskAge, cbAskGender, cbAskCons, cbAskFam;
    private ImageButton ibPt2;
    private ImageView ibFnHelp, ibInfoHelp;

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
        ibFnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeHelpDialog();
            }
        });
        ibInfoHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeInfoDialog();
            }
        });
        if (!etFilename.isFocusable()) {
            etFilename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getBaseContext(), "The filename cannot be edited", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public AlertDialog makeHelpDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This will be the name of the files related to this experiment.\nA .txt will be saved in your device as a script containing the experiment information.\nAlso, a .csv will be saved with the experiment results.\nYou can export both files!");
        builder.setTitle("File help");
        builder.setPositiveButton("I got it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public AlertDialog makeInfoDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Checking these boxes, you can select which information you will ask to the volunteer before he/she watches the video." +
                "\nYou can select none, some or all of them. Also, the volunteer will be able to answer or not. These information will be saved in the results document.");
        builder.setTitle("Personal information help");
        builder.setPositiveButton("I got it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
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
            cbAskAge.setChecked(experiment.getAskInfo().askAge());
            cbAskGender.setChecked(experiment.getAskInfo().askGender());
            cbAskCons.setChecked(experiment.getAskInfo().askCons());
            cbAskFam.setChecked(experiment.getAskInfo().askFam());
        } else
            experiment = new TExperiment();
    }

    private void goToPart2() {
        if (checkField(etFilename) && checkField(etName)) {
            experiment.setFilename(etFilename.getText().toString());
            experiment.setName(etName.getText().toString());
            if (etInstructions.getText() != null)
                experiment.setInstruction(etInstructions.getText().toString());
            experiment.setAskInfo(new InfoHelper(cbAskAge.isChecked(), cbAskGender.isChecked(), cbAskCons.isChecked(), cbAskFam.isChecked()));
            Bundle extras = new Bundle();
            extras.putSerializable("experiment", experiment);
            extras.putSerializable("experimentAux", experimentAux);
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
        cbAskAge = (CheckBox) findViewById(R.id.cb_ask_age_ac);
        cbAskGender = (CheckBox) findViewById(R.id.cb_ask_gender_ac);
        cbAskCons = (CheckBox) findViewById(R.id.cb_ask_cons_ac);
        cbAskFam = (CheckBox) findViewById(R.id.cb_ask_fam_ac);
        ibPt2 = (ImageButton) findViewById(R.id.ib_experiment_pt2);
        ibFnHelp = (ImageView) findViewById(R.id.im_filename_help);
        ibInfoHelp = (ImageView) findViewById(R.id.im_info_help);
        try {
            if (getIntent() != null && getIntent().hasExtra("jsonexperiment"))
                experimentAux = new TExperiment().fromJson(new JSONObject(getIntent().getExtras().getString("jsonexperiment")));
            if (experimentAux != null) {
                etName.setText(experimentAux.getName());
                etFilename.setText(experimentAux.getFilename());
                etFilename.setFocusable(false);
                etInstructions.setText(experimentAux.getInstruction());
                if (experimentAux.getAskInfo() != null) {
                    cbAskAge.setChecked(experimentAux.getAskInfo().askAge());
                    cbAskGender.setChecked(experimentAux.getAskInfo().askGender());
                    cbAskCons.setChecked(experimentAux.getAskInfo().askCons());
                    cbAskFam.setChecked(experimentAux.getAskInfo().askFam());
                }
            }
        } catch (JSONException je) {
        }
    }


}
