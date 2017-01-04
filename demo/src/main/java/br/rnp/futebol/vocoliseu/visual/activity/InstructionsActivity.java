package br.rnp.futebol.vocoliseu.visual.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.exoplayer2.demo.R;

import br.rnp.futebol.vocoliseu.util.adapter.InstructionsFragmentAdapter;

public class InstructionsActivity extends AppCompatActivity {

    private ViewPager pager;
    private Button btReady;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("VO", Context.MODE_PRIVATE);
        boolean jumpMain = preferences.getBoolean("VO_jump", false);
        boolean start = true;
        Intent intent = getIntent();
        Bundle extras = null;
        if (intent != null)
            extras = intent.getExtras();
        if (extras != null)
            start = extras.getBoolean("start", true);
        if (jumpMain && start)
            goToMain();
        setContentView(R.layout.instructions_controller);
        checkPerm();
        init();
        InstructionsFragmentAdapter adapter = new InstructionsFragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        btReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("VO_jump", true);
                editor.apply();
                goToMain();
            }
        });
    }

    private void checkPerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void goToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void init() {
        pager = (ViewPager) findViewById(R.id.vp_instructions);
        btReady = (Button) findViewById(R.id.bt_inst_ready);


    }
}