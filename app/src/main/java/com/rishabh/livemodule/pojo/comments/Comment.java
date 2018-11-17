
package com.rishabh.livemodule.pojo.comments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("CODE")
    @Expose
    private Integer cODE;
    @SerializedName("RESULT")
    @Expose
    private RESULT rESULT;

    public Integer getCODE() {
        return cODE;
    }

    public void setCODE(Integer cODE) {
        this.cODE = cODE;
    }

    public RESULT getRESULT() {
        return rESULT;
    }

    public void setRESULT(RESULT rESULT) {
        this.rESULT = rESULT;
    }

}
