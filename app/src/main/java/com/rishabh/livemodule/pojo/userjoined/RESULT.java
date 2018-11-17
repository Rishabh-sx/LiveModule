
package com.rishabh.livemodule.pojo.userjoined;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RESULT {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("userImage")
    @Expose
    private String userImage;
    @SerializedName("streamId")
    @Expose
    private Integer streamId;
    @SerializedName("viewCount")
    @Expose
    private Integer viewCount;
    @SerializedName("userList")
    @Expose
    private String userList;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Integer getStreamId() {
        return streamId;
    }

    public void setStreamId(Integer streamId) {
        this.streamId = streamId;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public String getUserList() {
        return userList;
    }

    public void setUserList(String userList) {
        this.userList = userList;
    }

}
