package com.water.project.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import com.water.project.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewSettingTimeAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Context context;
	private int count;
	public Map<Integer,Integer> map=new HashMap<>();
	private List<String> list;
	public NewSettingTimeAdapter(Context context, int count,List<String> list){
		this.context=context;
		this.count=count;
		this.list=list;
		this.inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return count;
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
			convertView = this.inflater.inflate(R.layout.item_setting_time, null);
			holder.tvTime=(TextView) convertView.findViewById(R.id.tv_time);
			holder.etTime=(EditText) convertView.findViewById(R.id.et_time);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvTime.setText("补发间隔时间"+(position+1));
		holder.etTime.setTag(position);
		holder.etTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					if(null==v.getTag()){
						return;
					}
					final int index=Integer.parseInt(v.getTag().toString());
					String strTime=((EditText)v).getText().toString().trim();
					if(!TextUtils.isEmpty(strTime)){
						final int data=Integer.parseInt(strTime);
						map.put(index,data);
					}
				}
			}
		});
		if(list!=null){
			if(list.size()>position){
				holder.etTime.setText(list.get(position));
			}else{
                holder.etTime.setText(null);
            }
		}
		return convertView;
	}


	private class ViewHolder {
		TextView tvTime;
		EditText etTime;
	}
}
