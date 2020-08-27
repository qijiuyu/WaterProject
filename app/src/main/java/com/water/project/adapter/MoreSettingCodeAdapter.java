package com.water.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import com.water.project.R;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreSettingCodeAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private int conut;
    private int m;//0(默认)表示采集探头, 1表示采集北斗设备数据

    public MoreSettingCodeAdapter(Context context, List<String> list,int conut,int m) {
        super();
        this.context = context;
        this.list = list;
        this.conut=conut;
        this.m=m;
    }

    @Override
    public int getCount() {
        return conut;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_more_setting_code, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tvName.setText("统一编码"+(position+1));
        if(list!=null){
            final String[] arrys=list.get(position).split(",");
            holder.etCode.setText(arrys[0]);
            holder.etOther.setText(arrys[1]);
        }else{
            holder.etCode.setText(null);
            holder.etOther.setText(null);
        }

        if(m==0){
            holder.etOther.setHint("请输入探头ID号");
        }else{
            holder.etOther.setHint("北斗SIM卡号");
        }
        return view;
    }


    static
    class ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.et_code)
        EditText etCode;
        @BindView(R.id.et_other)
        EditText etOther;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
