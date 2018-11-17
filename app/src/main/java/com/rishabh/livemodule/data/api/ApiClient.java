package com.rishabh.livemodule.data.api;

import com.rishabh.livemodule.pojo.livecommentresponses.CommentsResponse;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

/**
 * Created by Rishabh Saxena.
 */

interface ApiClient {

    String ENDPOINT = "https://dog.ceo/api/";

    @FormUrlEncoded
    @PATCH("saveStream")
    Call<ResponseBody> hitSaveStreamApi(@FieldMap HashMap<String, String> saveStreamMap);

    @FormUrlEncoded
    @POST("getStreamComments")
    Call<CommentsResponse> getComments(@Field("streamId") String streamId);

    @FormUrlEncoded
    @POST("reportStream")
    Call<ResponseBody> reportStream(@Field("reason") String reason, @Field("streamId") String streamId);
}
