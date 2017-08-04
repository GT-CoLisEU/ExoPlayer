package br.rnp.futebol.verona.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.demo.R;

import java.util.List;

import br.rnp.futebol.verona.pojo.TScript;

/**
 * Created by camargo on 18/10/16.
 */
public class LegendAdapter extends BaseAdapter {

    private Context ctx;
    private TextView tvName;
    private RelativeLayout color;
    private List<String> providers;
    private List<Integer> colors;


    public LegendAdapter() {
    }

    public LegendAdapter(Context context, List<String> providers, List<Integer> colors) {
        super();
        this.ctx = context;
        this.colors = colors;
        this.providers = providers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.legend_item, parent, false);

        tvName = (TextView) view.findViewById(R.id.tv_legend_item_name);
        color = (RelativeLayout) view.findViewById(R.id.tv_legend_item_color);
        tvName.setText(providers.get(position));
        color.setBackgroundColor(colors.get(position));
        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return providers.get(position);
    }

    @Override
    public int getCount() {
        return providers.size();
    }
}
