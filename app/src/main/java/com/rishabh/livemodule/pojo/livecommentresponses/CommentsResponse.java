
package com.rishabh.livemodule.pojo.livecommentresponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rishabh.livemodule.pojo.comments.RESULT;

import java.util.List;

public class CommentsResponse {

    @SerializedName("CODE")
    @Expose
    private Integer cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("RESULT")
    @Expose
    private List<RESULT> rESULT = null;

    public Integer getCODE() {
        return cODE;
    }

    public void setCODE(Integer cODE) {
        this.cODE = cODE;
    }

    public String getMESSAGE() {
        return mESSAGE;
    }

    public void setMESSAGE(String mESSAGE) {
        this.mESSAGE = mESSAGE;
    }

    public List<RESULT> getRESULT() {
        return rESULT;
    }

    public void setRESULT(List<RESULT> rESULT) {
        this.rESULT = rESULT;
    }

}
