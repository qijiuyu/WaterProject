package com.water.project.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.water.project.R;
import com.water.project.utils.Util;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GetMoreDataAdapter extends BaseAdapter {

    private Activity activity;
    private List<String> list;

    public GetMoreDataAdapter(Activity activity, List<String> list) {
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
            view = LayoutInflater.from(activity).inflate(R.layout.item_get_data, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String message=list.get(position);

        holder.tvPosition.setText("第"+(position+1)+"路数据");
        try {
            final int length=message.length();
            //显示采集时间
            message=message.replace("GDCURRENT>","");
            StringBuffer stringBuffer=new StringBuffer("20");
            stringBuffer.append(message.substring(0,2)+"-");
            stringBuffer.append(message.substring(2,4)+"-");
            stringBuffer.append(message.substring(4,6)+" ");
            stringBuffer.append(message.substring(6,8)+":");
            stringBuffer.append(message.substring(8,10)+":");
            stringBuffer.append(message.substring(10,12));
            holder.tvAgCjTime.setText(stringBuffer.toString());

            String YaLi=message.substring(93,103).replace("P","");
            if(YaLi.contains("99999999")){
                holder.tvAgYali.setText(YaLi+"");
            }else{
                holder.tvAgYali.setText(Util.setDouble(Double.parseDouble(YaLi),3)+"");
            }

            String MaiShen=message.substring(13,23).replace("L","");
            if(YaLi.contains("99999999") || MaiShen.equals("FFFF.FFF")){
                holder.tvAgMaishen.setText(MaiShen+"m");
            }else{
                holder.tvAgMaishen.setText(Util.setDouble(Double.parseDouble(MaiShen),3)+"m");
            }


            String  DianDaoLv=message.substring(40,50).replace("C","");
            holder.tvAgDiandaolv.setText(DianDaoLv+"uS/cm");


            String QiYa=message.substring(103,110).replace("B","");;
            if(YaLi.contains("99999999")){
                holder.tvAgQiya.setText(QiYa+"");
            }else{
                holder.tvAgQiya.setText(Util.setDouble(Double.parseDouble(QiYa),3)+"");
            }


            String ShuiWen=message.substring(24,33).replace("T","");
            if(YaLi.contains("99999999")){
                holder.tvAgShuiwen.setText(ShuiWen+"℃");
            }else{
                holder.tvAgShuiwen.setText(Util.setDouble(Double.parseDouble(ShuiWen),4)+"℃");
            }


            String  QiWen=message.substring(61,68).replace("R","");
            if(YaLi.contains("99999999")){
                holder.tvAgQiwen.setText(QiWen+"℃");
            }else{
                holder.tvAgQiwen.setText(Util.setDouble(Double.parseDouble(QiWen),3)+"℃");
            }


            String  DianYa=message.substring(50,56).replace("V","");
            if(YaLi.contains("99999999")){
                holder.tvAgDianya.setText(DianYa+"V");
            }else{
                holder.tvAgDianya.setText(Util.setDouble(Double.parseDouble(DianYa),1)+"V");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }


    static
    class ViewHolder {
        @BindView(R.id.tv_position)
        TextView tvPosition;
        @BindView(R.id.tv_ag_cjTime)
        TextView tvAgCjTime;
        @BindView(R.id.tv_ag_maishen)
        TextView tvAgMaishen;
        @BindView(R.id.tv_ag_diandaolv)
        TextView tvAgDiandaolv;
        @BindView(R.id.rel_ddl)
        RelativeLayout relDdl;
        @BindView(R.id.tv_ag_yali)
        TextView tvAgYali;
        @BindView(R.id.tv_ag_qiya)
        TextView tvAgQiya;
        @BindView(R.id.tv_ag_shuiwen)
        TextView tvAgShuiwen;
        @BindView(R.id.tv_ag_qiwen)
        TextView tvAgQiwen;
        @BindView(R.id.tv_ag_dianya)
        TextView tvAgDianya;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
