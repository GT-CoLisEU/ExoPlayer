package br.rnp.futebol.vocoliseu.visual.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.exoplayer2.demo.PlayerActivity;
import com.google.android.exoplayer2.demo.R;

import org.json.JSONException;
import org.json.JSONObject;

import br.rnp.futebol.vocoliseu.pojo.TExperiment;
import br.rnp.futebol.vocoliseu.visual.activity.ExperimentConfigurationControllerActivity;

/**
 * Created by camargo on 20/10/16.
 */
public class InitialActivity extends AppCompatActivity {

    private EditText etAddress, etFile;
    private Button btStart, btExamples;
    private final String PREF = "VO_PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_activity);

        init();

        checkPerm();

        SharedPreferences settings = getSharedPreferences(PREF, 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.initial_toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);

        setSupportActionBar(toolbar);

        etAddress.setText(settings.getString("ip", null));
        etFile.setText(settings.getString("file", null));
        btExamples.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View view) {
                                              Intent intent = new Intent(getBaseContext(), ExperimentConfigurationControllerActivity.class);
                                              startActivity(intent);
                                          }
                                      }

        );
        btStart.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View view) {
                                           if (etAddress != null)
                                               if (etAddress.getText() != null)
                                                   if (!etAddress.getText().toString().trim().equals("")) {
                                                       SharedPreferences settings = getSharedPreferences(PREF, 0);
                                                       SharedPreferences.Editor editor = settings.edit();
                                                       editor.putString("ip", etAddress.getText().toString());
                                                       editor.putString("file", etFile.getText().toString());
                                                       editor.apply();
//                            String json = "[{\"name\": \"Teste UFRGS\", \"samples\":[{\"name\":\"Teste de video\",\"uri\":\"http://".concat(etAddress.getText().toString()).concat("/manifest.mpd\"}]}]");
                                                       String json = "http://".concat(etAddress.getText().toString()).concat(".mpd");
                                                       Intent intent = new Intent(getBaseContext(), PlayerActivity.class);
                                                       Log.i("jsonjson", json);
//                            Bundle bld = new Bundle();
//                            bld.putString("test", json);
//                            intent.putExtras(bld);
                                                       intent.setData(Uri.parse(json));
                                                       intent.setAction(PlayerActivity.ACTION_VIEW);
                                                       startActivity(intent);
                                                   }

                                       }
                                   }

        );

    }

    private void checkPerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void init() {
        etAddress = (EditText) findViewById(R.id.et_address);
        etFile = (EditText) findViewById(R.id.et_file_name);
        btStart = (Button) findViewById(R.id.bt_start);
        btExamples = (Button) findViewById(R.id.bt_examples);
    }


}
