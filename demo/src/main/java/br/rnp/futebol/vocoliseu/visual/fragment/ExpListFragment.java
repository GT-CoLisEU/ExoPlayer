package br.rnp.futebol.vocoliseu.visual.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.exoplayer2.demo.PlayerActivity;
import com.google.android.exoplayer2.demo.R;

import java.util.ArrayList;

import br.rnp.futebol.vocoliseu.dao.TExpForListDAO;
import br.rnp.futebol.vocoliseu.dao.TExperimentDAO;
import br.rnp.futebol.vocoliseu.pojo.TExperiment;
import br.rnp.futebol.vocoliseu.pojo.TScript;
import br.rnp.futebol.vocoliseu.util.adapter.ExperimentAdapter;
import br.rnp.futebol.vocoliseu.visual.activity.UserActivity;

public class ExpListFragment extends Fragment {


    private static View view;
    private static ListView lvExperiments;
    private static ArrayList<TExperiment> exps;
    private static TExpForListDAO dao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.exp_list, container, false);
        lvExperiments = (ListView) view.findViewById(R.id.lv_exps);
        dao = new TExpForListDAO(getActivity());
        refreshList();
        lvExperiments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TExperiment exp = exps.get(position);
                makeDialog(exp);

            }
        });

        lvExperiments.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                makeCompleteDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TExperiment exp = exps.get(position);
                        dao.delete(exp.getFilename());
                        Toast.makeText(getActivity(), "Experiment deleted", Toast.LENGTH_SHORT).show();
                        refreshList();
                    }
                });
                return true;
            }
        });

        return view;
    }

    public AlertDialog makeDialog(final TExperiment exp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(exp.getInstruction());
        builder.setTitle("Start ".concat(exp.getName()));
        builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int index = 0;
                TScript first = exp.getScripts().get(index);

                String provider = first.getProvider();

                Bundle extras = new Bundle();
                extras.putInt("index", index);
                extras.putInt("loop", 1);
                extras.putSerializable("experiment", exp);

                Intent intent;
                if (exp.isAskInfo()) {
                    intent = new Intent((getActivity().getBaseContext()), UserActivity.class);
                    extras.putString("provider", provider);
                } else {
                    intent = new Intent((getActivity().getBaseContext()), PlayerActivity.class);
                    intent.setData(Uri.parse(provider));
                    intent.setAction(PlayerActivity.ACTION_VIEW);
                }
                intent.putExtras(extras);

                startActivity(intent);
            }
        });
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

    public AlertDialog makeCompleteDialog(DialogInterface.OnClickListener ok) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure?");
        builder.setTitle("Delete experiment");
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


    public static void refreshList() {
        exps = dao.getExpsByNames(dao.getExpsNames());
        if (exps != null) {
            ExperimentAdapter adapter = new ExperimentAdapter(view.getContext(), exps);
            lvExperiments.setAdapter(adapter);
        }
    }

}