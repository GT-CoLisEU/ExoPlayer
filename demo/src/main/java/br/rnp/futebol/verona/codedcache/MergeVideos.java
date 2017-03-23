package br.rnp.futebol.verona.codedcache;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.google.android.exoplayer2.demo.PlayerActivity;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MergeVideos extends AsyncTask<String, Integer, String> {
    // The working path where the video files are located
//    private String workingPath;
    // The files name to merge
    private ArrayList<String> videosToMerge;
    private String video;
    private int parts;
    private Context ctx;
    private String ext, dest, path;

    public MergeVideos(ArrayList<String> videosToMerge) {
//        this.workingPath = workingPath;
        this.videosToMerge = videosToMerge;
    }

    public MergeVideos(String video, int parts, String ext, String dest, Context ctx) {
//        this.workingPath = workingPath;
        this.video = video;
        this.parts = parts;
        this.ext = ext;
        this.dest = dest;
        this.ctx = ctx;
        path = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/").concat(dest);
        Log.i("path", Environment.getExternalStorageDirectory().getAbsolutePath().concat("/").concat(dest));
    }

    @Override
    protected void onPreExecute() {
    }

    private void try1() {

    }

    private void merge() {
        try {
            Movie[] inMovies = new Movie[parts];
            for (int i = 0; i < parts; i++) {
                String internal = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/");
                String svr = "http://143.54.12.47/";
                String filename = video.concat("" + (i + 1)).concat(ext);
                File file = new File(internal.concat(filename));
                if (file.exists())
                    inMovies[i] = MovieCreator.build(internal.concat(filename));
                else {
                    String svrAux = svr.concat(filename);
                    try {
                        HttpURLConnection.setFollowRedirects(false);
                        // note : you may also need
                        //        HttpURLConnection.setInstanceFollowRedirects(false)
                        HttpURLConnection con = (HttpURLConnection) new URL(svrAux).openConnection();
                        con.setRequestMethod("HEAD");
                        if ((con.getResponseCode() == HttpURLConnection.HTTP_OK)) {
                            inMovies[i] = MovieCreator.build(svrAux);
                        }
                    }
                    catch (Exception e) {
                        Log.i("error_cc", e.getMessage());
                    }
                }
            }
            if (inMovies.length > 0) {

                List<Track> videoTracks = new LinkedList<>();
                List<Track> audioTracks = new LinkedList<>();

                for (Movie m : inMovies) {
                    for (Track t : m.getTracks()) {
                        if (t.getHandler().equals("soun")) {
                            audioTracks.add(t);
                        }
                        if (t.getHandler().equals("vide")) {
                            videoTracks.add(t);
                        }
                    }
                }

                Movie result = new Movie();

                if (audioTracks.size() > 0) {
                    result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                }
                if (videoTracks.size() > 0) {
                    result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                }

                Container out = new DefaultMp4Builder().build(result);

                Log.i("path", Environment.getExternalStorageDirectory().getAbsolutePath().concat("/").concat(dest));
                FileChannel fc = new RandomAccessFile(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/").concat(dest), "rw").getChannel();
                out.writeContainer(fc);
                fc.close();
                Log.i("path", "closed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected String doInBackground(String... params) {
//        merge();
        return "";
    }

//    @Override
//    protected String doInBackground(String... params) {
//        int count = videosToMerge.size();
//        try {
//            Movie[] inMovies = new Movie[count];
//            for (int i = 0; i < count; i++) {
//                File file = new File(videosToMerge.get(i));
//                if (file.exists()) {
////                    FileInputStream fis = new FileInputStream(file);
////                    FileChannel fc = fis.getChannel();
//                    inMovies[i] = MovieCreator.build(videosToMerge.get(i));
////                    fis.close();
////                    fc.close();
//                }
//            }
//            if (inMovies.length > 0) {
//
//                List<Track> videoTracks = new LinkedList<>();
//                List<Track> audioTracks = new LinkedList<>();
//
//                for (Movie m : inMovies) {
//                    for (Track t : m.getTracks()) {
//                        if (t.getHandler().equals("soun")) {
//                            audioTracks.add(t);
//                        }
//                        if (t.getHandler().equals("vide")) {
//                            videoTracks.add(t);
//                        }
////                        if (t.getHandler().equals("")) {
////
////                        }
//                    }
//                }
//
//                Movie result = new Movie();
//
//                if (audioTracks.size() > 0) {
//                    result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
//                }
//                if (videoTracks.size() > 0) {
//                    result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
//                }
//
//                Container out = new DefaultMp4Builder().build(result);
//
//                FileChannel fc = new RandomAccessFile(String.format(Environment.getExternalStorageDirectory().getAbsolutePath() + "/outputbtls.mp4"), "rw").getChannel();
//                out.writeContainer(fc);
//                fc.close();
//
////                IsoFile out = (IsoFile) new DefaultMp4Builder().build(result);
////
////                long timestamp = new Date().getTime();
////                String timestampS = "" + timestamp;
////
////                File storagePath = new File(Environment.getExternalStorageDirectory() + "");
////                storagePath.mkdirs();
////
////                File myMovie = new File(storagePath, String.format("output-%s.mp4", timestampS));
////
////                FileOutputStream fos = new FileOutputStream(myMovie);
////                FileChannel fco = fos.getChannel();
////                fco.position(0);
////                out.getBox(fco);
////                fco.close();
////                fos.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
//        mFileName += "/output.mp4";
//        return mFileName;
//    }

    @Override
    protected void onPostExecute(String value) {
        super.onPostExecute(value);
    }
}