package br.rnp.futebol.verona.visual.activity.experiment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.exoplayer2.demo.R;

import br.rnp.futebol.verona.pojo.TExperiment;
import br.rnp.futebol.verona.util.adapter.ResultsFragmentAdapter;


public class ViewResultsActivity extends AppCompatActivity {

    private TExperiment experiment;
    private TabLayout tab;
//    private ListView lvQoS, lvQoE;
//    private ResultAdapter adapter;
//    private ArrayList<ResultPair> pairs;
    //private TextView tvRtt, tvLoss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_results_activity);
        init();

        final ViewPager vpResults = (ViewPager) findViewById(R.id.vp_results);
        ResultsFragmentAdapter adapter = new ResultsFragmentAdapter(getSupportFragmentManager());
        vpResults.setAdapter(adapter);

        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpResults.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });
//        ArrayList<Float> rtts = null;
//        ArrayList<String> providers = new ArrayList<String>(Arrays.asList(list.get(2)));
//        if (header.hasRTT())
//            rtts = listRTT(list, header.getRTTIndex());
//        if (rtts != null && !rtts.isEmpty())
//            tvRtt.setText("\tAverage: " + round2(average(rtts)) + "ms\n\tStandard Deviation: " + round2(standardDeviation(rtts)) + "ms");

//        int cont = 0;
//        for (String[] s : list) {
//            String line = "";
//            for (int i = 0; i < s.length; i++)
//                line += s[i] + " ";
//            Log.i("line", cont + ":" + line);
//            cont++;
//        }

    }

//    public ArrayList<QOSStatisticHelper> qosPerProvider(List<String[]> list, int strParamIndex, int valueIndex, int pcktLossIndex) {
//        ArrayList<QOSStatisticHelper> objects = new ArrayList<>();
//        ArrayList<String> providers = new ArrayList<>();
//        for (int i = 1; i < list.size(); i++) {
//            String s[] = list.get(i);
//            if (!providers.contains(s[strParamIndex])) {
//                providers.add(s[strParamIndex]);
//                objects.add(new QOSStatisticHelper(0F, s[strParamIndex]));
//            }
//        }
//        providers.clear();
//        for (int i = 1; i < list.size(); i++) {
//            String s[] = list.get(i);
//            for (QOSStatisticHelper sth : objects) {
//                if (s[strParamIndex].equals(sth.getProvider())) {
//                    Float value = Float.parseFloat(s[valueIndex]);
//                    Float loss = Float.parseFloat(s[pcktLossIndex].replace("%", ""));
//                    sth.addAux(value);
//                    sth.increaseValues(value);
//                    sth.increaseLoss(loss);
//                    sth.increaseCounter();
//                }
//            }
//        }
//        for (QOSStatisticHelper sth : objects) {
//            sth.setValue(sth.getValue() / sth.getCounter());
//            sth.setLoss(sth.getLoss() / sth.getCounter());
//        }
//        return objects;
//    }
//
//    public float average(ArrayList<Float> list) {
//        float total = 0;
//        for (Float i : list) {
//            total += i;
//        }
//        return total / list.size();
//    }
//
//    public float standardDeviation(ArrayList<Float> list) {
//        float avg = average(list);
//        float sum = 0;
//        for (Float f : list)
//            sum += (f - avg) * (f - avg);
//        Float v = list.size() > 1 ? sum / (list.size() - 1) : 0;
//        return (float) Math.sqrt(v.doubleValue());
//    }
//
//    public float round2(float num) {
//        Log.i("round2", num + "");
//        return round(num, 2);
//    }
//
//    public float round(float d, int decimalPlace) {
//        Log.i("round1", d + "");
//        BigDecimal bd = new BigDecimal(Float.toString(d));
//        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
//        return bd.floatValue();
//    }
//
//    private ArrayList<Float> listRTT(List<String[]> list, int rttIndex) {
//        ArrayList<Float> rtts = new ArrayList<>();
//        for (int i = 1; i < list.size(); i++) {
//            Float in = Float.parseFloat(list.get(i)[rttIndex]);
//            Log.i("integer", in + "");
//            rtts.add(in);
//        }
//        return rtts;
//    }
//
//    public List<String[]> read(String filename) {
//        List<String[]> resultList = new ArrayList<>();
//        InputStream inputStream = null;
//        try {
//            AssetManager assetManager = getBaseContext().getAssets();
//            inputStream = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename));
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            String csvLine;
//            while ((csvLine = reader.readLine()) != null) {
//                String[] row = csvLine.split(",");
//                resultList.add(row);
//            }
//        } catch (IOException ex) {
//            Log.i("errorCSV", "Error in reading CSV file: " + ex);
//        } finally {
//            try {
//                if (inputStream != null)
//                    inputStream.close();
//            } catch (IOException e) {
//                Log.i("errorCSV", "Error in closing CSV file: " + e);
//            }
//        }
//        return resultList;
//    }

    private void init() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("experiment"))
            experiment = (TExperiment) getIntent().getExtras().getSerializable("experiment");
        if (experiment == null)
            finish();
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_experiment_results);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("View Results");
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleAppearance);
        toolbar.setSubtitle("Experiment " + experiment.getName());
        toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleAppearance);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tab = (TabLayout) findViewById(R.id.tl_results);
        tab.addTab(tab.newTab().setText("QoE Results"));
        tab.addTab(tab.newTab().setText("QoS Results"));
        tab.setTabGravity(TabLayout.GRAVITY_FILL);
    }

}