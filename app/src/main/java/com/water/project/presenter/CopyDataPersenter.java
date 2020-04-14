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
import com.water.project.utils.BleUtils;
import com.water.project.utils.BuglyUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.FileUtils;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.ble.ByteUtil;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;
import com.water.project.view.LinearGradientTextView;

import org.greenrobot.eventbus.EventBus;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2020/3/18.
 */

public class CopyDataPersenter {

    private CopyDataActivity activity;
    private DialogView dialogView;
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
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
    //读取数据时的弹框
    public Dialog redDialog;

    private Handler handler=new Handler();
    public CopyDataPersenter(CopyDataActivity activity){
        this.activity=activity;
    }

    /**
     * 重新扫描蓝牙
     */
    public void resumeScan(){
        DialogUtils.closeProgress();
        dialogView = new DialogView(dialogView,activity, "扫描不到该蓝牙设备，请靠近设备再进行扫描！", "重新扫描","取消", new View.OnClickListener() {
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
        dialogView = new DialogView(dialogView,activity, "蓝牙连接断开，请靠近设备进行连接!","重新连接", "取消", new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
                EventBus.getDefault().post(new EventType(EventStatus.SEND_CHECK_MCD));
            }
        }, null);
        dialogView.show();
    }


    /**
     *组装第三条读取的命令
     * @param red1
     * @return
     */
    private long sLong,eLong;
    private String[] strings=null;
    public boolean setRed3Cmd(String red1){
        boolean isSend=true;
        try {
            if(strings==null){
                strings=red1.split(",");
                strings[1]="20"+strings[1].substring(0,strings[1].length()-2);
                strings[2]="20"+strings[2].substring(0,strings[2].length()-2);

                //间隔分钟
                minutes=Integer.parseInt(strings[3]);
                //获取总条数
                totalNum=(getGapMinutes(strings[1],strings[2])/minutes)+1;
            }

            //这是第一次读
            if(TextUtils.isEmpty(redStart)){
                redStart=strings[1];
            }else{
                //在上次结束时间上加上一次间隔分钟
                eLong=eLong+(minutes*60*1000);
                redStart=df.format(new Date(eLong));
            }
            //计算结束时间
            sLong=df.parse(redStart).getTime();
            eLong=sLong+(19*minutes*60*1000);
            //判断是否超过结束时间
            if(eLong<endLong){
                redEnd=df.format(new Date(eLong));
                isSend=true;
            }else{
                redEnd=strings[2];
                isSend=false;
            }

            //设置根据时间段读取设备里面的数据
            SendBleStr.redDeviceByTime(redStart,redEnd);
        }catch (Exception e){
            e.printStackTrace();
        }
        return isSend;
    }


    /**
     * 展示读取数据时的提示框
     */
    private TextView tvContent;
    private NumberFormat numberFormat = NumberFormat.getInstance();
    public void showTripDialog(String red3){
        //已读取了几条
        int newNum= BleUtils.getSendData(red3,256).size();

        //获取百分比
        numberFormat.setMaximumFractionDigits(2);
        String status= (numberFormat.format((float) newNum / (float) totalNum * 100))+"%";

        String startTime=strings[1].substring(0, 4)+"年"+strings[1].substring(4,6)+"月"+strings[1].substring(6,8)+"日"+strings[1].substring(8,10)+"时"+strings[1].substring(10,12)+"分";
        String endTime=strings[2].substring(0, 4)+"年"+strings[2].substring(4,6)+"月"+strings[2].substring(6,8)+"日"+strings[2].substring(8,10)+"时"+strings[2].substring(10,12)+"分";

        if(redDialog!=null && redDialog.isShowing() && tvContent!=null){
            tvContent.setText("需读取数据记录"+totalNum+"条\n\n已读取数据记录"+newNum+"条\n\n已读取数据记录百分比："+status+"\n\n最早数据记录时间："+startTime+"\n\n最新数据记录时间："+endTime+"\n\n数据记录间隔时间："+minutes+"分钟");
            return;
        }
        View view= LayoutInflater.from(activity).inflate(R.layout.dialog_copy,null);
        redDialog=DialogUtils.dialogPop(view,activity);
        LinearGradientTextView tvTitle=view.findViewById(R.id.tv_title);
        tvTitle.setText("正在读取数据记录...");
        tvContent=view.findViewById(R.id.tv_content);
        tvContent.setText("需读取数据记录"+totalNum+"条\n\n已读取数据记录"+newNum+"条\n\n已读取数据记录百分比："+status+"\n\n最早数据记录时间："+startTime+"\n\n最新数据记录时间："+endTime+"\n\n数据记录间隔时间："+minutes+"分钟");
    }


