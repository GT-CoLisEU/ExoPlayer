package br.rnp.futebol.vocoliseu.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.exoplayer2.demo.R;

import java.util.List;

import br.rnp.futebol.vocoliseu.pojo.TScript;

public class ScriptAdapter extends BaseAdapter {

    private Context ctx;
    private TextView tvVideo, tvAddress;
    private CheckBox cbUsed, cbDash;
    private TScript script;
    private List<TScript> scripts;


    public ScriptAdapter() {
    }

    public ScriptAdapter(Context context, List<TScript> scripts) {
        super();
        this.ctx = context;
        this.scripts = scripts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.script_item, parent, false);
        script = scripts.get(position);
        tvVideo = (TextView) view.findViewById(R.id.tv_script_video);
        tvAddress = (TextView) view.findViewById(R.id.tv_script_address);
        cbUsed = (CheckBox) view.findViewById(R.id.cb_script_use_dash);
//        cbDash = (CheckBox) view.findViewById(R.id.cb_use_dash_confirmation);
        tvVideo.setText(script.getVideo().concat(".").concat(script.getExtension()));
        tvAddress.setText(script.getAddress());
        cbUsed.setChecked(script.isUsedAux());
//        cbDash.setChecked(script.isUseDash());
        return view;
    }

    @Override
    public long getItemId(int position) {
//        return metrics.get(position).getId();
        return position;
    }

    @Override
    public Object getItem(int position) {
        return scripts.get(position);
    }

    @Override
    public int getCount() {
        return scripts.size();
    }
}
