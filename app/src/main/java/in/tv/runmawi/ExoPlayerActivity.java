package in.tv.runmawi;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.PlaybackException;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import in.tv.runmawi.databinding.ActivityExoplayerBinding;

public class ExoPlayerActivity extends Activity {

    private StyledPlayerView playerView;
    private ActivityExoplayerBinding binding;
    private ExoPlayer player;


    SharedPreferences pref;
    SharedPreferences.Editor editor;

    String user_id = "";
    String user_name = "";
    String user_email = "";
    String user_status = "";
    String user_phone = "";
    String tv_key = "";
    int show=0;
     String URL = "";
    String DRM_LICENSE_URL = "";
    private static final String USER_AGENT = "ExoPlayer-Drm";
    private static final UUID DRM_SCHEME_UUID = C.WIDEVINE_UUID;

    // Example subtitle URLs
    private static final Uri ENGLISH_SUBTITLE_URL = Uri.parse("https://runmawi.in/web/movie_details/subtitles/the_sky_is_ours_pg_en.vtt");
    private static final Uri SPANISH_SUBTITLE_URL = Uri.parse("https://runmawi.in/web/movie_details/subtitles/the_sky_is_ours_pg_es.vtt");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        URL =  getIntent().getStringExtra("url");
        DRM_LICENSE_URL  =  getIntent().getStringExtra("drm_license");
        setView();
        initializePlayer();

        pref = ExoPlayerActivity.this.getSharedPreferences("RunMawiPreferences",MODE_PRIVATE);
        editor = pref.edit();

        user_id = pref.getString("user_id", "x");
        user_name=pref.getString("user_name","x");
        user_email=pref.getString("user_email","x");
        user_status=pref.getString("user_status","x");
        user_phone=pref.getString("user_phone","x");
        tv_key=pref.getString("tv_key","x");


        TextView watermark_txt=findViewById(R.id.watermark_txt);
        watermark_txt.setText(""+user_name+" \n  ID : "+user_id+" Ph : "+user_phone+"");

        TextView watermark_txt1=findViewById(R.id.watermark_txt1);
        watermark_txt1.setText(""+user_name+" \n  ID : "+user_id+" Ph : "+user_phone+"");


        TextView watermark_txt2=findViewById(R.id.watermark_txt2);
        watermark_txt2.setText(""+user_name+" \n  ID : "+user_id+" Ph : "+user_phone+"");

        TextView watermark_txt3=findViewById(R.id.watermark_txt3);
        watermark_txt3.setText(""+user_name+" \n  ID : "+user_id+" Ph : "+user_phone+"");


        final Handler handler = new Handler();
        final int delay = 10000; // 10 sec
        // 1000 milliseconds == 1 second


        handler.postDelayed(new Runnable() {
            public void run() {
                show++;
                if(show==1){
                    watermark_txt.setVisibility(View.VISIBLE);
                    watermark_txt1.setVisibility(View.GONE);
                    watermark_txt2.setVisibility(View.GONE);
                    watermark_txt3.setVisibility(View.GONE);
                }

                if(show==2){
                    watermark_txt.setVisibility(View.GONE);
                    watermark_txt1.setVisibility(View.VISIBLE);
                    watermark_txt2.setVisibility(View.GONE);
                    watermark_txt3.setVisibility(View.GONE);
                }


                if(show==3){
                    watermark_txt.setVisibility(View.GONE);
                    watermark_txt1.setVisibility(View.GONE);
                    watermark_txt2.setVisibility(View.VISIBLE);
                    watermark_txt3.setVisibility(View.GONE);
                }

                if(show==4){
                    watermark_txt.setVisibility(View.GONE);
                    watermark_txt1.setVisibility(View.GONE);
                    watermark_txt2.setVisibility(View.GONE);
                    watermark_txt3.setVisibility(View.VISIBLE);

                }

                if(show==5){
                    watermark_txt.setVisibility(View.GONE);
                    watermark_txt1.setVisibility(View.GONE);
                    watermark_txt2.setVisibility(View.GONE);
                    watermark_txt3.setVisibility(View.GONE);

                }
                if(show==40){
                    show=0;
                }



                handler.postDelayed(this, delay);
            }
        }, delay);

    }

    private void setView() {
        binding = ActivityExoplayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        playerView = binding.playerView;
    }

    private void initializePlayer() {
        DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent(USER_AGENT)
                .setTransferListener(new DefaultBandwidthMeter.Builder(this).build());

        Map<String, String> drmHeaders = new HashMap<>();
        drmHeaders.put("Referer", "https://runmawi.in");

        // Create subtitle configurations
        MediaItem.SubtitleConfiguration englishSubtitle =
                new MediaItem.SubtitleConfiguration.Builder(ENGLISH_SUBTITLE_URL)
                        .setMimeType(MimeTypes.TEXT_VTT)
                        .setLanguage("en")
                        .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                        .build();

        MediaItem.SubtitleConfiguration spanishSubtitle =
                new MediaItem.SubtitleConfiguration.Builder(SPANISH_SUBTITLE_URL)
                        .setMimeType(MimeTypes.TEXT_VTT)
                        .setLanguage("es")
                        .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                        .build();

        // Build the media item with DRM and subtitles
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(Uri.parse(URL))
                .setDrmConfiguration(
                        new MediaItem.DrmConfiguration.Builder(DRM_SCHEME_UUID)
                                .setLicenseUri(DRM_LICENSE_URL)
                                .setLicenseRequestHeaders(drmHeaders)
                                .build()
                )
              //  .setSubtitleConfigurations(ImmutableList.of(englishSubtitle, spanishSubtitle))
                .build();

        // Create the HLS media source
        HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem);

        DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
        TrackSelectionParameters trackSelectionParameters = new TrackSelectionParameters.Builder()
                .setPreferredTextLanguage("en") // Default subtitle language
                .build();
        trackSelector.setParameters(trackSelectionParameters);

        player = new ExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .setSeekForwardIncrementMs(10000)
                .setSeekBackIncrementMs(10000)
                .build();

        playerView.setPlayer(player);
        player.setMediaSource(mediaSource);
        player.setPlayWhenReady(true);

       // playerView.setShowSubtitleButton(true);

        // Ensure subtitle view is visible


        player.prepare();

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                 //   Toast.makeText(ExoPlayerActivity.this, "Subtitles Loaded", Toast.LENGTH_SHORT).show();
                } else if (state == Player.STATE_IDLE) {
               //     Toast.makeText(ExoPlayerActivity.this, "Failed to Load Subtitles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Log.e("ExoPlayer", "Playback error: " + error.getMessage());
            //    Toast.makeText(ExoPlayerActivity.this, "Playback error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onMediaItemTransition(MediaItem mediaItem, int reason) {
                if (mediaItem != null) {
                    Log.d("ExoPlayer", "Media item transitioned: " + mediaItem.mediaId);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
        }
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
            player = null;
        }
    }
}
