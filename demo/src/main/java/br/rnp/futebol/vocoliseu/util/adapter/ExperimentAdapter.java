package br.rnp.futebol.vocoliseu.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import br.rnp.futebol.vocoliseu.pojo.Script;
import br.rnp.futebol.vocoliseu.pojo.TExperiment;

import com.google.android.exoplayer2.demo.R;

import java.util.List;

/**
 * Created by camargo on 18/10/16.
 */
public class ExperimentAdapter extends BaseAdapter {

    private Context ctx;
    private TextView tvName, tvFilename, tvAdress;
    private TExperiment exp;
    private List<TExperiment> exps;


    public ExperimentAdapter() {
    }

    public ExperimentAdapter(Context context, List<TExperiment> exps) {
        super();
        this.ctx = context;
        this.exps = exps;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.exp_item, parent, false);
        exp = exps.get(position);

        tvName = (TextView) view.findViewById(R.id.tv_exp_item_name);
        tvFilename = (TextView) view.findViewById(R.id.tv_exp_item_filename);
        tvAdress = (TextView) view.findViewById(R.id.tv_exp_item_address);

        tvName.setText(exp.getName());
        tvFilename.setText(exp.getFilename().concat(".txt"));
        tvAdress.setText(exp.getScripts().size() + " video(s)");

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
