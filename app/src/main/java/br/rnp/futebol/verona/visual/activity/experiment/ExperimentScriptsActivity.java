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
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.demo.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.rnp.futebol.verona.dao.TExpForListDAO;
import br.rnp.futebol.verona.dao.TScriptDAO;
import br.rnp.futebol.verona.pojo.BinaryQuestion;
import br.rnp.futebol.verona.pojo.TExperiment;
import br.rnp.futebol.verona.pojo.TScript;
import br.rnp.futebol.verona.util.ReadyMetrics;
import br.rnp.futebol.verona.util.adapter.ScriptAdapter;
import br.rnp.futebol.verona.visual.activity.MainActivity;

/**
 * An activity for selecting from a list of samples.
 */
public class ExperimentScriptsActivity extends AppCompatActivity {

    private static final String TAG = "ExperimentMetrics";
    private ListView lvScripts;
    private TScriptDAO dao;
    private Toolbar toolbar;
    private ScriptAdapter adapter;
    private boolean useACR, useDCR;
    private int repetition = 0;
    private BinaryQuestion question = null;
    private TExperiment experiment, experimentAux;
    private ImageButton ibFinal;
    private TScript script;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_scripts_activity);
        init();
        refreshList();

        lvScripts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (experiment.getScripts() == null)
                    experiment.setScripts(new ArrayList<TScript>());
                List<TScript> scripts = dao.getScripts();
                script = scripts.get(position);
                if (experiment.getScripts().isEmpty())
                    makeETDialog();
//                    addScript(scripts.get(position), position);
//                    experiment.getScripts().add(scripts.get(position));
                else {
                    boolean aux = false;
                    int cont = 0, pos = -1;
                    for (TScript s : experiment.getScripts()) {
                        if (compareScripts(s, scripts.get(position))) {
                            pos = cont;
                            aux = true;
                        }
                        cont++;
                    }
                    if (!aux)
                        makeETDialog();
//                        addScript(scripts.get(position), position);
                    else
                        experiment.getScripts().remove(pos);
                }
                refreshList();
            }
        });

        ibFinal.setOnClickListener(new View.OnClickListener() {
            String msg;

            @Override
            public void onClick(View v) {
                if (experiment.getScripts() != null && !experiment.getScripts().isEmpty()) {
//                    if (experimentAux != null) {
//                        if (compareExperiments(experiment, experimentAux))
//                            makeDialog("Experiment file", "Changes were found in this experiment. Do you want to save them in a new file?", getBaseContext(),
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                                        }
//                                    },
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                                        }
//                                    });
//                    }

                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), experiment.getFilename().concat(".txt"));
                    file.delete();
                    write(experiment.getFilename(), experiment.toJson().toString());
                    TExpForListDAO dao = new TExpForListDAO(getBaseContext());
                    if (experimentAux == null)
                        dao.insert(experiment);
                    dao.close();
                    msg = "Experiment configured!";
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else
                    msg = "At least one video should be selected";
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }



