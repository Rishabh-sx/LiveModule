package com.rishabh.livemodule.live.player.playeroverlay;

import com.rishabh.livemodule.base.BasePresenter;
import com.rishabh.livemodule.pojo.comments.RESULT;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rishabh Saxena.
 */

public class PlayerOverlayPresenter extends BasePresenter<PlayerOverlayView> implements
        PlayerOverlayModelListener {
    private PlaybackOverlayModel mModel;

    public PlayerOverlayPresenter(PlayerOverlayView view) {
        super(view);
    }

    @Override
    protected void setModel() {
        mModel = new PlaybackOverlayModel(this);
        mModel.init();
    }

    @Override
    protected void destroy() {
        mModel.detachListener();
        mModel = null;
    }

    @Override
    protected void initView() {
        if (getView() != null) {
            getView().initVariables(mModel.getUserId(), mModel.getSocket()/*,mModel.getUser()*/);
            getView().initViews();
        }
    }


    public void getComments(String streamId) {
        mModel.getComments(streamId);
    }

    @Override
    public void onGetCommentsResponse(List<RESULT> result) {
        if (getView() != null)
            getView().addCommentsToList((ArrayList<RESULT>) result);
    }

    public void reportStream(String s, String mStreamId) {
        mModel.reportStream(s, mStreamId);
    }

    @Override
    public void streamReported() {
        if (getView() != null)
            getView().streamReported();
    }
}
