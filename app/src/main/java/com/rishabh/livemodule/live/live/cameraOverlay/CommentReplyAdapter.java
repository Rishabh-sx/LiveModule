package com.rishabh.livemodule.live.live.cameraOverlay;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rishabh.livemodule.R;

/**
 * Created by Rishabh Saxena.
 */

public class CommentReplyAdapter extends RecyclerView.Adapter<CommentReplyAdapter.CommentReplyAdadpterViewHolder> {

    @NonNull
    @Override
    public CommentReplyAdadpterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment_reply_overlay, parent, false);
        return new CommentReplyAdadpterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentReplyAdadpterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 7;
    }

    class CommentReplyAdadpterViewHolder extends RecyclerView.ViewHolder {
        public CommentReplyAdadpterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
