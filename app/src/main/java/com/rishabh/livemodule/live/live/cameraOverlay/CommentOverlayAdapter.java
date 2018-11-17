package com.rishabh.livemodule.live.live.cameraOverlay;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rishabh.livemodule.R;
import com.rishabh.livemodule.pojo.comments.RESULT;
import com.rishabh.livemodule.utils.AppUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rishabh Saxena.
 */

public class CommentOverlayAdapter extends RecyclerView.Adapter<CommentOverlayAdapter.CommentAdapterViewHolder> {


    private final CommentAdapterListener listener;
    private ArrayList<RESULT> mCommentsList;

    public CommentOverlayAdapter(ArrayList<RESULT> commentsList, CommentAdapterListener commentAdapterListener) {
        listener = commentAdapterListener;
         mCommentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment_overlay, parent, false);
        return new CommentAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapterViewHolder holder, final int position) {
       AppUtils.makeImageCircularWithUri(holder.itemView.getContext(), mCommentsList.get(position).getImage(),holder.ivProfilePic);
       holder.tvUserName.setText(mCommentsList.get(position).getUsername());
       holder.tvComment.setText(mCommentsList.get(position).getComment().trim());
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               listener.reportComment((mCommentsList.get(position).getMessageId()));
           }
       });
    }

    @Override
    public int getItemCount() {
        return mCommentsList.size();
    }

    class CommentAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_profile_pic)
        ImageView ivProfilePic;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.tv_comment)
        TextView tvComment;
        public CommentAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface CommentAdapterListener{
        void reportComment(Integer CommentId);
    }
}
