package br.rnp.futebol.vocoliseu.util;

import java.util.ArrayList;

import br.rnp.futebol.vocoliseu.pojo.Metric;

/**
 * Created by camargo on 18/10/16.
 */
public class ReadyMetrics {

    public static ArrayList<Metric> QOS_METRICS, S_QOE_METRICS, O_QOE_METRICS;
    public static int BINARY_QUESTION_ID, ACR_ID, DCR_ID;

    public static void init() {
        int id = 0;

        QOS_METRICS = new ArrayList<>();
        S_QOE_METRICS = new ArrayList<>();
        O_QOE_METRICS = new ArrayList<>();

        QOS_METRICS.add(new Metric(++id, "RTT", Metric.QOS));
        QOS_METRICS.add(new Metric(++id, "Packet Loss", Metric.QOS));

        S_QOE_METRICS.add(new Metric(++id, "ACR scale", Metric.S_QOE));
        ACR_ID = id;
        S_QOE_METRICS.add(new Metric(++id, "DCR scale", Metric.S_QOE));
        DCR_ID = id;
        S_QOE_METRICS.add(new Metric(++id, "Binary Question", Metric.S_QOE));
        BINARY_QUESTION_ID = id;

        O_QOE_METRICS.add(new Metric(++id, "Number of Freezes", Metric.O_QOE));
        O_QOE_METRICS.add(new Metric(++id, "Duration of Freezes", Metric.O_QOE));
        O_QOE_METRICS.add(new Metric(++id, "Playback start time", Metric.O_QOE));
        O_QOE_METRICS.add(new Metric(++id, "Initial and Final Resolution (DASH)", Metric.O_QOE));
        O_QOE_METRICS.add(new Metric(++id, "Initial and Final Bitrate (DASH)", Metric.O_QOE));
        O_QOE_METRICS.add(new Metric(++id, "Bitrate switches (DASH)", Metric.O_QOE));

    }


}
