package com.water.project.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.water.project.R;
import com.water.project.bean.eventbus.EventStatus;
import com.water.project.bean.eventbus.EventType;
import com.water.project.utils.SelectTimeUtils;
import com.water.project.view.CycleWheelView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Administrator on 2019/9/23.
 */

public class DialogUtils {
    static ProgressDialog progressDialog = null;

   static String hour=null,minute=null,data=null;

    public static void getHourAndMinute(final Context context,final int type){
        View view= LayoutInflater.from(context).inflate(R.layout.wheel_hour_minute,null);
        final Dialog dialog=dialogPop(view,context);
        CycleWheelView wvHour=(CycleWheelView)view.findViewById(R.id.wv_hour);
        CycleWheelView wvMinute=(CycleWheelView)view.findViewById(R.id.wv_minute);
        //获取小时数据
        List<String> hourList= SelectTimeUtils.getHour();
        hourList.add("24时");
        //获取分钟数据
        final List<String> minuteList=SelectTimeUtils.getMinute();
        try {
            wvHour.setLabels(hourList);
            wvMinute.setLabels(minuteList);
            wvHour.setSelection(5);
            wvMinute.setSelection(5);
            wvHour.setWheelSize(5);
            wvMinute.setWheelSize(5);
        } catch (CycleWheelView.CycleWheelViewException e) {
            e.printStackTrace();
        }
        wvHour.setCycleEnable(false);
        wvHour.setSelection(0);
        wvHour.setAlphaGradual(0.5f);
        wvHour.setDivider(Color.parseColor("#abcdef"),1);
        wvHour.setSolid(Color.WHITE,Color.WHITE);
        wvHour.setLabelColor(Color.GRAY);
        wvHour.setLabelSelectColor(Color.BLACK);
        wvHour.setOnWheelItemSelectedListener(new CycleWheelView.WheelItemSelectedListener() {
            public void onItemSelected(int position, String label) {
                hour=label;
            }
        });

        wvMinute.setCycleEnable(false);
        wvMinute.setSelection(0);
        wvMinute.setAlphaGradual(0.5f);
        wvMinute.setDivider(Color.parseColor("#abcdef"),1);
        wvMinute.setSolid(Color.WHITE,Color.WHITE);
        wvMinute.setLabelColor(Color.GRAY);
        wvMinute.setLabelSelectColor(Color.BLACK);
        wvMinute.setOnWheelItemSelectedListener(new CycleWheelView.WheelItemSelectedListener() {
            public void onItemSelected(int position, String label) {
                minute=label;
            }
        });
        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int intHour=Integer.parseInt(hour.replace("时",""));
                int intMinute=Integer.parseInt(minute.replace("分",""));
                if(intHour==24 && intMinute>0){
                    Toast.makeText(context, "选择了24小时，分钟最多选00", Toast.LENGTH_LONG).show();
                    return;
                }
                int totalMinute=intHour*60+intMinute;
                if(type==1){
                    EventBus.getDefault().post(new EventType(EventStatus.NEW_SETTING_CJSJJG,totalMinute));
                }else{
                    EventBus.getDefault().post(new EventType(EventStatus.NEW_SETTING_FSSJJG,totalMinute));
                }
                dialog.dismiss();
            }
        });
    }


    public static void selectNewSetting(final Context context,final int type){
        View view= LayoutInflater.from(context).inflate(R.layout.wheel,null);
        final Dialog dialog=dialogPop(view,context);
        TextView tvTitle=(TextView)view.findViewById(R.id.tv_wh_title);
        List<String> dataList;
        if(type==1){
            tvTitle.setText("请选择连接服务器次数");
            dataList=SelectTimeUtils.getGPRS();
        }else{
            tvTitle.setText("请选择补发间隔次数");
            dataList=SelectTimeUtils.getSendNum();
        }
        CycleWheelView cycleWheelView=(CycleWheelView)view.findViewById(R.id.cycleWheelView);
        try {
            cycleWheelView.setLabels(dataList);
            cycleWheelView.setWheelSize(5);
        } catch (CycleWheelView.CycleWheelViewException e) {
            e.printStackTrace();
        }
        cycleWheelView.setCycleEnable(false);
        cycleWheelView.setSelection(0);
        cycleWheelView.setAlphaGradual(0.5f);
        cycleWheelView.setDivider(Color.parseColor("#abcdef"),1);
        cycleWheelView.setSolid(Color.WHITE,Color.WHITE);
        cycleWheelView.setLabelColor(Color.GRAY);
        cycleWheelView.setLabelSelectColor(Color.BLACK);
        cycleWheelView.setOnWheelItemSelectedListener(new CycleWheelView.WheelItemSelectedListener() {
            public void onItemSelected(int position, String label) {
                data=label;
            }
        });

        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(type==1){
                    EventBus.getDefault().post(new EventType(EventStatus.SELECT_GRPS,data));
                }else{
                    EventBus.getDefault().post(new EventType(EventStatus.SELECT_SEND_NUM,data));
                }
                dialog.dismiss();
            }
        });
    }


    /**
     * dialog弹框
     *
     * @param view
     */
    public static Dialog dialogPop(View view,Context context) {
        Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle(null);
        dialog.setCancelable(true);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
        dialog.show();
        return dialog;
    }


    /**
     * loding弹框
     */
    public static void showProgress(Activity activity, String message) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(message);
            return;
        }
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }


    /**
     * 取消进度条
     */
    public static void closeProgress() {
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

}