    /**
     * 关闭读取时的进度框
     */
    public void closeTripDialog(){
        if(redDialog!=null && redDialog.isShowing()){
            redDialog.dismiss();
            redDialog=null;
        }
    }

    /**
     * 显示读取完成的弹框
     */
    public void showRedComplete(String red3){
        //关闭读取时的进度框
        closeTripDialog();

        //已读取了几条
        int newNum= BleUtils.getSendData(red3,256).size();
        String startTime=strings[1].substring(0, 4)+"年"+strings[1].substring(4,6)+"月"+strings[1].substring(6,8)+"日"+strings[1].substring(8,10)+"时"+strings[1].substring(10,12)+"分";
        String endTime=strings[2].substring(0, 4)+"年"+strings[2].substring(4,6)+"月"+strings[2].substring(6,8)+"日"+strings[2].substring(8,10)+"时"+strings[2].substring(10,12)+"分";

        View view= LayoutInflater.from(activity).inflate(R.layout.dialog_copy_complete,null);
        final Dialog dialog=DialogUtils.dialogPop(view,activity);
        TextView tvTitle=view.findViewById(R.id.tv_title);
        tvTitle.setText("读取数据记录已全部完成,可以将数据记录拷贝到新设备了");
        TextView tvContent=view.findViewById(R.id.tv_content);
        TextView tvComplete=view.findViewById(R.id.tv_complete);
        tvComplete.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvContent.setText("共需拷贝数据记录"+newNum+"条\n\n最早数据记录时间："+startTime+"\n\n最新数据记录时间："+endTime+"\n\n数据记录间隔时间："+minutes+"分钟");
        tvComplete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();

                //将读取的数据存储在本地
                activity.saveSDCard();
            }
        });
    }



    /**
     * 组装写入新设备的数据
     * @param red3
     */
    public List<String> writeArray;
    //已经拷贝了几条了
    public int writeNum=0;
    public boolean setWriteData(String red3,String cmd){
        if(writeArray==null){
            writeArray=BleUtils.getSendData(red3,256);

            FileUtils.createFile("abcd.txt", red3);
        }

        //获取设备回执的开始与结束时间
        String[] strings=cmd.replace("GDRECORDA","").split(",");
        String writeStartTime=strings[0];
        String writeEndTime=strings[1];

        //计算并组装数据
        StringBuilder stringBuilder=new StringBuilder();
        for (int i=0,len=writeArray.size();i<len;i++){
             String item=writeArray.get(i).substring(0,10);
             if(Integer.parseInt(item)>=Integer.parseInt(writeStartTime)){
                 stringBuilder.append(writeArray.get(i));
             }
             if(Integer.parseInt(item)==Integer.parseInt(writeEndTime)){
                break;
            }
        }

        //判断这次的拷贝时间，是否跟上次一样
        String cacheTime=SPUtil.getInstance(activity).getString(SPUtil.COPY_TIME);
        if(TextUtils.isEmpty(cacheTime) || !cacheTime.equals(writeStartTime)){
            SPUtil.getInstance(activity).addString(SPUtil.COPY_TIME,writeStartTime);
            //叠加条数
            writeNum=writeNum+(BleUtils.getSendData(stringBuilder.toString(),256).size());
        }

        String head="47445245434F524441";   //头部命令
        String total= ByteUtil.cumulative(head+stringBuilder.toString());  //16进制累加总和
        String end="3E4F4B";       //结尾命令

        //设置给新设备写入大量数据
        SendBleStr.WRITE_NEW_DEVICE_LONG_DATA=head+stringBuilder.toString()+total+end;

        //保存每次拷贝的数据
        FileUtils.createFile(writeStartTime+".txt", SendBleStr.WRITE_NEW_DEVICE_LONG_DATA);
        return true;
    }


    /**
     * 展示拷贝数据时的提示框
     */
    public void showCopyDialog(){
        //获取百分比
        numberFormat.setMaximumFractionDigits(2);
        String status= (numberFormat.format((float) writeNum / (float) totalNum * 100))+"%";

        String startTime=strings[1].substring(0, 4)+"年"+strings[1].substring(4,6)+"月"+strings[1].substring(6,8)+"日"+strings[1].substring(8,10)+"时"+strings[1].substring(10,12)+"分";
        String endTime=strings[2].substring(0, 4)+"年"+strings[2].substring(4,6)+"月"+strings[2].substring(6,8)+"日"+strings[2].substring(8,10)+"时"+strings[2].substring(10,12)+"分";

        if(redDialog!=null && redDialog.isShowing() && tvContent!=null){
            tvContent.setText("需拷贝数据记录"+totalNum+"条\n\n已拷贝数据记录"+writeNum+"条\n\n已读取数据记录百分比："+status+"\n\n最早数据记录时间："+startTime+"\n\n最新数据记录时间："+endTime+"\n\n数据记录间隔时间："+minutes+"分钟");
            return;
        }
        View view= LayoutInflater.from(activity).inflate(R.layout.dialog_copy,null);
        redDialog=DialogUtils.dialogPop(view,activity);
        LinearGradientTextView tvTitle=view.findViewById(R.id.tv_title);
        tvTitle.setText("正在拷贝数据记录...");
        tvContent=view.findViewById(R.id.tv_content);
        tvContent.setText("需拷贝数据记录"+totalNum+"条\n\n已拷贝数据记录"+writeNum+"条\n\n已读取数据记录百分比："+status+"\n\n最早数据记录时间："+startTime+"\n\n最新数据记录时间："+endTime+"\n\n数据记录间隔时间："+minutes+"分钟");
    }


    /**
     * 显示拷贝完成的弹框
     */
    public void showCopyComplete(){
        if(redDialog!=null && redDialog.isShowing()){
            redDialog.dismiss();
            redDialog=null;
        }
        String startTime=strings[1].substring(0, 4)+"年"+strings[1].substring(4,6)+"月"+strings[1].substring(6,8)+"日"+strings[1].substring(8,10)+"时"+strings[1].substring(10,12)+"分";
        String endTime=strings[2].substring(0, 4)+"年"+strings[2].substring(4,6)+"月"+strings[2].substring(6,8)+"日"+strings[2].substring(8,10)+"时"+strings[2].substring(10,12)+"分";

        View view= LayoutInflater.from(activity).inflate(R.layout.dialog_copy_complete,null);
        final Dialog dialog=DialogUtils.dialogPop(view,activity);
        TextView tvTitle=view.findViewById(R.id.tv_title);
        tvTitle.setText("拷贝数据记录已全部完成");
        TextView tvContent=view.findViewById(R.id.tv_content);
        TextView tvComplete=view.findViewById(R.id.tv_complete);
        tvComplete.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvContent.setText("共拷贝数据记录"+writeNum+"条\n\n最早数据记录时间："+startTime+"\n\n最新数据记录时间："+endTime+"\n\n数据记录间隔时间："+minutes+"分钟");
        tvComplete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
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
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
            startLong = df.parse(startDate).getTime();
            endLong = df.parse(endDate).getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        int minutes = (int) ((endLong - startLong) / (1000 * 60));
        return minutes;
    }
}
