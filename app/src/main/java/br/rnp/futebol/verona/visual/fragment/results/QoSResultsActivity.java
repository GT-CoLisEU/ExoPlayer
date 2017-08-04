package br.rnp.futebol.verona.visual.fragment.results;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.exoplayer2.demo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.rnp.futebol.verona.pojo.ResultPair;
import br.rnp.futebol.verona.pojo.TExperiment;
import br.rnp.futebol.verona.util.QOSStatisticHelper;
import br.rnp.futebol.verona.util.adapter.ResultAdapter;
import br.rnp.futebol.verona.visual.activity.graphs.QoSChartsActivity;


public class QoSResultsActivity extends Fragment {

    private View view;
    private TExperiment experiment;
    //    private TabLayout tab;
    private ListView lvQoS;
    private ArrayList<ResultPair> pairs;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.qos_results_activity, container, false);
        final Button btCompare = (Button) view.findViewById(R.id.bt_compare_results);
        init();
        List<String[]> list = read("experiment.csv");
        Header header = new Header();
        header.buildHeader(list.get(0));

        final ArrayList<QOSStatisticHelper> qosPerProvider = qosPerProvider(list, header.getProviderIndex(), header.getRTTIndex(), header.getPcktLossIndex());

        for (QOSStatisticHelper qsh : qosPerProvider) {
            ResultPair pair = new ResultPair();
            String s = "";
            pair.setTitle(qsh.getProvider() + " (" + qsh.getCounter() + ")");
            s += "Average RTT: " + round2(qsh.getValue()) + "ms";
            s += "\nRTT Standard Deviation: " + round2(standardDeviation(qsh.getAuxiliar())) + "ms";
            s += "\nPacket Loss: " + round2(qsh.getLoss()) + "%";
            pair.setResults(s);
            pairs.add(pair);
        }

        btCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QoSChartsActivity.class);
                intent.putExtra("qshs", prepareJSON(qosPerProvider).toString());
                startActivity(intent);
            }
        });
//        for (int i = 0; i < qosPerProvider.size() - 1; i++) {
//            ResultPair pair = new ResultPair();
//            pair.setTitle(qosPerProvider.get(i).getProvider());
//            pair.setResults("");
//
//            QOSStatisticHelper s = qosPerProvider.get(i);
//            rtt += "\t\t" + s.getProvider() + ": " + round2(s.getValue()) + "ms +- "
//                    + round2(standardDeviation(s.getAuxiliar())) + "ms\n";
//            loss += "\t\t" + s.getProvider() + ": " + round2(s.getLoss()) + "%\n";
//        }
//        QOSStatisticHelper s = qosPerProvider.get(qosPerProvider.size() - 1);
//        rtt += "\t\t" + s.getProvider() + ": " + round2(s.getValue()) + "ms +- " + round2(standardDeviation(s.getAuxiliar())) + "ms";
//        loss += "\t\t" + s.getProvider() + ": " + round2(s.getLoss()) + "%";

//        pairs.add(new ResultPair("Round Trip Time (RTT)", rtt));
//        pairs.add(new ResultPair("Packet Loss", loss));
        ResultAdapter adapter = new ResultAdapter(getActivity(), pairs);
        lvQoS.setAdapter(adapter);

        return view;
    }

    public JSONObject prepareJSON(ArrayList<QOSStatisticHelper> qshs) {
        JSONArray array = new JSONArray();
        JSONObject json = new JSONObject();
        try {
            for (QOSStatisticHelper qsh : qshs) {
                JSONObject obj = new JSONObject();
                obj.put("qsh", qsh.toJSON().toString());
                array.put(obj);
            }
            json.put("array", array);
        } catch (JSONException e) {
            Log.i("error1", e.getMessage());
        }
        return json;
    }

    public ArrayList<QOSStatisticHelper> qosPerProvider(List<String[]> list, int strParamIndex, int valueIndex, int pcktLossIndex) {
        ArrayList<QOSStatisticHelper> objects = new ArrayList<>();
        ArrayList<String> providers = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            String s[] = list.get(i);
            if (!providers.contains(s[strParamIndex])) {
                providers.add(s[strParamIndex]);
                objects.add(new QOSStatisticHelper(0F, s[strParamIndex]));
            }
        }
        providers.clear();
        for (int i = 1; i < list.size(); i++) {
            String s[] = list.get(i);
            for (QOSStatisticHelper sth : objects) {
                if (s[strParamIndex].equals(sth.getProvider())) {
                    Float value = Float.parseFloat(s[valueIndex]);
                    Float loss = Float.parseFloat(s[pcktLossIndex].replace("%", ""));
                    sth.addAux(value);
                    sth.increaseValues(value);
                    sth.increaseLoss(loss);
                    sth.increaseCounter();
                }
            }
        }
        for (QOSStatisticHelper sth : objects) {
            sth.setValue(sth.getValue() / sth.getCounter());
            sth.setLoss(sth.getLoss() / sth.getCounter());
        }
        return objects;
    }

    public float average(ArrayList<Float> list) {
        float total = 0;
        for (Float i : list) {
            total += i;
        }
        return total / list.size();
    }

    public float standardDeviation(ArrayList<Float> list) {
        float avg = average(list);
        float sum = 0;
        for (Float f : list)
            sum += (f - avg) * (f - avg);
        Float v = list.size() > 1 ? sum / (list.size() - 1) : 0;
        return (float) Math.sqrt(v.doubleValue());
    }

    public float round2(float num) {
        Log.i("round2", num + "");
        return round(num, 2);
    }

    public float round(float d, int decimalPlace) {
        Log.i("round1", d + "");
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private ArrayList<Float> listRTT(List<String[]> list, int rttIndex) {
        ArrayList<Float> rtts = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            Float in = Float.parseFloat(list.get(i)[rttIndex]);
            Log.i("integer", in + "");
            rtts.add(in);
        }
        return rtts;
    }

    public List<String[]> read(String filename) {
        List<String[]> resultList = new ArrayList<>();
        InputStream inputStream = null;
        try {
//            AssetManager assetManager = getBaseContext().getAssets();
            inputStream = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                resultList.add(row);
            }
        } catch (IOException ex) {
            Log.i("errorCSV", "Error in reading CSV file: " + ex);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                Log.i("errorCSV", "Error in closing CSV file: " + e);
            }
        }
        return resultList;
    }

    private void init() {
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey("experiment"))
            experiment = (TExperiment) intent.getExtras().getSerializable("experiment");
        if (experiment == null)
            getActivity().finish();
//        tab = (TabLayout) findViewById(R.id.tl_results);
        pairs = new ArrayList<>();
        lvQoS = (ListView) view.findViewById(R.id.lv_qos_results);
    }

    private class Header {
        private boolean hasRTT = false;
        private int indexRTT = -1, indexProvider = -1, indexPckLoss = -1;

        public void buildHeader(String[] columns) {
            for (int i = 0; i < columns.length; i++) {
                String s = columns[i];
                switch (s) {
                    case ("Average RTT"):
                        hasRTT = true;
                        indexRTT = i;
                        break;
                    case ("Provider"):
                        indexProvider = i;
                        break;
                    case ("Packet Loss"):
                        indexPckLoss = i;
                        break;

                }
            }
        }

        public boolean hasRTT() {
            return hasRTT;
        }

        public int getRTTIndex() {
            return indexRTT;
        }

        public int getProviderIndex() {
            return indexProvider;
        }

        public int getPcktLossIndex() {
            return indexPckLoss;
        }
    }

}