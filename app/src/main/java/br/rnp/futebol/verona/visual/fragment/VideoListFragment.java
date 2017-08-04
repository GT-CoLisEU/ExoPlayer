package br.rnp.futebol.verona.visual.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.exoplayer2.demo.R;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import br.rnp.futebol.verona.dao.TScriptDAO;
import br.rnp.futebol.verona.pojo.TExperiment;
import br.rnp.futebol.verona.pojo.TScript;
import br.rnp.futebol.verona.util.adapter.VideoAdapter;
import br.rnp.futebol.verona.visual.activity.experiment.ExperimentGeneralActivity;
import br.rnp.futebol.verona.visual.activity.script.ScriptGeneralActivity;

public class VideoListFragment extends Fragment {


    View view;
    private ListView lvVids;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<TScript> vids;
    private TScriptDAO dao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.script_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.s_refresh_scripts);
        lvVids = (ListView) view.findViewById(R.id.lv_scripts);
        dao = new TScriptDAO(getActivity());
        refreshList();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        lvVids.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showPopup(view, i);
                return true;
            }
        });
        lvVids.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showPopup(view, i);
            }
        });
        return view;
    }

    public void showPopup(View v, final int position) {
        final TScript script = vids.get(position);
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.video_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        makeCompleteDialog(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dao.delete(script);
                                Toast.makeText(getActivity(), "Experiment deleted", Toast.LENGTH_SHORT).show();
                                refreshList();
                            }
                        });
                        break;
                    case R.id.action_edit:
                        if (script != null) {
                            Intent intent = new Intent(getActivity(), ScriptGeneralActivity.class);
                            intent.putExtra("script", script);
                            intent.putExtra("edit", true);
                            startActivity(intent);
                        }
                        break;
                }
                return false;
            }
        });
        popup.show();
    }


    public AlertDialog makeCompleteDialog(DialogInterface.OnClickListener ok) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure?");
        builder.setTitle("Delete video");
        builder.setPositiveButton("Yes", ok);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        vids = dao.getScripts();
        if (vids != null) {
            VideoAdapter adapter = new VideoAdapter(getActivity().getBaseContext(), vids);
            lvVids.setAdapter(adapter);
        }
    }
}