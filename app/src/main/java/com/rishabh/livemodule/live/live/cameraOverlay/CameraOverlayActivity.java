package com.rishabh.livemodule.live.live.cameraOverlay;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rishabh.livemodule.ApplicationLive;
import com.rishabh.livemodule.R;
import com.rishabh.livemodule.base.BaseActivity;
import com.rishabh.livemodule.live.ui.MultiStateButton;
import com.rishabh.livemodule.live.ui.StatusView;
import com.rishabh.livemodule.live.ui.TimerView;
import com.rishabh.livemodule.pojo.comments.Comment;
import com.rishabh.livemodule.pojo.comments.RESULT;
import com.rishabh.livemodule.pojo.userjoined.UserJoined;
import com.rishabh.livemodule.utils.AppConstants;
import com.wowza.gocoder.sdk.api.errors.WOWZError;
import com.wowza.gocoder.sdk.api.logging.WOWZLog;
import com.wowza.gocoder.sdk.api.status.WOWZState;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.view.View.GONE;

/**
 * Created by Rishabh Saxena.
 */

public class CameraOverlayActivity extends BaseActivity implements CameraOverlayView, CompoundButton.OnCheckedChangeListener, CommentOverlayAdapter.CommentAdapterListener {

    @BindView(R.id.tv_total_views)
    TextView tvTotalViews;
    @BindView(R.id.rv_comments)
    RecyclerView rvComments;
    @BindView(R.id.et_comments)
    EditText etComments;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    @BindView(R.id.ll_parent_comments)
    LinearLayout llParentComments;
    @BindView(R.id.tv_post)
    TextView tvPost;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.btn_status)
    TextView btnStatus;
    @BindView(R.id.user_interactable_view)
    RelativeLayout userIntractableView;
    @BindView(R.id.statusView)
    StatusView statusView;
    @BindView(R.id.rl_comment_overlay)
    RelativeLayout rlCommentOverlay;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.switch_share)
    Switch switchShare;
    @BindView(R.id.btn_share)
    Button btnShare;
    @BindView(R.id.ll_stream_ended)
    LinearLayout llStreamEnded;
    @BindView(R.id.view_editTextOverlay)
    View viewEditTextOverlay;
    @BindView(R.id.tv_live_status)
    TimerView tvLiveStatus;
    @BindView(R.id.tv_switch_camera)
    MultiStateButton tvSwitchCamera;
    @BindView(R.id.tv_connection_status)
    TextView tvConnectionStatus;

    private BroadcastReceiver liveActivityReceiver;
    private Socket mSocket;
    private String TAG = "Socket Callbacks ";
    private String mStreamId;
    private String mServerStreamID = "";
    private String mUserId;
    private CameraOverlayPresenter livePresenter;
    private boolean keyboardListenersAttached;

    private boolean endStream;
    private CommentOverlayAdapter mCommentOverlayAdapter;
    private ArrayList<RESULT> mCommentsList;
    private boolean isPostShown;
    //private boolean isSaved;
    private boolean isCommentingOFF;
    private boolean isAlreadyConnected;

    private String genStreamName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        super.changeBaseColor(Color.TRANSPARENT);
        setupReceivers();
        livePresenter = new CameraOverlayPresenter(this);
        livePresenter.getIntentData();
    }

    @Override
    public void getIntentData() {
        String email = getIntent().getStringExtra(AppConstants.EMAIL);
        String name = getIntent().getStringExtra(AppConstants.NAME);
        genStreamName = getIntent().getStringExtra(AppConstants.STREAMID);
        livePresenter.init(email, name);
    }

    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight() - llParentComments.getHeight();

            if (heightDiff <= contentViewTop) {
                onHideKeyboard();

            } else {
                //int keyboardHeight = heightDiff - contentViewTop;
                onShowKeyboard();

            }
        }
    };

    @Override
    protected int getResourceId() {
        return R.layout.activity_live_overlay;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    public void initVariables(/*String userId,*/ Socket socketInstance) {
        mStreamId = getStreamID();
        mSocket = socketInstance;
        mSocket.connect();
        mCommentsList = new ArrayList<>();
        mCommentOverlayAdapter = new CommentOverlayAdapter(mCommentsList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //   linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setAdapter(mCommentOverlayAdapter);

        switchShare.setOnCheckedChangeListener(this);

    }


    private String getStreamID() {
        return genStreamName;
    }


    /**
     * Setup receivers
     */
    private void setupReceivers() {
        liveActivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(CameraOverlayActivity.this,"Kuch to hua hai dost",Toast.LENGTH_SHORT).show();
                eventReceived(intent);
            }
        };
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(liveActivityReceiver,
                        new IntentFilter("live-activity-receiver"));
    }

    /**
     * Method to act according when events
     * received from {@link com.rishabh.livemodule.live.live.liveCamera.LiveCameraActivity}
     * @param intent will be used to get data to act with.
     */
    private void eventReceived(Intent intent) {
        Log.e("eventReceived: ", intent.getStringExtra("key"));
        switch (intent.getStringExtra("key")) {
            case AppConstants.BROADCAST_EVENT_STATUS:
                WOWZStatus wowzaStatus = ((ApplicationLive) getApplication()).getWowzaStatus();
                Log.e("eventReceived: ", String.valueOf(wowzaStatus.getState()));
                if (!wowzaStatus.isIdle() && !wowzaStatus.isRunning()) {
                    setStatus(wowzaStatus);
                }
                break;
            case AppConstants.BROADCAST_EVENT_STOPPED:
                endStream();
                break;
            case AppConstants.BROADCAST_EVENT_WZ_STATUS_ERROR:
                WOWZStatus wowzaErrorStatus = ((ApplicationLive) getApplication()).getWowzaStatus();
                setStatus(wowzaErrorStatus);
                break;
            case AppConstants.BROADCAST_EVENT_WZ_ERROR:
                WOWZError wowzaError = ((ApplicationLive) getApplication()).getWowzaError();
                displayErrorDialog(wowzaError.getErrorDescription());
                break;
            case AppConstants.BROADCAST_EVENT_TIMER:
                String timerStatus = intent.getStringExtra("data");
                syncTimer(timerStatus);
                if (!mServerStreamID.isEmpty() && !isAlreadyConnected) {
                    rlCommentOverlay.setVisibility(View.VISIBLE);
                    tvConnectionStatus.setVisibility(GONE);
                    isAlreadyConnected = true;
                }
                break;
        }

    }

    /**
     * Change screen state according to parameter.
     * @param status
     */
    private void setStatus(WOWZStatus status) {
        String mStatusMessage = "";
        if (status.getLastError() != null) {
            mStatusMessage = status.getLastError().getErrorDescription();
        } else {
            switch (status.getState()) {
                case WOWZState.IDLE:
                case WOWZState.RUNNING:
                case WOWZState.STOPPED:
                case WOWZState.COMPLETE:
                case WOWZState.SHUTDOWN:
                case WOWZState.UNKNOWN:
                    mStatusMessage = null;
                    //      mStatusMessage = getResources().getString(R.string.status_kuch_toh_hua_hai);
                    break;
                case WOWZState.PAUSED:
                    mStatusMessage = getResources().getString(R.string.status_paused);
                    break;
                case WOWZState.STARTING:
                    mStatusMessage = getResources().getString(R.string.status_connecting);
                    break;

                case WOWZState.READY:
                    //mStatusMessage = getResources().getString(R.string.status_connected);
                    break;

                case WOWZState.STOPPING:
                    //            btnStatus.setVisibility(View.GONE);
                    mStatusMessage = getResources().getString(R.string.status_disconnecting);
                    break;

                case WOWZState.ERROR:
                    WOWZError err = status.getLastError();
                    mStatusMessage = (err != null ? err.getErrorDescription() : "An error occurred.");

                    break;
            }

        }

    }

    private void syncTimer(String timerStatus) {
        switch (timerStatus) {
            case "start_timer":
                tvLiveStatus.startTimer();
                break;
            case "stop_timer":
                tvLiveStatus.stopTimer();
                break;
            case "gone":
                tvLiveStatus.setVisibility(GONE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(liveActivityReceiver);
        mSocket.off();
        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }

    }


    /**
     * Method to send comment via socket
     * @param comment
     */
    private void sendComment(String comment) {
        if (!mServerStreamID.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("streamId", mServerStreamID);
                jsonObject.put("userId", mUserId);
                jsonObject.put("msg", comment);
                if (mSocket != null && mSocket.connected())
                    mSocket.emit("new_message", jsonObject);
                else
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void setListeners() {
        attachKeyboardListeners();
    }

    @Override
    public void finishActivity() {
        onBackPressed();
    }


    /**
     * Display an alert dialog containing an error message.
     * Display an alert dialog containing an error message.
     *
     * @param errorMessage The error message text
     */
    protected void displayErrorDialog(String errorMessage) {
        // Log the error message
        try {
            WOWZLog.error(TAG, "ERROR: " + errorMessage);

            // Display an alert dialog containing the error message
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
            //AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            builder.setMessage(errorMessage)
                    .setTitle(R.string.dialog_title_error);
            builder.setPositiveButton(R.string.dialog_button_close, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        } catch (Exception ex) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSocket != null) {
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("new_message", onCommentReceived);
            mSocket.on("start_stream", streamID);
            mSocket.on("user_joined", userJoined);
            mSocket.on("stream_ended", streamEnded);
            mSocket.on("turnoff_commenting", commentStatusOff);
            mSocket.on("turnon_commenting", commentStatusOn);
            mSocket.on("logged_in", loggedIn);
        }
    }

    @OnClick({R.id.view_editTextOverlay, R.id.btn_status, R.id.tv_post, /*R.id.iv_send,*/ R.id.tv_live_status,
            R.id.tv_switch_camera, R.id.btn_share, R.id.tv_end, R.id.tv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_status:
                userIntractableView.setVisibility(GONE);
                break;
            case R.id.tv_post:
                if (isPostShown) {
                    if (!etComments.getText().toString().trim().isEmpty())
                        sendComment(etComments.getText().toString().trim());

                    etComments.setText("");
                    etComments.setHint("Comment");

                } else {

                    openCommentingToggleDialog();

                }
                break;
            case R.id.iv_send:
//                joinStream();
                Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_switch_camera:
                sendBroadcastToToggleCamera();
                break;
            case R.id.tv_live_status:
                tvLiveStatus.toggleTimerVisibility(!tvLiveStatus.showTimer(), "Live");
                break;
            case R.id.tv_end:
                openEndStreamDialog();
                break;
            case R.id.btn_share:
                /*if (switchShare.isChecked()) {
                    livePresenter.onShareButtonClicked(mServerStreamID, getStreamDuration(), isSaved);
                } else {
                    livePresenter.onDiscardButtonClicked();
                }*/
                //Todo
                // uncomment above code and add ask your backend to handle share case and add the
                // base url in the code.
                livePresenter.onDiscardButtonClicked();
                break;
            case R.id.tv_save:
                break;
            case R.id.view_editTextOverlay:
                openCommentingToggleDialog();
                break;

        }
    }

    private String getStreamDuration() {
        return String.valueOf(tvLiveStatus.getTotalSeconds());
    }

    /**
     * Open dialog to end stream.
     */
    private void openEndStreamDialog() {
        final Dialog endDialog = new Dialog(this, R.style.customDialog);
        endDialog.setContentView(R.layout.dialog_end_stream);
        dimDialogBackground(endDialog);
        endDialog.setCancelable(true);

        TextView tvEnd = endDialog.findViewById(R.id.tv_report);
        TextView tvCancel = endDialog.findViewById(R.id.tv_cancel);

        tvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDialog.dismiss();
                livePresenter.onEndStreamViewClicked();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDialog.dismiss();
            }
        });

        endDialog.show();

    }

    /**
     * Open method to toggle commenting current state
     */
    private void openCommentingToggleDialog() {
        final Dialog endDialog = new Dialog(this, R.style.customDialog);
        endDialog.setContentView(R.layout.dialog_end_stream);
        dimDialogBackground(endDialog);
        endDialog.setCancelable(true);

        TextView tvEnd = endDialog.findViewById(R.id.tv_report);
        TextView tvCancel = endDialog.findViewById(R.id.tv_cancel);
        tvCancel.setVisibility(GONE);

        if (isCommentingOFF)
            tvEnd.setText("Turn On Commenting");
        else
            tvEnd.setText("Turn Off Commenting");

        tvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOffCommenting();
                endDialog.dismiss();
            }
        });

        endDialog.show();

    }

    /**
     * Method to turn off Commenting.
     */
    private void turnOffCommenting() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("streamId", mServerStreamID);
            jsonObject.put("userId", mUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isCommentingOFF) {
            mSocket.emit("turnon_commenting", jsonObject);
        } else {
            mSocket.emit("turnoff_commenting", jsonObject);
        }
    }

    /**
     * method to dim the dialog background
     *
     * @param confirmationDialog Dialog whose background is to be altered
     */
    public void dimDialogBackground(Dialog confirmationDialog) {
        if (confirmationDialog.getWindow() != null) {
            confirmationDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            WindowManager.LayoutParams lp = confirmationDialog.getWindow().getAttributes();
            lp.dimAmount = 0.8f;
            confirmationDialog.getWindow().setAttributes(lp);
        }
    }

    /**
     * Method to end stream.
     */
    private void endStream() {
        if (!mServerStreamID.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("streamId", mServerStreamID);
                if (mSocket != null && mSocket.connected())
                    mSocket.emit("end_stream", jsonObject);
                else
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Send broadcast to end stream.
     */
    public void sendBroadcastToEndStream() {
        endStream = true;
        setStreamEndedScreenState(GONE);
        tvStatus.setText("Disconnecting...");
        tvLiveStatus.stopTimer();
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent("comment-overlay-activity-receiver")
                        .putExtra("key", "end_stream"));
    }

    /**
     * Method to send broadcast to toggle camera.
     */
    private void sendBroadcastToToggleCamera() {
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent("comment-overlay-activity-receiver")
                        .putExtra("key", "toggle_camera"));
    }

    /**
     * Method is used to attach listener to keyboard
     * to change screen state when keyboard state
     * changes
     */
    protected void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    /**
     * Method to change state of screen
     * when keyboard is opened.
     */
    private void onShowKeyboard() {
        isPostShown = true;
        tvPost.setText("Post");
        tvPost.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvSwitchCamera.setVisibility(GONE);
    }

    /**
     * Method to change state of screen
     * when keyboard is closed.
     */
    private void onHideKeyboard() {
        isPostShown = false;

        tvPost.setText("");
        tvPost.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_more_vert, 0, 0, 0);
        //ivSend.setVisibility(View.VISIBLE);
        tvSwitchCamera.setVisibility(View.VISIBLE);
    }

    /**
     * Method to change screen state
     * when stream ends
     * @param visibility
     */
    private void setStreamEndedScreenState(int visibility) {
        btnStatus.setVisibility(visibility);
        tvSwitchCamera.setVisibility(visibility);
        tvLiveStatus.setVisibility(visibility);
        llParentComments.setVisibility(visibility);
        tvTotalViews.setVisibility(visibility);
        tvEnd.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            userIntractableView.setVisibility(GONE);
        } else {
            userIntractableView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        if (endStream) {
            closeStreamingModule();
        } else {
            sendBroadcastToEndStream();
        }
    }

    private void closeStreamingModule() {
        setResult(RESULT_OK);
        finish();

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            //isSaved = true;
            btnShare.setText(R.string.share);
        } else {
            //isSaved = false;
            btnShare.setText(R.string.s_discard);
        }
    }

    @Override
    public void reportComment(Integer commentId) {
        openReportDialog(commentId);
    }

    /**
     * This method is used to open report dialog
     *
     * @param messageId will be used to pass forward.
     */
    private void openReportDialog(final Integer messageId) {
        final Dialog reportDialog = new Dialog(this, R.style.customDialog);
        reportDialog.setContentView(R.layout.dialog_end_stream);
        dimDialogBackground(reportDialog);
        reportDialog.setCancelable(true);

        TextView report = reportDialog.findViewById(R.id.tv_report);

        findViewById(R.id.tv_cancel).setVisibility(GONE);

        report.setText("Report...");
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReportOptionsDialog(messageId);
                reportDialog.dismiss();
            }
        });
        reportDialog.show();
    }

    /**
     * This method is used to open report dialog options
     *
     * @param messageId will be used to pass forward.
     */
    private void openReportOptionsDialog(final Integer messageId) {
        final Dialog endDialog = new Dialog(this, R.style.customDialog);
        endDialog.setContentView(R.layout.dialog_comment_report_reason);
        dimDialogBackground(endDialog);
        endDialog.setCancelable(true);

        TextView tvSpam = endDialog.findViewById(R.id.tv_spam);
        TextView tvInappropriate = endDialog.findViewById(R.id.tv_abusive);


        tvSpam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report("Spam or scam", messageId);
                endDialog.dismiss();
            }
        });

        tvInappropriate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report("Abusive content", messageId);
                endDialog.dismiss();
            }
        });

        endDialog.show();

    }

    /**
     * This method is used to report stream
     * via socket "Report Comment"
     *
     * @param msg
     * @param messageId
     */
    private void report(String msg, Integer messageId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("streamId", mStreamId);
            jsonObject.put("userId", mUserId);
            jsonObject.put("messageId", messageId);
            jsonObject.put("reason", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("report_comment", jsonObject);
    }

    /**
     * Method is to change stream state of edit text
     * when commenting is On/Off
     * via socket {@link #commentStatusOff} & {@link #commentStatusOn}
     *
     * @param status defines the state of edit text.
     */
    private void toggleCommentText(boolean status) {
        if (status) {
            viewEditTextOverlay.setVisibility(View.VISIBLE);
            etComments.setText("");
            etComments.setHint("Comments off");
            rlCommentOverlay.requestFocus();
        } else {
            viewEditTextOverlay.setVisibility(View.GONE);
            etComments.setText("");
            etComments.setHint("Comment");
        }
        isCommentingOFF = status;
    }

    /**
     * Method is to add comment in adapter via {@link #onCommentReceived}
     *
     * @param comment
     */
    private void addCommentToRecyclerView(RESULT comment) {
        mCommentsList.add(comment);
        mCommentOverlayAdapter.notifyDataSetChanged();
    }


    private Emitter.Listener onCommentReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final String s = args[0].toString();
            Log.e("onCommentReceived: ", s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Comment comment = new Gson().fromJson(s, Comment.class);
                    addCommentToRecyclerView(comment.getRESULT());
                }
            });
        }
    };
    private Emitter.Listener userJoined = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final String s = args[0].toString();
            Log.e("userJoined: ", s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UserJoined userJoined = new Gson().fromJson(s, UserJoined.class);
                    tvTotalViews.setText(String.valueOf(userJoined.getRESULT().getViewCount()));
                }
            });
        }
    };
    private Emitter.Listener streamEnded = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String s = args[0].toString();
            Log.e("streamID", s);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rlCommentOverlay.setVisibility(View.GONE);
                    llStreamEnded.setVisibility(View.VISIBLE);
                    endStream = true;
                }
            });
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "call: OnConnected");

        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e(TAG, "call: OnDiconnected");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*closeStreamingModule();
                    Toast.makeText(CameraOverlayActivity.this,"Due to" +
                            "connection Problem, Live stream ended",Toast.LENGTH_LONG).show();
                    Log.d("onSocketDisconnected: ", args[0].toString());*/
                }
            });
        }
    };
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e(TAG, "call: onConnectError");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*Toast.makeText(CameraOverlayActivity.this,"Due to" +
                            "connection Problem, Live stream ended",Toast.LENGTH_LONG).show();
                    closeStreamingModule();
                    Log.d("onSocketError: ", args[0].toString());*/
                }
            });
        }
    };
    private Emitter.Listener commentStatusOn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String s = args[0].toString();
            Log.e(TAG, " call: Comment Status On " + s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toggleCommentText(false);
                }
            });
        }
    };


    private Emitter.Listener commentStatusOff = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final String s = args[0].toString();
            Log.e(TAG, " call: Comment Status Off " + s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("commentStatus", s);
                    toggleCommentText(true);
                }
            });
        }
    };


    private Emitter.Listener streamID = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            final String s = args[0].toString();
            Log.e(TAG, "call : Live ConnectedstreamID " + s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        if (jsonObject.getInt("CODE") == 200) {
                            JSONObject result = jsonObject.getJSONObject("RESULT");
                            mServerStreamID = result.getString("streamId");

                            if (tvLiveStatus.isRunning() && !isAlreadyConnected) {
                                rlCommentOverlay.setVisibility(View.VISIBLE);
                                tvConnectionStatus.setVisibility(GONE);
                                isAlreadyConnected = true;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "run: ");
                }
            });
        }
    };
    private Emitter.Listener loggedIn = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            String s = args[0].toString();
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject result = jsonObject.getJSONObject("RESULT");
                mUserId = String.valueOf(result.getInt("userId"));
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (mServerStreamID.isEmpty()) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name", mStreamId);
                                jsonObject.put("userId", mUserId);

                                if (mSocket != null && mSocket.connected())
                                    mSocket.emit("start_stream", jsonObject);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

}