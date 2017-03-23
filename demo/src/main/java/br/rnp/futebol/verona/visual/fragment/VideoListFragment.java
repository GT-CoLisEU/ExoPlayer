package br.rnp.futebol.verona.visual.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.exoplayer2.demo.R;

import java.util.ArrayList;

import br.rnp.futebol.verona.dao.TScriptDAO;
import br.rnp.futebol.verona.pojo.TScript;
import br.rnp.futebol.verona.util.adapter.VideoAdapter;

public class VideoListFragment extends Fragment {


    View view;
    private ListView lvVids;
    private ArrayList<TScript> vids;
    private TScriptDAO dao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.script_list, container, false);
        lvVids = (ListView) view.findViewById(R.id.lv_scripts);
        dao = new TScriptDAO(getActivity());
        refreshList();
        return view;
    }

    private void refreshList() {
        vids = dao.getScripts();
        if (vids != null) {
            VideoAdapter adapter = new VideoAdapter(getActivity().getBaseContext(), vids);
            lvVids.setAdapter(adapter);
        }
    }
}