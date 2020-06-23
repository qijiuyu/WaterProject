package com.water.project.presenter;

import android.app.Dialog;
import android.graphics.Paint;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.activity.GetRecordActivity;
import com.water.project.bean.SelectTime;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.SaveExcel;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.Util;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.LinearGradientTextView;
import com.water.project.view.SelectTimeDialog;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetRecordPersenter {

    private GetRecordActivity activity;
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
    private Handler handler=new Handler();

    public GetRecordPersenter(GetRecordActivity activity){
        this.activity=activity;
    }

    /**
     * 读取第三条记录时，弹出开始与结束时间弹框
     * @param red1
     */
    //间隔分钟
    private int minutes;
    //总条数
    private int totalNum;
    //读取内容的时间段
    private String startTime,endTime;
    public void showDialogRed3(String red1){
        final String[] strings = red1.replace(" ","").split(",");
        strings[1] = "20"+strings[1].substring(0,2)+"-"+strings[1].substring(2,4)+"-"+strings[1].substring(4,6)+" "+strings[1].substring(6,8)+":"+strings[1].substring(8,10);
        strings[2] = "20"+strings[2].substring(0,2)+"-"+strings[2].substring(2,4)+"-"+strings[2].substring(4,6)+" "+strings[2].substring(6,8)+":"+strings[2].substring(8,10);
        //间隔分钟
        minutes = Integer.parseInt(strings[3]);

        View view= LayoutInflater.from(activity).inflate(R.layout.dialog_set_red_time,null);
        final Dialog dialog= DialogUtils.dialogPop(view,activity);
        final TextView tvStart=view.findViewById(R.id.tv_start);
        final TextView tvEnd=view.findViewById(R.id.tv_end);
        tvStart.setText(strings[1]);
        tvEnd.setText(strings[2]);
        //选择开始时间
        tvStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SelectTimeDialog(activity, new SelectTime() {
                    public void getTime(String time, int type) {
                        if(Util.compare(time,strings[1])){
                            ToastUtil.showLong("设备数据记录的起始时间是"+strings[1]);
                            return;
                        }
                        tvStart.setText(time);
                    }
                },4).show();
            }
        });
        //选择结束时间
        tvEnd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SelectTimeDialog(activity, new SelectTime() {
                    public void getTime(String time, int type) {
                        if(!time.equals(strings[2]) && !Util.compare(time,strings[2])){
                            ToastUtil.showLong("设备数据记录的最新时间是"+strings[2]);
                            return;
                        }
                        tvEnd.setText(time);
                    }
                },4).show();
            }
        });

        //确定
        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startTime=tvStart.getText().toString().trim().replace("-","").replace(":","").replace(" ","");
                endTime=tvEnd.getText().toString().trim().replace("-","").replace(":","").replace(" ","");
                if(startTime.equals(endTime)){
                    ToastUtil.showLong("开始时间与结束时间不能一致");
                    return;
                }
                dialog.dismiss();


                //获取总条数
                totalNum = (getGapMinutes(startTime, endTime) / minutes) + 1;

                //展示读取数据时的提示框
                showTripDialog();

                /**
                 * 组装第三条命令，并下发
                 */
                setRed3Cmd();
            }
        });
    }


    /**
     * 组装第三条读取命令
     */
    private String redStart="",redEnd=""; //记录上次读取的时间段
    private long eLong;
    public boolean setRed3Cmd(){
        try {
            if(redEnd.equals(endTime)){
                return false;
            }

            //这是第一次读
            if(TextUtils.isEmpty(redStart)){
                redStart=startTime;
            }else{
                //在上次结束时间上加上一次间隔分钟
                eLong=eLong+(minutes*60*1000);
                redStart=df.format(new Date(eLong));
            }
            //计算结束时间
            long sLong=df.parse(redStart).getTime();
            eLong=sLong+(4*minutes*60*1000);
            //判断是否超过结束时间
            if(eLong<df.parse(endTime).getTime()){
                redEnd=df.format(new Date(eLong));
            }else{
                redEnd=endTime;
            }

            //设置根据时间段读取设备里面的数据
            SendBleStr.redDeviceByTime2(redStart,redEnd);
            //下发命令
            activity.sendData(BleContant.RED_DEVICE_DATA_BY_TIME2);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 展示读取数据时的提示框
     */
    public Dialog redDialog;
    private TextView tvContent;
    public void showTripDialog(){
        View view= LayoutInflater.from(activity).inflate(R.layout.dialog_copy,null);
        redDialog=DialogUtils.dialogPop(view,activity);
        LinearGradientTextView tvTitle=view.findViewById(R.id.tv_title);
        tvTitle.setText("正在读取数据记录...");
        tvContent=view.findViewById(R.id.tv_content);

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,100);
    }

    Runnable runnable=new Runnable() {
        public void run() {
            if(redDialog==null || !redDialog.isShowing()){
                showTripDialog();
                return;
            }
            //已读取了几条
            int newNum= BleUtils.getSendData(activity.red3.toString(),123).size();
            //获取百分比
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            String status= (numberFormat.format((float) newNum / (float) totalNum * 100))+"%";

            String start=startTime.substring(0, 4)+"年"+startTime.substring(4,6)+"月"+startTime.substring(6,8)+"日"+startTime.substring(8,10)+"时"+startTime.substring(10,12)+"分";
            String end=endTime.substring(0, 4)+"年"+endTime.substring(4,6)+"月"+endTime.substring(6,8)+"日"+endTime.substring(8,10)+"时"+endTime.substring(10,12)+"分";

            tvContent.setText("需读取数据记录"+totalNum+"条\n\n已读取数据记录"+newNum+"条\n\n已读取数据记录百分比："+status+"\n\n最早数据记录时间："+start+"\n\n最新数据记录时间："+end+"\n\n数据记录间隔时间："+minutes+"分钟");

            handler.postDelayed(runnable,2000);
        }
    };


    /**
     * 关闭读取时的进度框
     */
    public void closeTripDialog(){
        handler.removeCallbacks(runnable);
        if(redDialog!=null && redDialog.isShowing()){
            redDialog.dismiss();
            redDialog=null;
        }
    }


    /**
     * 显示读取完成的弹框
     */
    public void showRedComplete(final String red2,final String red3){
        //关闭读取时的进度框
        closeTripDialog();

        //已读取了几条
        int newNum= BleUtils.getSendData(red3,123).size();
        String start=startTime.substring(0, 4)+"年"+startTime.substring(4,6)+"月"+startTime.substring(6,8)+"日"+startTime.substring(8,10)+"时"+startTime.substring(10,12)+"分";
        String end=endTime.substring(0, 4)+"年"+endTime.substring(4,6)+"月"+endTime.substring(6,8)+"日"+endTime.substring(8,10)+"时"+endTime.substring(10,12)+"分";

        View view= LayoutInflater.from(activity).inflate(R.layout.dialog_red_complete,null);
        final Dialog dialog=DialogUtils.dialogPop(view,activity);
        TextView tvTitle=view.findViewById(R.id.tv_title);
        tvTitle.setText("读取数据记录已全部完成,共读取数据记录"+newNum+"条");
        TextView tvContent=view.findViewById(R.id.tv_content);
        TextView tvComplete=view.findViewById(R.id.tv_complete);
        tvComplete.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvContent.setText("最早数据记录时间："+start+"\n\n最新数据记录时间："+end+"\n\n数据记录间隔时间："+minutes+"分钟");

        final EditText etName=view.findViewById(R.id.et_name);
        tvComplete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String name=etName.getText().toString().trim();
                if(TextUtils.isEmpty(name)){
                    ToastUtil.showLong("请输入名称");
                    return;
                }
                dialog.dismiss();

                DialogUtils.showProgress(activity,"保存中");
                SaveExcel.saveDataByExcel(activity,red2,name,red3);
            }
        });
    }



    /**
     * 计算分钟差
     * @param startDate
     * @param endDate
     * @return
     */
    public int getGapMinutes(String startDate, String endDate) {
        try {
            long startLong = df.parse(startDate).getTime();
            long endLong = df.parse(endDate).getTime();
            int minutes = (int) ((endLong - startLong) / (1000 * 60));
            return minutes;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
