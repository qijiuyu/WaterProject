package com.water.project.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.bean.OldData;

import java.util.ArrayList;
import java.util.List;

public class SearchOldAdapter extends BaseAdapter{

	private Context context;
	private List<OldData> list=new ArrayList();
	public SearchOldAdapter(Context context, List<OldData> list) {
		super();
		this.context = context;
		this.list=list;
	}

	@Override
	public int getCount() {
		return list==null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder = null;
		if(view==null){
			holder = new ViewHolder(); 
			view = LayoutInflater.from(context).inflate(R.layout.item_main, null);
			holder.tv1=(TextView)view.findViewById(R.id.tv1);
			holder.tv2=(TextView)view.findViewById(R.id.tv2);
			holder.tv3=(TextView)view.findViewById(R.id.tv3);
			holder.tv4=(TextView)view.findViewById(R.id.tv4);
			holder.tv5=(TextView)view.findViewById(R.id.tv5);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		holder.tv1.setText(list.get(position).getData1());
		holder.tv2.setText(list.get(position).getData2());
		holder.tv3.setText(list.get(position).getData3());
		holder.tv4.setText(list.get(position).getData4());
		holder.tv5.setText(list.get(position).getData5());
		return view;
	}
	
	private class ViewHolder{
		private TextView tv1,tv2,tv3,tv4,tv5;
	 }
}
