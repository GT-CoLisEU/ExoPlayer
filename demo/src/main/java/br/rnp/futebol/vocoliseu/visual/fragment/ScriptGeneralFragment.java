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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.android.exoplayer2.demo.R;

import br.rnp.futebol.vocoliseu.pojo.TScript;
import br.rnp.futebol.vocoliseu.visual.activity.ScriptControllerActivity;

public class ScriptGeneralFragment extends Fragment {

    private static final String TAG = "ExperimentMetrics";
    private TScript script;
    private EditText etAddress, etVideo, etExtension;
    private CheckBox cbUseDash;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.script_general_fragment, container, false);
        script = ((ScriptControllerActivity) getActivity()).getScript();
        init();
        cbUseDash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                script.setUseDash(isChecked);
            }
        });
        etAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateInfo();
            }
        });
        etVideo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateInfo();
            }
        });
        etExtension.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateInfo();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init() {
        etAddress = (EditText) view.findViewById(R.id.et_provider_address);
        etVideo = (EditText) view.findViewById(R.id.et_video_name);
        etExtension = (EditText) view.findViewById(R.id.et_video_extension);
        cbUseDash = (CheckBox) view.findViewById(R.id.cb_use_dash_flag);
    }

    private void updateInfo() {
        script.setAddress(etAddress.getText().toString());
        script.setVideo(etVideo.getText().toString());
        script.setExtension(etExtension.getText().toString());
    }


}
