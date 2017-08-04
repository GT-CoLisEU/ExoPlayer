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
package br.rnp.futebol.verona.visual.activity.script;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.exoplayer2.demo.R;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import br.rnp.futebol.verona.dao.TScriptDAO;
import br.rnp.futebol.verona.pojo.TScript;
import br.rnp.futebol.verona.visual.activity.MainActivity;

public class ScriptGeneralActivity extends AppCompatActivity {

    //    private static final String TAG = "ExperimentMetrics";
    private TScript script, oldScript;
    private EditText etVideo, etExtension;
    private AutoCompleteTextView actvAddress;
    private CheckBox cbUseDash;
    private boolean edit = false;
    private ImageButton ibPt2;
    private boolean ok = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.script_general_activity_simple);
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
        edit = (extras != null) && extras.getBoolean("edit");
        if (edit)
            oldScript = new TScript().copy(script);
        if (script != null) {
            actvAddress.setText(script.getAddress());
            etExtension.setText(script.getExtension());
            etVideo.setText(script.getVideo());
            cbUseDash.setChecked(script.isUseDash());
        } else
            script = new TScript();
    }

    private void goToPart2() {
        if (checkField(actvAddress) && checkField(etExtension) && checkField(etVideo)) {
            TScriptDAO dao = new TScriptDAO(getBaseContext());
            script.setAddress(actvAddress.getText().toString());
            script.setExtension(etExtension.getText().toString());
            script.setVideo(etVideo.getText().toString());
            script.setUseDash(cbUseDash.isChecked());
            if (dao.scriptAvailable(script)) {
                if (!exists(script.getProvider()))
                    makeContinueDialog();
                else
                    finishCreation(dao);
            } else
                Toast.makeText(getBaseContext(), "A video with the same data is already registered", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "All fields should be filled", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean exists(String URLName) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void finishCreation(TScriptDAO dao) {
        try {
            if (edit)
                dao.update(oldScript, script);
            else
                dao.insert(script);
            Toast.makeText(getBaseContext(), "Video registered!\nNow you can use it on new experiments.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Couldn't register video.", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void makeContinueDialog() {
        ok = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(ScriptGeneralActivity.this);
        String text = "The file ".concat(script.getProvider()).concat(" was not found. Contine anyway?");
        builder.setMessage(text);
        builder.setCancelable(false);
        builder.setTitle("Warning");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishCreation(new TScriptDAO(getBaseContext()));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Check the provided information.", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private boolean checkField(EditText et) {
        if (et != null)
            if (et.getText() != null)
                if (!et.getText().toString().trim().equals(""))
                    return true;
        return false;
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_script_general_acs);
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
        actvAddress = (AutoCompleteTextView) findViewById(R.id.et_provider_address_acs);
        TScriptDAO dao = new TScriptDAO(this);
        ArrayList<String> names = dao.getScriptsName();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, names);
        actvAddress.setAdapter(adapter);
        etVideo = (EditText) findViewById(R.id.et_video_name_acs);
        etExtension = (EditText) findViewById(R.id.et_video_extension_acs);
        cbUseDash = (CheckBox) findViewById(R.id.cb_use_dash_flag_acs);
        ibPt2 = (ImageButton) findViewById(R.id.ib_script_pt2s);
    }

//    private void updateInfo() {
//        script.setAddress(etAddress.getText().toString());
//        script.setVideo(etVideo.getText().toString());
//        script.setExtension(etExtension.getText().toString());
//    }


}
