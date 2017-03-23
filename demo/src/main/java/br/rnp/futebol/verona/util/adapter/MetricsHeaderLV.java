package br.rnp.futebol.verona.util.adapter;

import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.exoplayer2.demo.R;

import br.rnp.futebol.verona.pojo.Metric;

public class MetricsHeaderLV extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<Object> mData = new ArrayList<>();
    private TreeSet<Integer> sectionHeader = new TreeSet<>();

    private LayoutInflater mInflater;

    public MetricsHeaderLV(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void clear() {
        mData.clear();
    }

    public void addItem(final Object item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("ConstantConditions")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        FullViewHolder fHolder;
        int rowType = getItemViewType(position);
        if (convertView == null) {
            switch (rowType) {
                case TYPE_ITEM:
                    fHolder = new FullViewHolder();
                    convertView = mInflater.inflate(R.layout.metric_item, null);
                    fHolder.tvName = (TextView) convertView.findViewById(R.id.tv_metric_item_name);
                    fHolder.tvDesc = (TextView) convertView.findViewById(R.id.tv_metric_item_type);
                    fHolder.cbUsed = (CheckBox) convertView.findViewById(R.id.cb_metric_item_used);
                    Metric metric = (Metric) mData.get(position);
                    fHolder.tvName.setText(metric.getName());
                    fHolder.tvDesc.setText(metric.getDescription());
                    fHolder.cbUsed.setChecked(metric.isUsed());
                    break;
                case TYPE_SEPARATOR:
                    holder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.lv_header_separator, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.tv_header_separator);
                    holder.textView.setText(mData.get(position).toString());
                    convertView.setTag(holder);
                    break;
            }
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//            holder.textView.setText(mData.get(position).toString());
        }
        return convertView;
    }

    public static class ViewHolder {
        public TextView textView;
    }

    public static class FullViewHolder {
        public TextView tvName, tvDesc;
        public CheckBox cbUsed;
    }

}