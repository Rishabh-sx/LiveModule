package com.rishabh.livemodule.live.player.videoplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.exoplayer2.ui.PlayerView;
import com.rishabh.livemodule.utils.AppConstants;
import com.rishabh.livemodule.utils.PlayerManager;
import com.rishabh.livemodule.R;
import com.rishabh.livemodule.live.player.playeroverlay.PlayerOverlayActivity;
import com.rishabh.livemodule.pojo.LiveIntentData;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rishabh Saxena.
 */

public class VideoPlayerActivity extends Activity implements PlayerManager.EventListener {

    private static final int REQUEST_CODE_FINISH = 101;

    @BindView(R.id.player_view)
    PlayerView playerView;

    private PlayerManager playerManager;
    private String mStreamURL;
    private LiveIntentData intentData;
    private BroadcastReceiver playerOverlayActivityReceiver;
    private String email;
    private String name;
    private String streamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        ButterKnife.bind(this);
      //  getDataFromIntent();
            getIntentData();
    }

    private void getDataFromIntent() {
        if (getIntent().hasExtra("data")
                && getIntent().getParcelableExtra("data") != null) {

            intentData = getIntent().getParcelableExtra("data");
            mStreamURL = "http://184.169.197.143:1935/live/"
                    + intentData.getStreamName()
                    + "/playlist.m3u8";

        }
    }

    public void getIntentData() {
        email = getIntent().getStringExtra(AppConstants.EMAIL);
        name = getIntent().getStringExtra(AppConstants.NAME);
        streamId = getIntent().getStringExtra(AppConstants.STREAMID);
    }

    private void initPlayer() {
        playerManager = new PlayerManager(this, this);
    }

    public void startLiveStream() {
        playerManager.init(this, playerView, mStreamURL);
    }

    @Override
    public void onStart() {
        super.onStart();
        setupReceivers();
        //initPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mStreamURL == null) {
            getDataFromIntent();
        }
        startLiveStream();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openPlayerOverlay();
            }
        }, 100);
    }

    private void openPlayerOverlay() {
        startActivityForResult(new Intent(this, PlayerOverlayActivity.class)
                       // .putExtra("data", intentData)
                        .putExtra(AppConstants.STREAMID, streamId)
                        .putExtra(AppConstants.NAME, name)
                        .putExtra(AppConstants.EMAIL, email)
                , REQUEST_CODE_FINISH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (playerManager != null)
            playerManager.release();
    }

    /* private void communicateWithPlayerOverlayActivity(int playbackState) {
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent("live-player-activity-receiver")
                        .putExtra("data", playbackState));
    }*/

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onPlayerError() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(playerOverlayActivityReceiver);

    }

    /**
     * Setup receivers
     */
    private void setupReceivers() {
        playerOverlayActivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                eventReceived(intent);
            }
        };
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(playerOverlayActivityReceiver,
                        new IntentFilter("player-overlay-activity-receiver"));
    }

    private void eventReceived(Intent intent) {
        switch (intent.getStringExtra("key")) {
            case "streamEnded":
                streamEnded();
                break;
        }
    }
    private void streamEnded() {
        playerManager.pause();
    }


}