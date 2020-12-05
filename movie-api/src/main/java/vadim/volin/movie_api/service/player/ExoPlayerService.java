package vadim.volin.movie_api.service.player;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerService {

    private static ExoPlayerService exoPlayerService;
    private long playbackPosition;
    private int currentWindow;
    private SimpleExoPlayer exoPlayer;
    private MediaSource mediaSource;
    private boolean isReleased;

    private ExoPlayerService(Context context) {
        initPlayer(context);
    }

    /**
     * @param context applicationContext
     * @return instance of ExoPlayer
     */
    public static ExoPlayerService getInstance(Context context) {
        if (exoPlayerService == null) {
            exoPlayerService = new ExoPlayerService(context);
        }
        exoPlayerService.playbackPosition = 0;
        return exoPlayerService;
    }

    public void initPlayer(Context context) {
        Uri uri = Uri.parse("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8");
        DataSource.Factory dataSourceFactory =
                new DefaultHttpDataSourceFactory(Util.getUserAgent(context, "DalStream"));
        mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        exoPlayer = new SimpleExoPlayer.Builder(context).build();
        isReleased = false;
    }

    /**
     * method start play video
     */
    public void play() {
        exoPlayer.seekTo(currentWindow, playbackPosition);
        exoPlayer.prepare(mediaSource, true, true);
        exoPlayer.setPlayWhenReady(true);
    }

    /**
     * method set pause in player
     */
    public void pause() {
        if (exoPlayer != null) {
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop();
        }
    }

    public boolean isReleased() {
        return isReleased;
    }

    public void releasePlayer() {
        if (exoPlayer != null && !isReleased) {
            playbackPosition = 0;
            exoPlayer.stop(true);
            exoPlayer.release();
            isReleased = true;
        }
    }

    /**
     * @return SimpleExoPlayer object
     */
    public SimpleExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    /**
     * Reset position for player
     */
    public void resetPosition() {
        playbackPosition = 0;
    }

}
