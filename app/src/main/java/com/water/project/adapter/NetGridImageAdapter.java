package com.water.project.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.water.project.R;
import com.water.project.application.MyApplication;

import java.util.List;

public class NetGridImageAdapter extends BaseAdapter {

	private List<String> selectBitmap;
	private LayoutInflater inflater;
	private Context context;
	public NetGridImageAdapter(Context context, List<String> selectBitmap){
		this.context=context;
		this.selectBitmap=selectBitmap;
		this.inflater = LayoutInflater.from(context);
	}
	public int getCount() {
		if(selectBitmap==null){
			return 0;
		}
		return selectBitmap.size();
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
		String imgPath=selectBitmap.get(position);
		if(!TextUtils.isEmpty(imgPath)){
			if(!imgPath.equals(holder.grida_image.getTag())){
				holder.grida_image.setTag(imgPath);
				Glide.with(MyApplication.application).load(imgPath).into(holder.grida_image);
			}
		}
		return convertView;
	}

	private class ViewHolder {
		 ImageView grida_image;
	}
}
