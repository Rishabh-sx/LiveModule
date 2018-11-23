/**
 * This is sample code provided by Wowza Media Systems, LLC.  All sample code is intended to be a reference for the
 * purpose of educating developers, and is not intended to be used in any production environment.
 * <p>
 * IN NO EVENT SHALL WOWZA MEDIA SYSTEMS, LLC BE LIABLE TO YOU OR ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 * OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 * EVEN IF WOWZA MEDIA SYSTEMS, LLC HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * WOWZA MEDIA SYSTEMS, LLC SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. ALL CODE PROVIDED HEREUNDER IS PROVIDED "AS IS".
 * WOWZA MEDIA SYSTEMS, LLC HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * <p>
 * © 2015 – 2018 Wowza Media Systems, LLC. All rights reserved.
 */

package com.rishabh.livemodule.live.live.liveCamera;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.rishabh.livemodule.ApplicationLive;
import com.rishabh.livemodule.R;
import com.rishabh.livemodule.live.live.cameraOverlay.CameraOverlayActivity;
import com.rishabh.livemodule.live.ui.AutoFocusListener;
import com.rishabh.livemodule.live.ui.MultiStateButton;
import com.rishabh.livemodule.live.ui.TimerView;
import com.rishabh.livemodule.utils.AppConstants;
import com.wowza.gocoder.sdk.api.devices.WOWZCamera;
import com.wowza.gocoder.sdk.api.devices.WOWZCameraView;
import com.wowza.gocoder.sdk.api.errors.WOWZError;
import com.wowza.gocoder.sdk.api.geometry.WOWZSize;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rishabh Saxena.
 */

public class LiveCameraActivity extends CameraActivityBase implements LiveView {

    private final static String TAG = LiveCameraActivity.class.getSimpleName();

    // UI controls
    protected MultiStateButton mBtnSwitchCamera = null;
    protected MultiStateButton mBtnTorch = null;
    protected TimerView mTimerView = null;

    // Gestures are used to toggle the focus modes
    protected GestureDetectorCompat mAutoFocusDetector = null;
    @BindView(R.id.parent)
    RelativeLayout parent;
    @BindView(R.id.cameraPreview)
    WOWZCameraView cameraPreview;
    private BroadcastReceiver act2InitReceiver;

