package com.water.project.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.water.project.R;
import com.water.project.bean.MoreCode;
import java.util.List;

public class MoreSettingCodeAdapter extends RecyclerView.Adapter<MoreSettingCodeAdapter.MyHolder> {

    private Context context;
    private List<MoreCode> list;
    private int m;//0(默认)表示采集探头, 1表示采集北斗设备数据
    private MoreCode playObject;

    public MoreSettingCodeAdapter(Context context, List<MoreCode> list,int conut,int m) {
        super();
        this.context = context;
        this.list = list;
        this.m=m;
        while (conut>list.size()){
            list.add(new MoreCode());
        }
        for (int i=0;i<list.size();i++){
             if(list.size()>conut){
                 list.remove((list.size()-(i+1)));
             }
        }
    }

    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_more_setting_code, viewGroup,false);
        MyHolder holder = new MyHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int i) {
        final MoreCode moreCode=list.get(i);
        holder.tvName.setText("第"+(i+1)+"路");
        holder.etCode.setText(moreCode.getCode());

        if(m==0){
            if(!TextUtils.isEmpty(moreCode.getOther())){
                holder.etOther.setText(moreCode.getOther().substring(moreCode.getOther().length()-3));
            }
            holder.etOther.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
            holder.etOther.setHint("请输入探头ID号");
        }else{
            holder.etOther.setText(moreCode.getOther());
            holder.etOther.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
            holder.etOther.setHint("请输入北斗SIM卡号");
        }


        /**
         * 监听输入框
         */
        holder.etCode.setTag(moreCode);
        holder.etCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    playObject= (MoreCode) v.getTag();
                    holder.etCode.addTextChangedListener(textWatcher1);
                }else{
                    holder.etCode.removeTextChangedListener(textWatcher1);
                }
            }
        });


        /**
         * 监听输入框
         */
        holder.etOther.setTag(moreCode);
        holder.etOther.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    playObject= (MoreCode) v.getTag();
                    holder.etOther.addTextChangedListener(textWatcher2);
                }else{
                    holder.etOther.removeTextChangedListener(textWatcher2);
                }
            }
        });
    }


    /**
     * 监听输入框
     */
    TextWatcher textWatcher1=new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        public void afterTextChanged(Editable s) {
            String content=s.toString().trim();
            playObject.setCode(content);
        }
    };


    /**
     * 监听输入框
     */
    TextWatcher textWatcher2=new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        public void afterTextChanged(Editable s) {
            String content=s.toString().trim();
            playObject.setOther(content);
        }
    };


    @Override
    public int getItemCount() {
        return list==null ? 0 : list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        EditText etCode,etOther;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tv_name);
            etCode=itemView.findViewById(R.id.et_code);
            etOther=itemView.findViewById(R.id.et_other);
        }
    }
}