//    private String renameFile(String filename) {
//        String[] end = filename.split("_");
//        if (end.length == 0) return filename;
//
//
//    }

    private boolean compareExperiments(TExperiment e1, TExperiment e2) {
        return (e1.getFilename().equals(e2.getFilename()))
                && (e1.getName().equals(e2.getName()))
                && (e1.getInstruction().equals(e2.getInstruction()))
                && e1.getAskInfo().compareTo(e2.getAskInfo())
                && compareArrays(e1.getQosMetrics(), e2.getQosMetrics())
                && compareArrays(e1.getObjectiveQoeMetrics(), e2.getObjectiveQoeMetrics())
                && compareScripts(e1.getScripts(), e2.getScripts());
    }

    public boolean compareArrays(ArrayList<Integer> list1, ArrayList<Integer> list2) {

        if (list1 == null && list2 == null)
            return true;
        if ((list1 == null || list2 == null) || list1.size() != list2.size())
            return false;

        for (Integer itemList1 : list1)
            if (!list2.contains(itemList1))
                return false;
        return true;
    }

    public boolean compareScripts(ArrayList<TScript> list1, ArrayList<TScript> list2) {

        if (list1 == null && list2 == null)
            return true;
        if ((list1 == null || list2 == null) || list1.size() != list2.size())
            return false;

        for (TScript itemList1 : list1) {
            if (!list2.contains(itemList1))
                return false;
        }
        return true;
    }

    public AlertDialog makeDialog(String title, String message, Context myContext, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener okListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setPositiveButton("OK", okListener);
        builder.setNegativeButton("Cancel", listener);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public AlertDialog makeCBDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCon
        View view = LayoutInflater.from(this).inflate(R.layout.rating_checkbox_dialog, null);
        final CheckBox cbMale = (CheckBox) view.findViewById(R.id.cb_male_dialog);
        final CheckBox cbFemale = (CheckBox) view.findViewById(R.id.cb_female_dialog);
        final TextView tvInfo = (TextView) view.findViewById(R.id.tv_text_info);
        cbMale.setText("ACR");
        cbFemale.setText("DCR");
        builder.setTitle("Video Information - 2/3");
        builder.setMessage("Please select the metrics");
        tvInfo.setText("ACR is a scale from 1 to 5 that measures how good was the user experience about the video. DCR is a scale from 1 to 5 that measures how degradated was the user experience comparing the current video to a previous displayed one.");
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                useACR = cbMale.isChecked();
                useDCR = cbFemale.isChecked();
                dialog.dismiss();
                insertBinaryQuestion();
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public AlertDialog makeETDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCon
        View view = LayoutInflater.from(this).inflate(R.layout.rating_et_dialog, null);
        final EditText etRep = (EditText) view.findViewById(R.id.et_dialog_age);
        etRep.setHint("5, for example");
        builder.setTitle("Video Information - 1/3");
        builder.setMessage("Please provide the number of times which the video will be repeated. If empty, the value will be 1.");
        builder.setCancelable(false);
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (etRep.getText() == null || etRep.getText().toString().trim().equals(""))
                    repetition = 1;
                else
                    repetition = Integer.parseInt(etRep.getText().toString());
                dialog.dismiss();
                makeCBDialog();
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private void insertBinaryQuestion() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.binary_question_form, null);
        final EditText etQuestion = (EditText) view.findViewById(R.id.et_binary_question);
        final EditText etAnswer1 = (EditText) view.findViewById(R.id.et_binary_answer_1);
        final EditText etAnswer2 = (EditText) view.findViewById(R.id.et_binary_answer_2);

        alert.setView(view);
        alert.setCancelable(false);
        alert.setTitle("Binary Question");
        alert.setMessage("The question will be saved only if all three fields are not empty.");
        alert.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (etQuestion.getText() != null && !etQuestion.getText().toString().trim().equals("")
                        && etAnswer1.getText() != null && !etAnswer1.getText().toString().trim().equals("")
                        && etAnswer2.getText() != null && !etAnswer2.getText().toString().trim().equals("")) {
                    question = new BinaryQuestion();
                    question.setQuestion(etQuestion.getText().toString());
                    question.setAnswer1(etAnswer1.getText().toString());
                    question.setAnswer2(etAnswer2.getText().toString());
                    script.setQuestion(question);
                    if (script.getSubjectiveQoeMetrics() == null)
                        script.setSubjectiveQoeMetrics(new ArrayList<Integer>());
                    script.getSubjectiveQoeMetrics().add(ReadyMetrics.BINARY_QUESTION_ID);
                    if (useACR)
                        script.getSubjectiveQoeMetrics().add(ReadyMetrics.ACR_ID);
                    if (useDCR)
                        script.getSubjectiveQoeMetrics().add(ReadyMetrics.DCR_ID);
                    script.setLoop(repetition > 0 ? repetition : 1);
                    experiment.getScripts().add(script);
                    script = null;
                    useACR = false;
                    useDCR = false;
                    question = null;
                    repetition = 0;
                    dialog.dismiss();
                    refreshList();
                } else
                    Toast.makeText(getBaseContext(), "Please provide the question or click in 'Skip'", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (script.getSubjectiveQoeMetrics() == null)
                    script.setSubjectiveQoeMetrics(new ArrayList<Integer>());
                if (useACR)
                    script.getSubjectiveQoeMetrics().add(ReadyMetrics.ACR_ID);
                if (useDCR)
                    script.getSubjectiveQoeMetrics().add(ReadyMetrics.DCR_ID);
                script.setLoop(repetition > 0 ? repetition : 1);
                experiment.getScripts().add(script);
                script = null;
                useACR = false;
                useDCR = false;
                question = null;
                repetition = 0;
                dialog.dismiss();
                refreshList();
            }
        });
        alert.show();

    }

