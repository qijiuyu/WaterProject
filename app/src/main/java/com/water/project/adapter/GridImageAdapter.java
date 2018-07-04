package com.water.project.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.water.project.R;
import com.water.project.application.MyApplication;
import com.water.project.http.HttpConstant;
import com.water.project.utils.photo.ImageItem;

import java.util.List;

public class GridImageAdapter extends BaseAdapter {

	private List<ImageItem> selectBitmap;
	private LayoutInflater inflater;
	private Context context;
	public GridImageAdapter(Context context, List<ImageItem> selectBitmap){
		this.context=context;
		this.selectBitmap=selectBitmap;
		this.inflater = LayoutInflater.from(context);
	}
	public int getCount() {
		return selectBitmap.size()+1;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = this.inflater.inflate(R.layout.selectphoto_item, null);
			holder.grida_image=(ImageView)convertView.findViewById(R.id.item_grida_image);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position==selectBitmap.size()){
			holder.grida_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.add_photo_icon));
		}else{
			String imgPath=selectBitmap.get(position).getImagePath();
			if(!TextUtils.isEmpty(imgPath)){
				if(imgPath.indexOf(HttpConstant.IP)==-1){
					Glide.with(MyApplication.application).load("file://" +imgPath).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.grida_image);
				}else{
					Glide.with(MyApplication.application).load(imgPath).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.grida_image);
				}
			}
		}
		return convertView;
	}

	private class ViewHolder {
		ImageView grida_image;
	}
}
