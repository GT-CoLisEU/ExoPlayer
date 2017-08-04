package br.rnp.futebol.verona.visual.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import br.rnp.futebol.verona.exoplayerlegacy.PlayerActivity;

import com.google.android.exoplayer2.demo.R;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import br.rnp.futebol.verona.pojo.TExperiment;
import br.rnp.futebol.verona.pojo.TScript;
import br.rnp.futebol.verona.pojo.unused.Experiment;

public class StartActivity extends AppCompatActivity {


    private TextView tvInstruction, tvProvide;
    private Button btProvide, btStart;
    private TExperiment experiment;
    private float cons = -1, fam = -1;
    private int age = -1;
    private boolean isMale, filledGender;
    private final String[] levels = {"Never", "Sometimes", "Eventually", "Frequently", "Always"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_experiment);
        init();
        if (getIntent() == null || getIntent().getExtras() == null)
            finish();
        experiment = (TExperiment) getIntent().getExtras().getSerializable("experiment");
        if (experiment == null)
            finish();
        if (experiment.getAskInfo() == null || !experiment.getAskInfo().hasInfo()) {
            tvProvide.setVisibility(View.INVISIBLE);
            btProvide.setVisibility(View.INVISIBLE);
        }
        tvInstruction.setText(experiment.getInstruction() != null ? experiment.getInstruction() : "");
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                makeCBDialogForMig();
                goToPlayer();
            }
        });
        btProvide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!experiment.getAskInfo().hasInfo()) {
                    goToPlayer();
                } else {
                    if (experiment.getAskInfo().askAge())
                        makeETDialog();
                    else if (experiment.getAskInfo().askGender())
                        makeCBDialog();
                    else if (experiment.getAskInfo().askFam())
                        makeRBDialog(true);
                    else if (experiment.getAskInfo().askCons())
                        makeRBDialog(false);
                }
            }
        });


    }

//    public AlertDialog makeRBDialog(final boolean isFam) {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
////        builder.setCon
//        View view = LayoutInflater.from(this).inflate(R.layout.rating_metric_dialog, null);
//        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar_dialog);
//        builder.setTitle("User information");
//        builder.setMessage(isFam ? "How familiar are you with video technologies?" : "How do you categorize your level of video consumption?");
//        builder.setCancelable(false);
//        builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (isFam) {
//                    dialog.dismiss();
//                    if (experiment.getAskInfo().askCons())
//                        makeRBDialog(false);
//                    else
//                        goToPlayer();
//                } else {
//                    dialog.dismiss();
//                    goToPlayer();
//                }
//            }
//        });
//        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (isFam) {
//                    fam = ratingBar.getRating();
//                    dialog.dismiss();
//                    if (experiment.getAskInfo().askCons())
//                        makeRBDialog(false);
//                    else
//                        goToPlayer();
//                } else {
//                    cons = ratingBar.getRating();
//                    dialog.dismiss();
//                    goToPlayer();
//                }
//            }
//        });
//        builder.setView(view);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//        return dialog;
//    }


    public AlertDialog makeRBDialog(final boolean isFam) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.rating_seekbar_dialog, null);
        SeekBar sk = (SeekBar) view.findViewById(R.id.sk_opinion);
        final TextView tv = (TextView) view.findViewById(R.id.tv_opinion_status);
        builder.setTitle("User information");
        builder.setMessage(isFam ? "How familiar are you with video technologies?" : "How do you categorize your level of video consumption?");
        builder.setCancelable(false);

        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv.setText(levels[progress]);
                if (isFam)
                    fam = progress;
                else
                    cons = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isFam) {
                    dialog.dismiss();
                    if (experiment.getAskInfo().askCons())
                        makeRBDialog(false);
                    else
                        goToPlayer();
                } else {
                    dialog.dismiss();
                    goToPlayer();
                }
            }
        });
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isFam) {
                    dialog.dismiss();
                    if (experiment.getAskInfo().askCons())
                        makeRBDialog(false);
                    else
                        goToPlayer();
                } else {
                    dialog.dismiss();
                    goToPlayer();
                }
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public AlertDialog makeCBDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCon
        View view = LayoutInflater.from(this).inflate(R.layout.rating_checkbox_dialog, null);
        final CheckBox cbMale = (CheckBox) view.findViewById(R.id.cb_male_dialog);
        cbMale.setChecked(true);
        final CheckBox cbFemale = (CheckBox) view.findViewById(R.id.cb_female_dialog);
        cbMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cbFemale.setChecked(!isChecked);
            }
        });
        cbFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cbMale.setChecked(!isChecked);
            }
        });
        builder.setTitle("User information");
        builder.setMessage("Please provide your gender");
        builder.setCancelable(false);
        builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (experiment.getAskInfo().askFam())
                    makeRBDialog(true);
                else if (experiment.getAskInfo().askCons())
                    makeRBDialog(false);
                else
                    goToPlayer();
            }
        });
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isMale = cbMale.isChecked();
                filledGender = true;
                dialog.dismiss();
                if (experiment.getAskInfo().askFam())
                    makeRBDialog(true);
                else if (experiment.getAskInfo().askCons())
                    makeRBDialog(false);
                else
                    goToPlayer();
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public AlertDialog makeCBDialogForMig() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCon
        View view = LayoutInflater.from(this).inflate(R.layout.migration_checkbox_dialog, null);
        final CheckBox cbMig = (CheckBox) view.findViewById(R.id.cb_mig_per_tier);
        final CheckBox cbDown = (CheckBox) view.findViewById(R.id.cb_migration_down);
        final CheckBox cbUp = (CheckBox) view.findViewById(R.id.cb_migration_up);
        builder.setTitle("Experiment information");
        builder.setCancelable(false);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToPlayer(cbMig.isChecked(), cbDown.isChecked(), !cbUp.isChecked());
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

