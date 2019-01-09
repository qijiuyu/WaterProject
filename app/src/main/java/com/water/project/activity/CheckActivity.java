package com.water.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.BuglyUtils;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.StatusBarUtils;
import com.water.project.utils.SystemBarTintManager;
import com.water.project.utils.Util;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;

/**
 * 数据校验
 * Created by Administrator on 2018/9/2.
 */

public class CheckActivity extends BaseActivity implements View.OnClickListener{
    private ImageView imgClear;
    private TextView tvTime,tvYaLi,tvQiYa,tvTanTou,tvShuiWei,tvWuCha;
    private EditText etCheck;
    private DialogView dialogView;
    private Handler mHandler=new Handler();
    //下发命令的编号
    private int SEND_STATUS;
    //手动输入的误差数据
    private String wuCha;
    //是否校验完成
    private boolean isCheck=false;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_check);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_1fc37f);
        initView();
        register();//注册广播
        sendData(BleContant.SEND_REAL_TIME_DATA);
//        showData("GDCURRENT>180812153625L0010.97T028.8B100V05.98CSQ31R-35.4E0098P0010.125B10.009C0011.000C001413.01B001413.00T001413.00R001413.00+0000.0200;");
    }

    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("校测水位数据");
        imgClear=(ImageView)findViewById(R.id.img_clear_check);
        tvTime=(TextView)findViewById(R.id.tv_ac_cjTime);
        tvYaLi=(TextView)findViewById(R.id.tv_ac_yali);
        tvQiYa=(TextView)findViewById(R.id.tv_ac_qiya);
        tvTanTou=(TextView)findViewById(R.id.tv_ac_tantou);
        tvShuiWei=(TextView)findViewById(R.id.tv_ac_shuiwei);
        etCheck=(EditText)findViewById(R.id.et_ac_check);
        tvWuCha=(TextView)findViewById(R.id.tv_ac_wucha);
        imgClear.setOnClickListener(this);
        findViewById(R.id.tv_btn).setOnClickListener(this);
        findViewById(R.id.tv_btn_update).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);

        etCheck.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    imgClear.setVisibility(View.VISIBLE);
                }else{
                    imgClear.setVisibility(View.GONE);
                    tvWuCha.setText("");
                    return;
                }

                //计算误差
                final String strShui=tvShuiWei.getText().toString().trim().replace("m","");
                final double shuiWei=Double.parseDouble(strShui);
                final double check=Double.parseDouble(s.toString());
                //显示误差
                wuCha=Util.sub(shuiWei,check)+"";
                tvWuCha.setText(wuCha);
                if(wuCha.equals("0.0")){
                    tvWuCha.setText("0");
                }
            }
        });
    }


    /**
     * 发送蓝牙命令
     */
    private void sendData(int status){
        //判断蓝牙是否打开
        if(!BleUtils.isEnabled(CheckActivity.this,MainActivity.mBtAdapter)){
            return;
        }
        SEND_STATUS=status;
        if(SEND_STATUS==BleContant.SEND_REAL_TIME_DATA){
            showProgress("正在读取实时数据...");
        }else{
            showProgress("正在进行数据校正...");
        }
        //如果蓝牙连接断开，就扫描重连
        if(MainActivity.bleService.connectionState==MainActivity.bleService.STATE_DISCONNECTED){
            //扫描并重连蓝牙
            final Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
            if(null!=ble){
                showProgress("扫描并连接蓝牙设备...");
                MainActivity.bleService.scanDevice(ble.getBleName());
            }
            return;
        }
        SendBleStr.sendBleData(status,1);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_clear_check:
                 etCheck.setText(null);
                 break;
            //数据修改
            case R.id.tv_btn:
                final String YaLi=tvYaLi.getText().toString().trim();
                if(YaLi.contains("99999999")){
                    dialogView = new DialogView(mContext, "传感器故障，无法参与计算校准，请查找原因！", "确认",null, new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                        }
                    }, null);
                    dialogView.show();
                    return;
                }
                wuCha=tvWuCha.getText().toString().trim();
                final String strCheck=etCheck.getText().toString().trim();
                final int qIndex=strCheck.indexOf(".");
                final int hIndex=strCheck.length()-qIndex-1;
                if(strCheck.indexOf(".")==-1 && strCheck.length()>4){
                    showToastView("人工实测水位埋深最多只能输入4位整数！");
                }else if(qIndex>4){
                    showToastView("人工实测水位埋深的小数点前面最多只能是4位数");
                }else if(hIndex>3){
                    showToastView("人工实测水位埋深的小数点后面最多只能是3位数");
                }else if(TextUtils.isEmpty(wuCha)){
                    showToastView("没有误差数据！");
                }else if(Double.parseDouble(wuCha)==0){
                    showToastView("水位埋深数据无误差，无需校正！");
                }else{
                    double d=Double.parseDouble(wuCha.replace("-",""))*100;
                    if(d>0 && d<10){
                        sendData(BleContant.SEND_CHECK_ERROR);
                    }
                    if(d>=10 && d<20){
                        if(wuCha.contains("-")){
                            showPop(1,d);
                        }else{
                            showPop(2,d);
                        }
                    }
                    if(d>=20){
                        showNotCheckPop();
                    }
                }
                 break;
            //数据实时更新
            case R.id.tv_btn_update:
                 isCheck=false;
                 sendData(BleContant.SEND_REAL_TIME_DATA);
                 break;
            case R.id.lin_back:
                 finish();
                 break;
             default:
                 break;
        }
    }


    /**
     * 弹出提示误差框
     */
    private void showPop(int type,double data){
        final String wuchaStr=Util.setDouble(data,1);
        View view=getLayoutInflater().inflate(R.layout.pop_check,null);
        dialogPop(view,true);
        ImageView imageView=(ImageView)view.findViewById(R.id.img_pc);
        TextView textView=(TextView)view.findViewById(R.id.tv_pc);
        if(type==1){
            textView.setText("人工实测水位埋深大于设备采集水位埋深"+wuchaStr+"cm，需要将线夹子向下调整"+wuchaStr+"cm，线夹子调整完成，请重新读取实时数据。");
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.pop_check_down));
        }else{
            textView.setText("人工实测水位埋深小于设备采集水位埋深"+wuchaStr+"cm，需要将线夹子向上调整"+wuchaStr+"cm，线夹子调整完成，请重新读取实时数据。");
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.pop_check_up));
        }
        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                closeDialog();
            }
        });
    }


    /**
     * 误差过大的提示
     */
    private void showNotCheckPop(){
        View view=getLayoutInflater().inflate(R.layout.pop_not_check,null);
        dialogPop(view,true);
        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                closeDialog();
            }
        });
    }


    /**
     * 注册广播
     */
    private void register() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BleService.ACTION_NO_DISCOVERY_BLE);//扫描不到指定蓝牙设备
        myIntentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);//蓝牙断开连接
        myIntentFilter.addAction(BleService.ACTION_ENABLE_NOTIFICATION_SUCCES);//蓝牙初始化通道成功
        myIntentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);//接收到了回执的数据
        myIntentFilter.addAction(BleService.ACTION_INTERACTION_TIMEOUT);//发送命令超时
        myIntentFilter.addAction(BleService.ACTION_SEND_DATA_FAIL);//发送数据失败
        myIntentFilter.addAction(BleService.ACTION_GET_DATA_ERROR);//回执error数据
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                //扫描不到指定蓝牙设备
                case BleService.ACTION_NO_DISCOVERY_BLE:
                    clearTask();
                    dialogView = new DialogView(mContext, "扫描不到该蓝牙设备，请靠近设备再进行扫描！", "重新扫描","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS);
                        }
                    }, null);
                    dialogView.show();
                    break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                     clearTask();
                     dialogView = new DialogView(mContext, "蓝牙连接断开，请靠近设备进行连接!","重新连接", "取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            showProgress("蓝牙连接中...");
                            mHandler.postDelayed(new Runnable() {
                                public void run() {
                                    Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
                                    MainActivity.bleService.connect(ble.getBleMac());
                                }
                            },100);
                         }
                    }, null);
                     dialogView.show();
                     break;
                //初始化通道成功
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES:
                    if(SEND_STATUS==BleContant.NOT_SEND_DATA){
                        showToastView("蓝牙连接成功！");
                    }else{
                        sendData(SEND_STATUS);
                    }
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                     final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                     if(SEND_STATUS==BleContant.SEND_REAL_TIME_DATA){
                         //解析并显示回执的数据
                         showData(data);
                         clearTask();
                         SEND_STATUS=BleContant.NOT_SEND_DATA;
                     }else if(SEND_STATUS==BleContant.SEND_CHECK_ERROR){
                         SendBleStr.setCheck(wuCha,data);
                         sendData(BleContant.SET_DATA_CHECK);
                     }else{
                         isCheck=true;
                         sendData(BleContant.SEND_REAL_TIME_DATA);
                     }
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    clearTask();
                    dialogView = new DialogView(mContext, "接收数据超时！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS);
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    clearTask();
                    dialogView = new DialogView(mContext, "下发命令失败！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS);
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_GET_DATA_ERROR:
                     clearTask();
                     showToastView("设备回执数据异常！");
                     break;
                default:
                    break;
            }
        }
    };

    /**
     * 显示数据
     */
    private void showData(String msg){
        BuglyUtils.uploadBleMsg("数据校测界面读取的数据是："+msg);

        final int length=msg.length();
        //显示采集时间
        msg=msg.replace("GDCURRENT>","");
        StringBuffer stringBuffer=new StringBuffer("20");
        stringBuffer.append(msg.substring(0,2)+"-");
        stringBuffer.append(msg.substring(2,4)+"-");
        stringBuffer.append(msg.substring(4,6)+" ");
        stringBuffer.append(msg.substring(6,8)+":");
        stringBuffer.append(msg.substring(8,10)+":");
        stringBuffer.append(msg.substring(10,12));
        tvTime.setText(stringBuffer.toString());

        //显示压力值
        String YaLi = null;
        if(length==79){
            YaLi=msg.substring(43,52).replace("P","");
        }
        if(length==88 || length==138){
            YaLi=msg.substring(52,61).replace("P","");
        }
        if(length==91 || length==140){
            YaLi=msg.substring(55,64).replace("P","");
        }
        if(YaLi.contains("99999999")){
            tvYaLi.setText(YaLi+"");
        }else{
            tvYaLi.setText(Util.setDouble(Double.parseDouble(YaLi),3)+"");
        }


        //显示气压值
        String QiYa = null;
        if(length==79){
            QiYa=msg.substring(52,59).replace("B","");
        }
        if(length==88 || length==138){
            QiYa=msg.substring(61,68).replace("B","");
        }
        if(length==91 || length==140){
            QiYa=msg.substring(64,71).replace("B","");
        }
        if(YaLi.contains("99999999")){
            tvQiYa.setText(QiYa+"");
        }else{
            tvQiYa.setText(Util.setDouble(Double.parseDouble(QiYa),3)+"");
        }


        //显示探头埋深
        String TanTou=null;
        if(length==79){
            TanTou=msg.substring(59,68).replace("C","");
        }
        if(length==88 || length==138){
            TanTou=msg.substring(68,77).replace("C","");
        }
        if(length==91 || length==140){
            TanTou=msg.substring(71,80).replace("C","");
        }
        if(YaLi.contains("99999999")){
            tvTanTou.setText(TanTou+"m");
        }else{
            tvTanTou.setText(Util.setDouble(Double.parseDouble(TanTou),3)+"m");
        }


        //显示水位埋深
        String MaiShen=null;
        if(length==91 || length==140){
            MaiShen=msg.substring(12,21).replace("L","");
        }else{
            MaiShen=msg.substring(12,20).replace("L","");
        }
        if(YaLi.contains("99999999")){
            tvShuiWei.setText(MaiShen+"m");
        }else{
            tvShuiWei.setText(Util.setDouble(Double.parseDouble(MaiShen),3)+"m");
        }

        //判断是否是数据校验完成
        if(isCheck){
            //重新计算误差
            final String strCheck=etCheck.getText().toString().trim();
            final String strShui=tvShuiWei.getText().toString().trim().replace("m","");
            //显示误差
            wuCha=Util.sub(Double.parseDouble(strShui),Double.parseDouble(strCheck))+"";
            tvWuCha.setText(wuCha);
            if(wuCha.equals("0.0")){
                tvWuCha.setText("0");
            }

            dialogView = new DialogView(mContext, "数据校验完成！", "确认",null, new View.OnClickListener() {
                public void onClick(View v) {
                    dialogView.dismiss();
                }
            }, null);
            dialogView.show();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

}
