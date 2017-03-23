package br.rnp.futebol.verona.codedcache;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.demo.PlayerActivity;

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
import br.rnp.futebol.verona.visual.activity.MainActivity;


/**
 * A helper class for periodically updating a {@link TextView} with debug information obtained from
 * a {@link SimpleExoPlayer}.
 */
public final class VOPlayerHelperWithCC implements Runnable, ExoPlayer.EventListener {

    private static final int REFRESH_INTERVAL_MS = 50;
    private int PARTS;
    private int CHUNK_DURATION_MS;
//    private static final String LOG_CC = "LOG_VIDEO_CC";
    public static boolean EXPERIMENT_UP = true;

    private final String[] PARAMETERS = {"loop"};
    private final SimpleExoPlayer player;
    private final TextView textView;

    private long playbackStartTime, bufferingStartAux;
    private boolean started, isPstFilled;//, checkedChunk;
    private Context ctx;
    private int loop, contAux, indexAux; //, counter = 0;
    private ArrayList<String> uris;
    private String prov;
    private FileCC filecc;
    private ArrayList<Long> freezes = new ArrayList<>();
//    @SuppressWarnings("unused")
//    private PlaybackControlView playbackControl;

    /**
     * @param player   The {@link SimpleExoPlayer} from which debug information should be obtained.
     * @param textView The {@link TextView} that should be updated to display the information.
     */
    public VOPlayerHelperWithCC(SimpleExoPlayer player, TextView textView, Context ctx, int loop, ArrayList<String> uris, FileCC filecc) {
        this.player = player;
        this.textView = textView;
        this.ctx = ctx;
        this.loop = loop;
        this.uris = uris;
        this.filecc = filecc;
        this.PARTS = filecc.getParts();
        this.prov = uris.get(0);
        this.indexAux = player.getCurrentWindowIndex();
        this.CHUNK_DURATION_MS = filecc.getChunkDuration();
        write("\n", "praia_full");
    }

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

//    private void log(String msg) {
//        Log.i(LOG_CC, msg);
//    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//        log("STATE: " + playbackState);
        if (playbackState == ExoPlayer.STATE_BUFFERING && playWhenReady) {
            bufferingStartAux = Calendar.getInstance().getTimeInMillis();
            if (isPstFilled)
                player.increaseStalls();
        } else if (playbackState == ExoPlayer.STATE_READY) {
            long bufferingEndAux = Calendar.getInstance().getTimeInMillis();
            if (isPstFilled) {
                player.setStallsDuration(bufferingEndAux - bufferingStartAux);
                freezes.add(bufferingEndAux - bufferingStartAux);
            }
            else {
                playbackStartTime = bufferingEndAux - bufferingStartAux;
                isPstFilled = true;
            }
        }
        updateAndPost();
    }

    @Override
    public void onPositionDiscontinuity() {
//        if (!checkedChunk && counter <= PARTS) {
//            log("stall");
//        writeFull();
//        }
    }

    private void writeFull() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        write((player.getCurrentWindowIndex()) + ","
                + sdf.format(new Date()) + ","
                + prov + ","
                + playbackStartTime + ","
                + player.getBufferedPercentage() + ","
                + player.getStalls() + ","
                + strFreezes() + ","
                + player.getStallsDuration(), filecc.getName() + "_full");
