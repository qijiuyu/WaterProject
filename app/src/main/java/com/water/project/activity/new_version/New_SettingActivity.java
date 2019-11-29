package com.water.project.activity.new_version;

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
import android.widget.ListView;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.activity.BaseActivity;
import com.water.project.activity.MainActivity;
import com.water.project.adapter.NewSettingTimeAdapter;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.bean.SelectTime;
import com.water.project.bean.eventbus.EventStatus;
import com.water.project.bean.eventbus.EventType;
import com.water.project.presenter.new_device.New_SettingPresenter;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.BuglyUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.Util;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.utils.DialogUtils;
import com.water.project.view.CustomListView;
import com.water.project.view.DialogView;
import com.water.project.view.SelectTimeDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 参数设置
 * Created by Administrator on 2018/9/1.
 */

public class New_SettingActivity extends BaseActivity implements View.OnClickListener,SelectTime{
    private EditText etCode,etPhone,etTanTou;
    private TextView etCStime,etFStime,etCEtime,etFEtime,tvGprs,tvSendNum,tvNewTime;
    private ImageView imgClear1,imgClear2,imgClear3;
    private DialogView dialogView;
    private CustomListView listView;
    //下发命令的编号
    private int SEND_STATUS;
    /**
     * 1：刚进入读取命令
     * 2： 单独读取，设置命令
     */
    private int SEND_TYPE;
    //设置统一编码和SIM的数据
    private String CODE_SIM_DATA;
    //MVP对象
    private New_SettingPresenter new_settingPresenter;
    private NewSettingTimeAdapter newSettingTimeAdapter;
    //补发间隔时间集合
    private List<String> list;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_setting);
        //注册EventBus
        EventBus.getDefault().register(this);
        //实例化MVP
        new_settingPresenter=new New_SettingPresenter(this);
        initView();
        register();//注册广播
        sendData(BleContant.RED_NEW_GET_CODE,1); //发送蓝牙命令
    }


    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("参数设置");
        etCode=(EditText)findViewById(R.id.et_as_code);
        etPhone=(EditText)findViewById(R.id.et_as_phone);
        etTanTou=(EditText)findViewById(R.id.et_as_tantou);
        etCStime=(TextView) findViewById(R.id.et_as_cstime);
        etCEtime=(TextView)findViewById(R.id.tv_as_cetime);
        etFStime=(TextView)findViewById(R.id.et_as_fstime);
        etFEtime=(TextView)findViewById(R.id.et_as_fetime);
        imgClear1=(ImageView)findViewById(R.id.img_clear1);
        imgClear2=(ImageView)findViewById(R.id.img_clear2);
        imgClear3=(ImageView)findViewById(R.id.img_clear3);
        tvGprs=(TextView)findViewById(R.id.tv_as_grps);
        tvSendNum=(TextView)findViewById(R.id.tv_send_num);
        tvNewTime=findViewById(R.id.tv_new_time);
        listView=(CustomListView) findViewById(R.id.listView);
        etCStime.setOnClickListener(this);
        etFStime.setOnClickListener(this);
        etCEtime.setOnClickListener(this);
        etFEtime.setOnClickListener(this);
        imgClear1.setOnClickListener(this);
        imgClear2.setOnClickListener(this);
        imgClear3.setOnClickListener(this);
        tvNewTime.setOnClickListener(this);
        findViewById(R.id.tv_setting_code).setOnClickListener(this);
        findViewById(R.id.tv_setting_mobile).setOnClickListener(this);
        findViewById(R.id.tv_setting_two).setOnClickListener(this);
        findViewById(R.id.tv_setting_three).setOnClickListener(this);
        findViewById(R.id.tv_setting_four).setOnClickListener(this);
        findViewById(R.id.tv_get_code).setOnClickListener(this);
        findViewById(R.id.tv_get_mobile).setOnClickListener(this);
        findViewById(R.id.tv_get_two).setOnClickListener(this);
        findViewById(R.id.tv_get_three).setOnClickListener(this);
        findViewById(R.id.tv_get_four).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
        findViewById(R.id.tv_setting_five).setOnClickListener(this);
        tvGprs.setOnClickListener(this);
        tvSendNum.setOnClickListener(this);
        etCode.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    imgClear1.setVisibility(View.VISIBLE);
                }else{
                    imgClear1.setVisibility(View.GONE);
                    etCode.setTextColor(getResources().getColor(R.color.color_1fc37f));
                }
            }
        });
        etPhone.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    imgClear2.setVisibility(View.VISIBLE);
                }else{
                    imgClear2.setVisibility(View.GONE);
                    etPhone.setTextColor(getResources().getColor(R.color.color_1fc37f));
                }
            }
        });
        etTanTou.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    imgClear3.setVisibility(View.VISIBLE);
                }else{
                    imgClear3.setVisibility(View.GONE);
                    etTanTou.setTextColor(getResources().getColor(R.color.color_1fc37f));
                }
            }
        });

        //显示当前的时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tvNewTime.setText(dateFormat.format(new Date()));
    }


    /**
     * 发送蓝牙命令
     * @param status
     */
    private void sendData(int status,int type){
        //判断蓝牙是否打开
        if(!BleUtils.isEnabled(New_SettingActivity.this,MainActivity.mBtAdapter)){
            return;
        }
        SEND_STATUS=status;
        SEND_TYPE=type;
        switch (SEND_STATUS){
            case BleContant.RED_NEW_GET_CODE:
            case BleContant.SEND_GET_CODE_PHONE:
            case BleContant.SEND_GET_TANTOU:
            case BleContant.SEND_CAI_JI_PIN_LU:
            case BleContant.SEND_FA_SONG_PIN_LU:
                 DialogUtils.showProgress(New_SettingActivity.this,"正在读取参数设置...");
                 break;
             default:
                 DialogUtils.showProgress(New_SettingActivity.this,"正在设置参数信息...");
                 break;
        }
        //如果蓝牙连接断开，就扫描重连
        if(MainActivity.bleService.connectionState==MainActivity.bleService.STATE_DISCONNECTED){
            //扫描并重连蓝牙
            final Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
            if(null!=ble){
                DialogUtils.showProgress(New_SettingActivity.this,"扫描并连接蓝牙设备...");
                MainActivity.bleService.scanDevice(ble.getBleName());
            }
            return;
        }
        SendBleStr.sendBleData(status);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //设置统一编码
            case R.id.tv_setting_code:
                  final String code=etCode.getText().toString().trim();
                  if(TextUtils.isEmpty(code)){
                      showToastView("请输入统一编码！");
                  }else if(code.length()<10 || code.length()>15){
                      etCode.setTextColor(getResources().getColor(R.color.color_EC191B));
                      showToastView("统一编码的长度只能是10到15位！");
                  }else{
                      etCode.setTextColor(getResources().getColor(R.color.color_1fc37f));
                      SendBleStr.set_new_code(code);
                      sendData(BleContant.SET_CODE_PHONE,2);
                  }
                 break;
            //设置SIM卡号
            case R.id.tv_setting_mobile:
                final String sim=etPhone.getText().toString().trim();
                if(TextUtils.isEmpty(sim)){
                    showToastView("请输入SIM卡号！");
                }else if(sim.length()<11 || sim.length()==12){
                    etPhone.setTextColor(getResources().getColor(R.color.color_EC191B));
                    showToastView("SIM卡号位数错误！");
                }else{
                    etPhone.setTextColor(getResources().getColor(R.color.color_1fc37f));
                    SendBleStr.set_new_Sim(sim,CODE_SIM_DATA);
                    sendData(BleContant.SET_CODE_PHONE,2);
                }
                  break;
            //设置探头埋深
            case R.id.tv_setting_two:
                  final String tantou=etTanTou.getText().toString().trim();
                  final int qIndex=tantou.indexOf(".");
                  final int hIndex=tantou.length()-qIndex-1;
                  etTanTou.setTextColor(getResources().getColor(R.color.color_EC191B));
                  if(TextUtils.isEmpty(tantou)){
                      showToastView("请输入探头埋深！");
                  }else if(tantou.indexOf(".")==-1 && tantou.length()>4){
                      showToastView("探头埋深最多只能输入4位整数！");
                  }else if(qIndex>4){
                      showToastView("探头埋深的小数点前面最多只能是4位数");
                  }else if(hIndex>3){
                      showToastView("探头埋深的小数点后面最多只能是3位数");
                  }else{
                      etTanTou.setTextColor(getResources().getColor(R.color.color_1fc37f));
                      SendBleStr.sendSetTanTou(tantou);
                      sendData(BleContant.SET_TANTOU,2);
                  }
                break;
            //选择采集时间
            case R.id.et_as_cstime:
                 SelectTimeDialog selectTimeDialog=new SelectTimeDialog(New_SettingActivity.this,this,3);
                 selectTimeDialog.show();
                 break;
            //选择采集间隔时间
            case R.id.tv_as_cetime:
                 DialogUtils.getHourAndMinute(New_SettingActivity.this,1);
                 break;
            //设置采集频率
            case R.id.tv_setting_three:
                 final String date=etCStime.getText().toString().trim();
                 final String totalMinute=etCEtime.getText().toString().trim();
                 if(TextUtils.isEmpty(date)){
                     showToastView("请选择采集起始时间！");
                 }else if(TextUtils.isEmpty(totalMinute)){
                     showToastView("请选择采集间隔时间！");
                 }else{
                     dialogView = new DialogView(mContext, "采集起始时间和采集间隔时间一旦修改，RTU内部存储数据将全部清空!","确定", "取消", new View.OnClickListener() {
                         public void onClick(View v) {
                             dialogView.dismiss();
                             SendBleStr.new_sendCaiJi(date.substring(0, date.length()-2).replace("-","").replace(" ","").replace(":",""),totalMinute);
                             sendData(BleContant.SET_CAI_JI_PIN_LU,2);
                         }
                     }, null);
                     dialogView.show();
                 }
                break;
            //选择发送起始时间
            case R.id.et_as_fstime:
                 SelectTimeDialog selectTimeDialog2=new SelectTimeDialog(New_SettingActivity.this,this,4);
                 selectTimeDialog2.show();
                  break;
            //选择发送间隔小时
            case R.id.et_as_fetime:
                 DialogUtils.getHourAndMinute(New_SettingActivity.this,2);
                 break;
            //选择GPRS模式
            case R.id.tv_as_grps:
                DialogUtils.selectNewSetting(New_SettingActivity.this,1);
                 break;
            //选择补发次数
            case R.id.tv_send_num:
                DialogUtils.selectNewSetting(New_SettingActivity.this,2);
                  break;
            //设置发送频率
            case R.id.tv_setting_four:
                 final String startTime=etFStime.getText().toString().trim();
                 final String minute=etFEtime.getText().toString().trim();
                 final String grps=tvGprs.getText().toString().trim();
                 final String number=tvSendNum.getText().toString().trim();
                 if(TextUtils.isEmpty(startTime)){
                     showToastView("请选择发送起始时间！");
                     return;
                 }
                 if(TextUtils.isEmpty(minute)){
                     showToastView("请选择发送间隔时间！");
                     return;
                 }
                if(TextUtils.isEmpty(grps)){
                    showToastView("请选择连接服务器次数！");
                    return;
                }
                if(TextUtils.isEmpty(number)){
                    showToastView("请选择补发数据次数！");
                    return;
                }
                 if(Integer.parseInt(number)>0){
                     if(Integer.parseInt(number)>newSettingTimeAdapter.map.size()){
                         showToastView("请将"+Integer.parseInt(number)+"个补发间隔时间数据完善");
                         return;
                     }
                     boolean b=true;
                    for (int i=0;i<newSettingTimeAdapter.map.size();i++){
                            if(i==0 && newSettingTimeAdapter.map.get(i)>=Integer.parseInt(minute)){
                                b=false;
                                new_settingPresenter.trip("补发间隔时间1的分钟必须小于发送间隔时间的分钟");
                                break;
                            }
                            if(i>0 && newSettingTimeAdapter.map.get(i)<=newSettingTimeAdapter.map.get(i-1)){
                                b=false;
                                new_settingPresenter.trip("补发间隔时间"+(i+1)+"的分钟必须大于补发间隔时间"+(i)+"的分钟");
                                break;
                            }
                            //最后一个数据不能大于发送间隔时间
                            if(i==newSettingTimeAdapter.map.size()-1){
                                if(newSettingTimeAdapter.map.get(i)>=Integer.parseInt(minute)){
                                    b=false;
                                    new_settingPresenter.trip("最后一个补发间隔时间的分钟必须小于发送间隔时间的分钟");
                                    break;
                                }
                            }

                    }
                    if(!b){
                        return;
                    }
                 }
                 //设置并发送命令
                SendBleStr.new_setFaSong(startTime,minute,grps,number,newSettingTimeAdapter==null ? null : newSettingTimeAdapter.map);
                sendData(BleContant.SET_FA_SONG,2);
                break;
            //选择时间---设置设备时间用
            case R.id.tv_new_time:
                  SelectTimeDialog selectTimeDialog3=new SelectTimeDialog(New_SettingActivity.this,this,5);
                  selectTimeDialog3.show();
                  break;
            //设置设备时间
            case R.id.tv_setting_five:
                  SendBleStr.sendDeviceTime(tvNewTime.getText().toString().trim());
                  sendData(BleContant.SEND_DEVICE_TIME,2);
                  break;
            //读取统一编码
            case R.id.tv_get_code:
                 sendData(BleContant.RED_NEW_GET_CODE,2);
                  break;
            //读取SIM
            case R.id.tv_get_mobile:
                 sendData(BleContant.SEND_GET_CODE_PHONE,2);
                 break;
            //读取探头埋深
            case R.id.tv_get_two:
                sendData(BleContant.SEND_GET_TANTOU,2);
                break;
            //读取采集频率
            case R.id.tv_get_three:
                sendData(BleContant.SEND_CAI_JI_PIN_LU,2);
                break;
            //读取发送频率
            case R.id.tv_get_four:
                sendData(BleContant.SEND_FA_SONG_PIN_LU,2);
                break;
            case R.id.img_clear1:
                 etCode.setText(null);
                 imgClear1.setVisibility(View.GONE);
                 break;
            case R.id.img_clear2:
                etPhone.setText(null);
                imgClear2.setVisibility(View.GONE);
                break;
            case R.id.img_clear3:
                etTanTou.setText(null);
                imgClear3.setVisibility(View.GONE);
                break;
            case R.id.lin_back:
                 finish();
                 break;
            default:
                break;
        }
    }


    /**
     * 解析并显示回执的数据
     * @param data
     */
    private void showData(String data){
        try {
            String[] strings =null;
            switch (SEND_STATUS){
                //显示统一编码
                case BleContant.RED_NEW_GET_CODE:
                      String code=data.replace("GDIDR","");
                      etCode.setText(code);
                      break;
                //SIM卡号
                case BleContant.SEND_GET_CODE_PHONE:
                    CODE_SIM_DATA=data;
                    strings=data.split(";");
                    etPhone.setText(strings[6]);
                    break;
                //显示探头埋深
                case BleContant.SEND_GET_TANTOU:
                    final String strTanTou=data.replace("GDLINER","");
                    etTanTou.setText(Util.setDouble(Double.parseDouble(strTanTou),3));
                    break;
                //显示采集频率
                case BleContant.SEND_CAI_JI_PIN_LU:
                    strings=data.split(",");
                    final String s=strings[0].replace("GDREADR","");
                    StringBuffer sb=new StringBuffer();
                    sb.append(s.substring(0, 4)+"-");
                    sb.append(s.substring(4, 6)+"-");
                    sb.append(s.substring(6, 8)+" ");
                    sb.append(s.substring(8, 10)+":00");
                    etCStime.setText(sb.toString());
                    //要显示分钟
                    String ceMinute=SendBleStr.append(4,strings[1]);
                    etCEtime.setText(ceMinute);
                    break;
                //显示发送频率
                case BleContant.SEND_FA_SONG_PIN_LU:
                    strings=data.split(",");
                    if(strings==null || strings.length==0){
                        return;
                    }
                    final String time=strings[0].replace("GDSENDR","");
                    etFStime.setText(time.substring(0, 4)+"-"+time.substring(4,6)+"-"+time.substring(6,8)+" "+time.substring(8,10)+":"+time.substring(10,12)+":"+time.substring(12,14));
                    //显示发送间隔时间
                    etFEtime.setText(strings[1]);
                    tvGprs.setText(strings[2]);
                    tvSendNum.setText(strings[3]);

                    if(strings.length<=4){
                        return;
                    }
                    //补发间隔时间集合
                    list=new ArrayList<>();
                    for(int i=4;i<strings.length;i++){
                        list.add(strings[i]);
                    }
                    newSettingTimeAdapter=new NewSettingTimeAdapter(New_SettingActivity.this,Integer.parseInt(strings[3]),list);
                    listView.setAdapter(newSettingTimeAdapter);
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
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
                     new_settingPresenter.resumeScan();
                     break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                     new_settingPresenter.bleConncation();
                     break;
                //初始化通道成功
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES:
                     if(SEND_STATUS==BleContant.NOT_SEND_DATA){
                         showToastView("蓝牙连接成功！");
                     }else{
                         sendData(SEND_STATUS,SEND_TYPE);
                     }
                    break;
                //接收到了读取回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);

                    //刚进入界面读取的操作
                    if(SEND_TYPE==1){
                        //解析并显示回执的数据
                        showData(data);
                        //继续发送命令
                        switch (SEND_STATUS){
                            case BleContant.RED_NEW_GET_CODE:
                                  sendData(BleContant.SEND_GET_CODE_PHONE,1);
                                  break;
                            case BleContant.SEND_GET_CODE_PHONE:
                                sendData(BleContant.SEND_GET_TANTOU,1);
                                break;
                            case BleContant.SEND_GET_TANTOU:
                                sendData(BleContant.SEND_CAI_JI_PIN_LU,1);
                                break;
                            case BleContant.SEND_CAI_JI_PIN_LU:
                                sendData(BleContant.SEND_FA_SONG_PIN_LU,1);
                                break;
                            case BleContant.SEND_FA_SONG_PIN_LU:
                                DialogUtils.closeProgress();
                                SEND_STATUS=BleContant.NOT_SEND_DATA;
                                break;
                            default:
                                DialogUtils.closeProgress();
                                break;
                        }
                    }

                    //单独读取与设置等操作
                    if(SEND_TYPE==2){
                        DialogUtils.closeProgress();
                        if(SEND_STATUS==BleContant.RED_NEW_GET_CODE || SEND_STATUS==BleContant.SEND_GET_CODE_PHONE || SEND_STATUS==BleContant.SEND_GET_TANTOU || SEND_STATUS==BleContant.SEND_CAI_JI_PIN_LU || SEND_STATUS==BleContant.SEND_FA_SONG_PIN_LU){
                            //解析并显示回执的数据
                            showData(data);
                        }else{
                            dialogView = new DialogView(mContext, "参数设置成功！", "确定",null, new View.OnClickListener() {
                                public void onClick(View v) {
                                    dialogView.dismiss();
                                }
                            }, null);
                            dialogView.show();
                        }
                        SEND_STATUS=BleContant.NOT_SEND_DATA;
                    }
                    break;
                //接收数据超时
                case BleService.ACTION_INTERACTION_TIMEOUT:
                      new_settingPresenter.timeOut();
                    break;
                //下发命令失败
                case BleService.ACTION_SEND_DATA_FAIL:
                     new_settingPresenter.sendCmdFail();
                     break;
                case BleService.ACTION_GET_DATA_ERROR:
                     DialogUtils.closeProgress();
                     showToastView("设备回执数据异常！");
                default:
                    break;
            }
        }
    };


    /**
     * EventBus注解
     */
    @Subscribe
    public void onEvent(EventType eventType){
        switch (eventType.getStatus()){
            //重新扫描蓝牙设备
            case EventStatus.RESUME_SCAN_BLE:
                  sendData(SEND_STATUS,SEND_TYPE);
                  break;
            //获取采集时间间隔数据
            case EventStatus.NEW_SETTING_CJSJJG:
                  etCEtime.setText(String.valueOf(eventType.getObject()));
                  break;
            //获取发送时间间隔数据
            case EventStatus.NEW_SETTING_FSSJJG:
                 etFEtime.setText(String.valueOf(eventType.getObject()));
                 break;
            //获取gprs模式数据
            case EventStatus.SELECT_GRPS:
                  tvGprs.setText(eventType.getObject().toString());
                  break;
            //获取补发次数数据
            case EventStatus.SELECT_SEND_NUM:
                  final String num=eventType.getObject().toString();
                  tvSendNum.setText(num);
                  newSettingTimeAdapter=new NewSettingTimeAdapter(New_SettingActivity.this,Integer.parseInt(num),list);
                  listView.setAdapter(newSettingTimeAdapter);
                  break;
            default:
                break;
        }
    }

    public void getTime(String time,int type) {
        if(type==3){
            etCStime.setText(time+":00");
        }
        if(type==4){
            etFStime.setText(time+":00");
        }
        if(type==5){
            tvNewTime.setText(time);
        }
    }
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mBroadcastReceiver);
    }

}
