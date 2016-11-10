package br.rnp.futebol.vocoliseu.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import br.rnp.futebol.vocoliseu.pojo.Metric;
import com.google.android.exoplayer2.demo.R;

import java.util.List;

/**
 * Created by camargo on 18/10/16.
 */
public class MetricAdapter extends BaseAdapter {

    private Context ctx;
    private TextView tvName, tvType;
    private CheckBox cbUsed;
    private Metric metric;
    private List<Metric> metrics;


    public MetricAdapter() {}

    public MetricAdapter(Context context, List<Metric> metrics) {
        super();
        this.ctx = context;
        this.metrics = metrics;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.metric_item, parent, false);
        metric = metrics.get(position);
        tvName = (TextView) view.findViewById(R.id.tv_metric_item_name);
        tvType = (TextView) view.findViewById(R.id.tv_metric_item_type);
        cbUsed = (CheckBox) view.findViewById(R.id.cb_metric_item_used);
        tvName.setText(metric.getName());
        tvType.setText(metric.getTypeName());
        cbUsed.setChecked(metric.isUsed());
        return view;
    }

    @Override
    public long getItemId(int position) {
//        return metrics.get(position).getId();
        return position;
    }

    @Override
    public Object getItem(int position) {
        return metrics.get(position);
    }

    @Override
    public int getCount() {
        return metrics.size();
    }
}
