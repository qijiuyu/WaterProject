package com.water.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.adapter.GridImageAdapter;
import com.water.project.photo.BigPhotoActivity;
import com.water.project.utils.photo.Bimp;
import com.water.project.utils.photo.ImageItem;
import com.water.project.utils.photo.PicturesUtil;
import com.water.project.view.MyGridView;

import java.io.File;

/**
 * 资料上传
 * Created by Administrator on 2018/7/4 0004.
 */

public class UploadActivity extends BaseActivity implements View.OnClickListener{

    private MyGridView gridView;
    private TextView tvIs;
    private GridImageAdapter adapter = null;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_img_upload);
        initView();
    }


    private void initView(){
        TextView textView=(TextView)findViewById(R.id.tv_head);
        textView.setText("资料上传");
        tvIs=(TextView)findViewById(R.id.tv_aiu);
        gridView=(MyGridView)findViewById(R.id.mg_aiu);
        findViewById(R.id.lin_back).setOnClickListener(this);
        //清空图片集合
        if (Bimp.selectBitmap.size() != 0) {
            Bimp.selectBitmap.clear();
        }
        adapter = new GridImageAdapter(getApplicationContext(), Bimp.selectBitmap);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == Bimp.selectBitmap.size()) {
                    if (Bimp.selectBitmap.size() >=5) {
                        showToastView("图片最多选择5个！");
                    } else {
                        PicturesUtil.selectPhoto(UploadActivity.this,1);
                    }
                } else {
                    Intent intent = new Intent(mContext, BigPhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivityForResult(intent, PicturesUtil.CODE_RESULT_REQUEST);
                }
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //返回拍照图片
            case PicturesUtil.CODE_CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    File file = new File(PicturesUtil.pai);
                    if(file.isFile()){
                        ImageItem takePhoto = new ImageItem();
                        takePhoto.setImagePath(file.getPath());
                        Bimp.selectBitmap.add(takePhoto);
                        Bimp.imgList.add(takePhoto);
                        adapter=new GridImageAdapter(getApplicationContext(), Bimp.selectBitmap);
                        gridView.setAdapter(adapter);
                        //判断是否显示提示
                        isTv();
                    }else{
                        showToastView("拍照失败！");
                    }
                }
                break;
            //返回相册选择图片
            case PicturesUtil.CODE_GALLERY_REQUEST:
                adapter=new GridImageAdapter(getApplicationContext(), Bimp.selectBitmap);
                gridView.setAdapter(adapter);
                //判断是否显示提示
                isTv();
                break;
            case PicturesUtil.CODE_RESULT_REQUEST:
                 adapter.notifyDataSetChanged();
                 //判断是否显示提示
                 isTv();
                 break;
            default:
                break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lin_back:
                 finish();
                 break;
            default:
                break;
        }

    }


    /**
     * 判断是否显示提示
     */
    private void isTv(){
        if(Bimp.selectBitmap.size()>0){
            tvIs.setVisibility(View.VISIBLE);
        }else{
            tvIs.setVisibility(View.GONE);
        }
    }
}
