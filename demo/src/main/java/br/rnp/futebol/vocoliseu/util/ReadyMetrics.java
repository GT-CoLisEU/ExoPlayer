package br.rnp.futebol.vocoliseu.util;

import java.util.ArrayList;

import br.rnp.futebol.vocoliseu.pojo.Metric;

/**
 * Created by camargo on 18/10/16.
 */
public class ReadyMetrics {

    public static ArrayList<Metric> QOS_METRICS, S_QOE_METRICS, O_QOE_METRICS;

    public static void init() {
        int id = 0;

        QOS_METRICS = new ArrayList<>();
        S_QOE_METRICS = new ArrayList<>();
        O_QOE_METRICS = new ArrayList<>();

        QOS_METRICS.add(new Metric(++id, "RTT", Metric.QOS));
        QOS_METRICS.add(new Metric(++id, "Packet Loss", Metric.QOS));

        S_QOE_METRICS.add(new Metric(++id, "ACR scale", Metric.S_QOE));
        S_QOE_METRICS.add(new Metric(++id, "DCR scale", Metric.S_QOE));

        O_QOE_METRICS.add(new Metric(++id, "Number of Freezes", Metric.O_QOE));
        O_QOE_METRICS.add(new Metric(++id, "Bitrate switchs", Metric.O_QOE));
    }


}
