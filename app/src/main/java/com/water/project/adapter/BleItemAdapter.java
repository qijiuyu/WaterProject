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
import com.water.project.bean.eventbus.EventStatus;
import com.water.project.bean.eventbus.EventType;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class BleItemAdapter extends BaseAdapter {

	private List<Ble> list;
	private LayoutInflater inflater;
	private Context context;
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
			holder.tvMac=convertView.findViewById(R.id.tv_mac);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Ble ble=list.get(position);
		holder.tvBleName.setText("设备名称：ZKGD");
		holder.tvMac.setText(ble.getBleMac());
		holder.tvConnect.setTag(list.get(position));
		if(ble.getBleName().contains("ZKGD")){
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
				EventBus.getDefault().post(new EventType(EventStatus.CONNCATION_BLE,v.getTag()));
			}
		});
		return convertView;
	}


	private class ViewHolder {
		TextView tvBleName,tvConnect,tvMac;
	}
}
