package com.google.android.exoplayer2.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.rnp.futebol.verona.OWAMP.OWAMP;
import br.rnp.futebol.verona.OWAMP.OWAMPArguments;
import br.rnp.futebol.verona.OWAMP.OWAMPResult;
import br.rnp.futebol.verona.pojo.BinaryQuestion;
import br.rnp.futebol.verona.pojo.Metric;
import br.rnp.futebol.verona.pojo.TExperiment;
import br.rnp.futebol.verona.pojo.TScript;
import br.rnp.futebol.verona.util.ReadyMetrics;
import br.rnp.futebol.verona.visual.activity.MainActivity;


/**
 * A helper class for periodically updating a {@link TextView} with debug information obtained from
 * a {@link SimpleExoPlayer}.
 */
public final class VOPlayerHelper implements Runnable, ExoPlayer.EventListener {

    private boolean first = true, showedQuestion = false;
    private final String[] PARAMETERS = {"loop", "index", "experiment", "userInfo"};
    private boolean openedBq, openedACR, openedDCR, finished;
    private int bitrateAux = 0, bseCont = 0, index, loop;
    private static final int REFRESH_INTERVAL_MS = 1000;
    private long playbackStartTime, bufferingStartAux;
    private boolean readyBq, readyACR, readyDCR, mig3to2, mig2to1;
    private double value1 = -1, value2 = -1;
    private final SimpleExoPlayer player;
    private boolean started, isPstFilled;
    private String initialRes, initialBR, userInfo = null;
    private final TextView textView;
    private Runnable mStatusChecker;
    private TExperiment experiment;
    private Handler mHandler;
    private int mCount = 0, stallAux;
    private Context ctx;
    private boolean bugged;
    private int choose, rttCont = 0, received = 0, sent = 0, isFrozen = 0;
    private float rttTotal = 0;

    /**
     * @param player   The {@link SimpleExoPlayer} from which debug information should be obtained.
     * @param textView The {@link TextView} that should be updated to display the information.
     */
    public VOPlayerHelper(SimpleExoPlayer player, TextView textView, TExperiment experiment, Context ctx, int index, int loop, String userInfo) {
        this.player = player;
        this.textView = textView;
        this.experiment = experiment;
        this.ctx = ctx;
        this.index = index;
        this.loop = loop;
        this.userInfo = userInfo;
    }

    /**
     * Starts periodic updates of the {@link TextView}. Must be called from the application's main
     * thread.
     */
    public void start() {
        if (started) {
            return;
        }
//        written = false;
        started = true;
        player.addListener(this);
        updateAndPost();
    }

    /**
     * Stops periodic updates of the {@link TextView}. Must be called from the application's main
     * thread.
     */
    public void stop() {
        if (!started) {
            return;
        }
        started = false;
        player.removeListener(this);
        textView.removeCallbacks(this);
    }

    // ExoPlayer.EventListener implementation.