//    private void addScript(TScript script, int pos) {
//        Intent intent = new Intent(this, ScriptMetricsActivity.class);
//        Bundle extras = new Bundle();
//        extras.putSerializable("script", script);
//        extras.putInt("pos", pos);
//        intent.putExtras(extras);
//        startActivityForResult(intent, 1);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            if (data != null && data.getExtras() != null) {
//                TScript temp = (TScript) data.getExtras().get("modScript");
//                int pos = data.getIntExtra("pos", -1);
//                if (temp != null) {
//                    experiment.getScripts().get(pos).setSubjectiveQoeMetrics(temp.getSubjectiveQoeMetrics());
//                    experiment.getScripts().get(pos).setLoop(temp.getLoop());
//                    experiment.getScripts().add(temp);
//                    refreshList();
//                }
//            }
//        }
//    }

    //    private void addScript(final ArrayList<TScript> myScripts, final TScript selectedScript) {
//
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setTitle("Set the video for this experiment");
//        View view = getLayoutInflater().inflate(R.layout.subjective_metric_selection_popup, null);
//        builder.setView(view);
//        builder.setCancelable(false);
////        AlertDialog dialog = builder.create();
//
//        final EditText etRep = (EditText) view.findViewById(R.id.et_popup_rep);
//
//        ArrayList<Metric> metrics = new ArrayList<>();
//        ArrayList<Intent> ids = new ArrayList<>();
//        ListView lvMetrics = (ListView) view.findViewById(R.id.sm_popup_list);
//
//        if (ReadyMetrics.S_QOE_METRICS == null)
//            ReadyMetrics.init();
//        metrics.addAll(ReadyMetrics.S_QOE_METRICS);
//
//        MetricAdapter adapter = new MetricAdapter(this, metrics);
//        lvMetrics.setAdapter(adapter);
//        lvMetrics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
//
//        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//                dialog.dismiss();
//            }
//        });
//        builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (etRep != null && etRep.getText() != null && !etRep.getText().toString().trim().equals(""))
//                    selectedScript.setLoop(Integer.parseInt(etRep.getText().toString()));
//                else
//                    selectedScript.setLoop(1);
//                myScripts.add(selectedScript);
//                refreshList();
//            }
//        });
//        builder.show();
//    }

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

    private void init() {
        Bundle extras = null;
        if (getIntent() != null)
            extras = getIntent().getExtras();
        experiment = (extras != null) ? (TExperiment) extras.getSerializable("experiment") : null;
        experimentAux = (extras != null) ? (TExperiment) extras.getSerializable("experimentAux") : null;
        if (experimentAux != null && experiment != null)
            experiment.setScripts(experimentAux.getScripts());
        lvScripts = (ListView) findViewById(R.id.lv_exp_scripts_ac);
        ibFinal = (ImageButton) findViewById(R.id.ib_exp_final);
        dao = new TScriptDAO(getBaseContext());
        toolbar = (Toolbar) findViewById(R.id.tb_exp_scripts_ac);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("New experiment");
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleAppearance);
        toolbar.setSubtitle("Select which videos will be displayed");
        toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleAppearance);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void refreshList() {
        List<TScript> aux = null;
        List<TScript> scripts = new TScriptDAO(this).getScripts();
        if (scripts != null) {
            aux = scripts;
            if (experiment.getScripts() != null)
                if (!experiment.getScripts().isEmpty()) {
                    for (TScript s : experiment.getScripts()) {
                        for (TScript sc : aux) {
                            if (compareScripts(s, sc)) {
                                sc.setUsedAux(true);
                                sc.setLoop(s.getLoop());
                                sc.setSubjectiveQoeMetrics(s.getSubjectiveQoeMetrics());
                            }
                        }
                    }
                }
        }
        if (aux != null && !aux.isEmpty()) {
            adapter = new ScriptAdapter(this, aux);
            lvScripts.setAdapter(adapter);
        }
    }

    private boolean compareScripts(TScript sc1, TScript sc2) {
        return (sc1.getAddress().compareTo(sc2.getAddress()) == 0
                && sc1.getVideo().compareTo(sc2.getVideo()) == 0
                && sc1.getExtension().compareTo(sc2.getExtension()) == 0);
    }

//    public static TExperiment getExperiment() {
//        return experiment;
//    }

//    public static void setExperiment(TExperiment experiment) {
//        this.experiment = experiment;
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (experiment == null)
            finish();
    }


}
