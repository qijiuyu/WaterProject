package com.water.project.photo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.water.project.R;
import com.water.project.application.MyApplication;
import com.water.project.http.HttpConstant;
import com.water.project.utils.photo.Bimp;
import com.water.project.view.TouchImageView;

import java.util.ArrayList;


/**
 * @author Administrator
 */

public class BigPhotoActivity extends Activity {
    private ArrayList<View> listViews = null;
    private ArrayList<Bitmap> bitList = new ArrayList<>();
    private TouchImageView bigImage;
    private int id;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.big_photo);
        Intent intent = getIntent();
        id = intent.getIntExtra("ID", 0);
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("图片预览");
        TextView tvRight=(TextView)findViewById(R.id.tv_right);
        tvRight.setText("删除");
        bigImage = (TouchImageView) findViewById(R.id.img_big_photo);
        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        tvRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    {
                        Bimp.selectBitmap.remove(id);
                        Bimp.max--;
                        finish();
                }
            }
        });
        for (int i = 0; i < Bimp.selectBitmap.size(); i++) {
            final String imgPath = Bimp.selectBitmap.get(i).getImagePath();
            if (id == i) {
                if (imgPath.indexOf(HttpConstant.IP) == -1) {
                        Glide.with(MyApplication.application).load("file://" + imgPath).diskCacheStrategy(DiskCacheStrategy.NONE).into(bigImage);
                } else {
                    Glide.with(MyApplication.application).load(imgPath).diskCacheStrategy(DiskCacheStrategy.NONE).into(bigImage);
                }
            }
        }
    }
    @Override
    protected void onStop() {
        if (listViews != null) {
            for (int i = 0, len = bitList.size(); i < len; i++) {
                Bitmap b = bitList.get(i);
                if (b != null && !b.isRecycled()) {
                    b.isRecycled();
                    b = null;
                }
            }
        }
        super.onStop();
    }
}
