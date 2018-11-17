package com.rishabh.livemodule.base;

import com.rishabh.livemodule.pojo.FailureResponse;

/**
 * Created by Rishabh Saxena.
 */

public interface BaseView {

    void showNoNetworkError();
    void showToastLong(String message);
    void showSpecificError(FailureResponse failureResponse);
    void showProgressDialog();
    void hideProgressDialog();
    boolean isNetworkAvailable();
}
