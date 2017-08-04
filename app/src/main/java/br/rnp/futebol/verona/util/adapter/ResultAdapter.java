package br.rnp.futebol.verona.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.exoplayer2.demo.R;

import java.util.List;

import br.rnp.futebol.verona.pojo.ResultPair;

public class ResultAdapter extends BaseAdapter {

    private Context ctx;
    private TextView tvName, tvResults;
    private ResultPair r;
    private List<ResultPair> res;


    public ResultAdapter() {
    }

    public ResultAdapter(Context context, List<ResultPair> res) {
        super();
        this.ctx = context;
        this.res = res;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.result_item, parent, false);
        r = res.get(position);

        tvName = (TextView) view.findViewById(R.id.tv_metric_name_title);
        tvResults = (TextView) view.findViewById(R.id.tv_results_list);

        tvName.setText(r.getTitle());
        tvResults.setText(r.getResults());
        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return res.get(position);
    }

    @Override
    public int getCount() {
        return res.size();
    }
}
