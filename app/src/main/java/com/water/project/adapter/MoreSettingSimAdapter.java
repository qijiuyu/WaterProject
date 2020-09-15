package com.water.project.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.bean.MoreCode;

import java.util.List;

public class MoreSettingSimAdapter extends RecyclerView.Adapter<MoreSettingSimAdapter.MyHolder> {

    private Context context;

    public MoreSettingSimAdapter(Context context) {
        super();
        this.context = context;
    }

    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_more_setting_beidou, viewGroup,false);
        MyHolder holder = new MyHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int i) {
        if(i<10){
            holder.tvTime.setText("0"+i);
        }else{
            holder.tvTime.setText(""+i);
        }

    }




    @Override
    public int getItemCount() {
        return 60;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView tvTime,tvUpdate,tvRed;
        EditText etSim;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvTime=itemView.findViewById(R.id.tv_time);
            etSim=itemView.findViewById(R.id.et_sim);
            tvUpdate=itemView.findViewById(R.id.tv_update);
            tvRed=itemView.findViewById(R.id.tv_red);
        }
    }
}

