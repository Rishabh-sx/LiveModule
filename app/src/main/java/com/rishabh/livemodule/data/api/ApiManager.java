package com.rishabh.livemodule.data.api;


import com.rishabh.livemodule.pojo.livecommentresponses.CommentsResponse;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rishabh Saxena.
 */

public class ApiManager {

    private static final ApiManager instance = new ApiManager();
    private final ApiClient apiClient;

    private ApiManager() {
        apiClient = getRetrofitService();
    }

    public static ApiManager getInstance() {
        return instance;
    }

    private static ApiClient getRetrofitService() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ApiClient.ENDPOINT)
                .build();

        return retrofit.create(ApiClient.class);
    }

    public Call<ResponseBody> hitSaveStreamApi(HashMap<String, String> saveStreamMap) {
        return apiClient.hitSaveStreamApi(saveStreamMap);
    }

    public Call<CommentsResponse> getComments(String streamId) {
        return apiClient.getComments(streamId);
    }

    public Call<ResponseBody> reportStream(String reason, String streamId) {
        return apiClient.reportStream(reason,streamId);
    }

}
