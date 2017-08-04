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
import br.rnp.futebol.verona.util.QOEStatisticHelper;
import br.rnp.futebol.verona.util.adapter.ResultAdapter;
import br.rnp.futebol.verona.visual.activity.graphs.QoEChartsActivity;


public class QoEResultsActivity extends Fragment {

    private View view;
    private TExperiment experiment;
    //    private TabLayout tab;
    private ListView lvQoS;
    private ArrayList<ResultPair> pairs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.qos_results_activity, container, false);
        final Button btCompare = (Button) view.findViewById(R.id.bt_compare_results);
        init();
        List<String[]> list = read("experiment.csv");
        Header header = new Header();
        header.buildHeader(list.get(0));

        final ArrayList<QOEStatisticHelper> qoePerProvider = qoePerProvider(list, header.getProviderIndex(),
                header.getFrzIndex(), header.getVideoIndex(), header.getFrzDurIndex(), header.getPST(),
                header.getIniRes(), header.getFinalRes(), header.getIniBR(), header.getFinalBR());

        for (QOEStatisticHelper qsh : qoePerProvider) {
            ResultPair pair = new ResultPair();
            String s = "";
            pair.setTitle(qsh.getProvider() + " (" + qsh.getCount() + ")");
            s += "Playback Start Time: " + round2(qsh.getPST() / qsh.getCount()) + "ms";
            s += "\nNumber of Freezes: " + round2(qsh.getFreezes() / qsh.getCount());
            s += "\nFrozen Time: " + round2(qsh.getFrzDuration() / qsh.getCount()) + "ms";
            s += "\nResolution: Initial: " + qsh.getIniRes() + " | Final: " + qsh.getFinalRes();
            s += "\nBitrate: Initial: " + qsh.getIniBR() + " | Final: " + qsh.getFinalBR();
            pair.setResults(s);
            pairs.add(pair);
        }

        btCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QoEChartsActivity.class);
                intent.putExtra("qshs", prepareJSON(qoePerProvider).toString());
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

    public JSONObject prepareJSON(ArrayList<QOEStatisticHelper> qshs) {
        JSONArray array = new JSONArray();
        JSONObject json = new JSONObject();
        try {
            for (QOEStatisticHelper qsh : qshs) {
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

    /*
            private int indexFrz = -1, indexProvider = -1,
                indexFrzDur = -1, indexPST = -1, indexFinalRes = -1,
                indexIniRes = -1, indexFinalBR = -1, indexIniBR = -1;
     */

    public ArrayList<QOEStatisticHelper> qoePerProvider(List<String[]> list, int prov, int freezes, int video,
                                                        int frzDur, int PST, int iniBR, int finalBR, int iniRes, int finalRes) {

        ArrayList<QOEStatisticHelper> objects = new ArrayList<>();
        ArrayList<String> providers = new ArrayList<>();

        for (int i = 1; i < list.size(); i++) {
            String s[] = list.get(i);
            String fullProv = s[prov].concat("/").concat(s[video]);
            if (!providers.contains(fullProv)) {
                providers.add(fullProv);

                QOEStatisticHelper qoeHelper = new QOEStatisticHelper();
                qoeHelper.setProvider(fullProv);
//                qoeHelper.setIniBR(s[iniBR]);
//                qoeHelper.setFinalBR(s[finalBR]);
//                qoeHelper.setFinalRes(s[finalRes]);
//                qoeHelper.setIniRes(s[iniRes]);
//                qoeHelper.setFreezes(Integer.valueOf(s[freezes]));
//                qoeHelper.setFrzDuration(Integer.valueOf(s[frzDur]));
//                qoeHelper.setPST(PST);

                objects.add(qoeHelper);
            }
        }
        providers.clear();
        for (int i = 1; i < list.size(); i++) {
            String s[] = list.get(i);
            String fullProv = s[prov].concat("/").concat(s[video]);
            for (QOEStatisticHelper sth : objects) {
                sth.addFinalRes(s[finalRes]);
                sth.addIniRes(s[iniRes]);
                sth.addIniBR(s[iniBR]);
                sth.addFinalBR(s[finalBR]);
                if (fullProv.equals(sth.getProvider())) {
                    sth.increaseFreezes(Integer.valueOf(s[freezes]));
                    sth.increaseFrzDur(Integer.valueOf(s[frzDur]));
                    sth.increasePST(Integer.valueOf(s[PST]));
//                    sth.increaseIniBr(Integer.valueOf(s[iniBR]));
//                    sth.increaseFinalBr(Integer.valueOf(s[finalBR]));
//                    sth.increaseIniRes(Integer.valueOf(s[iniRes]));
                    sth.increaseCounter();
                }
            }
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

//    Repetition,Timestamp,Provider,Video,Freezes,Freezes Duration,Playback Start Time,Initial Res,Final Res,Initial Bitrate,Final Bitrate,Average RTT,Packet Loss,Age,Gender,Consumption

    private class Header {
        private boolean hasRTT = false;
        private int indexFrz = -1, indexProvider = -1, indexVideo = -1,
                indexFrzDur = -1, indexPST = -1, indexFinalRes = -1,
                indexIniRes = -1, indexFinalBR = -1, indexIniBR = -1;

        public void buildHeader(String[] columns) {
            for (int i = 0; i < columns.length; i++) {
                String s = columns[i];
                switch (s) {
                    case ("Freezes"):
                        hasRTT = true;
                        indexFrz = i;
                        break;
                    case ("Provider"):
                        indexProvider = i;
                        break;
                    case ("Freezes Duration"):
                        indexFrzDur = i;
                        break;
                    case ("Playback Start Time"):
                        indexPST = i;
                        break;
                    case ("Initial Res"):
                        indexIniRes = i;
                        break;
                    case ("Final Res"):
                        indexFinalRes = i;
                        break;
                    case ("Initial Bitrate"):
                        indexIniBR = i;
                        break;
                    case ("Video"):
                        indexVideo = i;
                        break;
                    case ("Final Bitrate"):
                        indexFinalBR = i;
                        break;

                }
            }
        }


        public int getProviderIndex() {
            return indexProvider;
        }

        public boolean hasRTT() {
            return hasRTT;
        }

        public int getFrzIndex() {
            return indexFrz;
        }

        public int getVideoIndex() {
            return indexVideo;
        }

        public int getFrzDurIndex() {
            return indexFrzDur;
        }

        public int getPST() {
            return indexPST;
        }

        public int getFinalRes() {
            return indexFinalRes;
        }

        public int getIniRes() {
            return indexIniRes;
        }

        public int getFinalBR() {
            return indexFinalBR;
        }

        public int getIniBR() {
            return indexIniBR;
        }
    }

    /*
            private int indexFrz = -1, indexProvider = -1,
                indexFrzDur = -1, indexPST = -1, indexFinalRes = -1,
                indexIniRes = -1, indexFinalBR = -1, indexIniBR = -1;
     */

}