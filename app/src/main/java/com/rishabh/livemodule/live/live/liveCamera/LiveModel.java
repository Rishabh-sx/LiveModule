package com.rishabh.livemodule.live.live.liveCamera;

import com.rishabh.livemodule.base.BaseModel;

import io.socket.client.Socket;

/**
 * Created by Rishabh Saxena.
 */

public class LiveModel extends BaseModel<LiveModelListener> {

    private Socket mSocket;

    public LiveModel(LiveModelListener listener) {
        super(listener);
    }

    @Override
    public void init() {

    }

    public String getUserId() {
        return getDataManager().getUserId();

    }
}


