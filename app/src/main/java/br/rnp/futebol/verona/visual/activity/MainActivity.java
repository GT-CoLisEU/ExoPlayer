package br.rnp.futebol.verona.visual.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.demo.R;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import br.rnp.futebol.verona.OWAMP.OWAMP;
import br.rnp.futebol.verona.OWAMP.OWAMPArguments;
import br.rnp.futebol.verona.OWAMP.OWAMPResult;
import br.rnp.futebol.verona.dao.TExpForListDAO;
import br.rnp.futebol.verona.dao.TScriptDAO;
import br.rnp.futebol.verona.pojo.TExperiment;
import br.rnp.futebol.verona.pojo.TScript;
import br.rnp.futebol.verona.util.adapter.DashboardFragmentAdapter;
import br.rnp.futebol.verona.util.adapter.SelectableExperimentAdapter;
import br.rnp.futebol.verona.visual.activity.experiment.ExperimentGeneralActivity;
import br.rnp.futebol.verona.visual.activity.script.ScriptGeneralActivity;
import br.rnp.futebol.verona.visual.fragment.ExpListFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView lvAux;
    private ArrayList<TExperiment> exps;
    private TExpForListDAO dao;
    private Toolbar toolbar;
    private TabLayout tab;

    private final int SELECT_FILE_CODE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPerm();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tab = (TabLayout) findViewById(R.id.tl_main);
        final ViewPager vpMain = (ViewPager) findViewById(R.id.vp_dashboard);
        boolean noExps;

        toolbar.setSubtitle("Swipe to manage experiments and videos");
        toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleAppearance);
        setSupportActionBar(toolbar);

        tab.addTab(tab.newTab().setText("Experiments"));
        tab.addTab(tab.newTab().setText("Videos"));
        tab.setTabGravity(TabLayout.GRAVITY_FILL);

        dao = new TExpForListDAO(getBaseContext());
        exps = dao.getExpsByNames(dao.getExpsNames());
        noExps = (exps == null || exps.isEmpty());

        DashboardFragmentAdapter adapter = new DashboardFragmentAdapter(getSupportFragmentManager(), noExps ? 1 : 2);
        vpMain.setAdapter(adapter);

        if (!noExps)
            vpMain.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpMain.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });


