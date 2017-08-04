package br.rnp.futebol.verona.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import br.rnp.futebol.verona.pojo.Metric;

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
    private boolean blue;
    private List<Metric> metrics;


    public MetricAdapter() {
    }

    public MetricAdapter(Context context, List<Metric> metrics) {
        super();
        this.ctx = context;
        this.metrics = metrics;
        this.blue = true;
    }

    public MetricAdapter(Context context, List<Metric> metrics, boolean blue) {
        super();
        this.ctx = context;
        this.metrics = metrics;
        this.blue = blue;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.metric_item, parent, false);
        metric = metrics.get(position);
        tvName = (TextView) view.findViewById(R.id.tv_metric_item_name);
//        if (!blue)
//            tvName.setTextColor(view.getResources().getColor(R.color.gray));
        tvType = (TextView) view.findViewById(R.id.tv_metric_item_type);
        cbUsed = (CheckBox) view.findViewById(R.id.cb_metric_item_used);
//        String str = "</b> " + metric.getName() + "</b> [" + metric.getTypeName() + "]";
//        tvName.setText(Html.fromHtml(str));
        tvName.setText(metric.getName());
        tvType.setText(metric.getDescription());
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
