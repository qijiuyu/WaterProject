package com.water.project.utils.photo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.youmai.project.R;
import com.youmai.project.activity.photo.ImageGridActivity;
import com.youmai.project.utils.Util;

import java.io.File;

/**
 * Created by Administrator on 2017/12/29 0029.
 */

public class PicturesUtil {

    /* 请求识别码 */
    public static final int CODE_GALLERY_REQUEST = 0xa0;
    public static final int CODE_CAMERA_REQUEST = 0xa1;
    public static final int CODE_RESULT_REQUEST = 0xa2;
    public static final String pai = Util.getSdcardPath() + "pictures.jpg";
    public static final String newPath = Util.getSdcardPath() + "new_photo.jpg";
    public static final String newPath2 = Util.getSdcardPath() + "new_photo2.jpg";
    public static final String crop = Util.getSdcardPath() + "crop.jpg";

    public static void selectPhoto(final Activity context, final int type) {
        File file=new File(crop);
        if(null!=file && file.isFile()){
            file.delete();
        }
        View view_choise = LayoutInflater.from(context).inflate(R.layout.dialog_choseimg, null);
        final Dialog dialog  = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle(null);
        dialog.setCancelable(true);
        dialog.setContentView(view_choise);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
        dialog.show();
        //拍照
        view_choise.findViewById(R.id.textview_choseimg_camera).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 判断存储卡是否可用，存储照片文件
                if (Util.hasSdcard()) {
                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(pai)));
                    context.startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
                } else {
                    Toast.makeText(context,"没有SDCard!",Toast.LENGTH_LONG).show();
                }
            }
        });
        //本地相册选择
        view_choise.findViewById(R.id.textview_choseimg_gallery).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if(type==1){
                    Intent intent=new Intent(context, ImageGridActivity.class);
                    context.startActivityForResult(intent,CODE_GALLERY_REQUEST );
                    Bimp.imgList.clear();
                }else{
                    Intent intentFromGallery = new Intent();
                    // 设置文件类型
                    intentFromGallery.setType("image/*");
                    intentFromGallery.setAction(Intent.ACTION_PICK);
                    context.startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
                }

            }
        });
    }
}