//        lvExperiments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TExperiment exp = exps.get(position);
//                makeDialog(exp);
//
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open, R.string.close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_FILE_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    importExp(getPath(this, uri));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst())
                        return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private void importExp(String path) {
        String json = read(path);
        boolean success = false;
        try {
            TExperiment experiment = new TExperiment().fromJson(new JSONObject(json));
            if (experiment != null) {
                TExpForListDAO dao = new TExpForListDAO(getBaseContext());
                dao.insert(experiment);
                dao.close();
                refreshList();
//                refreshList();
                success = true;
            }
        } catch (Exception e) {
            // eat it
            Log.i("erro", e.getMessage());
        }
        Toast.makeText(getBaseContext(), success ? "Success!" : "Could not load the file.", Toast.LENGTH_SHORT).show();
    }

    private String read(String file) {
        try {
//            String csv = Environment.getExternalStorageDirectory().getAbsolutePath().concat(file);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String text = "", line;
            while ((line = reader.readLine()) != null) {
                text += line.concat(" ");
            }
            return text;
        } catch (IOException e) {
            return "";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        refreshList();
    }

    public void refreshList() {
        ExpListFragment fragment = new ExpListFragment();
        fragment.refreshList();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    private void refreshList() {
//        exps = dao.getExpsByNames(dao.getExpsNames());
//        if (exps != null) {
//            ExperimentAdapter adapter = new ExperimentAdapter(getBaseContext(), exps);
//            lvExperiments.setAdapter(adapter);
//        }
//    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

//    public AlertDialog makeDialog(final TExperiment exp) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(exp.getInstruction());
//        builder.setTitle("Start ".concat(exp.getName()));
//        builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                int index = 0;
//                TScript first = exp.getScripts().get(index);
//
//                String provider = first.getProvider();
//
//                Bundle extras = new Bundle();
//                extras.putInt("index", index);
//                extras.putInt("loop", 1);
//                extras.putSerializable("experiment", exp);
//
//                Intent intent;
//                if (exp.isAskInfo()) {
//                    intent = new Intent((getBaseContext()), UserActivity.class);
//                    extras.putString("provider", provider);
//                } else {
//                    intent = new Intent((getBaseContext()), PlayerActivityWithMigration.class);
//                    intent.setData(Uri.parse(provider));
//                    intent.setAction(PlayerActivityWithMigration.ACTION_VIEW);
//                }
//                intent.putExtras(extras);
//
//                startActivity(intent);
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//        return dialog;
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case (R.id.navigation_item_experiment):
                if ((new TScriptDAO(this)).getScriptsCount() > 0)
                    startActivity(new Intent(this, ExperimentGeneralActivity.class));
                else
                    Toast.makeText(getBaseContext(), "No video has been created.\nBefore creating an experiment, configure some videos!", Toast.LENGTH_LONG).show();
                break;
            case (R.id.navigation_item_video):
                startActivity(new Intent(this, ScriptGeneralActivity.class));
                break;
            case (R.id.navigation_item_import):
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/plain");
                startActivityForResult(intent, SELECT_FILE_CODE);
                break;
            case (R.id.navigation_item_open_results):
                if (dao.getExpsNames() != null)
                    if (dao.getExpsNames().size() > 0) {
                        cleanResults();
                        break;
                    }
                Toast.makeText(getBaseContext(), "No results are available", Toast.LENGTH_SHORT).show();
                break;
            case (R.id.navigation_item_export_results):
                if (dao.getExpsNames() != null)
                    if (dao.getExpsNames().size() > 0) {
                        exportResults();
                        break;
                    }
                Toast.makeText(getBaseContext(), "No results available to export", Toast.LENGTH_SHORT).show();
                break;
            case (R.id.navigation_item_export):
                if (dao.getExpsNames() != null)
                    if (dao.getExpsNames().size() > 0) {
                        exportExps();
                        break;
                    }
                Toast.makeText(getBaseContext(), "No experiment available to export", Toast.LENGTH_SHORT).show();
                break;
            case (R.id.navigation_item_tutorial):
                Intent itInst = new Intent(getBaseContext(), InstructionsActivity.class);
                itInst.putExtra("start", false);
                startActivity(itInst);
                break;
            case (R.id.navigation_item_about):
                startActivity(new Intent(getBaseContext(), AboutActivity.class));
            default:
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void write(String msg) {
        try {
            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/".concat("filertt".concat(".csv"));
            BufferedWriter output = new BufferedWriter(new FileWriter(csv, true));
            output.append(msg);
            output.newLine();
            output.close();
        } catch (IOException e) {
            Log.i("teste", e.getMessage());
        }
    }

    private OWAMPResult measureRTT(String ip) {
        OWAMPArguments args = new OWAMPArguments.Builder().url(ip).timeout(1).count(1).bytes(16).build();
        return OWAMP.ping(args, OWAMP.Backend.UNIX);
    }


    private void refreshExportList(ArrayList<TExperiment> exps, ArrayList<TExperiment> exps2, String ext) {
//        lvAux = new ListView(getBaseContext());
        for (TExperiment te : exps2)
            te.setUsedAux(false);
        for (TExperiment e : exps)
            for (TExperiment te : exps2)
                if (te.getFilename().equals(e.getFilename()))
                    te.setUsedAux(true);

        SelectableExperimentAdapter selAdapter = new SelectableExperimentAdapter(getBaseContext(), exps2, ext);
        lvAux.setAdapter(selAdapter);
    }

    private void exportExps() {
        export(".txt", "experiments");
    }

    private void exportResults() {
        export(".csv", "results");
    }

    private void cleanResults() {
        try {
            lvAux = new ListView(this);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final ArrayList<TExperiment> experiments = new ArrayList<>();
            final ArrayList<TExperiment> auxExps = exps;

            refreshExportList(experiments, auxExps, ".csv");
            lvAux.setOnItemClickListener(new ListView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> listView, View itemView, int position, long itemId) {
                    boolean added = false;
                    int count = 0;
                    for (TExperiment e : experiments)
                        if (e.getFilename().equals(exps.get(position).getFilename()))
                            added = true;
                        else
                            count++;
                    if (!added)
                        experiments.add(exps.get(position));
                    else
                        experiments.remove(count);
                    refreshExportList(experiments, auxExps, ".csv");
                }
            });

            builder.setNegativeButton(
                    "cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            dialog.dismiss();
                        }
                    });

            builder.setPositiveButton("clean", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ArrayList<Boolean> array = new ArrayList<>();
                    for (TExperiment e : experiments) {
                        File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), e.getFilename().concat(".csv"));
//                    filelocation.setReadable(true, false);
                        boolean fileDelete = filelocation.delete();
                        array.add(fileDelete);
                    }
                    int deleted = 0;
                    for (Boolean b : array)
                        if (b)
                            deleted++;
                    Toast.makeText(getBaseContext(), deleted + " of " + array.size() + " result files were cleaned", Toast.LENGTH_SHORT).show();
//                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
////                emailIntent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
//                emailIntent.setType("file/*");
//                startActivity(emailIntent);
//                dialog.cancel();
//                dialog.dismiss();
                }
            });

            builder.setTitle("Select one or more result files:");
            builder.setCancelable(false);
            builder.setView(lvAux);
//            builder.setView(lvAux);
            builder.show();
        } catch (Exception e) {
            Log.i("ERRO", e.getMessage());
        }
    }

    private void export(final String ext, String name) {
        try {
            final String sufixes[] = {"", "_sbs", "_mig"};
            lvAux = new ListView(this);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final ArrayList<TExperiment> experiments = new ArrayList<>();
            final ArrayList<TExperiment> auxExps = exps;

            refreshExportList(experiments, auxExps, ext);
            lvAux.setOnItemClickListener(new ListView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> listView, View itemView, int position, long itemId) {
                    boolean added = false;
                    int count = 0;
                    for (TExperiment e : experiments)
                        if (e.getFilename().equals(exps.get(position).getFilename()))
                            added = true;
                        else
                            count++;
                    if (!added)
                        experiments.add(exps.get(position));
                    else
                        experiments.remove(count);
                    refreshExportList(experiments, auxExps, ext);
                }
            });

            builder.setNegativeButton(
                    "cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            dialog.dismiss();
                        }
                    });

            builder.setPositiveButton("send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    ArrayList<Uri> uris = new ArrayList<>();
                    for (TExperiment e : experiments) {
                        for (String sufix: sufixes) {
                            File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), e.getFilename().concat(sufix + ext));
                            Uri path = Uri.fromFile(filelocation);
                            uris.add(path);
                        }
                    }
                    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
//                emailIntent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "VERONA experiment(s)");
                    emailIntent.setType("file/*");
                    startActivity(emailIntent);
                    dialog.cancel();
                    dialog.dismiss();
                }
            });

            builder.setTitle("Select one or more " + name + ":");
            builder.setCancelable(false);
            builder.setView(null);
            builder.setView(lvAux);
            builder.show();
        } catch (Exception e) {
            Log.i("ERRO", e.getMessage());
        }
    }

    public void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            Log.i("error", e.getMessage());
        }
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir != null && dir.delete();
    }
}
