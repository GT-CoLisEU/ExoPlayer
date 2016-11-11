package br.rnp.futebol.vocoliseu.visual.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import br.rnp.futebol.vocoliseu.dao.ScriptDAO;
import br.rnp.futebol.vocoliseu.pojo.Script;
import br.rnp.futebol.vocoliseu.pojo.TExperiment;
import br.rnp.futebol.vocoliseu.util.adapter.ExperimentAdapter;
import com.google.android.exoplayer2.demo.PlayerActivity;
import com.google.android.exoplayer2.demo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by camargo on 20/10/16.
 */
public class DashboardActivity extends AppCompatActivity {

    //    private EditText etAddress, etFile;
//    private Button btStart, btExamples;
//    private final String PREF = "VO_PREFS";
    private ImageButton ibNewExp;
    private List<Script> myExps;
    //    private TextView tvTitle;
    private ListView lvExps;
    private ExperimentAdapter adapter;
    private ScriptDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard_activity);
        String str = "{\"name\": \"exp teste\", \"filename\": \"exptst\",\"askinfo\": false,\"instruction\": \"none\",\"qosMetrics\": [1, 3, 4],\"objQoeMetrics\": [1, 3, 4],\"scripts\": []}";
        try {
            JSONObject teste = new JSONObject(str);
            TExperiment t = new TExperiment().fromJson(teste);
        } catch (JSONException e){

        }
//        tvTitle = (TextView) findViewById(R.id.tv_title);
        ibNewExp = (ImageButton) findViewById(R.id.ib_new_experiment);
        lvExps = (ListView) findViewById(R.id.lv_my_experiments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.dashboard_toolbar);
        dao = new ScriptDAO(getBaseContext());

//        toolbar.setLogo(R.drawable.ic_temp_icon_white_2);
        setSupportActionBar(toolbar);
        checkPerm();
        refreshList();
        ibNewExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ExperimentConfigurationControllerActivity.class);
                startActivity(intent);
            }
        });
        lvExps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Script exp = myExps.get(position);
                Intent intent = new Intent(getBaseContext(), PlayerActivity.class);
                String provider = "http://".concat(exp.getAddress());
                Bundle extras = new Bundle();
                extras.putString("file", myExps.get(position).getFileName());
                intent.putExtras(extras);
                intent.setData(Uri.parse(provider));
                intent.setAction(PlayerActivity.ACTION_VIEW);
                startActivity(intent);
            }
        });
        lvExps.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
               makeDialog(myExps.get(position).getName(),
                        "Do you want to remove the selected experiments?\n(The file won't be removed from your device)",
                        DashboardActivity.this, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new ScriptDAO(getBaseContext()).delete(myExps.get(position));
                                refreshList();
                                Toast.makeText(getBaseContext(), "Script removed", Toast.LENGTH_SHORT).show();
                            }
                        });

                return true;
            }
        });
//        etAddress.setText(settings.getString("ip", null));
//        etFile.setText(settings.getString("file", null));
//        btExamples.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getBaseContext(), ExperimentConfigurationControllerActivity.class);
//                startActivity(intent);
//            }
//        });
//        btStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (etAddress != null)
//                    if (etAddress.getText() != null)
//                        if (!etAddress.getText().toString().trim().equals("")) {
//                            SharedPreferences settings = getSharedPreferences(PREF, 0);
//                            SharedPreferences.Editor editor = settings.edit();
//                            editor.putString("ip", etAddress.getText().toString());
//                            editor.putString("file", etFile.getText().toString());
//                            editor.apply();
//                            String json = "http://".concat(etAddress.getText().toString()).concat(".mpd");
//                            Intent intent = new Intent(getBaseContext(), PlayerActivity.class);
//                            Log.i("jsonjson", json);
//                            intent.setData(Uri.parse(json));
//                            intent.setAction(PlayerActivity.ACTION_VIEW);
//                            startActivity(intent);
//                        }
//
//            }
//        });

    }

    public AlertDialog makeDialog(String title, String message, Context myContext, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("OK", listener);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        refreshList();
        super.onResume();
    }

    private void checkPerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void refreshList() {
        myExps = dao.getExperiments();
        if (myExps != null) {
            adapter = new ExperimentAdapter(getBaseContext(), myExps);
            lvExps.setAdapter(adapter);
        }
    }

//    private void init() {
//        etAddress = (EditText) findViewById(R.id.et_address);
//        etFile = (EditText) findViewById(R.id.et_file_name);
//        btStart = (Button) findViewById(R.id.bt_start);
//        btExamples = (Button) findViewById(R.id.bt_examples);
//    }


}