    private String streamId;
    private LivePresenter livePresenter;
    private BroadcastReceiver commentOverlayActivityReceiver;
    private String email;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        livePresenter = new LivePresenter(this);
        livePresenter.getIntentData();
        livePresenter.init();
    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_live_camera;
    }


    @Override
    public void getIntentData() {
        email = getIntent().getStringExtra(AppConstants.EMAIL);
        name = getIntent().getStringExtra(AppConstants.NAME);
        streamId = getIntent().getStringExtra(AppConstants.STREAMID);
    }

    @Override
    protected void initVariables() {
    }

    @Override
    public void setListeners() {
        setupReceivers();
    }

    private void initBroadcastListener() {

    }

    /**
     * Android Activity lifecycle methods
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (this.hasDevicePermissionToAccess() && GoCoderSDKActivityBase.sGoCoderSDK != null && mWZCameraView != null) {
            if (mAutoFocusDetector == null)
                mAutoFocusDetector = new GestureDetectorCompat(this, new AutoFocusListener(this, mWZCameraView));

            WOWZCamera activeCamera = mWZCameraView.getCamera();
            if (activeCamera != null && activeCamera.hasCapability(WOWZCamera.FOCUS_MODE_CONTINUOUS))
                activeCamera.setFocusMode(WOWZCamera.FOCUS_MODE_CONTINUOUS);
        }


        if (!streamId.isEmpty())
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivityForResult(new Intent(LiveCameraActivity.this, CameraOverlayActivity.class)
                            .putExtra(AppConstants.STREAMID, streamId)
                            .putExtra(AppConstants.NAME, name)
                            .putExtra(AppConstants.EMAIL, email), 1);
                }
            }, 2);
        else
            Log.e(TAG, "onResume: Socket Error, Stream id");


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Click handler for the switch camera button
     */
    public void onSwitchCamera(View v) {


        if (mWZCameraView == null) return;

        mBtnTorch.setState(false);
        mBtnTorch.setEnabled(false);

        // Set the new surface extension prior to camera switch such that
        // setting will take place with the new one.  So if it is currently the front
        // camera, then switch to default setting (not mirrored).  Otherwise show mirrored.
//        if(mWZCameraView.getCamera().getDirection() == WOWZCamera.DIRECTION_FRONT) {
//            mWZCameraView.setSurfaceExtension(mWZCameraView.EXTENSION_DEFAULT);
//        }
//        else{
//            mWZCameraView.setSurfaceExtension(mWZCameraView.EXTENSION_MIRROR);
//        }

        WOWZCamera newCamera = mWZCameraView.switchCamera();
        if (newCamera != null) {
            if (newCamera.hasCapability(WOWZCamera.FOCUS_MODE_CONTINUOUS))
                newCamera.setFocusMode(WOWZCamera.FOCUS_MODE_CONTINUOUS);

            boolean hasTorch = newCamera.hasCapability(WOWZCamera.TORCH);
            if (hasTorch) {
                mBtnTorch.setState(newCamera.isTorchOn());
                mBtnTorch.setEnabled(true);
            }
        }
    }

    /**
     * Click handler for the torch/flashlight button
     */
    public void onToggleTorch(View v) {
        if (mWZCameraView == null) return;
        WOWZCamera activeCamera = mWZCameraView.getCamera();
        activeCamera.setTorchOn(mBtnTorch.toggleState());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAutoFocusDetector != null)
            mAutoFocusDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    /**
     * Update the state of the UI controls
     */
    @Override
    protected boolean syncUIControlState() {
        boolean disableControls = super.syncUIControlState();

        if (disableControls) {
            mBtnSwitchCamera.setEnabled(false);
            mBtnTorch.setEnabled(false);
        } else {

            boolean isDisplayingVideo = (this.hasDevicePermissionToAccess(Manifest.permission.CAMERA) && getBroadcastConfig().isVideoEnabled() && mWZCameraView.getCameras().length > 0);
            boolean isStreaming = getBroadcast().getStatus().isRunning();

            if (isDisplayingVideo) {
                WOWZCamera activeCamera = mWZCameraView.getCamera();

                boolean hasTorch = (activeCamera != null && activeCamera.hasCapability(WOWZCamera.TORCH));
                mBtnTorch.setEnabled(hasTorch);
                if (hasTorch) {
                    mBtnTorch.setState(activeCamera.isTorchOn());
                }

                mBtnSwitchCamera.setEnabled(mWZCameraView.getCameras().length > 0);
            } else {
                mBtnSwitchCamera.setEnabled(false);
                mBtnTorch.setEnabled(false);
            }

            if (isStreaming && !mTimerView.isRunning()) {
                mTimerView.startTimer();
                sendUISyncToCommentOverlay("start_timer");
            } else if (getBroadcast().getStatus().isIdle() && mTimerView.isRunning()) {
                mTimerView.stopTimer();
                sendUISyncToCommentOverlay("stop_timer");
            } else if (!isStreaming) {
                mTimerView.setVisibility(View.GONE);
                sendUISyncToCommentOverlay("gone");
            }
        }
        return disableControls;
    }

    private void sendUISyncToCommentOverlay(String timerStatus) {
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent("live-activity-receiver")
                        .putExtra("key", "timer")
                        .putExtra("data", timerStatus));
    }


    private void sendStatusToCommentOverlay(String key) {
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent("live-activity-receiver")
                        .putExtra("key", key)
                );
    }

    @Override
    public void onWZCameraPreviewStarted(WOWZCamera wzCamera, WOWZSize wzSize, int i) {
        super.onWZCameraPreviewStarted(wzCamera, wzSize, i);
    }

    @Override
    public void onWZCameraPreviewStopped(int cameraId) {
        super.onWZCameraPreviewStopped(cameraId);
    }

    @Override
    public void onWZCameraPreviewError(WOWZCamera wzCamera, WOWZError wzError) {
        super.onWZCameraPreviewError(wzCamera, wzError);
        ((ApplicationLive) getApplication()).setWowzaError(wzError);
        sendStatusToCommentOverlay("wzError");
    }


    @Override
    public void onWZStatus(WOWZStatus goCoderStatus) {
        super.onWZStatus(goCoderStatus);
        if (goCoderStatus.isStopping()) {
            sendStatusToCommentOverlay("broadcastStopped");
        } else {
            ((ApplicationLive) getApplication()).setWowzaStatus(goCoderStatus);
            sendStatusToCommentOverlay("wzStatus");
        }
    }

    @Override
    public void onWZError(WOWZStatus goCoderStatus) {
        super.onWZError(goCoderStatus);
        ((ApplicationLive) getApplication()).setWowzaStatus(goCoderStatus);
        sendStatusToCommentOverlay("wzStatusError");
    }



    @Override
    public void setupLiveStream() {
        mRequiredPermissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        };
        // Initialize the UI controls
        mBtnTorch = (MultiStateButton) findViewById(R.id.ic_torch);
        mBtnSwitchCamera = (MultiStateButton) findViewById(R.id.ic_switch_camera);
        mTimerView = (TimerView) findViewById(R.id.txtTimer);
        initBroadcastListener();
    }

    @Override
    protected String getStreamId() {
        return streamId;
    }

    /**
     * Setup receivers
     */
    private void setupReceivers() {
        commentOverlayActivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                eventReceived(intent);
            }
        };
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(commentOverlayActivityReceiver,
                        new IntentFilter("comment-overlay-activity-receiver"));


    }

    private void eventReceived(Intent intent) {
        switch (intent.getStringExtra("key")) {
            case "end_stream":
                //           cameraPreview.stopPreview();
                endBroadcast();
                break;
            case "toggle_camera":
                onSwitchCamera(null);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(commentOverlayActivityReceiver);
    }


}