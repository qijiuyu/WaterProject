package com.water.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.view.MyWatcher;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreSettingTanTouAdapter extends BaseAdapter {

    private Context context;
    private List<String[]> list;
    private int conut;

    public MoreSettingTanTouAdapter(Context context, List<String[]> list, int conut) {
        super();
        this.context = context;
        this.list = list;
        this.conut = conut;
    }

    @Override
    public int getCount() {
        return conut+list.size();
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
            view = LayoutInflater.from(context).inflate(R.layout.item_more_setting_tantou, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tvName.setText("探头埋深"+(position+1));

        //限制小数点前后
        holder.etTantou.addTextChangedListener(new MyWatcher(5,4));
        if(list.size()>position){
            final String[] arrys=list.get(position);
            holder.etTantou.setText(arrys[1]);
        }else{
            holder.etTantou.setText(null);
        }
        return view;
    }


    static
    class ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.et_tantou)
        EditText etTantou;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