//        if (contAux < PARTS)
//            prov = uris.get(contAux);
    }

    private void writeSbs() {
        write("", "praia_sbs");
    }

    private String strFreezes() {
        String f = "[";
        if (!freezes.isEmpty()) {
            for (int i = 0; i < (freezes.size() - 1); i++) {
                f += freezes.get(i) + "; ";
            }
            f += freezes.get(freezes.size() - 1) + "]";
        }
        return f;
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void run() {
        updateAndPost();
    }

    private void updateAndPost() {
        if (!(player.getPlaybackState() == ExoPlayer.STATE_ENDED)) {
            prov = uris.get(player.getCurrentWindowIndex());
            if (indexAux != player.getCurrentWindowIndex()) {
                writeFull();
                indexAux = player.getCurrentWindowIndex();
            }
//            playbackControl.setVideoIndex();
//            if (contAux != PlayerActivityWithCC.cont) {
//                log("ca: " + contAux);
//            contAux = PlayerActivityWithCC.cont;
//                playbackControl.setVideoIndex(contAux);
//            }
//            if (player.getCurrentPosition() > 0 && player.getCurrentPosition() < REFRESH_INTERVAL_MS + 1 && !checkedChunk) {
//                counter++;
//                log("Chunk n: " + counter);
//                checkedChunk = true;
//            } else if (player.getCurrentPosition() > CHUNK_DURATION_MS - (REFRESH_INTERVAL_MS * 2)) {
//                checkedChunk = false;
//            }
            textView.setText(getLogString());
            textView.removeCallbacks(this);
            textView.postDelayed(this, REFRESH_INTERVAL_MS);
        } else {
            writeFull();
            if ((loop - 1) == 0)
                EXPERIMENT_UP = false;
            else
                stop();
        }

    }


    @SuppressWarnings("unused")
    private void goToPlayer() {
        if (30 - loop > 0) {
            loop++;
        } else {
            makeOverDialog();
            return;
        }
        Toast.makeText(ctx, "Starting next video", Toast.LENGTH_SHORT).show();
        Bundle extras = new Bundle();
        extras.putInt(PARAMETERS[0], loop);
        ArrayList<String> vids = new ArrayList<>();
        String internal = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/");
        String svr = "http://143.54.12.47:8081/";
        for (int i = 0; i < 16; i++) {
            String filename = "bbb_sunflower".concat("" + (i + 1)).concat(".mp4");
            File file = new File(internal.concat(filename));
            if (file.exists())
                vids.add(internal.concat(filename));
            else
                vids.add(svr.concat(filename));
        }
        extras.putStringArrayList(PlayerActivityWithCC.URI_LIST_EXTRA, vids);
        Intent intent = new Intent(ctx, PlayerActivityWithCC.class);
        intent.setAction(PlayerActivityWithCC.ACTION_VIEW_LIST);
        intent.putExtras(extras);
        ctx.startActivity(intent);
    }

    @SuppressWarnings("unused")
    private String buildCsvText() {
        Format format = player.getVideoFormat();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss", Locale.US);
        String txt = sdf.format(new Date());
        txt += "," + player.getStalls();
        txt += "," + player.getStallsDuration();
        txt += "," + playbackStartTime;
//        txt += "," + (rttTotal / rttCont);
//        txt += "," + (100 - ((received * 100) / sent)) + "%";
        txt += "";
        return txt;
    }

    private String getLogString() {
        String text = "";
        text += "Chunk position: " + ((int) player.getCurrentPosition()/1000) + " / (" + ((int) ((((PlayerActivityWithCC.cont - 1) * CHUNK_DURATION_MS)  + player.getCurrentPosition())/1000)) + "/ " + (CHUNK_DURATION_MS * PARTS)/1000 + ")";
        text += getStatusString();
        text += getPlayerStateString();
        text += getBufferProgress();
        text += getVideoString();
        text += getAudioString();
        text += getStallsString();
//        text += "\nLoop: " + loop;
        text += getDecoderCountersBufferCountString("video", player.getVideoDecoderCounters());
        text += "\nSource: " + prov;
        if (prov.startsWith("http://"))
            text += " (EXTERNAL)";
        else
            text += " (CACHE)";
        text += "\nChunk: " + player.getCurrentWindowIndex();
        return text;
    }

    private String getPlayerStateString() {
        String text = " / Playback State: ";
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
        String txt = "Timestamp";
        txt += ",Freezes";
        txt += ",Freezes Duration";
        txt += ",Playback Start Time";
        return txt;
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

    @SuppressWarnings("unused")
    private OWAMPResult measureRTT(String ip) {
        OWAMPArguments args = new OWAMPArguments.Builder().url(ip).timeout(5).count(1).bytes(32).build();
        return OWAMP.ping(args, OWAMP.Backend.UNIX);
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
        text += " (" + player.getStallsDuration() + " ms)";
        text += "\nPlayback Start Time: " + playbackStartTime + "ms";
        return text;
    }

    private String getStatusString() {
        String status = ("\nReady to Play: ");
        status += (player.getPlayWhenReady());
        status += (" / Is Loading Content: ");
        status += (player.isLoading());
        return status;
    }

    private void write(String msg, String name) {
        try {
            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/".concat(name.concat(".csv"));
            BufferedWriter output = new BufferedWriter(new FileWriter(csv, true));
            output.append(msg);
            output.newLine();
            output.close();
        } catch (IOException e) {
            Log.i("teste", e.getMessage());
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
        if (video == null)
            return "";
        String builder = "\nVideo Resolution: ";
        builder += (video.width);
        builder += ("x");
        builder += (video.height);
        return builder;
    }

    private String getAudioString() {
        Format format = player.getAudioFormat();
        if (format == null) {
            return "";
        }
        return "\nAudio samplerate: ".concat(format.sampleRate + "");
    }

    @SuppressWarnings("unused")
    private String getDecoderCountersBufferCountString(String type, DecoderCounters counters) {
        if (counters == null) {
            return "";
        }
        counters.ensureUpdated();
        return "\n" + type + " counters: "
                + "rob:" + counters.renderedOutputBufferCount
                + " sob:" + counters.skippedOutputBufferCount
                + " dob:" + counters.droppedOutputBufferCount
                + " mcdb:" + counters.maxConsecutiveDroppedOutputBufferCount;
    }

}