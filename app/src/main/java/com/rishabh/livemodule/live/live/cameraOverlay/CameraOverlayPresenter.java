package com.rishabh.livemodule.live.live.cameraOverlay;

import com.rishabh.livemodule.base.BasePresenter;

import java.util.HashMap;

import io.socket.client.Socket;

/**
 * Created by Rishabh Saxena.
 */

public class CameraOverlayPresenter extends BasePresenter<CameraOverlayView> implements CameraOverlayModelListener {

    private CameraOverlayModel model;

    public CameraOverlayPresenter(CameraOverlayView view) {
        super(view);
    }

    @Override
    protected void setModel() {
        model = new CameraOverlayModel(this);
    }

    @Override
    protected void destroy() {

    }

    @Override
    protected void initView() {


    }

    public void init(String email, String name) {
        if (getView() != null) {
            getView().initVariables(getSocketInstance(email,name));
            getView().setListeners();
        }
    }

    public Socket getSocketInstance(String email, String name) {
        return model.getSocket(email,name);
    }

    public void onShareButtonClicked(String streamId, String streamDuration, boolean isSaved) {
        if(getView()!=null){
            getView().showProgressDialog();

            HashMap<String,String> saveStreamMap = new HashMap<>();
            saveStreamMap.put("streamId",streamId);
            saveStreamMap.put("duration",streamDuration);
            if(isSaved)
                saveStreamMap.put("isSaved","1");
            else  saveStreamMap.put("isSaved","0");

            model.saveStreamAndShare(saveStreamMap);
        }
    }

    public void onDiscardButtonClicked() {
        if(getView()!=null){
            getView().finishActivity();
        }
    }

    public void onEndStreamViewClicked() {
        if(getView()!=null){
            getView().sendBroadcastToEndStream();
        }
    }

    @Override
    public void onStreamShared() {
        if(getView()!=null){
            getView().hideProgressDialog();
            getView().finishActivity();
        }
    }

    public void onSaveButtonClicked() {

    }

    public void getIntentData() {
    if(getView()!=null)
        getView().getIntentData();
    }
}