//    public AlertDialog makeSKDialog() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCon
//        View view = LayoutInflater.from(this).inflate(R.layout.rating_seekbar_dialog, null);
//        SeekBar sk = (SeekBar) view.findViewById(R.id.sk_opinion);
//        final TextView tv = (TextView) view.findViewById(R.id.tv_opinion_status);
//        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                tv.setText(levels[progress]);
//                cons = progress;
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//        builder.setTitle("User information");
//        builder.setMessage("How do you categorize your video consumption level? (From 1 to 5)");
//        builder.setCancelable(false);
//        builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                if (experiment.getAskInfo().askFam())
//                    makeRBDialog(true);
//                else if (experiment.getAskInfo().askCons())
//                    makeRBDialog(false);
//                else
//                    goToPlayer();
//            }
//        });
//        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });
//        builder.setView(view);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//        return dialog;
//    }

    public AlertDialog makeETDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCon
        View view = LayoutInflater.from(this).inflate(R.layout.rating_et_dialog, null);
        final EditText etAge = (EditText) view.findViewById(R.id.et_dialog_age);
        builder.setTitle("User information");
        builder.setMessage("Please provide your age");
        builder.setCancelable(false);
        builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (experiment.getAskInfo().askGender())
                    makeCBDialog();
                else if (experiment.getAskInfo().askFam())
                    makeRBDialog(true);
                else if (experiment.getAskInfo().askCons())
                    makeRBDialog(false);
                else
                    goToPlayer();

            }
        });
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (etAge.getText() == null || etAge.getText().toString().trim().equals(""))
                    age = -1;
                else
                    age = Integer.parseInt(etAge.getText().toString());
                dialog.dismiss();
                if (experiment.getAskInfo().askGender())
                    makeCBDialog();
                else if (experiment.getAskInfo().askFam())
                    makeRBDialog(true);
                else if (experiment.getAskInfo().askCons())
                    makeRBDialog(false);
                else
                    goToPlayer();
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private void goToPlayer() {
        TExperiment exp =  copyExperiment(experiment);
        experiment.setScripts(new ArrayList<TScript>());
        for (TScript script : exp.getScripts()) {
            if (exists(script.getProvider()))
                experiment.getScripts().add(script);
        }
        if (!experiment.getScripts().isEmpty())
            goToPlayer(false, false, false);
        else
            Toast.makeText(getBaseContext(), "None of this experiment's videos where found in their addresses", Toast.LENGTH_SHORT).show();
    }

    /*
        private String name;
    private String filename;
    private InfoHelper askInfo;
    private boolean usedAux;
    private String instruction;
    private ArrayList<Integer> qosMetrics;
    private ArrayList<Integer> objectiveQoeMetrics;
    private ArrayList<TScript> scripts;
     */

    private TExperiment copyExperiment(TExperiment experiment) {
        TExperiment exp = new TExperiment();

        exp.setName(experiment.getName());
        exp.setFilename(experiment.getFilename());
        exp.setAskInfo(experiment.getAskInfo());
        exp.setInstruction(experiment.getInstruction());
        exp.setQosMetrics(experiment.getQosMetrics());
        exp.setObjectiveQoeMetrics(experiment.getObjectiveQoeMetrics());
        exp.setScripts(experiment.getScripts());

        return exp;
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

    private void goToPlayer(boolean mig, boolean during, boolean downstream) {
        Toast.makeText(this, "The video will start now", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getBaseContext(), PlayerActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("loop", 1);
        extras.putInt("index", 0);
        extras.putBoolean("mig", during);
        extras.putBoolean("downstream", downstream);
        extras.putSerializable("experiment", experiment);
        String provider = experiment.getScripts().get(0).getProvider();
        intent.setData(Uri.parse(provider));
        intent.setAction(PlayerActivity.ACTION_VIEW);
        getUserInfo(extras);
        intent.putExtras(extras);
        finish();
        startActivity(intent);
    }

    private void getUserInfo(Bundle bundle) {
        JSONObject json = new JSONObject();
        try {
            if (experiment.getAskInfo().askAge() && age != -1)
                json.put("age", age);
            if (experiment.getAskInfo().askGender() && filledGender)
                json.put("gender", isMale ? "M" : "F");
            if (experiment.getAskInfo().askCons() && cons != -1)
                json.put("consumption", cons);
            if (experiment.getAskInfo().askFam() && fam != -1)
                json.put("familiar", fam);
            bundle.putString("userInfo", json.toString());
        } catch (Exception e) {
        }
    }

    private void init() {
        tvInstruction = (TextView) findViewById(R.id.tv_start_inst);
        tvProvide = (TextView) findViewById(R.id.tv_provide_info);
        btProvide = (Button) findViewById(R.id.bt_provide_info);
        btStart = (Button) findViewById(R.id.bt_start_videos);
    }
}