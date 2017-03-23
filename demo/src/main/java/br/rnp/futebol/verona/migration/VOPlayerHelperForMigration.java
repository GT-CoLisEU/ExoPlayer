package br.rnp.futebol.verona.migration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import br.rnp.futebol.verona.OWAMP.OWAMP;
import br.rnp.futebol.verona.OWAMP.OWAMPArguments;
import br.rnp.futebol.verona.OWAMP.OWAMPResult;
import br.rnp.futebol.verona.pojo.TExperiment;
import br.rnp.futebol.verona.pojo.TScript;
import br.rnp.futebol.verona.util.VolleyRequest;
import br.rnp.futebol.verona.visual.activity.MainActivity;


/**
 * A helper class for periodically updating a {@link TextView} with debug information obtained from
 * a {@link SimpleExoPlayer}.
 */
public final class VOPlayerHelperForMigration implements Runnable, ExoPlayer.EventListener {

    private int rttCont = 0, received = 0, sent = 0, isFrozen = 0, stallAux, prov;
    private final String[] PARAMETERS = {"loop", "index", "experiment", "userInfo"};
    private boolean started, isPstFilled, mig3to2, mig2to1;
    private boolean first = true, showedQuestion = false;
    private static final int REFRESH_INTERVAL_MS = 1000;
    private long playbackStartTime, bufferingStartAux;
    private int bitrateAux = 0, bseCont = 0, loop;
    private final SimpleExoPlayer player;
    private String initialRes, initialBR;
    private final TextView textView;
    private TExperiment experiment;
    private float rttTotal = 0;
    private Context ctx;

    private int LOOP_PER_TIER = 1;
    private long TIME_TO_MIG[] = {0, 0};
    private boolean migDuringVideo, bugged, isMigrating, downStream, submisse;
    private static final int CLOUD = 3, FOG = 2, AP = 1;

    /**
     * @param player   The {@link SimpleExoPlayer} from which debug information should be obtained.
     * @param textView The {@link TextView} that should be updated to display the information.
     */
    public VOPlayerHelperForMigration(SimpleExoPlayer player, TextView textView, TExperiment experiment, Context ctx, int loop, boolean migDuringVideo, boolean downStream) {
        this.player = player;
        this.textView = textView;
        this.experiment = experiment;
        this.ctx = ctx;
        this.loop = loop;
        this.bugged = false;
        this.migDuringVideo = migDuringVideo;
        this.downStream = downStream;
        this.submisse = true;
        if (experiment != null && experiment.getScripts() != null
                && !experiment.getScripts().isEmpty())
            this.LOOP_PER_TIER = experiment.getScripts().get(0).getLoop();

        if (!migDuringVideo)
            getProvName();
        else
            prov = downStream ? CLOUD : AP;
        if (downStream) {
            TIME_TO_MIG[0] = 15000;
            TIME_TO_MIG[1] = 360000;
        } else {
            TIME_TO_MIG[0] = 85000;
            TIME_TO_MIG[1] = 290000;
        }
    }

//    private void setProvNum() {
//        if (loop > LOOP_PER_TIER)
//            prov = FOG;
//        if (loop > (LOOP_PER_TIER * 2))
//            prov = AP;
//    }

