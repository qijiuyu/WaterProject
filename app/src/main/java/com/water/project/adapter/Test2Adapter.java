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

public class Test2Adapter extends BaseAdapter {

	private List<String> list;
	private LayoutInflater inflater;
	private Context context;
	public Test2Adapter(Context context, List<String> list){
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
			convertView = this.inflater.inflate(R.layout.item_test2, null);
			holder.tvName=(TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvName.setText(list.get(position));
		return convertView;
	}


	private class ViewHolder {
		TextView tvName;
	}
}
