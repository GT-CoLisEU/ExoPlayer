package br.rnp.futebol.verona.visual.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.exoplayer2.demo.R;

import java.io.File;
import java.util.ArrayList;

import br.rnp.futebol.verona.codedcache.FileCC;
import br.rnp.futebol.verona.codedcache.MergeVideos;
import br.rnp.futebol.verona.codedcache.PlayerActivityWithCC;

public class CodedMergeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coded_merge);
        checkPerm();
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_cc_title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("Coded Cache Experiment");
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleAppearance);
        toolbar.setSubtitle("Provide the necessary information");
        toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleAppearance);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button btMerge = (Button) findViewById(R.id.bt_merge_test);
        final EditText etName = (EditText) findViewById(R.id.et_cc_name);
        etName.setText("praia");
        final EditText etExt = (EditText) findViewById(R.id.et_cc_ext);
        etExt.setText("mkv");
        final EditText etParts = (EditText) findViewById(R.id.et_cc_parts);
        etParts.setText("6");
        final EditText etProv = (EditText) findViewById(R.id.et_cc_provider);
        etProv.setText("http://143.54.12.47");
        final EditText etChunk = (EditText) findViewById(R.id.et_cc_chunk);
        etChunk.setText("10000");
        //http://www.androidbegin.com/tutorial/AndroidCommercial.3gp
//        final ArrayList<String> videos = new ArrayList<>();
//        videos.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/beatles_1.mp4");
//        videos.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/beatles_2.mp4");
//        videos.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/beatles_3.mp4");
//        videos.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/beatles_4.mp4");
        btMerge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MergeVideos mv = new MergeVideos("ride", 11, ".mp4", "ride.mp4", getBaseContext());
//                mv.execute();
                goToPlayer(new FileCC(etName.getText().toString(), etProv.getText().toString(),
                        etExt.getText().toString(), Integer.parseInt(etChunk.getText().toString()),
                        Integer.parseInt(etParts.getText().toString())));
            }
        });
    }

    private void goToPlayer(FileCC filecc) {
        Bundle extras = new Bundle();
        ArrayList<String> vids = new ArrayList<>();
        String internal = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/");
        String svr = filecc.getProvider().concat("/");
        for (int i = 0; i < filecc.getParts(); i++) {
            String filename = filecc.getName().concat("" + (i + 1)).concat("." + filecc.getExtension());
            File file = new File(internal.concat(filename));
            if (file.exists())
                vids.add(internal.concat(filename));
            else
                vids.add(svr.concat(filename));
        }
//        vids.add(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/").concat("ride1.mp4"));
//        vids.add("http://143.54.12.47:8081/vids/ride2.mp4");
        extras.putStringArrayList(PlayerActivityWithCC.URI_LIST_EXTRA, vids);
        extras.putSerializable("filecc", filecc);
        Intent intent = new Intent(getBaseContext(), PlayerActivityWithCC.class);
//        intent.setData(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/").concat("ride1.mp4")));
        intent.setAction(PlayerActivityWithCC.ACTION_VIEW_LIST);
        intent.putExtras(extras);
        startActivity(intent);
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
}