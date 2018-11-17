package com.rishabh.livemodule.live.live.cameraOverlay;

import com.rishabh.livemodule.base.BaseView;

import io.socket.client.Socket;

/**
 * Created by Rishabh Saxena.
 */

public interface CameraOverlayView extends BaseView {

    void initVariables(String userId, Socket socketInstance);

    void setListeners();

    void finishActivity();

    void sendBroadcastToEndStream();
}
