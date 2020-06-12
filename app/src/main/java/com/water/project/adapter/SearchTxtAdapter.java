package com.water.project.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchTxtAdapter extends BaseAdapter {

    private Activity activity;
    private List<File> list;
    private SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SearchTxtAdapter(Activity activity, List<File> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
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
            view = LayoutInflater.from(activity).inflate(R.layout.item_txt, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        File file=list.get(position);
        holder.tvName.setText(file.getName());
        holder.tvSize.setText(FileUtils.FormetFileSize(file.length()));
        holder.tvDate.setText(df.format(new Date(file.lastModified())));

        holder.relClick.setTag(file.getPath());
        holder.relClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path= (String) v.getTag();
                Intent intent=new Intent();
                intent.putExtra("path",path);
                activity.setResult(200,intent);
                activity.finish();
            }
        });
        return view;
    }


    static
    class ViewHolder {
        @BindView(R.id.rel_click)
        RelativeLayout relClick;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_size)
        TextView tvSize;
        @BindView(R.id.tv_date)
        TextView tvDate;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
