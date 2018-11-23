package com.rishabh.livemodule.live.live.liveCamera;

import com.rishabh.livemodule.base.BaseView;

/**
 * Created by Rishabh Saxena.
 */

public interface LiveView extends BaseView {
    //void setStreamId(String s);

    void setupLiveStream();

    void getIntentData();
}
