package com.rishabh.livemodule.data;

import com.rishabh.livemodule.data.api.ApiManager;
import com.rishabh.livemodule.pojo.livecommentresponses.CommentsResponse;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Rishabh Saxena.
 */

public class DataManager implements IDataManager {

    private ApiManager apiManager;
    private static DataManager instance;


    private DataManager() {
        apiManager = ApiManager.getInstance();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            synchronized (DataManager.class){
                if (instance == null)
                    instance = new DataManager();
            }
        }
        return instance;
    }

    public String getUserId() {
        return "70";
    }

    @Override
    public Call<ResponseBody> hitSaveStreamApi(HashMap<String, String> saveStreamMap) {
        return apiManager.hitSaveStreamApi(saveStreamMap);
    }


    public Call<CommentsResponse> getComments(String streamId) {
        return apiManager.getComments(streamId);
    }

    public Call<ResponseBody> reportStream(String reason, String streamId) {
        return apiManager.reportStream(reason, streamId);
    }

}
