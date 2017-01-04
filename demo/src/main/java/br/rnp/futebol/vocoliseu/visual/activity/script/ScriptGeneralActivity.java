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
package br.rnp.futebol.vocoliseu.visual.activity.script;

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

import br.rnp.futebol.vocoliseu.pojo.TScript;

public class ScriptGeneralActivity extends AppCompatActivity {

    //    private static final String TAG = "ExperimentMetrics";
    private TScript script;
    private EditText etAddress, etVideo, etExtension, etLoop;
    private CheckBox cbUseDash;
    private ImageButton ibPt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.script_general_activity);
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
        script = (extras != null) ? (TScript) extras.getSerializable("script") : null;
        if (script != null) {
            etAddress.setText(script.getAddress());
            etExtension.setText(script.getExtension());
            etVideo.setText(script.getVideo());
            cbUseDash.setChecked(script.isUseDash());
        } else
            script = new TScript();
    }

    private void goToPart2() {
        if (checkField(etAddress) && checkField(etExtension) && checkField(etVideo)) {
            script.setAddress(etAddress.getText().toString());
            script.setExtension(etExtension.getText().toString());
            script.setVideo(etVideo.getText().toString());
            script.setUseDash(cbUseDash.isChecked());
            script.setLoop(checkField(etLoop) ? Integer.parseInt(etLoop.getText().toString()) : 1);
            Bundle extras = new Bundle();
            extras.putSerializable("script", script);
            Intent intent = new Intent(this, ScriptMetricsActivity.class);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_script_general_ac);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("New Video");
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
        etAddress = (EditText) findViewById(R.id.et_provider_address_ac);
        etVideo = (EditText) findViewById(R.id.et_video_name_ac);
        etExtension = (EditText) findViewById(R.id.et_video_extension_ac);
        cbUseDash = (CheckBox) findViewById(R.id.cb_use_dash_flag_ac);
        etLoop = (EditText) findViewById(R.id.et_video_loop_ac);
        ibPt2 = (ImageButton) findViewById(R.id.ib_script_pt2);
    }

//    private void updateInfo() {
//        script.setAddress(etAddress.getText().toString());
//        script.setVideo(etVideo.getText().toString());
//        script.setExtension(etExtension.getText().toString());
//    }


}
