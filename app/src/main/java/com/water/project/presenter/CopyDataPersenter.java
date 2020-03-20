package com.water.project.presenter;

import android.app.Dialog;
import android.graphics.Paint;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.activity.MainActivity;
import com.water.project.activity.menu5.CopyDataActivity;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.bean.eventbus.EventStatus;
import com.water.project.bean.eventbus.EventType;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;

import org.greenrobot.eventbus.EventBus;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2020/3/18.
 */

public class CopyDataPersenter {

    private CopyDataActivity activity;
    private DialogView dialogView;
    /**
     * 设备里数据的开始与结束时间---字符串
     */
    private String startTime,endTime;
    //设备里数据的开始与结束时间---long型
    private long startLong,endLong;
    /**
     * 读取数据时的开始与结束时间
     */
    private String redStart,redEnd;
    //间隔分钟
    private int minutes;
    //总条数
    private int totalNum;
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
    //读取数据时的弹框
    public Dialog redDialog;

    public CopyDataPersenter(CopyDataActivity activity){
        this.activity=activity;
    }

    /**
     * 重新扫描蓝牙
     */
    public void resumeScan(){
        DialogUtils.closeProgress();
        if(null!=dialogView){
            dialogView.dismiss();
        }
        dialogView = new DialogView(activity, "扫描不到该蓝牙设备，请靠近设备再进行扫描！", "重新扫描","取消", new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
                EventBus.getDefault().post(new EventType(EventStatus.SEND_CHECK_MCD));
            }
        }, null);
        dialogView.show();
    }

    /**
     * 蓝牙连接断开
     */
    public void bleDisConnect(){
        DialogUtils.closeProgress();
        if(null!=dialogView){
            dialogView.dismiss();
        }
        dialogView = new DialogView(activity, "蓝牙连接断开，请靠近设备进行连接!","重新连接", "取消", new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
                EventBus.getDefault().post(new EventType(EventStatus.SEND_CHECK_MCD));
            }
        }, null);
        dialogView.show();
    }


    /**
     * 读取数据超时
     */
    public void timeOut(){
        DialogUtils.closeProgress();
        if(null!=dialogView){
            dialogView.dismiss();
        }
        dialogView = new DialogView(activity, "接收数据超时！", "重试","取消", new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
                EventBus.getDefault().post(new EventType(EventStatus.SEND_CHECK_MCD));
            }
        }, null);
        dialogView.show();
    }


    /**
     * 展示读取数据时的提示框
     */
    private TextView tvContent;
    public void showTripDialog(String red3){
        int newNum=0;//当前第几条
        String status;//百分比
        if(!TextUtils.isEmpty(red3)){
            String[] strings=red3.split(";");
            newNum=strings.length;
        }
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        status= numberFormat.format((float) newNum / (float) totalNum * 100);

        if(redDialog!=null && redDialog.isShowing() && tvContent!=null){
            tvContent.setText("需读取数据记录"+totalNum+"条\n\n已读取数据记录"+newNum+"条\n\n已读取数据记录百分比："+status+"\n\n最早数据记录时间："+startTime+"\n\n最新数据记录时间："+endTime+"\n\n数据记录间隔时间："+minutes+"分钟");
             return;
        }
        View view= LayoutInflater.from(activity).inflate(R.layout.dialog_copy,null);
        redDialog=DialogUtils.dialogPop(view,activity);
        tvContent=view.findViewById(R.id.tv_content);
        tvContent.setText("需读取数据记录"+totalNum+"条\n\n已读取数据记录"+newNum+"条\n\n已读取数据记录百分比："+status+"\n\n最早数据记录时间："+startTime+"\n\n最新数据记录时间："+endTime+"\n\n数据记录间隔时间："+minutes+"分钟");
    }


    /**
     * 显示读取完成的弹框
     */
    public void showRedComplete(String red3){
        if(redDialog!=null && redDialog.isShowing()){
            redDialog.dismiss();
        }
        int newNum=0;//当前第几条
        if(!TextUtils.isEmpty(red3)){
            String[] strings=red3.split(";");
            newNum=strings.length;
        }
        View view= LayoutInflater.from(activity).inflate(R.layout.dialog_copy_complete,null);
        final Dialog dialog=DialogUtils.dialogPop(view,activity);
        TextView tvContent=view.findViewById(R.id.tv_content);
        TextView tvComplete=view.findViewById(R.id.tv_complete);
        tvComplete.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvContent.setText("共需拷贝数据记录"+newNum+"条\n\n最早数据记录时间："+startTime+"\n\n最新数据记录时间："+endTime+"\n\n数据记录间隔时间："+minutes+"分钟");
        tvComplete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    /**
     *组装第三条读取的命令
     * @param red1
     * @return
     */
    public boolean setRed3Cmd(String red1){
        try {
            String[] strings=red1.split(",");

            //已经读取完毕
            if (!TextUtils.isEmpty(redEnd) && redEnd.equals(strings[2])){
                return false;
            }

            //获取设备里的开始与结束时间
            startTime="20"+strings[1].substring(0, 2)+"-"+strings[1].substring(2,4)+"-"+strings[1].substring(4,6)+" "+strings[1].substring(6,8)+":"+strings[1].substring(8,10)+":"+strings[1].substring(10,12);
            endTime="20"+strings[2].substring(0, 2)+"-"+strings[2].substring(2,4)+"-"+strings[2].substring(4,6)+" "+strings[2].substring(6,8)+":"+strings[2].substring(8,10)+":"+strings[2].substring(10,12);
            //间隔分钟
            minutes=Integer.parseInt(strings[3]);
            //获取总条数
            totalNum=getGapMinutes(startTime,endTime)/minutes;

            if(totalNum<=20){
                redStart=strings[1];
                redEnd=strings[2];
            }else{
                //这是第一次读
                if(TextUtils.isEmpty(redStart) && TextUtils.isEmpty(redEnd)){
                    redStart=strings[1];
                }else{
                    redStart=redEnd;
                }
                //获取long日期
                long sLong=df.parse("20"+redStart).getTime();
                long eLong=sLong+(20*minutes*60*1000);
                //判断是否超过结束时间
                if(eLong<endLong){
                    String time=df.format(new Date(eLong));
                    redEnd=time.substring(2,time.length());
                }else{
                    redEnd=strings[2];
                }

                //设置根据时间段读取设备里面的数据
                SendBleStr.redDeviceByTime(redStart,redEnd);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 计算分钟差
     * @param startDate
     * @param endDate
     * @return
     */
    public int getGapMinutes(String startDate, String endDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            startLong = df.parse(startDate).getTime();
            endLong = df.parse(endDate).getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        int minutes = (int) ((endLong - startLong) / (1000 * 60));
        return minutes;
    }
}
