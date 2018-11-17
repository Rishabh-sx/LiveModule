package com.rishabh.livemodule.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.rishabh.livemodule.R;


public class AppUtils {



    //Method Below is used for making image circular with Contact Image Uri
    public static void makeImageCircularWithUri(final Context context, String uri, final ImageView imageView) {
        Glide.with(context).load(uri).asBitmap().centerCrop().error(context.getResources().getDrawable(R.drawable.ic_user)).placeholder(context.getResources().getDrawable(R.drawable.ic_user)).into(new BitmapImageViewTarget(imageView) {
            //        Glide.with(context).load(uri).asBitmap().centerCrop().error(context.getResources().getDrawable(R.drawable.ic_side_menu_img_placeholder)).placeholder((context.getResources().getDrawable(R.drawable.ic_side_menu_img_placeholder))).into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

}