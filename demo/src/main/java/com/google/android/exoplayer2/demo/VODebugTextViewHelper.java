///*
// * Copyright (C) 2016 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.google.android.exoplayer2.ui;
//
//import android.widget.TextView;
//import com.google.android.exoplayer2.ExoPlaybackException;
//import com.google.android.exoplayer2.ExoPlayer;
//import com.google.android.exoplayer2.Format;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.Timeline;
//import com.google.android.exoplayer2.decoder.DecoderCounters;
//
///**
// * A helper class for periodically updating a {@link TextView} with debug information obtained from
// * a {@link SimpleExoPlayer}.
// */
//public final class DebugTextViewHelper implements Runnable, ExoPlayer.EventListener {
//
//  private static final int REFRESH_INTERVAL_MS = 1000;
//
//  private final SimpleExoPlayer player;
//  private final TextView textView;
//
//  private boolean started;
//
//  /**
//   * @param player The {@link SimpleExoPlayer} from which debug information should be obtained.
//   * @param textView The {@link TextView} that should be updated to display the information.
//   */
//  public DebugTextViewHelper(SimpleExoPlayer player, TextView textView) {
//    this.player = player;
//    this.textView = textView;
//  }
//
//  /**
//   * Starts periodic updates of the {@link TextView}. Must be called from the application's main
//   * thread.
//   */
//  public void start() {
//    if (started) {
//      return;
//    }
//    started = true;
//    player.addListener(this);
//    updateAndPost();
//  }
//
//  /**
//   * Stops periodic updates of the {@link TextView}. Must be called from the application's main
//   * thread.
//   */
//  public void stop() {
//    if (!started) {
//      return;
//    }
//    started = false;
//    player.removeListener(this);
//    textView.removeCallbacks(this);
//  }
//
//  // ExoPlayer.EventListener implementation.
//
//  @Override
//  public void onLoadingChanged(boolean isLoading) {
//    // Do nothing.
//  }
//
//  @Override
//  public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//    updateAndPost();
//  }
//
//  @Override
//  public void onPositionDiscontinuity() {
//    updateAndPost();
//  }
//
//  @Override
//  public void onTimelineChanged(Timeline timeline, Object manifest) {
//    // Do nothing.
//  }
//
//  @Override
//  public void onPlayerError(ExoPlaybackException error) {
//    // Do nothing.
//  }
//
//  // Runnable implementation.
//
//  @Override
//  public void run() {
//    updateAndPost();
//  }
//
//  // Private methods.
//
//  private void updateAndPost() {
//    textView.setText(getPlayerStateString() + getPlayerWindowIndexString() + getVideoString()
//        + getAudioString());
//    textView.removeCallbacks(this);
//    textView.postDelayed(this, REFRESH_INTERVAL_MS);
//  }
//
//  private String getPlayerStateString() {
//    String text = "playWhenReady:" + player.getPlayWhenReady() + " playbackState:";
//    switch (player.getPlaybackState()) {
//      case ExoPlayer.STATE_BUFFERING:
//        text += "buffering";
//        break;
//      case ExoPlayer.STATE_ENDED:
//        text += "ended";
//        break;
//      case ExoPlayer.STATE_IDLE:
//        text += "idle";
//        break;
//      case ExoPlayer.STATE_READY:
//        text += "ready";
//        break;
//      default:
//        text += "unknown";
//        break;
//    }
//    return text;
//  }
//
//  private String getPlayerWindowIndexString() {
//    return " window:" + player.getCurrentWindowIndex();
//  }
//
//  private String getVideoString() {
//    Format format = player.getVideoFormat();
//    if (format == null) {
//      return "";
//    }
//    return "\n" + format.sampleMimeType + "(id:" + format.id + " r:" + format.width + "x"
//        + format.height + getDecoderCountersBufferCountString(player.getVideoDecoderCounters())
//        + ")";
//  }
//
//  private String getAudioString() {
//    Format format = player.getAudioFormat();
//    if (format == null) {
//      return "";
//    }
//    return "\n" + format.sampleMimeType + "(id:" + format.id + " hz:" + format.sampleRate + " ch:"
//        + format.channelCount
//        + getDecoderCountersBufferCountString(player.getAudioDecoderCounters()) + ")";
//  }
//
//  private static String getDecoderCountersBufferCountString(DecoderCounters counters) {
//    if (counters == null) {
//      return "";
//    }
//    counters.ensureUpdated();
//    return " rb:" + counters.renderedOutputBufferCount
//        + " sb:" + counters.skippedOutputBufferCount
//        + " db:" + counters.droppedOutputBufferCount
//        + " mcdb:" + counters.maxConsecutiveDroppedOutputBufferCount;
//  }
//
//}

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.rnp.futebol.vocoliseu.pojo.TExperiment;
import br.rnp.futebol.vocoliseu.pojo.TScript;
import br.rnp.futebol.vocoliseu.visual.activity.MainActivity;


