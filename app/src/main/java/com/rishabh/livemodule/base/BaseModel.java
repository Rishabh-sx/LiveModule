package com.rishabh.livemodule.base;

import com.rishabh.livemodule.data.DataManager;
import com.rishabh.livemodule.network.CommonResponseHandler;

import java.lang.ref.SoftReference;

/**
 * Created by Rishabh Saxena.
 */

public abstract class BaseModel<T extends BaseModelListener> implements CommonResponseHandler {

    private static final int NO_NETWORK = 999;
    private SoftReference<T> listener;

    public BaseModel(T listener) {
        this.listener = new SoftReference<>(listener);
    }

    public void attachListener(T listener) {
        this.listener = new SoftReference<>(listener);
    }

    public void detachListener() {
        this.listener = null;
    }

    public T getListener() {
        return (listener != null) ? listener.get() : null;
    }

    public abstract void init();

//    private void onSpecificErrorOccurred(Throwable e) {
//        FailureResponse failureResponse = null;
//        if (e != null && e instanceof HttpException) {
//            failureResponse = new FailureResponse();
//            failureResponse.setErrorCode(((HttpException) e).code());
//            failureResponse.setMsg(((HttpException) e).message());
//        }
//        getListener().onErrorOccurred(failureResponse);
//    }

    private void noNetworkAvailableError() {
        getListener().noNetworkError();
    }

    @Override
    public void onNetworkError() {
        noNetworkAvailableError();
    }

    public DataManager getDataManager() {
        return DataManager.getInstance();
    }
}
