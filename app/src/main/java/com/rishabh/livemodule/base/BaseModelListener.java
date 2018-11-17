package com.rishabh.livemodule.base;

import com.rishabh.livemodule.pojo.FailureResponse;

/**
 * Created by Rishabh Saxena.
 */

public interface BaseModelListener {
    void noNetworkError();
    void onErrorOccurred(FailureResponse failureResponse);
}