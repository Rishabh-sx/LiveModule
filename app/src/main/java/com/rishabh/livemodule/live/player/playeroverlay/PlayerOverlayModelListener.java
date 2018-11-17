package com.rishabh.livemodule.live.player.playeroverlay;

import com.rishabh.livemodule.base.BaseModelListener;
import com.rishabh.livemodule.pojo.comments.RESULT;

import java.util.List;

/**
 * Created by Rishabh Saxena.
 */

public interface PlayerOverlayModelListener extends BaseModelListener {
    void onGetCommentsResponse(List<RESULT> result);

    void streamReported();
}
