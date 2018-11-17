package com.rishabh.livemodule.live.player.playeroverlay;

import com.rishabh.livemodule.base.BaseView;
import com.rishabh.livemodule.pojo.comments.RESULT;

import java.util.ArrayList;

import io.socket.client.Socket;

/**
 * Created by Rishabh Saxena.
 */

public interface PlayerOverlayView extends BaseView {

    void initVariables(String userId, Socket socket/*, Bundle user*/);

    void initViews();

    void addCommentsToList(ArrayList<RESULT> result);

    void streamReported();
}
