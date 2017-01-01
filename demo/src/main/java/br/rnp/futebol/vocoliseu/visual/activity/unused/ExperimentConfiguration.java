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
package br.rnp.futebol.vocoliseu.visual.activity.unused;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.exoplayer2.demo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * An activity for selecting from a list of samples.
 */
public class ExperimentConfiguration extends Fragment {

    private static final String TAG = "ExperimentConfiguration";
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.experiment_configuration, container, false);
        final TextView show = (TextView) view.findViewById(R.id.tv_show);
        final EditText file = (EditText) view.findViewById(R.id.et_file_name), name = (EditText) view.findViewById(R.id.et_name), info = (EditText) view.findViewById(R.id.et_info);
        Button write = (Button) view.findViewById(R.id.bt_write), read = (Button) view.findViewById(R.id.bt_read);
//        checkPerm();
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write(file.getText().toString(), prepareMsg(name.getText().toString(), info.getText().toString()));
            }
        });
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.setText(read(file.getText().toString()));
            }
        });
        return view;
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
