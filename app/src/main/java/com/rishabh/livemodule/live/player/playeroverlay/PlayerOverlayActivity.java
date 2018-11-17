package com.rishabh.livemodule.live.player.playeroverlay;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.Player;
import com.google.gson.Gson;
import com.rishabh.livemodule.R;
import com.rishabh.livemodule.base.BaseActivity;
import com.rishabh.livemodule.live.live.liveCamera.LiveCameraActivity;
import com.rishabh.livemodule.live.live.cameraOverlay.CommentOverlayAdapter;
import com.rishabh.livemodule.live.live.cameraOverlay.CommentReplyAdapter;
import com.rishabh.livemodule.live.ui.MultiStateButton;
import com.rishabh.livemodule.pojo.LiveIntentData;
import com.rishabh.livemodule.pojo.comments.Comment;
import com.rishabh.livemodule.pojo.comments.RESULT;
import com.rishabh.livemodule.pojo.userjoined.UserJoined;
import com.rishabh.livemodule.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.view.View.GONE;

public class PlayerOverlayActivity extends BaseActivity implements PlayerOverlayView, CommentOverlayAdapter.CommentAdapterListener {
    @BindView(R.id.iv_profile_pic)
    ImageView ivProfilePic;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_live_status)
    TextView tvLiveStatus;
    @BindView(R.id.tv_total_views)
    TextView tvTotalViews;
    @BindView(R.id.rv_comments)
    RecyclerView rvComments;
    @BindView(R.id.rv_reply)
    RecyclerView rvReply;
    @BindView(R.id.et_comments)
    EditText etComments;
    @BindView(R.id.tv_post)
    TextView tvPost;
    /*@BindView(R.id.iv_send)
    ImageView ivSend;*/
    @BindView(R.id.ll_comment_viewer_overlay)
    LinearLayout llCommentViewerOverlay;
    @BindView(R.id.rl_comment_viewer_overlay)
    RelativeLayout rlCommentViewerOverlay;
    @BindView(R.id.nsv_player_overlay)
    NestedScrollView nsvPlayerOverlay;
    @BindView(R.id.iv_send)
    ImageView ivSend;
    @BindView(R.id.ic_broadcast)
    MultiStateButton icBroadcast;
    @BindView(R.id.rl_live_end)
    RelativeLayout rlLiveEnd;
    @BindView(R.id.view_editTextOverlay)
    View viewEditTextOverlay;
    private BroadcastReceiver playerActivityReceiver;
    private String mStreamId;
    private LiveIntentData liveIntentData;
    private PlayerOverlayPresenter mPresenter;
    private String mUserId;
    private Socket mSocket;

    private ArrayList<RESULT> mCommentsList;
    private CommentOverlayAdapter mCommentOverlayAdapter;
    private CommentReplyAdapter mCommentReplyAdapter;
    private boolean isPostShown;
    private Bundle bundle;
    private boolean userEndedStream;
    private boolean commentReporting;


    @Override
    protected int getResourceId() {
        return R.layout.activity_viewer_overlay;
    }

    @Override
    protected void initVariables() {
    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mStreamId != null) {
                        joinStream();
                    }
                }
            });

        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onSocketDisconnected: ", args[0].toString());

                }
            });
        }
    };
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onSocketError: ", args[0].toString());
                }
            });
        }
    };

    @Override
    public void setListeners() {
        setupReceivers();
        attachKeyboardListeners();
    }


    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight() - llCommentViewerOverlay.getHeight();

            if (heightDiff <= contentViewTop) {
                onHideKeyboard();

            } else {
                //  int keyboardHeight = heightDiff - contentViewTop;
                onShowKeyboard();

            }
        }
    };

    private void onShowKeyboard() {
        isPostShown = true;

        tvPost.setText(R.string.s_post);
        tvPost.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        //ivSend.setVisibility(View.GONE);
        icBroadcast.setVisibility(View.GONE);
    }

    private void onHideKeyboard() {

        isPostShown = false;

        tvPost.setText("");
        tvPost.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_more_vert, 0, 0, 0);

        //ivSend.setVisibility(View.VISIBLE);
        //  icBroadcast.setVisibility(View.VISIBLE);
    }

    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;


    protected void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }

        rootLayout = findViewById(R.id.rl_comment_viewer_overlay);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        super.changeBaseColor(Color.TRANSPARENT);
        getDataFromIntent();
        mPresenter = new PlayerOverlayPresenter(this);
        mPresenter.getComments(liveIntentData.getStreamId());
        setUpAdapter();


    }

    private void getDataFromIntent() {
        if (getIntent().hasExtra("data")
                && getIntent().getParcelableExtra("data") != null) {

            liveIntentData = getIntent()
                    .getParcelableExtra("data");
        }
    }

    @Override
    public void initVariables(String userId, Socket socket/*, Bundle user*/) {
        mUserId = userId;
        mSocket = socket;
        mCommentsList = new ArrayList<>();
        mStreamId = liveIntentData.getStreamId();
     //   bundle = user;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mSocket != null) {
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("new_message", onCommentRecieved);
            mSocket.on("user_joined", userJoined);
            mSocket.on("stream_ended", streamEnded);
            mSocket.on("turnoff_commenting", commentStatusOff);
            mSocket.on("turnon_commenting", commentStatusOn);


        }

        mSocket.connect();

    }

    private Emitter.Listener commentStatusOn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final String s = args[0].toString();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("commentStatus", s);
                    toggleCommentText(true);
                }
            });
        }
    };

    private void toggleCommentText(boolean status) {
        if (status) {
            viewEditTextOverlay.setVisibility(View.VISIBLE);
            etComments.setText("");
            etComments.setHint("Comments off");
            rvComments.setVisibility(GONE);
        } else {
            viewEditTextOverlay.setVisibility(View.GONE);
            etComments.setText("");
            etComments.setHint("Comment");
            rvComments.setVisibility(View.VISIBLE);
        }

    }

    private Emitter.Listener commentStatusOff = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final String s = args[0].toString();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("commentStatus", s);
                    toggleCommentText(false);
                }
            });
        }
    };


    @Override
    public void initViews() {
        tvUserName.setText(liveIntentData.getName());
        AppUtils.makeImageCircularWithUri
                (this, liveIntentData.getProfilePic(), ivProfilePic);
    }

    /**
     * sets up adapter for comments and replies
     */
    private void setUpAdapter() {
        mCommentOverlayAdapter = new CommentOverlayAdapter(mCommentsList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setAdapter(mCommentOverlayAdapter);

        mCommentReplyAdapter = new CommentReplyAdapter();
        rvReply.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        rvReply.setAdapter(mCommentReplyAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private Emitter.Listener streamEnded = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String s = args[0].toString();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLiveStreamEndedView();
                }
            });
        }
    };

    private void setLiveStreamEndedView() {
        userEndedStream = true;
        tvLiveStatus.setVisibility(View.GONE);
        tvTotalViews.setVisibility(View.GONE);
        nsvPlayerOverlay.setVisibility(View.GONE);
        rlLiveEnd.setVisibility(View.VISIBLE);
        sendPlayerStopBroadcast();
    }

    private void sendPlayerStopBroadcast() {
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent("player-overlay-activity-receiver")
                        .putExtra("key", "streamEnded"));
    }

    private Emitter.Listener onCommentRecieved = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final String s = args[0].toString();
            Log.e("onCommentRecieved: ", s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Comment comment = new Gson().fromJson(s, Comment.class);
                    addCommentToRecyclerView(comment.getRESULT());
                }
            });
        }
    };

    private void addCommentToRecyclerView(RESULT result) {
        mCommentsList.add(result);
        mCommentOverlayAdapter.notifyDataSetChanged();
    }


    /**
     * Setup receivers
     */
    private void setupReceivers() {
        playerActivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Toast.makeText(PlayerOverlayActivity.this, "On Event Received", Toast.LENGTH_SHORT).show();
                eventReceived(intent);
            }
        };

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(playerActivityReceiver,
                        new IntentFilter("live-player-activity-receiver"));


    }

    private boolean userJoinedStream;
    private Emitter.Listener userJoined = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final String s = args[0].toString();
            Log.e("userJoined: ", s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    userJoinedStream = true;
                    UserJoined userJoined = new Gson().fromJson(s, UserJoined.class);
                    tvTotalViews.setText(userJoined.getRESULT().getViewCount().toString());

                }
            });
        }
    };

    private void eventReceived(Intent intent) {
        switch (intent.getIntExtra("data", 0)) {
            case Player.STATE_IDLE:

                break;
            case Player.STATE_BUFFERING:

                break;
            case Player.STATE_READY:

                break;
            case Player.STATE_ENDED:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playerActivityReceiver);
        mSocket.off();
        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!userEndedStream)
            leaveLiveStream();
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    private void leaveLiveStream() {

        if (!mStreamId.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("streamId", mStreamId);


                JSONObject user = new JSONObject();
                user.put("userId", bundle.get("userId"));
                user.put("username", bundle.get("username"));
                user.put("userImage", bundle.get("userImage"));

                jsonObject.put("user", user);

                if (mSocket != null && mSocket.connected())
                    mSocket.emit("leave", jsonObject);
                else
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.tv_post, R.id.view_editTextOverlay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_post:
                if (isPostShown) {
                    if (!etComments.getText().toString().trim().isEmpty()) {
                        sendComment(etComments.getText().toString().trim());
                    }
                } else {
                    openReportStreamDialog();

                }

                break;
            case R.id.iv_send:

                //joinStream();

                break;


            case R.id.view_editTextOverlay:


                break;
        }
    }


    private void openReportStreamDialog() {
        final Dialog endDialog = new Dialog(this, R.style.customDialog);
        endDialog.setContentView(R.layout.dialog_end_stream);
        dimDialogBackground(endDialog);
        endDialog.setCancelable(true);

        TextView tvEnd = endDialog.findViewById(R.id.tv_report);
        TextView tvCancel = endDialog.findViewById(R.id.tv_cancel);
        tvCancel.setVisibility(GONE);

        tvEnd.setText("Report...");

        tvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStreamReportOptionsDailog();
                endDialog.dismiss();

            }
        });

        endDialog.show();

    }


    private void openStreamReportOptionsDailog() {

    }

    private void reportStream(String s) {
        mPresenter.reportStream(s, mStreamId);
    }

    private void openReportDialog(final Integer messageId) {
        final Dialog endDialog = new Dialog(this, R.style.customDialog);
        endDialog.setContentView(R.layout.dialog_end_stream);
        dimDialogBackground(endDialog);
        endDialog.setCancelable(true);

        TextView tvEnd = endDialog.findViewById(R.id.tv_report);
        TextView tvCancel = endDialog.findViewById(R.id.tv_cancel);
        tvCancel.setVisibility(GONE);

        tvEnd.setText("Report...");

        tvEnd.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         openReportOptionsDailog(messageId);
                                         endDialog.dismiss();
                                     }
                                 }

        );

        endDialog.show();

    }

    private void openReportOptionsDailog(final Integer messageId) {
        final Dialog endDialog = new Dialog(this, R.style.customDialog);
        endDialog.setContentView(R.layout.dialog_comment_report_reason);
        dimDialogBackground(endDialog);
        endDialog.setCancelable(true);

        TextView tvSpam = endDialog.findViewById(R.id.tv_spam);
        TextView tvInappropiate = endDialog.findViewById(R.id.tv_abusive);


        tvSpam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report("Spam or scam", messageId);
                endDialog.dismiss();
            }
        });

        tvInappropiate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report("Abusive content", messageId);
                endDialog.dismiss();
            }
        });

        endDialog.show();

    }

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


    private void sendComment(String comment) {
        if (!mStreamId.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("streamId", mStreamId);
                jsonObject.put("userId", mUserId);
                jsonObject.put("msg", comment);
                if (mSocket != null && mSocket.connected()) {
                    mSocket.emit("new_message", jsonObject);
                    etComments.setText("");
                } else
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void joinStream() {
        if (!mStreamId.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("streamId", mStreamId);
                jsonObject.put("userId", mUserId);
                if (mSocket != null && mSocket.connected())
                    mSocket.emit("join_stream", jsonObject);
                else
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.tv_live_video)
    public void onViewClicked() {
        if (isNetworkAvailable()) {
            setResult(RESULT_OK);
            finish();
            startActivity(new Intent(this, LiveCameraActivity.class));
        } else {
            showNoNetworkError();
        }
    }


    @Override
    public void addCommentsToList(ArrayList<RESULT> result) {
        mCommentsList.addAll(0, result);
        mCommentOverlayAdapter.notifyDataSetChanged();
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

    @Override
    public void reportComment(Integer commentId) {
        openReportDialog(commentId);
    }

    @Override
    public void streamReported() {
        Toast.makeText(this, "Thanks for reporting.", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
}
