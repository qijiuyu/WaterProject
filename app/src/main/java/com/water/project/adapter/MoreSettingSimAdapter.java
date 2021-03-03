package com.water.project.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.water.project.R;
import com.water.project.utils.ToastUtil;

import java.util.List;

public class MoreSettingSimAdapter extends RecyclerView.Adapter<MoreSettingSimAdapter.MyHolder> {

    private Context context;
    private List<String> list;
    private Face face;

    public MoreSettingSimAdapter(Context context,List<String> list,Face face) {
        super();
        this.context = context;
        this.list=list;
        this.face=face;
    }

    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_more_setting_beidou, viewGroup,false);
        MyHolder holder = new MyHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int i) {
        final String[] arrays=list.get(i).split(",");
        if(arrays==null || arrays.length!=2){
            return;
        }
        holder.tvTime.setText(arrays[0]);
        holder.etSim.setText(arrays[1]);


        /**
         * 修改
         */
        holder.tvUpdate.setTag(R.id.tag1,arrays[0]);
        holder.tvUpdate.setTag(R.id.tag2,holder.etSim);
        holder.tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String num= (String) v.getTag(R.id.tag1);
                final EditText etSim= (EditText) v.getTag(R.id.tag2);
                final String sim=etSim.getText().toString().trim();
                if(TextUtils.isEmpty(sim)){
                    ToastUtil.showLong("请输入SIM数据");
                    return;
                }
                face.update(num,sim);

            }
        });


        /**
         * 读取
         */
        holder.tvRed.setTag(arrays[0]);
        holder.tvRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String num= (String) v.getTag();
                face.red(num);
            }
        });
    }


    public interface Face{
        void update(String num,String sim);

        void red(String num);
    }


    @Override
    public int getItemCount() {
        return list==null ? 0 : list.size();
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