/**
 * A helper class for periodically updating a {@link TextView} with debug information obtained from
 * a {@link SimpleExoPlayer}.
 */
public final class VODebugTextViewHelper implements Runnable, ExoPlayer.EventListener {

    private static final int REFRESH_INTERVAL_MS = 1000;

    private final SimpleExoPlayer player;
    private final TextView textView;
    private boolean started, isPstFilled;
    private long playbackStartTime, bufferingStartAux, bufferingEndAux;
    private int bitrateAux = 0, bseCont = 0, index;
    private boolean first = true, written = false, showedQuestion = false;
    private String initialRes, initialBR;
    private Context ctx;
    private TExperiment experiment;

    /**
     * @param player   The {@link SimpleExoPlayer} from which debug information should be obtained.
     * @param textView The {@link TextView} that should be updated to display the information.
     */
    public VODebugTextViewHelper(SimpleExoPlayer player, TextView textView, TExperiment experiment, Context ctx, int index) {
        this.player = player;
        this.textView = textView;
        this.experiment = experiment;
        this.ctx = ctx;
        this.index = index;
    }

    /**
     * Starts periodic updates of the {@link TextView}. Must be called from the application's main
     * thread.
     */
    public void start() {
        if (started) {
            return;
        }
        written = false;
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
            if (isPstFilled)
                player.increaseStalls();
        } else if (playbackState == ExoPlayer.STATE_READY) {
            bufferingEndAux = Calendar.getInstance().getTimeInMillis();
            if (isPstFilled)
                player.setStallsDuration(bufferingEndAux - bufferingStartAux);
            else {
                playbackStartTime = bufferingEndAux - bufferingStartAux;
                isPstFilled = true;
            }
        }
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
//    textView.setText(getPlayerStateString() + getPlayerWindowIndexString() + getVideoString() + getAudioString());
//    textView.setText(getPlayerStateString() + "Buffer Percentage: " + player.getBufferedPercentage() + "%\n" +
//                     "Buffer Position: " + player.getBufferedPosition() + "\n");
        if (!(player.getPlaybackState() == ExoPlayer.STATE_ENDED)) {
            textView.setText(getLogString());
            textView.removeCallbacks(this);
            textView.postDelayed(this, REFRESH_INTERVAL_MS);
        } else {
            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/".concat(experiment.getFilename().concat(".csv"));
            File header = new File(csv);
            if (!header.exists())
                write(getHeader(), false);
            if (!written)
                write(buildCsvText(), true);
            if (!showedQuestion)
                if (experiment.getScripts().size() > ++index) {
                    makeDialog("The video is finished", "Do you want to start a new video?",
                            ctx, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    TScript first = experiment.getScripts().get(index);

                                    String provider = first.getProvider();

                                    Bundle extras = new Bundle();
                                    extras.putInt("index", index);
                                    extras.putSerializable("experiment", experiment);

                                    Intent intent = new Intent(ctx, PlayerActivity.class);
                                    intent.putExtras(extras);

                                    intent.setData(Uri.parse(provider));
                                    intent.setAction(PlayerActivity.ACTION_VIEW);

                                    ctx.startActivity(intent);
                                }
                            });
                } else {
                    makeDialog("The video is finished", "That was the last one. Thanks for your participation",
                            ctx, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ctx.startActivity(new Intent(ctx, MainActivity.class));
                                    ((PlayerActivity) ctx).finish();
                                }
                            });
                }

            showedQuestion = true;

        }
    }

    private String buildCsvText() {
        Format format = player.getVideoFormat();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
        String txt = sdf.format(new Date()) + ",";
        txt += initialBR + "," + format.bitrate + "" + ",";
        txt += initialRes + "," + format.width + "x" + format.height + ",";
        txt += player.getStalls() + "," + player.getStallsDuration() + ",";
        txt += playbackStartTime;
        return txt;
    }

    private String getLogString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getStatusString());
//    builder.append(getPlayerWindowIndexString());
        builder.append(getPlayerStateString());
        builder.append(getBufferProgress());
        builder.append(getVideoString());
        builder.append(getAudioString());
//    builder.append(getDecoderCountersBufferCountString("Video", player.getVideoDecoderCounters()));
//    builder.append(getDecoderCountersBufferCountString("Audio", player.getAudioDecoderCounters()));
        builder.append(getStallsString());
        return builder.toString();
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
        return "Timestamp, Initial Bitrate, Final Bitrate, Initial Res, Final Res, Freezes, Freezes Duration, Playback Start Time";
    }

    public AlertDialog makeDialog(String title, String message, Context myContext, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("OK", listener);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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

    private void write(String msg, boolean written) {
        try {
            this.written = written;
            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/".concat(experiment.getFilename().concat(".csv"));
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
