package in.tv.runmawi;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import in.tv.runmawi.databinding.ActivityExoplayerBinding;

public class ExoPlayerActivitySample extends Activity {

    private StyledPlayerView playerView;
    private ActivityExoplayerBinding binding;
    private ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView();
        initializePlayer();
    }

    private void setView() {
        binding = ActivityExoplayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        playerView = binding.playerView;
    }

    private void initializePlayer() {
        DefaultHttpDataSource.Factory defaultHttpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent(USER_AGENT)
                .setTransferListener(
                        new DefaultBandwidthMeter.Builder(this)
                                .setResetOnNetworkTypeChange(false)
                                .build()
                );

        HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(defaultHttpDataSourceFactory)
                .createMediaSource(
                        new MediaItem.Builder()
                                .setUri(Uri.parse(URL))
                                .setDrmConfiguration(
                                        new MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                                                .setLicenseUri(DRM_LICENSE_URL)
                                                .build()
                                )
                                .setMimeType(MimeTypes.APPLICATION_M3U8) // MIME type for HLS
                                .build()
                );

        player = new ExoPlayer.Builder(this)
                .setSeekForwardIncrementMs(10000)
                .setSeekBackIncrementMs(10000)
                .build();

        playerView.setPlayer(player);
        player.setMediaSource(hlsMediaSource);

        player.setPlayWhenReady(true);
        player.prepare();

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e("ExoPlayer", "Playback error: " + error.getMessage());
                // Handle error, maybe show a user-friendly message
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }

    private static final String URL =
            "https://vz-408b4d55-9c6.b-cdn.net/0bab8df9-dc0b-4d29-a49c-a900e37d6cb4/playlist.m3u8";
    // Use the correct URL for the HLS stream

    private static final String DRM_LICENSE_URL =
            "https://video.bunnycdn.com/WidevineLicense/270162/0bab8df9-dc0b-4d29-a49c-a900e37d6cb4?token=5324714327bad35be8367bab640f49a9da44c399caeff5369bf7908a148c3159&expires=1723118937";
    // Use the correct DRM license URL

    private static final String USER_AGENT = "ExoPlayer-Drm";
    private static final String drmSchemeUuid = String.valueOf(C.WIDEVINE_UUID); // DRM Type
}
