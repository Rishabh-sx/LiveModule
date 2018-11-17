package com.rishabh.livemodule.network;


/**
 * This is to be used for handling common responses
 * such as no network or authentication failed
 * */

public interface CommonResponseHandler {
    void onNetworkError();
}
