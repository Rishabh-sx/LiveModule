package com.rishabh.livemodule.network;

import com.rishabh.livemodule.pojo.FailureResponse;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by appinventiv on 27/3/18.
 */

public abstract class NetworkResponse<T> implements Callback<T> {
    private CommonResponseHandler handler;

    public NetworkResponse(CommonResponseHandler handler){
        this.handler = handler;
    }

    public abstract void onSuccess(T body);
    public abstract void onFailure(int code, FailureResponse failureResponse);
    public abstract void onError(Throwable t);




    protected void failure(int code, FailureResponse failureResponse){
        onFailure(code, failureResponse);
    }

    protected void error(Throwable t){
        if(t instanceof SocketTimeoutException || t instanceof UnknownHostException){
            handler.onNetworkError();
        }
        onError(t);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            onSuccess(response.body());
        } else {
            failure(response.code(),getFailureErrorBody(call.request().url().url().getFile(),response));
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        error(t);
    }


    /**
     * Create your custom failure response out of server response
     * Also save Url for any further use
     * */
    protected final FailureResponse getFailureErrorBody(String url, Response<T> errorBody) {
        FailureResponse failureResponse = new FailureResponse();
        failureResponse.setMsg(errorBody.message());
        failureResponse.setErrorCode(errorBody.code());
        return failureResponse;
    }
}
