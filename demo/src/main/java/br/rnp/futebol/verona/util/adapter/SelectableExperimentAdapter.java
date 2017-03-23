package br.rnp.futebol.verona.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.exoplayer2.demo.R;

import java.util.List;

import br.rnp.futebol.verona.pojo.TExperiment;

public class SelectableExperimentAdapter extends BaseAdapter {

    private Context ctx;
    private TextView tvName, tvFilename;
    private CheckBox cbUsed;
    private String ext;
    private TExperiment exp;
    private List<TExperiment> exps;


    public SelectableExperimentAdapter() {
    }

    public SelectableExperimentAdapter(Context context, List<TExperiment> exps, String ext) {
        super();
        this.ctx = context;
        this.exps = exps;
        this.ext = ext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.exp_item_with_checkbox, parent, false);
        exp = exps.get(position);

        tvName = (TextView) view.findViewById(R.id.tv_exp_item_name_selected);
        tvFilename = (TextView) view.findViewById(R.id.tv_exp_item_filename_selected);
        cbUsed = (CheckBox) view.findViewById(R.id.cb_selected);

        tvName.setText(exp.getName());
        tvFilename.setText(exp.getFilename().concat(ext));
        cbUsed.setChecked(exp.isUsedAux());

        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return exps.get(position);
    }

    @Override
    public int getCount() {
        return exps.size();
    }
}
