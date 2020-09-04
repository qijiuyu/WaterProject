package com.water.project.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.water.project.R;
import com.water.project.bean.MoreTanTou;
import com.water.project.view.MyWatcher;
import java.util.List;

public class MoreSettingTanTouAdapter extends RecyclerView.Adapter<MoreSettingTanTouAdapter.MyHolder> {

    private Context context;
    private List<MoreTanTou> list;
    private MoreTanTou playObject;

    public MoreSettingTanTouAdapter(Context context, List<MoreTanTou> list, int conut) {
        super();
        this.context = context;
        this.list = list;
        while (conut>list.size()){
            list.add(new MoreTanTou());
        }
    }

    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_more_setting_tantou, viewGroup,false);
        MyHolder holder = new MyHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int i) {
        final MoreTanTou moreTanTou=list.get(i);
        holder.tvName.setText("第"+(i+1)+"路");

        //限制小数点前后
        holder.etTanTou.addTextChangedListener(new MyWatcher(5,4));
        holder.etTanTou.setText(moreTanTou.getMaishen());


        /**
         * 监听输入框
         */
        holder.etTanTou.setTag(moreTanTou);
        holder.etTanTou.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    playObject= (MoreTanTou) v.getTag();
                    holder.etTanTou.addTextChangedListener(textWatcher);
                }else{
                    holder.etTanTou.removeTextChangedListener(textWatcher);
                }
            }
        });

    }


    /**
     * 监听输入框
     */
    TextWatcher textWatcher=new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        public void afterTextChanged(Editable s) {
            String content=s.toString().trim();
            playObject.setMaishen(content);
        }
    };


    @Override
    public int getItemCount() {
        return list==null ? 0 : list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        EditText etTanTou;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tv_name);
            etTanTou=itemView.findViewById(R.id.et_tantou);
        }
    }
}