    /**
     * Starts periodic updates of the {@link TextView}. Must be called from the application's main
     * thread.
     */
    public void start() {
        if (started) {
            return;
        }
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

    private String getProvName() {
        return getProvName(prov);
    }

    private String getProvName(int prov) {
        whereis();
        switch (prov) {
            case AP:
                return "AP";
            case FOG:
                return "Fog";
            case CLOUD:
                return "Cloud";
        }
        return "None";
    }

    private void updateAndPost() {
        final TScript script = experiment.getScripts().get(0);

        if (migDuringVideo)
            if (player.getCurrentPosition() > TIME_TO_MIG[0] && !mig3to2) {
                if (downStream)
                    migrate(CLOUD, FOG, true);
                else
                    migrate(AP, FOG, true);
                mig3to2 = true;
            } else if (player.getCurrentPosition() > TIME_TO_MIG[1] && !mig2to1) {
                if (downStream)
                    migrate(FOG, AP, true);
                else
                    migrate(FOG, CLOUD, true);
                mig2to1 = true;
            }

        if (player.getCurrentPosition() > (player.getDuration() - 1500))
            bugged = true;

        if (!(player.getPlaybackState() == ExoPlayer.STATE_ENDED) && !bugged) {
            Log.i("PHS", "running");
            textView.setText(getLogString());
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

            writeHeader(3);
            writeHeader(2);
            Format format = player.getVideoFormat();

            if (rtt != null) {
                Log.i("RTTX", rtt.getRtt_avg() + "");
                rttTotal += round(rtt.getRtt_avg(), 2);
                rttCont++;
                received += rtt.getReceived();
                sent += rtt.getTransmitted();
                if (format != null) {
                    String txt = loop + "," + (player.getCurrentPosition() / 1000) + "," + round(rtt.getRtt_avg(), 2)
                            + "," + (100 - (100 * rtt.getReceived() / rtt.getTransmitted())) + "," + isFrozen + ","
                            + format.bitrate + "," + format.width + "x" + format.height + "," + getProvName()
                            + "," + isMigrating;
                    Log.i("txtxt", txt);
                    writePerSecond(txt);
                }
            } else {
                if (format != null) {
                    String txt = loop + "," + player.getCurrentPosition() / 1000 + ",,," + isFrozen + ","
                            + format.bitrate + "," + format.width + "x" + format.height + "," + getProvName()
                            + "," + isMigrating;
                    Log.i("txtxt", txt);
                    writePerSecond(txt);
                }
            }

            textView.removeCallbacks(this);
            textView.postDelayed(this, REFRESH_INTERVAL_MS);

        } else {
            writeHeader(1);
            Log.i("PHS", "finished");
            if (!showedQuestion) {
                showedQuestion = true;
                write(buildCsvText().concat(appendDot(3)));
                if (!migDuringVideo) {
                    if (loop == LOOP_PER_TIER || loop == LOOP_PER_TIER * 2) {
                        if (loop == LOOP_PER_TIER)
                            if (!submisse)
                                migrate(CLOUD, FOG, true, script);
                            else
                                while (prov != FOG) {
                                    try {
                                        whereis();
                                        TimeUnit.SECONDS.sleep(3);
                                        Log.i("esperando", getProvName() + " / " + prov);
                                    } catch (Exception e) {
                                        Log.i("esperando", e.getMessage());
                                    }
                                }
                        else {
                            if (!submisse)
                                migrate(FOG, AP, true, script);
                            else
                                while (prov != AP) {
                                    try {
                                        TimeUnit.SECONDS.sleep(3);
                                        Log.i("esperando", getProvName());
                                    } catch (Exception e) {
                                        Log.i("esperando", e.getMessage());
                                    }
                                }
                        }
                    } else
                        nextLoop(script);
                } else {
                    if (downStream)
                        migrate(AP, CLOUD, true, script);
                    else
                        migrate(CLOUD, AP, true, script);
                }
            }
        }
    }

    private float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_DOWN);
        return bd.floatValue();
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

