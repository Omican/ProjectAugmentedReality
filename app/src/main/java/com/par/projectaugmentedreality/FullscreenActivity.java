package com.par.projectaugmentedreality;


import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * Sample activity showing how to properly enable custom fullscreen behavior.
 * <p>
 * This is the preferred way of handling fullscreen because the default fullscreen implementation
 * will cause re-buffering of the video.
 */
public class FullscreenActivity extends YoutubeFailureRecoveryActivity implements
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        YouTubePlayer.OnFullscreenListener {

    private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
            ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

    private LinearLayout baseLayout;
    private YouTubePlayerView playerView;
    private YouTubePlayer player;
    private Button fullscreenButton;
    private CompoundButton checkbox;
    private View otherViews;
    private String VIDEO_ID;

    private boolean fullscreen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent urlIntent = getIntent();
        VIDEO_ID = urlIntent.getStringExtra("VideoURL");

        setContentView(R.layout.activity_fullscreen);
        baseLayout = (LinearLayout) findViewById(R.id.layout);
        playerView = (YouTubePlayerView) findViewById(R.id.player);
        fullscreenButton = (Button) findViewById(R.id.fullscreen_button);
        checkbox = (CompoundButton) findViewById(R.id.landscape_fullscreen_checkbox);
        otherViews = findViewById(R.id.other_views);

        checkbox.setOnCheckedChangeListener(this);
        // You can use your own button to switch to fullscreen too
        fullscreenButton.setOnClickListener(this);

        playerView.initialize(DeveloperKey.DEVELOPER_KEY, this);

        doLayout();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        this.player = player;
        setControlsEnabled();
        // Specify that we want to handle fullscreen behavior ourselves.
       // player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
       // player.setOnFullscreenListener(this);
        if (!wasRestored) {
            player.cueVideo(VIDEO_ID);
        }
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return playerView;
    }

    @Override
    public void onClick(View v) {
        player.setFullscreen(!fullscreen);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int controlFlags = YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
  /*      if (isChecked) {
            // If you use the FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE, your activity's normal UI
            // should never be laid out in landscape mode (since the video will be fullscreen whenever the
            // activity is in landscape orientation). Therefore you should set the activity's requested
            // orientation to portrait. Typically you would do this in your AndroidManifest.xml, we do it
            // programmatically here since this activity s fullscreen behavior both with and without
            // this flag).
            setRequestedOrientation(PORTRAIT_ORIENTATION);
            controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            controlFlags &= ~YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
        }*/
        player.setFullscreenControlFlags(controlFlags);
    }

    private void doLayout() {
        LinearLayout.LayoutParams playerParams =
                (LinearLayout.LayoutParams) playerView.getLayoutParams();
        if (fullscreen) {
            // When in fullscreen, the visibility of all other views than the player should be set to
            // GONE and the player should be laid out across the whole screen.

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            playerParams.width = LayoutParams.MATCH_PARENT;
            playerParams.height = LayoutParams.MATCH_PARENT;

            otherViews.setVisibility(View.GONE);
        } else {
            // This layout is up to you - this is just a simple example (vertically stacked boxes in
            // portrait, horizontally stacked in landscape).
            otherViews.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams otherViewsParams = otherViews.getLayoutParams();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                playerParams.width = otherViewsParams.width = 0;
                playerParams.height = WRAP_CONTENT;
                otherViewsParams.height = MATCH_PARENT;
                playerParams.weight = 1;
                baseLayout.setOrientation(LinearLayout.HORIZONTAL);
            } else {
                playerParams.width = otherViewsParams.width = MATCH_PARENT;
                playerParams.height = WRAP_CONTENT;
                playerParams.weight = 0;
                otherViewsParams.height = 0;
                baseLayout.setOrientation(LinearLayout.VERTICAL);
            }
            setControlsEnabled();
        }
    }

    private void setControlsEnabled() {
        checkbox.setEnabled(player != null
                && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
        fullscreenButton.setEnabled(player != null);
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        fullscreen = true;
        doLayout();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doLayout();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, CloudReco.class);
        startActivity(intent);
    }

}
