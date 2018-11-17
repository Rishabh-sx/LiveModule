package com.rishabh.livemodule.data;

import com.rishabh.livemodule.pojo.livecommentresponses.CommentsResponse;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Rishabh Saxena.
 */

interface IDataManager {

    Call<ResponseBody> hitSaveStreamApi(HashMap<String,String> saveStreamMap);

    Call<ResponseBody> reportStream(String reason, String streamId);

    Call<CommentsResponse> getComments(String s);
}