    @SuppressWarnings("unused")
    private void writeHeader(int code) {
        String sufix = "", header = "";
        switch (code) {
            case 1:
                sufix = ".csv";
//                header = getHeader();
                break;
            case 2:
                sufix = "_mig.csv";
//                header = getMigHeader();
                break;
            case 3:
                sufix = "_sbs.csv";
//                header = getHeaderSBS();
                break;
        }
        String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/".concat(experiment.getFilename().concat(sufix));
        File fHeader = new File(csv);
//        if (!fHeader.exists() && !header.equals(""))
//            write(header);
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

    private String appendDot(int num) {
        String s = "";
        for (int i = 0; i < num; i++)
            s = s.concat(", ");
        return s;
    }

    private void nextLoop(TScript script) {
        if ((!migDuringVideo ? script.getLoop() * 3 : script.getLoop()) - loop > 0) {
            loop++;
        } else {
//            migrate(AP, CLOUD, true, script);
            makeOverDialog();
            return;
        }
        Toast.makeText(ctx, "Starting next video", Toast.LENGTH_SHORT).show();
        try {
            deleteCache(ctx);
        } catch (Exception e) {
            Log.i("error", e.getMessage());
        }

        Bundle extras = new Bundle();
        extras.putInt(PARAMETERS[0], loop);
        extras.putSerializable(PARAMETERS[2], experiment);
        extras.putBoolean("mig", migDuringVideo);
        extras.putBoolean("downstream", downStream);

        Intent intent = new Intent(ctx, PlayerActivityWithMigration.class);
        intent.putExtras(extras);
        String provider = experiment.getScripts().get(0).getProvider();
        intent.setData(Uri.parse(provider));
        intent.setAction(PlayerActivityWithMigration.ACTION_VIEW);

        ctx.startActivity(intent);
    }

    private void migrate(final int src, final int dest, final boolean write) {
        migrate(src, dest, write, null);
    }

    private void migrate(final int src, final int dest, final boolean write, final TScript script) {
        isMigrating = true;
        VolleyRequest request = new VolleyRequest(ctx);
        final long begin = player.getCurrentPosition();
        request.getJSON("http://192.168.0.1:8080/migrate/" + src + "/" + dest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    isMigrating = false;
                    prov = dest;
                    JSONObject json = new JSONObject(response);
                    if (json.has("success") && json.has("duration") && write)
                        writePerMigration(json.getString("success").concat(",")
                                .concat(begin + "").concat(",").concat(json.getString("duration"))
                                .concat(",").concat(getProvName(src)).concat(",").concat(getProvName(dest)));
                    if (script != null)
                        nextLoop(script);
                } catch (JSONException e) {
                    Log.i("Migrate", e.getMessage());
                }
            }
        });
    }

    private void whereis() {
        VolleyRequest request = new VolleyRequest(ctx);
        request.getJSON("http://192.168.0.1:8080/whereis", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response != null) {
                        JSONObject json = new JSONObject(response);
                        prov = json.getInt("vm");
                        Log.i("esperando", prov+"");
                    }
                } catch (JSONException e) {
                    Log.i("Migrate", e.getMessage());
                }
            }
        });
    }

//    private void helper() {
//        isMigrating = true;
//        VolleyRequest request = new VolleyRequest(ctx);
//        request.getJSON("http://192.168.0.1:8080/whereis", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    int aux = 0;
//                    isMigrating = false;
//                    if (response != null) {
//                        JSONObject json = new JSONObject(response);
//                        aux = json.getInt("vm");
//                    }
//                    if (aux != 0 && aux != 3)
//                        migrate(aux, 3, false);
//                } catch (JSONException e) {
//                    Log.i("Migrate", e.getMessage());
//                }
//            }
//        });
//    }

    private String buildCsvText() {
        ArrayList<Integer> metrics = experiment.getObjectiveQoeMetrics();
        Format format = player.getVideoFormat();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss", Locale.US);
        String txt = loop + "," + sdf.format(new Date());
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
        txt += "";
        return txt;
    }

    private String getLogString() {
        String text = "";
        text += "Player pos: " + player.getCurrentPosition() + "/" + player.getDuration() + "\n";
        text += getStatusString();
        text += getPlayerStateString();
        text += getBufferProgress();
        text += getVideoString();
        text += getAudioString();
        text += getStallsString();
        text += "\nLoop: " + loop + "/" + (!migDuringVideo ? (experiment.getScripts().get(0).getLoop() * 3)
                : experiment.getScripts().get(0).getLoop()) + " (" + getProvName() + ")" + (isMigrating ? " - MIGRATING " : "");
        return text;
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

    @SuppressWarnings("unused")
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
        return txt;
    }

    @SuppressWarnings("unused")
    private String getHeaderSBS() {
        return "Repetition,Position,RTT,Packet Loss,is Frozen,Bitrate,Resolution,Provider";
    }

    @SuppressWarnings("unused")
    private String getMigHeader() {
        return "Success,Position,Duration,From,To";
    }

    @SuppressWarnings("unused")
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
//                ((PlayerActivity) ctx).finish();
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

    private String getStatusString() {
        String status = ("Ready to Play: ");
        status += (player.getPlayWhenReady());
        status += ("\nIs Loading Content: ");
        status += (player.isLoading());
        return status;
    }

    private void write(String msg) {
        try {
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
        try {
            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/".concat(experiment.getFilename().concat("_sbs.csv"));
            BufferedWriter output = new BufferedWriter(new FileWriter(csv, true));
            output.append(msg);
            output.newLine();
            output.close();
        } catch (IOException e) {
            Log.i("Error", e.getMessage());
        }
    }

    private void writePerMigration(String msg) {
        try {
//            this.writenBsb = written;

            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/".concat(experiment.getFilename().concat("_mig.csv"));
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