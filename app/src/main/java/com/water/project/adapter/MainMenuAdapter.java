package com.water.project.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.water.project.R;
import com.water.project.bean.Menu;

import java.util.List;
/**
 */
public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.MyHolder> {

    private Context context;
    private List<Menu> menuList;
    //设置该下标的菜单无法点击
    private int noClick=-1;
    private OnItemClickListener onItemClickListener;
    public MainMenuAdapter(Context context, List<Menu> menuList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.menuList=menuList;
        this.onItemClickListener=onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_menu, viewGroup,false);
        MyHolder holder = new MyHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        MyHolder holder = (MyHolder) myHolder;
        holder.tvName.setText(menuList.get(i).getName());
        holder.imgHead.setImageResource(menuList.get(i).getImg());
        holder.relClick.setTag(i);
        holder.relClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final int position=(Integer)v.getTag();
                if(position!=noClick){
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView imgHead;
        TextView tvName;
        RelativeLayout relClick;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvName=(TextView)itemView.findViewById(R.id.tv_name);
            imgHead=(ImageView)itemView.findViewById(R.id.img_head);
            relClick=itemView.findViewById(R.id.rel_click);
        }
    }

    public void setNoClickIndex(int noClick){
        this.noClick=noClick;
    }
}

