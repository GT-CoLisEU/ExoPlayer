package br.rnp.futebol.vocoliseu.visual.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.exoplayer2.demo.PlayerActivity;
import com.google.android.exoplayer2.demo.R;

import org.json.JSONObject;
import org.w3c.dom.Text;

import br.rnp.futebol.vocoliseu.pojo.TExperiment;

public class UserActivity extends AppCompatActivity {

    private EditText etAge;
    private ImageButton ibSkip, ibGo;
    private CheckBox cbMale, cbFemale;
    private RatingBar rbCons, rbFamiliar;
    private TExperiment experiment;
    private String provider;
    private int index, loop;
    private boolean ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_user_activity);
        init();
        Intent intent = getIntent();
        Bundle extras = null;
        if (intent != null)
            extras = intent.getExtras();
        if (extras != null) {
            ok = true;
            provider = extras.getString("provider");
            index = extras.getInt("index");
            loop = extras.getInt("loop");
            experiment = (TExperiment) extras.getSerializable("experiment");
        }

        final Intent newIntent = new Intent(getBaseContext(), PlayerActivity.class);
        final Bundle newExtras = new Bundle();

        newExtras.putInt("index", index);
        newExtras.putInt("loop", loop);
        newExtras.putSerializable("experiment", experiment);
        newIntent.setData(Uri.parse(provider));
        newIntent.setAction(PlayerActivity.ACTION_VIEW);

        ibSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newIntent.putExtras(newExtras);
                startActivity(newIntent);
                finish();
            }
        });

        ibGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserInfo(newExtras);
                newIntent.putExtras(newExtras);
                startActivity(newIntent);
                finish();
            }
        });

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

    }

    private void getUserInfo(Bundle bundle) {
        JSONObject json = new JSONObject();
        try {
            json.put("age", etAge.getText().toString());
            json.put("gender", cbMale.isChecked() ? "M" : "F");
            json.put("consumption", String.valueOf(rbCons.getRating()));
            json.put("familiar", String.valueOf(rbFamiliar.getRating()));
            bundle.putString("userInfo", json.toString());
        } catch (Exception e) {}
    }

    private void init() {
        etAge = (EditText) findViewById(R.id.et_age);
        ibSkip = (ImageButton) findViewById(R.id.ib_skip);
        ibGo = (ImageButton) findViewById(R.id.ib_go_ahead);
        cbMale = (CheckBox) findViewById(R.id.cb_info_male);
        cbFemale = (CheckBox) findViewById(R.id.cb_info_female);
        rbCons = (RatingBar) findViewById(R.id.rating_bar_consumption);
        rbFamiliar = (RatingBar) findViewById(R.id.rating_bar_familiar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_about_user);
//        toolbar.setNavigationIcon(R.drawable.ic_temp_icon_color_2);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("Before starting the video...");
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleAppearance);
        toolbar.setSubtitle("Provide some information (or just skip it)");
        toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleAppearance);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}