package com.water.project.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.water.project.R;
import com.water.project.bean.Ble;
import com.water.project.bean.BleConCallBack;

import java.util.List;

public class BleItemAdapter extends BaseAdapter {

	private List<Ble> list;
	private LayoutInflater inflater;
	private Context context;
	private BleConCallBack bleConCallBack;
	public BleItemAdapter(Context context, List<Ble> list){
		this.context=context;
		this.list=list;
		this.inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return list==null ? 0 : list.size();
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
			convertView = this.inflater.inflate(R.layout.ble_item, null);
			holder.tvBleName=(TextView) convertView.findViewById(R.id.tv_bi_bleName);
			holder.tvConnect=(TextView)convertView.findViewById(R.id.tv_bi_connect);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		final String bleName=list.get(position).getBleName();
		if(!TextUtils.isEmpty(bleName)){
			holder.tvBleName.setText(bleName);
			holder.tvConnect.setTag(list.get(position));
			if(bleName.contains("ZX-PARK")){
				holder.tvConnect.setVisibility(View.VISIBLE);
			}else{
				holder.tvConnect.setVisibility(View.GONE);
			}
			holder.tvConnect.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(null==v.getTag()){
						return;
					}
					//连接蓝牙
					bleConCallBack.connetion((Ble) v.getTag());
				}
			});
		}
		return convertView;
	}


	public void setCallBack(BleConCallBack bleConCallBack){
		this.bleConCallBack=bleConCallBack;
	}

	private class ViewHolder {
		TextView tvBleName,tvConnect;
	}
}
