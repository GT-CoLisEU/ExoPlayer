package br.rnp.futebol.verona.util;

import java.util.ArrayList;

import br.rnp.futebol.verona.pojo.Metric;

/**
 * Created by camargo on 18/10/16.
 */
public class ReadyMetrics {

    public static ArrayList<Metric> QOS_METRICS, S_QOE_METRICS, O_QOE_METRICS;
    public static int BINARY_QUESTION_ID, ACR_ID, DCR_ID, O_QOE_INDEX_AUX, S_QOE_INDEX_AUX;

    public static void init() {
        int id = 0;

        QOS_METRICS = new ArrayList<>();
        S_QOE_METRICS = new ArrayList<>();
        O_QOE_METRICS = new ArrayList<>();

        QOS_METRICS.add(new Metric(++id, "RTT", Metric.QOS, "The delay of packet transmission between the mobile device and the video provider."));
        QOS_METRICS.add(new Metric(++id, "Packet Loss", Metric.QOS, "The percentage of how many of the transmitted packets were lost."));
        S_QOE_INDEX_AUX = id;

        S_QOE_METRICS.add(new Metric(++id, "ACR scale", Metric.S_QOE, "A scale from 1 to 5 that measures how good was the user experience about the video."));
        ACR_ID = id;
        S_QOE_METRICS.add(new Metric(++id, "DCR scale", Metric.S_QOE, "A scale from 1 to 5 that measures how degradated was the user experience comparing the current video to a previous displayed one."));
        DCR_ID = id;
        S_QOE_METRICS.add(new Metric(++id, "Binary Question", Metric.S_QOE, "A question with two possibilities of answer."));
        BINARY_QUESTION_ID = id;
        O_QOE_INDEX_AUX = id;

        O_QOE_METRICS.add(new Metric(++id, "Number of Freezes", Metric.O_QOE, "Number of stalls/freezes during the exhibition. (Do not count pauses)"));
        O_QOE_METRICS.add(new Metric(++id, "Duration of Freezes", Metric.O_QOE, "Total amount of time that the video was frozen. (Do not count pauses)"));
        O_QOE_METRICS.add(new Metric(++id, "Playback start time", Metric.O_QOE, "Amount of time that the player took to start the video after it was requested."));
        O_QOE_METRICS.add(new Metric(++id, "Initial and Final Resolution (DASH)", Metric.O_QOE, "Video bitrate of the beginning and the final of the exhibition."));
        O_QOE_METRICS.add(new Metric(++id, "Initial and Final Bitrate (DASH)", Metric.O_QOE, "Video resolution of the beginning and the final of the exhibition."));
        O_QOE_METRICS.add(new Metric(++id, "Bitrate switches (DASH)", Metric.O_QOE, "Number of times where the resolution changed."));

    }


}
