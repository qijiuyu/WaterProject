package com.water.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.presenter.GetDataPresenter;
import com.water.project.presenter.GetDataPresenterImpl;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.BuglyUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.StatusBarUtils;
import com.water.project.utils.SystemBarTintManager;
import com.water.project.utils.Util;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;

/**
 * 实时数据查询
 * Created by Administrator on 2018/9/2.
 */

public class GetDataActivity extends BaseActivity implements View.OnClickListener,GetDataPresenter {

    private TextView tvCJTime,tvMaiShen,tvYaLi,tvQiYa,tvShuiWen,tvQiWen,tvDianYa,tvDianDaoLv;
    private DialogView dialogView;
    private Handler mHandler=new Handler();
    private GetDataPresenterImpl getDataPresenter;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_getdata);
        initMvp();
        initView();
        register();//注册广播
        sendData(); //发送蓝牙命令
//        showData("GDCURRENT>080808203500LFFFF.FFFT017.00B100V05.85CSQ00R999993E0098P0010.432B999999C0011.000;");
//        showData("GDCURRENT>180812153625L0010.975T028.893B100V05.98CSQ31R-35.452E0098P0010.125B10.009C0011.000C001413.01B001413.00T001413.00R001413.00+0.0200;");
//         showData("GDCURRENT>180812153625L+0010.9758T+028.8967B100.00C001413.01V05.98CSQ31R-35.45E0098P0010.1256B10.0098A-0043.5000D01.00000C-0011.0000;");
    }

    /**
     * 初始化MVP
     */
    private void initMvp(){
        getDataPresenter=new GetDataPresenterImpl(this,this);
    }

    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("实时数据");
        tvCJTime=(TextView)findViewById(R.id.tv_ag_cjTime);
        tvMaiShen=(TextView)findViewById(R.id.tv_ag_maishen);
        tvYaLi=(TextView)findViewById(R.id.tv_ag_yali);
        tvQiYa=(TextView)findViewById(R.id.tv_ag_qiya);
        tvShuiWen=(TextView)findViewById(R.id.tv_ag_shuiwen);
        tvQiWen=(TextView)findViewById(R.id.tv_ag_qiwen);
        tvDianYa=(TextView)findViewById(R.id.tv_ag_dianya);
        tvDianDaoLv=(TextView)findViewById(R.id.tv_ag_diandaolv);
        //查询实时数据
        findViewById(R.id.tv_get).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
        findViewById(R.id.tv_shuiwen).setOnClickListener(this);
        findViewById(R.id.tv_maishen).setOnClickListener(this);
        findViewById(R.id.rel_ddl).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //获取实时数据
            case R.id.tv_get:
                 sendData();
                 break;
            //设置水温偏移量
            case R.id.tv_shuiwen:
                 getDataPresenter.setShuiWen();
                 break;
            //设置水位埋深
            case R.id.tv_maishen:
                 getDataPresenter.setSWMS();
                 break;
             //设置电导率
            case R.id.rel_ddl:
                 getDataPresenter.setDdl();
                 break;
            case R.id.lin_back:
                 finish();
                 break;
        }

    }


    /**
     * 发送蓝牙命令
     */
    private void sendData(){
        //判断蓝牙是否打开
        if(!BleUtils.isEnabled(GetDataActivity.this,MainActivity.mBtAdapter)){
            return;
        }
        DialogUtils.showProgress(GetDataActivity.this,"正在读取实时数据...");
        //如果蓝牙连接断开，就扫描重连
        if(MainActivity.bleService.connectionState==MainActivity.bleService.STATE_DISCONNECTED){
            //扫描并重连蓝牙
            final Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
            if(null!=ble){
                DialogUtils.showProgress(GetDataActivity.this,"扫描并连接蓝牙设备...");
                MainActivity.bleService.scanDevice(ble.getBleName());
            }
            return;
        }
        SendBleStr.sendBleData(BleContant.SEND_REAL_TIME_DATA);
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
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "扫描不到该蓝牙设备，请靠近设备再进行扫描！", "重新扫描","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData();
                        }
                    }, null);
                    dialogView.show();
                    break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                    DialogUtils.closeProgress();
                     dialogView = new DialogView(mContext, "蓝牙连接断开，请靠近设备进行连接!","重新连接", "取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            DialogUtils.showProgress(GetDataActivity.this,"蓝牙连接中...");
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
                     sendData();
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                     DialogUtils.closeProgress();
                     final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA).replace(">OK","");
                     //解析并显示回执的数据
                     showData(data);
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "接收数据超时！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData();
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "下发命令失败！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData();
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_GET_DATA_ERROR:
                    DialogUtils.closeProgress();
                    showToastView("设备回执数据异常");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 展示数据
     */
    private void showData(String msg){
//        BuglyUtils.uploadBleMsg("实时数据界面读取的数据是："+msg);

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
        tvCJTime.setText(stringBuffer.toString());

        //显示压力值
        String YaLi = null;
        if(length==79){
            YaLi=msg.substring(43,52).replace("P","");
        }
        if(length==88 || length==138){
            YaLi=msg.substring(52,61).replace("P","");
        }
        if(length==91){
            YaLi=msg.substring(55,64).replace("P","");
        }
        if(length==140){
            if(Util.isInteger(msg.substring(28,29))){
                YaLi=msg.substring(57,66).replace("P","");
            }else{
                YaLi=msg.substring(55,64).replace("P","");
            }
        }
        if(length==93){
            YaLi=msg.substring(57,66).replace("P","");
        }
        if(length==133){
            YaLi=msg.substring(74,83).replace("P","");
        }

        if(YaLi.contains("99999999")){
            tvYaLi.setText(YaLi+"");
        }else{
            tvYaLi.setText(Util.setDouble(Double.parseDouble(YaLi),3)+"");
        }

        //显示水位埋深
        String MaiShen=null;
        if(length==91 || length==93 || length==140){
            MaiShen=msg.substring(12,21).replace("L","");
        }
        if(length==79 || length==88 || length==138){
            MaiShen=msg.substring(12,20).replace("L","");
        }
        if(length==133){
            MaiShen=msg.substring(13,23).replace("L","");
        }
        if(YaLi.contains("99999999") || MaiShen.equals("FFFF.FFF")){
            tvMaiShen.setText(MaiShen+"m");
        }else{
            tvMaiShen.setText(Util.setDouble(Double.parseDouble(MaiShen),3)+"m");
        }

        //显示气压值
        String QiYa = null;
        if(length==79){
            QiYa=msg.substring(52,59).replace("B","");
        }
        if(length==88 || length==138){
            QiYa=msg.substring(61,68).replace("B","");
        }
        if(length==91){
            QiYa=msg.substring(64,71).replace("B","");
        }
        if(length==93){
            QiYa=msg.substring(66,73).replace("B","");
        }
        if(length==140){
            if(Util.isInteger(msg.substring(28,29))){
                QiYa=msg.substring(66,73).replace("B","");
            }else{
                QiYa=msg.substring(64,71).replace("B","");
            }
        }
        if(length==133){
            QiYa=msg.substring(33,40).replace("B","");
        }
        if(YaLi.contains("99999999")){
            tvQiYa.setText(QiYa+"");
        }else{
            tvQiYa.setText(Util.setDouble(Double.parseDouble(QiYa),3)+"");
        }

        //显示水温值
        String ShuiWen=null;
        if(length==79 || length==88 || length==138){
            ShuiWen=msg.substring(20,26).replace("T","");
        }
        if(length==91){
            ShuiWen=msg.substring(21,28).replace("T","");
        }
        if(length==93){
            ShuiWen=msg.substring(21,29).replace("T","");
        }
        if(length==140){
            if(Util.isInteger(msg.substring(28,29))){
                ShuiWen=msg.substring(21,29).replace("T","");
            }else{
                ShuiWen=msg.substring(21,28).replace("T","");
            }
        }
        if(length==133){
            ShuiWen=msg.substring(24,33).replace("T","");
        }
        if(YaLi.contains("99999999")){
            tvShuiWen.setText(ShuiWen+"℃");
        }else{
            tvShuiWen.setText(Util.setDouble(Double.parseDouble(ShuiWen),4)+"℃");
        }

        //显示气温值
        String QiWen=null;
        if(length==79){
            QiWen=msg.substring(37,43).replace("R","");
        }
        if(length==88 || length==138){
            QiWen=msg.substring(41,47).replace("R","");
        }
        if(length==91){
            QiWen=msg.substring(43,50).replace("R","");
        }
        if(length==93){
            QiWen=msg.substring(44,52).replace("R","");
        }
        if(length==140){
            if(Util.isInteger(msg.substring(28,29))){
                QiWen=msg.substring(44,52).replace("R","");
            }else{
                QiWen=msg.substring(43,50).replace("R","");
            }
        }
        if(length==133){
            QiWen=msg.substring(61,68).replace("R","");
        }
        if(YaLi.contains("99999999")){
            tvQiWen.setText(QiWen+"℃");
        }else{
            tvQiWen.setText(Util.setDouble(Double.parseDouble(QiWen),3)+"℃");
        }

        //显示电压值
        String DianYa=null;
        if(length==79){
            DianYa=msg.substring(26,32).replace("V","");
        }
        if(length==88 || length==138){
            DianYa=msg.substring(30,36).replace("V","");
        }
        if(length==91){
            DianYa=msg.substring(32,38).replace("V","");
        }
        if(length==93){
            DianYa=msg.substring(33,39).replace("V","");
        }
        if(length==140){
            if(Util.isInteger(msg.substring(28,29))){
                DianYa=msg.substring(33,39).replace("V","");
            }else{
                DianYa=msg.substring(32,38).replace("V","");
            }
        }
        if(length==133){
            DianYa=msg.substring(50,56).replace("V","");
        }
        if(YaLi.contains("99999999")){
            tvDianYa.setText(DianYa+"V");
        }else{
            tvDianYa.setText(Util.setDouble(Double.parseDouble(DianYa),1)+"V");
        }


        //显示电导率
        if(length==138 || length==140 || length==133){
            findViewById(R.id.rel_ddl).setVisibility(View.VISIBLE);
            String DianDaoLv=null;
            if(length==138){
                DianDaoLv=msg.substring(77,87).replace("C","");
            }
            if(length==140){
                if(Util.isInteger(msg.substring(28,29))){
                    DianDaoLv=msg.substring(82,92).replace("C","");
                }else{
                    DianDaoLv=msg.substring(80,90).replace("C","");
                }
            }
            if(length==133){
                DianDaoLv=msg.substring(40,50).replace("C","");
            }
            tvDianDaoLv.setText(Util.setDouble(Double.parseDouble(DianDaoLv),2)+"uS/cm");
        }


        updateData(1);
        updateData(2);
        updateData(3);
    }


    /**
     * 修改数据
     * @param type
     */
    public void updateData(int type) {
        String pyl;//偏移量
        double data; //加减后的值
        switch (type){
            //修改水温值
            case 1:
                 final String ShuiWen=tvShuiWen.getText().toString().trim().replace("℃","");
                 pyl=MyApplication.spUtil.getString(SPUtil.SHUI_WEN);
                 if(TextUtils.isEmpty(pyl)){
                     return;
                 }
                 data=Util.sum(Double.parseDouble(ShuiWen),Double.parseDouble(pyl));
                 tvShuiWen.setText(Util.setDouble(data,2)+"℃");
                 break;
            //修改水位埋深
            case 2:
                final String maiShen=tvMaiShen.getText().toString().trim().replace("m","");
                pyl=MyApplication.spUtil.getString(SPUtil.SHUI_WEI_MAI_SHEN);
                if(TextUtils.isEmpty(pyl)){
                    return;
                }
                data=Util.sum(Double.parseDouble(maiShen),Double.parseDouble(pyl));
                tvMaiShen.setText(Util.setDouble(data,3)+"m");
                break;
            //修改电导率
            case 3:
                final String ddl=tvDianDaoLv.getText().toString().trim().replace("uS/cm","");
                pyl=MyApplication.spUtil.getString(SPUtil.DIAN_DAO_LV);
                if(TextUtils.isEmpty(pyl)){
                    return;
                }
                data=Util.sum(Double.parseDouble(ddl),Double.parseDouble(pyl));
                tvDianDaoLv.setText(Util.setDouble(data,2)+"uS/cm");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

}
