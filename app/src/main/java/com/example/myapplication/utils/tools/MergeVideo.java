package com.example.myapplication.utils.tools;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: AlanWang4523.
 * Date: 19/4/15 01:46.
 * Mail: alanwang4523@gmail.com
 */
public class MergeVideo {
    private final static String PREFIX_VIDEO_HANDLER = "vide";
    private final static String PREFIX_AUDIO_HANDLER = "soun";

    /**
     * 合并视频
     * @param inputVideos
     * @param outputPath
     * @throws IOException
     */
    public static void mergeVideos(List<String> inputVideos, String outputPath) throws IOException {
        List<Movie> inputMovies = new ArrayList<>();
        for (String input : inputVideos) {
            inputMovies.add(MovieCreator.build(input));
        }

        List<Track> videoTracks = new LinkedList<>();
        List<Track> audioTracks = new LinkedList<>();

        for (Movie m : inputMovies) {
            for (Track t : m.getTracks()) {
                if (PREFIX_AUDIO_HANDLER.equals(t.getHandler())) {
                    audioTracks.add(t);
                }
                if (PREFIX_VIDEO_HANDLER.equals(t.getHandler())) {
                    videoTracks.add(t);
                }
            }
        }

        Movie outputMovie = new Movie();
        if (audioTracks.size() > 0) {
            outputMovie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (videoTracks.size() > 0) {
            outputMovie.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }

        Container out = new DefaultMp4Builder().build(outputMovie);

        FileChannel fc = new RandomAccessFile(outputPath, "rw").getChannel();
        out.writeContainer(fc);
        fc.close();
    }
}

