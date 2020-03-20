package com.water.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.water.project.bean.eventbus.EventStatus;
import com.water.project.bean.eventbus.EventType;
import com.water.project.presenter.check.CheckPresenterImpl;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.Util;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 数据校验
 * Created by Administrator on 2018/9/2.
 */

public class CheckActivity extends BaseActivity implements View.OnClickListener {
    private ImageView MS_clear, SW_clear,DDL_clear;
    private TextView tvTime,tvYaLi,tvQiYa,tvTanTou,tvShuiWei, tv_MS_wucha, tv_SW_wucha,tv_DDL_wucha,tvShuiWen,tvDianDaoLv;
    private EditText et_MS_check, et_SW_check,et_DDL_check;
    private DialogView dialogView;
    private Handler mHandler=new Handler();
    private CheckPresenterImpl checkPresenter;
    //下发命令的编号
    private int SEND_STATUS;
    //手动输入的误差数据
    private String MS_wucha,SW_wucha,DDL_wucha;
    //是否校验完成
    private boolean isCheck=false;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_check);
        //注册EventBus
        EventBus.getDefault().register(this);
        initMVP();
        initView();
        register();//注册广播
        sendData(BleContant.SEND_REAL_TIME_DATA);
//        showData("GDCURRENT>180812153625L0010.975T028.893B100V05.98CSQ31R-35.452E0098P0010.125B10.009C0011.000C001413.01B001413.00T001413.00R001413.00+0.0200;");
    }

    /**
     * 初始化MVP
     */
    private void initMVP(){
        checkPresenter=new CheckPresenterImpl(this);
    }

    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("校测水位数据");
        MS_clear =(ImageView)findViewById(R.id.img_clear_check);
        SW_clear =(ImageView)findViewById(R.id.img_clear_SW);
        DDL_clear=(ImageView)findViewById(R.id.img_clear_DDL);
        tvTime=(TextView)findViewById(R.id.tv_ac_cjTime);
        tvYaLi=(TextView)findViewById(R.id.tv_ac_yali);
        tvQiYa=(TextView)findViewById(R.id.tv_ac_qiya);
        tvTanTou=(TextView)findViewById(R.id.tv_ac_tantou);
        tvShuiWei=(TextView)findViewById(R.id.tv_ac_shuiwei);
        et_MS_check =(EditText)findViewById(R.id.et_ac_check);
        tv_MS_wucha =(TextView)findViewById(R.id.tv_ac_wucha);
        tvShuiWen=(TextView)findViewById(R.id.tv_ag_shuiwen);
        tvDianDaoLv=(TextView)findViewById(R.id.tv_ag_diandaolv);
        et_SW_check =(EditText)findViewById(R.id.et_sw_check);
        tv_SW_wucha =(TextView)findViewById(R.id.tv_wc_sw);
        et_DDL_check=(EditText)findViewById(R.id.et_ddl_check);
        tv_DDL_wucha=(TextView)findViewById(R.id.tv_wc_ddl);
        MS_clear.setOnClickListener(this);
        SW_clear.setOnClickListener(this);
        DDL_clear.setOnClickListener(this);
        findViewById(R.id.tv_btn).setOnClickListener(this);
        findViewById(R.id.tv_SW_btn).setOnClickListener(this);
        findViewById(R.id.tv_DDL_btn).setOnClickListener(this);
        findViewById(R.id.tv_btn_update).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);

        //水位埋深
        et_MS_check.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    MS_clear.setVisibility(View.VISIBLE);
                }else{
                    MS_clear.setVisibility(View.GONE);
                    tv_MS_wucha.setText(null);
                    return;
                }

                //计算误差
                final String strMS=tvShuiWei.getText().toString().trim().replace("m","");
                MS_wucha =Util.sub(Double.parseDouble(strMS),Double.parseDouble(s.toString()))+"";
                if(Double.parseDouble(MS_wucha)==0){
                    tv_MS_wucha.setText("0");
                }else{
                    tv_MS_wucha.setText(MS_wucha);
                }
            }
        });


        //水温
        et_SW_check.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    SW_clear.setVisibility(View.VISIBLE);
                }else{
                    SW_clear.setVisibility(View.GONE);
                    tv_SW_wucha.setText(null);
                    return;
                }

                //计算误差
                final String strSW=tvShuiWen.getText().toString().trim().replace("℃","");
                SW_wucha =Util.sub(Double.parseDouble(s.toString()),Double.parseDouble(strSW))+"";
                if(Double.parseDouble(SW_wucha)==0){
                    tv_SW_wucha.setText("0");
                }else{
                    tv_SW_wucha.setText(SW_wucha);
                }
            }
        });


        //电导率
        et_DDL_check.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    DDL_clear.setVisibility(View.VISIBLE);
                }else{
                    DDL_clear.setVisibility(View.GONE);
                    tv_DDL_wucha.setText(null);
                    return;
                }

                //计算误差
                final String strSW=tvDianDaoLv.getText().toString().trim().replace("uS/cm","");
                DDL_wucha =Util.sub(Double.parseDouble(s.toString()),Double.parseDouble(strSW))+"";
                if(Double.parseDouble(DDL_wucha)==0){
                    tv_DDL_wucha.setText("0");
                }else{
                    tv_DDL_wucha.setText(DDL_wucha);
                }
            }
        });
    }


    /**
     * 发送蓝牙命令
     */
    public void sendData(int status){
        //判断蓝牙是否打开
        if(!BleUtils.isEnabled(CheckActivity.this,MainActivity.mBtAdapter)){
            return;
        }
        SEND_STATUS=status;
        if(SEND_STATUS==BleContant.SEND_REAL_TIME_DATA){
            DialogUtils.showProgress(CheckActivity.this,"正在读取实时数据...");
        }else{
            DialogUtils.showProgress(CheckActivity.this,"正在进行数据校正...");
        }
        //如果蓝牙连接断开，就扫描重连
        if(MainActivity.bleService.connectionState==MainActivity.bleService.STATE_DISCONNECTED){
            //扫描并重连蓝牙
            final Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
            if(null!=ble){
                DialogUtils.showProgress(CheckActivity.this,"扫描并连接蓝牙设备...");
                MainActivity.bleService.scanDevice(ble.getBleName());
            }
            return;
        }
        SendBleStr.sendBleData(status);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //清空水位埋深
            case R.id.img_clear_check:
                 et_MS_check.setText(null);
                 break;
            //清空水温数据
            case R.id.img_clear_SW:
                 et_SW_check.setText(null);
                 break;
            //清空电导率数据
            case R.id.img_clear_DDL:
                 et_DDL_check.setText(null);
                 break;
            //水位埋深数据修改
            case R.id.tv_btn:
                 checkPresenter.ShuiWei_btn(tvYaLi, tv_MS_wucha, et_MS_check);
                 break;
            //水温数据修改
            case R.id.tv_SW_btn:
                 checkPresenter.SW_btn(tvYaLi,tv_SW_wucha,et_SW_check);
                 break;
             //电导率数据修改
            case R.id.tv_DDL_btn:
                 checkPresenter.DDL_btn(tvYaLi,tv_DDL_wucha,et_DDL_check);
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
                    checkPresenter.resumeScan(SEND_STATUS);
                    break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                     checkPresenter.bleDisConnect();
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
                     final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA).replace(">OK","");
                     switch (SEND_STATUS){
                         //查询数据回执
                         case BleContant.SEND_REAL_TIME_DATA:
                              DialogUtils.closeProgress();
                              SEND_STATUS=BleContant.NOT_SEND_DATA;
                              //解析并显示回执的数据
                              showData(data);
                              break;
                         //读取水位偏移量回执
                         case BleContant.SEND_CHECK_ERROR:
                              SendBleStr.setMS_check(MS_wucha,data);
                              sendData(BleContant.SET_DATA_CHECK);
                              break;
                         //读取水温偏移量回执
                         case BleContant.RED_SHUI_WEN_PYL:
//                             BuglyUtils.uploadBleMsg("水温偏移量回执："+data);
                              SendBleStr.setSW_check(SW_wucha,data);
                              sendData(BleContant.SEND_DATA_SHUI_WEN);
                              break;
                         //读取电导率偏移量回执
                         case BleContant.RED_DIAN_DAO_LV_PYL:
//                             BuglyUtils.uploadBleMsg("电导率偏移量回执："+data);
                              SendBleStr.setDDL_check(DDL_wucha,data);
                              sendData(BleContant.SEND_DATA_DIAN_DAO_LV);
                              break;
                         //校测成功回执
                         case BleContant.SEND_DATA_SHUI_WEN:
                         case BleContant.SET_DATA_CHECK:
                         case BleContant.SEND_DATA_DIAN_DAO_LV:
                              isCheck=true;
                              sendData(BleContant.SEND_REAL_TIME_DATA);
                              break;
                     }
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                     checkPresenter.timeOut(SEND_STATUS);
                     break;
                case BleService.ACTION_SEND_DATA_FAIL:
                     checkPresenter.sendCmdFail(SEND_STATUS);
                     break;
                case BleService.ACTION_GET_DATA_ERROR:
                     DialogUtils.closeProgress();
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
//        BuglyUtils.uploadBleMsg("数据校测界面读取的数据是："+msg);

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
        if(YaLi.contains("99999999")){
            tvShuiWen.setText(ShuiWen+"℃");
        }else{
            tvShuiWen.setText(Util.setDouble(Double.parseDouble(ShuiWen),3)+"℃");
        }


        //显示探头埋深
        String TanTou=null;
        if(length==79){
            TanTou=msg.substring(59,68).replace("C","");
        }
        if(length==88 || length==138){
            TanTou=msg.substring(68,77).replace("C","");
        }
        if(length==91){
            TanTou=msg.substring(71,80).replace("C","");
        }
        if(length==93){
            TanTou=msg.substring(73,82).replace("C","");
        }
        if(length==140){
            if(Util.isInteger(msg.substring(28,29))){
                TanTou=msg.substring(73,82).replace("C","");
            }else{
                TanTou=msg.substring(71,80).replace("C","");
            }
        }
        if(YaLi.contains("99999999")){
            tvTanTou.setText(TanTou+"m");
        }else{
            tvTanTou.setText(Util.setDouble(Double.parseDouble(TanTou),3)+"m");
        }


        //显示水位埋深
        String MaiShen=null;
        if(length==91 || length==93 || length==140){
            MaiShen=msg.substring(12,21).replace("L","");
        }else{
            MaiShen=msg.substring(12,20).replace("L","");
        }
        if(YaLi.contains("99999999") || MaiShen.equals("FFFF.FFF")){
            tvShuiWei.setText(MaiShen+"m");
        }else{
            tvShuiWei.setText(Util.setDouble(Double.parseDouble(MaiShen),3)+"m");
        }


        //显示电导率
        if(length==138 || length==140){
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
            tvDianDaoLv.setText(Util.setDouble(Double.parseDouble(DianDaoLv),2)+"uS/cm");
        }

        //判断是否是数据校验完成
        if(isCheck){
            //重新计算水位埋深误差
            final String etMS= et_MS_check.getText().toString().trim();
            if(!TextUtils.isEmpty(etMS)){
                final String strMS=tvShuiWei.getText().toString().trim().replace("m","");
                //显示误差
                MS_wucha =Util.sub(Double.parseDouble(strMS),Double.parseDouble(etMS))+"";
                if(Double.parseDouble(MS_wucha)==0){
                    tv_MS_wucha.setText("0");
                }else{
                    tv_MS_wucha.setText(MS_wucha);
                }
            }


            //重新计算水温误差
            final String etSW= et_SW_check.getText().toString().trim();
            if(!TextUtils.isEmpty(etSW)){
                final String strSW=tvShuiWen.getText().toString().trim().replace("℃","");
                //显示误差
                SW_wucha =Util.sub(Double.parseDouble(strSW),Double.parseDouble(etSW))+"";
                if(Double.parseDouble(SW_wucha)==0){
                    tv_SW_wucha.setText("0");
                }else{
                    tv_SW_wucha.setText(SW_wucha);
                }
            }



            //重新计算电导率误差
            final String etDDL= et_DDL_check.getText().toString().trim();
            if(!TextUtils.isEmpty(etDDL)){
                final String strDDL=tvDianDaoLv.getText().toString().trim().replace("uS/cm","");
                //显示误差
                DDL_wucha =Util.sub(Double.parseDouble(strDDL),Double.parseDouble(etDDL))+"";
                if(Double.parseDouble(DDL_wucha)==0){
                    tv_DDL_wucha.setText("0");
                }else{
                    tv_DDL_wucha.setText(DDL_wucha);
                }
            }


            dialogView = new DialogView(mContext, "数据校验完成！", "确认",null, new View.OnClickListener() {
                public void onClick(View v) {
                    dialogView.dismiss();
                }
            }, null);
            dialogView.show();

        }
    }


    /**
     * EventBus注解
     */
    @Subscribe
    public void onEvent(EventType eventType) {
        switch (eventType.getStatus()) {
            //发送命令
            case EventStatus.SEND_CHECK_MCD:
                  final int cmd= (int) eventType.getObject();
                  sendData(cmd);
                  break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mBroadcastReceiver);
    }

}
