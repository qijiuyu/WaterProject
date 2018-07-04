package com.water.project.photo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.water.project.R;
import com.water.project.activity.BaseActivity;
import com.water.project.application.MyApplication;

import java.util.List;


/**
 * @author Administrator
 */

public class NetBigPhotoActivity extends BaseActivity {

    private ImageView bigImage;
    private int id;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.big_photo);
        id = this.getIntent().getIntExtra("ID", 0);
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("选择图片");
        TextView tvRight=(TextView)findViewById(R.id.tv_right);
        tvRight.setText("删除");
        bigImage = (ImageView) findViewById(R.id.img_big_photo);
        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.tv_right).setVisibility(View.GONE);
        try {
            String imgList = this.getIntent().getStringExtra("imgList");
            if (!TextUtils.isEmpty(imgList)) {
                List<String> list = new Gson().fromJson(imgList, new TypeToken<List<String>>() {
                }.getType());
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        if (id == i) {
                            Glide.with(MyApplication.application).load(list.get(i)).diskCacheStrategy(DiskCacheStrategy.NONE).into(bigImage);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
