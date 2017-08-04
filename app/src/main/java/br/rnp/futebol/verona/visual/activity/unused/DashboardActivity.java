package br.rnp.futebol.verona.visual.activity.unused;

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

import br.rnp.futebol.verona.dao.unused.ScriptDAO;
import br.rnp.futebol.verona.pojo.unused.Script;
import br.rnp.futebol.verona.util.adapter.ExperimentAdapter;

import br.rnp.futebol.verona.exoplayerlegacy.PlayerActivity;
import com.google.android.exoplayer2.demo.R;


import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private ImageButton ibNewExp;
    private List<Script> myExps;
    //    private TextView tvTitle;
    private ListView lvExps;
    private ExperimentAdapter adapter;
    private ScriptDAO dao;
//    private DrawerLayout drawer;

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.dashboard_toolbar);
//        NavigationView navView = (NavigationView) findViewById(R.id.main_drawer);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);

        setSupportActionBar(toolbar);

//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

//        navView.setNavigationItemSelectedListener(this);

        dao = new ScriptDAO(getBaseContext());
        checkPerm();
        refreshList();
        ibNewExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ExperimentControllerActivity.class);
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
    }

    public void init() {
        ibNewExp = (ImageButton) findViewById(R.id.ib_new_experiment);
        lvExps = (ListView) findViewById(R.id.lv_my_experiments);
//        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
//            adapter = new ExperimentAdapter(getBaseContext(), myExps);
//            lvExps.setAdapter(adapter);
        }
    }

}