    @Override
    public void onLoadingChanged(boolean isLoading) {
        // Do nothing.
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_BUFFERING && playWhenReady) {
            bufferingStartAux = Calendar.getInstance().getTimeInMillis();
//            stallAux = player.getStalls();
            if (isPstFilled) {
                isFrozen = 1;
                player.increaseStalls();
            }
        } else if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
            long bufferingEndAux = Calendar.getInstance().getTimeInMillis();
            if (isPstFilled) {
                if (stallAux != playbackState) {
                    isFrozen = 0;
                    player.setStallsDuration(bufferingEndAux - bufferingStartAux);
                }
            } else {
                playbackStartTime = bufferingEndAux - bufferingStartAux;
                isPstFilled = true;
            }
        }
        stallAux = playbackState;
        updateAndPost();
    }

    @Override
    public void onPositionDiscontinuity() {
        updateAndPost();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        // Do nothing.
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        // Do nothing.
    }

    // Runnable implementation.

    @Override
    public void run() {
        updateAndPost();
    }

    // Private methods.

    private void updateAndPost() {
//        setProvNum();
        final TScript script = experiment.getScripts().get(index);
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
        if (player.getCurrentPosition() > 10000 && !mig3to2) {
//            migrate(3, 2, true);
            mig3to2 = true;
        } else if (player.getCurrentPosition() > 100000 && !mig2to1) {
//            migrate(2, 1, true);
            mig2to1 = true;
        }

        if (player.getCurrentPosition() > (player.getDuration() - 1500))
            bugged = true;
//            }
//        });

//    textView.setText(getPlayerStateString() + getPlayerWindowIndexString() + getVideoString() + getAudioString());
//    textView.setText(getPlayerStateString() + "Buffer Percentage: " + player.getBufferedPercentage() + "%\n" +
//                     "Buffer Position: " + player.getBufferedPosition() + "\n");
//        Log.i("PLAYBACKSTATE", player.getPlaybackState() + "");
//        Toast.makeText(ctx, "" + player.getPlaybackState(), Toast.LENGTH_SHORT).show();
        if (!(player.getPlaybackState() == ExoPlayer.STATE_ENDED) && !bugged) {
            textView.setText(getLogString());
//            if (experiment.getQosMetrics().contains(1) || experiment.getQosMetrics().contains(2)) {
            String provAux = script.getAddress();
            OWAMPResult rtt = null;
            if (provAux != null) {
                rtt = measureRTT(provAux);
                if (rtt == null) {
                    provAux = provAux.substring(provAux.indexOf("//") + 2, provAux.length());
                    rtt = measureRTT(provAux);
                    if (rtt == null) {
                        if (provAux.contains("/"))
                            provAux = provAux.substring(0, provAux.indexOf("/"));
                        if (provAux.contains(":"))
                            provAux = provAux.substring(0, provAux.indexOf(":"));
                        rtt = measureRTT(provAux);
                    }
                }
            }
            String bsbCsv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/".concat(experiment.getFilename().concat("_sbs.csv"));
            File header = new File(bsbCsv);
            if (!header.exists())
                writePerSecond(getHeaderBsb());
            Format format = player.getVideoFormat();
            if (rtt != null) {
                rttTotal += rtt.getRtt_avg();
                rttCont++;
                received += rtt.getReceived();
                sent += rtt.getTransmitted();
                if (format != null)
                    writePerSecond(loop + "," + player.getCurrentPosition() / 1000 + "," + rtt.getRtt_avg() + "," + (100 - (100 * rtt.getReceived() / rtt.getTransmitted())) + "," + isFrozen + "," + format.bitrate + "," + format.width + "x" + format.height);
            } else {
                if (format != null)
                    writePerSecond(loop + "," + player.getCurrentPosition() / 1000 + ",,," + isFrozen + "," + format.bitrate + "," + format.width + "x" + format.height);
            }

//            }
            textView.removeCallbacks(this);
            textView.postDelayed(this, REFRESH_INTERVAL_MS);
        } else {
            checkRM();
//            boolean finished = false;
            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/".concat(experiment.getFilename().concat(".csv"));
//            String message = ctx.getString(R.string.want_to_start_new_video);
//            DialogInterface.OnClickListener listener;
            File header = new File(csv);
            if (!header.exists())
                write(getHeader());
            if (!showedQuestion) {
                ArrayList<Metric> metricsAux = checkMetrics(script.getSubjectiveQoeMetrics());
                if (metricsAux != null) {
                    final ArrayList<Metric> metrics = orderMetrics(metricsAux);
                    mStatusChecker = new Runnable() {
                        @Override
                        public void run() {
                            showedQuestion = true;
                            for (Metric m : metrics) {
                                if (m.getId() == ReadyMetrics.BINARY_QUESTION_ID && script.getQuestion() != null && !readyBq && !openedBq) {
                                    openedBq = true;
                                    makeQuestion(title(++mCount, metrics.size()), script.getQuestion(), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            choose = 1;
                                            mHandler.postDelayed(mStatusChecker, 100);
                                            readyBq = true;
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            readyBq = true;
                                            mHandler.postDelayed(mStatusChecker, 100);
                                        }
                                    });
                                } else if (m.getId() == ReadyMetrics.ACR_ID && readyBq && !openedACR) {
                                    makeAcrDcrDialog(title(++mCount, metrics.size()), true);
                                    openedACR = true;
                                } else if (m.getId() == ReadyMetrics.DCR_ID && readyBq && readyACR && !openedDCR) {
                                    openedDCR = true;
                                    makeAcrDcrDialog(title(++mCount, metrics.size()), false);
                                } else if (allReady() && !finished) {
                                    finished = true;
                                    stopRepeatingTask();
                                    write(buildCsvText().concat(dot()).concat(star(value1)).concat(dot()).concat(star(value2))
                                            .concat(dot()).concat(questionToCsv(script.getQuestion(), choose)));
                                    nextLoop(script);
                                }
                            }
                        }
                    };
                    mHandler = new Handler();
                    startRepeatingTask();
                } else {
                    showedQuestion = true;
                    write(buildCsvText().concat(appendDot(3)));
                    nextLoop(script);
                }
            }

        }
    }

    public void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            Log.i("error", e.getMessage());
        }
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String s : children) {
                boolean success = deleteDir(new File(dir, s));
                if (!success)
                    return false;
            }
        }
        return dir != null && dir.delete();
    }

    private boolean allReady() {
        return readyBq && readyACR && readyDCR;
    }

    private String star(double v) {
        if (v == -1)
            return "";
        else
            return String.valueOf(v);
    }

    private String dot() {
        return appendDot(1);
    }

    private String appendDot(int num) {
        String s = "";
        for (int i = 0; i < num; i++)
            s = s.concat(", ");
        return s;
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    private ArrayList<Metric> orderMetrics(ArrayList<Metric> metricsAux) {
        ArrayList<Metric> metrics = null;
        if (metricsAux != null) {
            ArrayList<Integer> ids = new ArrayList<>();
            metrics = new ArrayList<>();
            for (Metric m : metricsAux)
                ids.add(m.getId());
            if (ids.contains(ReadyMetrics.ACR_ID))
                metrics.add(ReadyMetrics.S_QOE_METRICS.get(ReadyMetrics.ACR_ID - 3));
            else
                readyACR = true;
            if (ids.contains(ReadyMetrics.BINARY_QUESTION_ID))
                metrics.add(ReadyMetrics.S_QOE_METRICS.get(ReadyMetrics.BINARY_QUESTION_ID - 3));
            else
                readyBq = true;
            if (ids.contains(ReadyMetrics.DCR_ID))
                metrics.add(ReadyMetrics.S_QOE_METRICS.get(ReadyMetrics.DCR_ID - 3));
            else
                readyDCR = true;
        }
        return metrics;
    }

    private String questionToCsv(BinaryQuestion bq, int answer) {
        String s;
        if (bq != null) {
            s = bq.getQuestion().concat(dot());
            s = s.concat(answer == 1 ? bq.getAnswer1() : bq.getAnswer2());
        } else {
            s = dot();
        }
        return s;
    }

    private String title(int i, int t) {
        return "Evaluation " + i + " of " + t;
    }

    @SuppressWarnings("unused")
    private boolean isACR(int mId) {
        return (mId == ReadyMetrics.ACR_ID);
    }

    private void nextLoop(TScript script) {
//        ((PlayerActivityWithMigration) ctx).releasePlayer();
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//        migrate(1, 3, false);
//            }
//        });
//        mig2to1 = true;
//        mig3to2 = true;
        if (script.getLoop() - loop > 0) {
            loop++;
        } else if (experiment.getScripts().size() > index + 1) {
//            if (index == 0)
//                migrate(3, 2, true);
//            else if (index == 1)
//                migrate(2, 1, true);
            index++;
            loop = 1;
        } else {
            makeOverDialog();
            return;
        }
        Toast.makeText(ctx, "Starting next video", Toast.LENGTH_SHORT).show();
        try {
            deleteCache(ctx);
        } catch (Exception e) {
            Log.i("error", e.getMessage());
        }
//        mHandler.postDelayed(mStatusChecker, 500);
        Bundle extras = new Bundle();
        extras.putInt(PARAMETERS[0], loop);
        extras.putInt(PARAMETERS[1], index);
        extras.putSerializable(PARAMETERS[2], experiment);
        extras.putString(PARAMETERS[3], userInfo);

        Intent intent = new Intent(ctx, PlayerActivity.class);
        intent.putExtras(extras);
        String provider = experiment.getScripts().get(index).getProvider();
        intent.setData(Uri.parse(provider));
        intent.setAction(PlayerActivity.ACTION_VIEW);

        ctx.startActivity(intent);
    }

    private void checkRM() {
        if (ReadyMetrics.O_QOE_METRICS == null || ReadyMetrics.QOS_METRICS == null || ReadyMetrics.S_QOE_METRICS == null)
            ReadyMetrics.init();
    }

    private ArrayList<Metric> checkMetrics(ArrayList<Integer> list) {
        ArrayList<Metric> metrics = null;
        if (list != null)
            if (!list.isEmpty()) {
                metrics = new ArrayList<>();
                for (Integer i : list)
                    metrics.add(ReadyMetrics.S_QOE_METRICS.get(i - 3));
            }
        return metrics;
    }

    private String buildCsvText() {
        ArrayList<Integer> metrics = experiment.getObjectiveQoeMetrics();
        Format format = player.getVideoFormat();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss", Locale.US);
        String txt = loop + "," + sdf.format(new Date());
        if (metrics.contains(6))
            txt += "," + player.getStalls();
        if (metrics.contains(7))
            txt += "," + player.getStallsDuration();
        if (metrics.contains(8))
            txt += "," + playbackStartTime;
        if (metrics.contains(9))
            txt += "," + initialRes + "," + format.width + "x" + format.height;
        if (metrics.contains(10))
            txt += "," + initialBR + "," + format.bitrate;
        metrics = experiment.getQosMetrics();
        if (metrics.contains(1))
            txt += "," + (rttTotal / rttCont);
        if (metrics.contains(2))
            if (sent != 0)
                txt += "," + (100 - ((received * 100) / sent)) + "%";
            else
                txt += ",";
        if (userInfo != null)
            try {
                JSONObject json = new JSONObject(userInfo);
                if (json.has("age"))
                    txt += "," + json.getString("age");
                else
                    txt += ",";
                if (json.has("gender"))
                    txt += "," + json.getString("gender");
                else
                    txt += ",";
                if (json.has("consumption"))
                    txt += "," + json.getString("consumption");
                else
                    txt += ",";
                if (json.has("familiar"))
                    txt += "," + json.getString("familiar");
                else
                    txt += ",";
            } catch (Exception e) {
                Log.i("error", "Error reading personal info");
            }
        txt += "";
        return txt;
    }

    private String getLogString() {
        String txt = "";
        txt += getStatusString();
        txt += getPlayerStateString();
        txt += getBufferProgress();
        txt += getVideoString();
        txt += getAudioString();
        txt += getStallsString();
        txt += "\nLoop: " + loop + "/" + experiment.getScripts().get(index).getLoop();
        return txt;
    }

    private String getPlayerStateString() {
        String text = "\nPlayback State: ";
        switch (player.getPlaybackState()) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                break;
            case ExoPlayer.STATE_READY:
                text += "ready";
                break;
            default:
                text += "unknown";
                break;
        }
        return text;
    }

    private String getHeader() {
        ArrayList<Integer> metrics = experiment.getObjectiveQoeMetrics();
        String txt = "Repetition,Timestamp";
        if (metrics.contains(6))
            txt += ",Freezes";
        if (metrics.contains(7))
            txt += ",Freezes Duration";
        if (metrics.contains(8))
            txt += ",Playback Start Time";
        if (metrics.contains(9))
            txt += ",Initial Res,Final Res";
        if (metrics.contains(10))
            txt += ",Initial Bitrate,Final Bitrate";
        metrics = experiment.getQosMetrics();
        if (metrics.contains(1))
            txt += ",Average RTT";
        if (metrics.contains(2))
            txt += ",Packet Loss";
        if (userInfo != null)
            txt += ",Age,Gender,Consumption Level,Familiarity";
        txt += ",ACR,DCR,Binary Question,Answer";
        return txt;
    }

    private String getHeaderBsb() {
        return "Repetition,Position,RTT,Packet Loss,is Frozen,Bitrate,Resolution";
    }

    public AlertDialog makeCompleteDialog(String title, String message, String okt, String cancelt, Context ctx,
                                          DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setPositiveButton(okt, ok);
        builder.setNegativeButton(cancelt, cancel);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private OWAMPResult measureRTT(String ip) {
        OWAMPArguments args = new OWAMPArguments.Builder().url(ip).timeout(5).count(1).bytes(32).build();
        return OWAMP.ping(args, OWAMP.Backend.UNIX);
    }

    public AlertDialog makeAcrDcrDialog(String title, final boolean isAcr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//        builder.setCon
        View view = LayoutInflater.from(ctx).inflate(R.layout.rating_metric_dialog, null);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar_dialog);
        builder.setTitle(title);
        builder.setMessage(isAcr ? "How good was the video quality?" : "How degradated was the video?");
        builder.setCancelable(false);
//        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isAcr) {
                    readyACR = true;
                    value1 = ratingBar.getRating();
                    mHandler.postDelayed(mStatusChecker, 100);
                } else {
                    readyDCR = true;
                    value2 = ratingBar.getRating();
                    mHandler.postDelayed(mStatusChecker, 100);
                }
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public AlertDialog makeQuestion(String title, BinaryQuestion bq, DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel) {
        return makeCompleteDialog(title, bq.getQuestion(), bq.getAnswer1(), bq.getAnswer2(), ctx, ok, cancel);
    }

    @SuppressWarnings("unused")
    public AlertDialog makeDialog(String title, String message, Context myContext, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setPositiveButton("OK", listener);
        builder.setNegativeButton("Cancel", listener);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public AlertDialog makeOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage("Thanks for participating.");
        builder.setCancelable(false);
        builder.setTitle("That's it!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ctx.startActivity(new Intent(ctx, MainActivity.class));
                ((PlayerActivity) ctx).finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private String getStallsString() {
        String text = "\nFreezes: " + player.getStalls();
        text += "\nFreezes Duration: " + player.getStallsDuration() + "ms";
        text += "\nPlayback Start Time: " + playbackStartTime + "ms";
        return text;
    }

    @SuppressWarnings("unused")
    private String getStatusString() {
        String status = ("Ready to Play: ");
        status += (player.getPlayWhenReady());
        status += ("\nIs Loading Content: ");
        status += (player.isLoading());
        return status;
    }

    private void write(String msg) {
//    }, boolean written) {
        try {
//            this.written = written;
            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/".concat(experiment.getFilename().concat(".csv"));
            BufferedWriter output = new BufferedWriter(new FileWriter(csv, true));
            output.append(msg);
            output.newLine();
            output.close();
        } catch (IOException e) {
            Log.i("Error", e.getMessage());
        }
    }

    private void writePerSecond(String msg) {
//        , boolean written) {
        try {
//            this.writenBsb = written;
            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/".concat(experiment.getFilename().concat("_sbs.csv"));
            BufferedWriter output = new BufferedWriter(new FileWriter(csv, true));
            output.append(msg);
            output.newLine();
            output.close();
        } catch (IOException e) {
            Log.i("Error", e.getMessage());
        }
    }

    private String getBufferProgress() {
        String text = ("\nBuffer Progress: ");
        text += ((player.getBufferedPosition()) / 1000) + ("s/");
        text += (player.getDuration() / 1000) + ("s (");
        text += (player.getBufferedPercentage()) + ("%)");
        return text;
    }

    @SuppressWarnings("unused")
    private String getPlayerWindowIndexString() {
        String index = ("\nPeriod Index: ");
        index += (player.getCurrentPeriodIndex());
        return (index.concat("\nWindow Index: ").concat(player.getCurrentWindowIndex() + ""));
    }

    private String getVideoString() {
        Format video = player.getVideoFormat();
        if (video == null) {
            return "";
        }
        if (first) {
            initialBR = video.bitrate + "";
            initialRes = video.width + "x" + video.height;
            first = false;
        }
        if (bitrateAux == 0)
            bitrateAux = video.bitrate;
        if (bitrateAux != video.bitrate) {
            bseCont++;
            bitrateAux = video.bitrate;
        }
        String builder = "\nVideo Resolution: ";
        builder += (video.width);
        builder += ("x");
        builder += (video.height);
        builder += ("\nVideo bitrate: ");
        builder += (video.bitrate);
        builder += (" bits/s");
        builder += ("\nVideo codecs: ");
        builder += (video.codecs);
        builder += ("\nBitrate switchs: ");
        builder += (bseCont);
//        builder += ("\nVideo framerate: ");
//        builder += (video.frameRate);
        return builder;

//        return "\n" + format.sampleMimeType + "(id:" + format.id + " r:" + format.width + "x"
//                + format.height + getDecoderCountersBufferCountString(player.getVideoDecoderCounters())
//                + ")";
    }

    private String getAudioString() {
        Format format = player.getAudioFormat();
        if (format == null) {
            return "";
        }
        return "\nAudio samplerate: ".concat(format.sampleRate + "");
//        return "\n" + format.sampleMimeType + "(id:" + format.id + " hz:" + format.sampleRate + " ch:"
//                + format.channelCount
//                + getDecoderCountersBufferCountString(player.getAudioDecoderCounters()) + ")";
    }

    @SuppressWarnings("unused")
    private String getDecoderCountersBufferCountString(String type, DecoderCounters counters) {
        if (counters == null) {
            return "";
        }
        counters.ensureUpdated();
        return "\n" + type + " counters:\n"
                + "rob:" + counters.renderedOutputBufferCount
                + " sob:" + counters.skippedOutputBufferCount
                + " dob:" + counters.droppedOutputBufferCount
                + " mcdb:" + counters.maxConsecutiveDroppedOutputBufferCount;
    }

}