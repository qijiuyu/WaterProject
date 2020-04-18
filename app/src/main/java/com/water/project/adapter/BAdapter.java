package com.water.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.water.project.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BAdapter extends BaseAdapter {

    private Context context;
    private String[] str;
    private List<TextView> list=new ArrayList<>();
    public BAdapter(Context context, String[] str) {
        super();
        this.context = context;
        this.str = str;
    }

    @Override
    public int getCount() {
        return str == null ? 0 : str.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder holder = null;
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_bactivity, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tvName.setText("通道"+(position+1)+"：");
        //设置信号强度
        String data=str[position];
        if(data.indexOf("V")!=-1){
            data=data.substring(0,1);
        }
        setSignal(Integer.parseInt(data));
        return view;
    }

    /**
     * 设置信号强度
     * @param index
     */
    private void setSignal(int index){
        list.clear();
        list.add(holder.tv1);
        list.add(holder.tv2);
        list.add(holder.tv3);
        list.add(holder.tv4);
        for (int i=0;i<list.size();i++){
             if(index>i){
                 list.get(i).setBackgroundResource(R.drawable.bg_signal_yes);
             }else{
                 list.get(i).setBackgroundResource(R.drawable.bg_signal_no);
             }
        }
    }

    static class ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv1)
        TextView tv1;
        @BindView(R.id.tv2)
        TextView tv2;
        @BindView(R.id.tv3)
        TextView tv3;
        @BindView(R.id.tv4)
        TextView tv4;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
