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
package br.rnp.futebol.verona.visual.fragment.unused;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import br.rnp.futebol.verona.pojo.unused.Script;
import com.google.android.exoplayer2.demo.R;

import br.rnp.futebol.verona.visual.activity.unused.ExperimentConfigurationControllerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * An activity for selecting from a list of samples.
 */
public class ExperimentConfigurationGeneralFragment extends Fragment {

    private static final String TAG = "ExpConfiguration1";
    private View view;
    private ExperimentConfigurationControllerActivity activity;
    private Script script;
    private EditText etName, etFilename, etAddress, etIntruction;
    private CheckBox cbAskInfo, cbUseDash;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.f_experiment_configuration_general, container, false);
        activity = (ExperimentConfigurationControllerActivity) getActivity();
        script = activity.getScript();
        init();

        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                script.setName(etName.getText().toString());
            }
        });

        etFilename.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                script.setFileName(etFilename.getText().toString());
            }
        });

        etAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                script.setAddress(etAddress.getText().toString());
            }
        });

        etIntruction.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                script.setDescription(etIntruction.getText().toString());
            }
        });

        cbAskInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                script.setAskInfo(isChecked);
            }
        });

        cbUseDash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                script.setUseDash(isChecked);
            }
        });

        return view;
    }

    public void init() {
        etName = (EditText) view.findViewById(R.id.et_exp_name);
        etFilename = (EditText) view.findViewById(R.id.et_file_name);
        etAddress = (EditText) view.findViewById(R.id.et_provider_ip);
        etIntruction = (EditText) view.findViewById(R.id.et_instructions);
        cbAskInfo = (CheckBox) view.findViewById(R.id.cb_ask_info);
        cbUseDash = (CheckBox) view.findViewById(R.id.cb_use_dash);
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
