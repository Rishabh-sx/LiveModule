package com.rishabh.livemodule;

import android.app.Application;

import com.wowza.gocoder.sdk.api.errors.WOWZError;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;

public class ApplicationLive extends Application {

    private WOWZStatus wowzaStatus;
    private WOWZError wowzaError;

    public WOWZStatus getWowzaStatus() {
        return wowzaStatus;
    }

    public void setWowzaStatus(WOWZStatus wowzaStatus) {
        this.wowzaStatus = wowzaStatus;
    }

    public WOWZError getWowzaError() {
        return wowzaError;
    }

    public void setWowzaError(WOWZError wowzaError) {
        this.wowzaError = wowzaError;
    }


}
