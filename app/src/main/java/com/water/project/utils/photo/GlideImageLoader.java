package com.water.project.utils.photo;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by Administrator on 2018/9/1.
 */

public class GlideImageLoader extends ImageLoader {
    public void displayImage(Context context, Object path, ImageView imageView) {
        int imgPath= (int) path;
        imageView.setImageDrawable(context.getResources().getDrawable(imgPath));
    }
}
