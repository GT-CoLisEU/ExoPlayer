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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.android.exoplayer2.demo.R;

import br.rnp.futebol.verona.pojo.TExperiment;
import br.rnp.futebol.verona.visual.activity.unused.ExperimentControllerActivity;

public class ExperimentGeneralFragment extends Fragment {

    private static final String TAG = "ExperimentMetrics";
    private TExperiment experiment;
    private EditText etName, etFilename, etInstructions;
    private CheckBox cbAskInfo;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.experiment_general_fragment, container, false);
        experiment = ((ExperimentControllerActivity) getActivity()).getExperiment();
        init();
        cbAskInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                experiment.setAskInfo(null);
            }
        });
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateInfo();
            }
        });
        etFilename.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateInfo();
            }
        });
        etInstructions.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        etName = (EditText) view.findViewById(R.id.et_exp_name);
        etFilename = (EditText) view.findViewById(R.id.et_file_name);
        etInstructions = (EditText) view.findViewById(R.id.et_instructions);
        cbAskInfo = (CheckBox) view.findViewById(R.id.cb_ask_info);
    }

    private void updateInfo() {
        experiment.setName(etName.getText().toString());
        experiment.setFilename(etFilename.getText().toString());
        experiment.setInstruction(etInstructions.getText().toString());
    }


}
