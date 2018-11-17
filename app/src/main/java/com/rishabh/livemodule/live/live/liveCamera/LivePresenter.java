package com.rishabh.livemodule.live.live.liveCamera;

import android.os.SystemClock;

import com.rishabh.livemodule.base.BasePresenter;


public class LivePresenter extends BasePresenter<LiveView> implements LiveModelListener {

    private LiveModel model;

    public LivePresenter(LiveView view) {
        super(view);
    }

    @Override
    protected void setModel() {
        model = new LiveModel(this);
    }

    @Override
    protected void destroy() {

    }

    @Override
    protected void initView() {


    }

    public void init() {
      String s =   model.getUserId();
      if(getView()!=null)
      {
          getView().setStreamId(generateStreamId(s));
          getView().setupLiveStream();
      }
    }

    private String generateStreamId(String s) {
      //  return "myStream";
       return  SystemClock.currentThreadTimeMillis()+s;

    }

}
