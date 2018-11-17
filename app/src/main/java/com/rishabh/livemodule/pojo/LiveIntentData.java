package com.rishabh.livemodule.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class LiveIntentData implements Parcelable {


    String streamId;
    String streamName;
    String profilePic;
    String name;

    public LiveIntentData() {
    }

    protected LiveIntentData(Parcel in) {
        streamId = in.readString();
        streamName = in.readString();
        profilePic = in.readString();
        name = in.readString();
    }

    public static final Creator<LiveIntentData> CREATOR = new Creator<LiveIntentData>() {
        @Override
        public LiveIntentData createFromParcel(Parcel in) {
            return new LiveIntentData(in);
        }

        @Override
        public LiveIntentData[] newArray(int size) {
            return new LiveIntentData[size];
        }
    };

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(streamId);
        dest.writeString(streamName);
        dest.writeString(profilePic);
        dest.writeString(name);
    }
}
