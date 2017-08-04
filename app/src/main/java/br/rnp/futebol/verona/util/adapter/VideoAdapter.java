package br.rnp.futebol.verona.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.exoplayer2.demo.R;

import java.util.List;

import br.rnp.futebol.verona.pojo.TScript;

/**
 * Created by camargo on 18/10/16.
 */
public class VideoAdapter extends BaseAdapter {

    private Context ctx;
    private TextView tvName, tcAddress;
    private TScript vid;
    private List<TScript> vids;


    public VideoAdapter() {
    }

    public VideoAdapter(Context context, List<TScript> vids) {
        super();
        this.ctx = context;
        this.vids = vids;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.video_item, parent, false);
        vid = vids.get(position);

        tvName = (TextView) view.findViewById(R.id.tv_video_item_name);
        tcAddress = (TextView) view.findViewById(R.id.tv_video_item_url);

        tvName.setText(vid.getVideo().concat(".".concat(vid.getExtension())));
        tcAddress.setText(vid.getAddress());
        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return vids.get(position);
    }

    @Override
    public int getCount() {
        return vids.size();
    }
}
