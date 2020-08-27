package com.water.project.activity.new_version;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.water.project.activity.BaseActivity;
import com.water.project.adapter.NewSettingTimeAdapter;
import com.water.project.application.MyApplication;
import com.water.project.bean.BindService;
import com.water.project.bean.Ble;
import com.water.project.bean.SelectTime;
import com.water.project.bean.eventbus.EventStatus;
import com.water.project.bean.eventbus.EventType;
import com.water.project.presenter.new_device.New_SettingPresenter;
import com.water.project.service.BleService;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.BleObject;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.CustomListView;
import com.water.project.view.DialogView;
import com.water.project.view.MeasureListView;
import com.water.project.view.SelectRoadView;
import com.water.project.view.SelectTimeDialog;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 多路设置参数
 */
public class MoreSettingActivity extends BaseActivity implements SelectTime {

    @BindView(R.id.tv_head)
    TextView tvHead;
    @BindView(R.id.tv_road_num)
    TextView tvRoadNum;
    @BindView(R.id.list_code)
    MeasureListView listCode;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.img_clear2)
    ImageView imgClear2;
    @BindView(R.id.list_tantou)
    MeasureListView listTantou;
    @BindView(R.id.et_as_cstime)
    TextView etAsCstime;
    @BindView(R.id.tv_as_cetime)
    TextView tvAsCetime;
    @BindView(R.id.et_as_fstime)
    TextView etAsFstime;
    @BindView(R.id.et_as_fetime)
    TextView etAsFetime;
    @BindView(R.id.tv_as_grps)
    TextView tvAsGrps;
    @BindView(R.id.tv_send_num)
    TextView tvSendNum;
    @BindView(R.id.listView)
    CustomListView listView;
    @BindView(R.id.tv_new_time)
    TextView tvNewTime;
    //下发命令的编号
    private int SEND_STATUS;
    //设置SIM的数据
    private String CODE_SIM_DATA;
    /**
     * 1：刚进入读取命令
     * 2： 单独读取，设置命令
     */
    private int SEND_TYPE;
    //MVP对象
    private New_SettingPresenter new_settingPresenter;
    private NewSettingTimeAdapter newSettingTimeAdapter;
    //补发间隔时间集合
    private List<String> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_more_setting);
        ButterKnife.bind(this);
        //注册EventBus
        EventBus.getDefault().register(this);
        initView();
        register();//注册广播
        sendData(BleContant.RED_CAIJI_ROAD,1); //发送蓝牙命令
    }

    /**
     * 初始化
     */
    private void initView() {
        tvHead.setText("多路参数设置");
        //实例化MVP
        new_settingPresenter=new New_SettingPresenter(this);

        etPhone.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    imgClear2.setVisibility(View.VISIBLE);
                } else {
                    imgClear2.setVisibility(View.GONE);
                    etPhone.setTextColor(getResources().getColor(R.color.color_1fc37f));
                }
            }
        });
    }


    /**
     * 发送蓝牙命令
     * @param SEND_STATUS
     */
    private BleService bleService;
    private void sendData(final int SEND_STATUS, final int type){
        this.SEND_STATUS=SEND_STATUS;
        SEND_TYPE=type;
        bleService= BleObject.getInstance().getBleService(this, new BindService() {
            @Override
            public void onSuccess() {
                sendData(SEND_STATUS,type);
            }
        });
        if(bleService==null){
            return;
        }
        switch (SEND_STATUS){
            case BleContant.RED_CAIJI_ROAD:
            case BleContant.SEND_GET_CODE_PHONE:
            case BleContant.SEND_CAI_JI_PIN_LU:
            case BleContant.SEND_FA_SONG_PIN_LU:
            case BleContant.RED_DEVICE_TIME:
                DialogUtils.showProgress(this,"正在读取参数设置...");
                break;
            default:
                DialogUtils.showProgress(this,"正在设置参数信息...");
                break;
        }
        //如果蓝牙连接断开，就扫描重连
        if(bleService.connectionState==bleService.STATE_DISCONNECTED){
            //扫描并重连蓝牙
            final Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
            if(null!=ble){
                DialogUtils.showProgress(this,"扫描并连接蓝牙设备...");
                bleService.scanDevice(ble.getBleName());
            }
            return;
        }
        SendBleStr.sendBleData(this,SEND_STATUS);
    }


    @OnClick({R.id.lin_back, R.id.tv_road_num, R.id.tv_setting_road, R.id.tv_get_road, R.id.tv_setting_code, R.id.tv_get_code, R.id.img_clear2, R.id.tv_setting_mobile, R.id.tv_get_mobile, R.id.tv_setting_tantou, R.id.tv_get_tantou, R.id.et_as_cstime, R.id.tv_as_cetime, R.id.tv_setting_cj, R.id.tv_get_cj, R.id.et_as_fstime, R.id.et_as_fetime, R.id.tv_as_grps, R.id.tv_send_num, R.id.tv_setting_fs, R.id.tv_get_fs, R.id.tv_new_time, R.id.tv_setting_time, R.id.tv_get_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lin_back:
                finish();
                break;
            //选择采集路数
            case R.id.tv_road_num:
                 new SelectRoadView(this,tvRoadNum).show();
                break;
            //设置采集路数
            case R.id.tv_setting_road:
                break;
            //读取采集路数
            case R.id.tv_get_road:
                break;
            //设置统一编码
            case R.id.tv_setting_code:
                break;
            //读取统一编码
            case R.id.tv_get_code:
                break;
            case R.id.img_clear2:
                 etPhone.setText(null);
                 imgClear2.setVisibility(View.GONE);
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
            //读取SIM卡号
            case R.id.tv_get_mobile:
                sendData(BleContant.SEND_GET_CODE_PHONE,2);
                break;
            //设置探头埋深
            case R.id.tv_setting_tantou:
                break;
            //读取探头埋深
            case R.id.tv_get_tantou:
                break;
            //选择采集时间
            case R.id.et_as_cstime:
                SelectTimeDialog selectTimeDialog = new SelectTimeDialog(this, this, 3);
                selectTimeDialog.show();
                break;
            //选择采集间隔时间
            case R.id.tv_as_cetime:
                DialogUtils.getHourAndMinute(this, 1);
                break;
            //设置采集频率
            case R.id.tv_setting_cj:
                final String date=etAsCstime.getText().toString().trim();
                final String totalMinute=tvAsCetime.getText().toString().trim();
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
            //读取采集频率
            case R.id.tv_get_cj:
                sendData(BleContant.SEND_CAI_JI_PIN_LU,2);
                break;
            //选择发送起始时间
            case R.id.et_as_fstime:
                SelectTimeDialog selectTimeDialog2 = new SelectTimeDialog(this, this, 4);
                selectTimeDialog2.show();
                break;
            //选择发送间隔小时
            case R.id.et_as_fetime:
                DialogUtils.getHourAndMinute(this, 2);
                break;
            //选择GPRS模式
            case R.id.tv_as_grps:
                DialogUtils.selectNewSetting(this, 1);
                break;
            //选择补发次数
            case R.id.tv_send_num:
                DialogUtils.selectNewSetting(this, 2);
                break;
            //设置发送频率
            case R.id.tv_setting_fs:
                final String startTime=etAsFstime.getText().toString().trim();
                final String minute=etAsFetime.getText().toString().trim();
                final String grps=tvAsGrps.getText().toString().trim();
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
            //读取发送频率
            case R.id.tv_get_fs:
                sendData(BleContant.SEND_FA_SONG_PIN_LU,2);
                break;
            //选择时间---设置设备时间用
            case R.id.tv_new_time:
                SelectTimeDialog selectTimeDialog3 = new SelectTimeDialog(this, this, 5);
                selectTimeDialog3.show();
                break;
            //设置设备时间
            case R.id.tv_setting_time:
                SendBleStr.sendDeviceTime(tvNewTime.getText().toString().trim());
                sendData(BleContant.SEND_DEVICE_TIME,2);
                break;
            //读取设备时间
            case R.id.tv_get_time:
                sendData(BleContant.RED_DEVICE_TIME,2);
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
                    new_settingPresenter.resumeScan();
                    break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                    new_settingPresenter.bleConncation(bleService);
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
                    final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA).replace(">OK","");

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
                                sendData(BleContant.RED_DEVICE_TIME,1);
                                break;
                            case BleContant.RED_DEVICE_TIME:
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
                        if(SEND_STATUS==BleContant.RED_NEW_GET_CODE || SEND_STATUS==BleContant.SEND_GET_CODE_PHONE || SEND_STATUS==BleContant.SEND_GET_TANTOU || SEND_STATUS==BleContant.SEND_CAI_JI_PIN_LU || SEND_STATUS==BleContant.SEND_FA_SONG_PIN_LU || SEND_STATUS==BleContant.RED_DEVICE_TIME){
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
     * 解析并显示回执的数据
     * @param data
     */
    private void showData(String data){
        try {
            String[] strings =null;
            switch (SEND_STATUS){
                //显示采集路数
                case BleContant.RED_CAIJI_ROAD:
                     String[] msg=data.split(",");
                     tvRoadNum.setText(msg[1]);
                     break;
                //显示统一编码
                case BleContant.RED_NEW_GET_CODE:
                    break;
                //SIM卡号
                case BleContant.SEND_GET_CODE_PHONE:
                    CODE_SIM_DATA=data;
                    strings=data.split(";");
                    etPhone.setText(strings[6]);
                    break;
                //显示探头埋深
                case BleContant.SEND_GET_TANTOU:
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
                    etAsCstime.setText(sb.toString());
                    //要显示分钟
                    String ceMinute=SendBleStr.append(4,strings[1]);
                    tvAsCetime.setText(ceMinute);
                    break;
                //显示发送频率
                case BleContant.SEND_FA_SONG_PIN_LU:
                    strings=data.split(",");
                    if(strings==null || strings.length==0){
                        return;
                    }
                    final String time=strings[0].replace("GDSENDR","");
                    etAsFstime.setText(time.substring(0, 4)+"-"+time.substring(4,6)+"-"+time.substring(6,8)+" "+time.substring(8,10)+":"+time.substring(10,12)+":"+time.substring(12,14));
                    //显示发送间隔时间
                    etAsFetime.setText(strings[1]);
                    tvAsGrps.setText(strings[2]);
                    tvSendNum.setText(strings[3]);

                    if(strings.length<=4){
                        return;
                    }
                    //补发间隔时间集合
                    list=new ArrayList<>();
                    for(int i=4;i<strings.length;i++){
                        list.add(strings[i]);
                    }
                    newSettingTimeAdapter=new NewSettingTimeAdapter(this,Integer.parseInt(strings[3]),list);
                    listView.setAdapter(newSettingTimeAdapter);
                    break;
                //显示设备时间
                case BleContant.RED_DEVICE_TIME:
                    final String time2=data.replace("GDTIMER","");
                    StringBuffer sb2=new StringBuffer("20");
                    sb2.append(time2.substring(0, 2)+"-");
                    sb2.append(time2.substring(2,4)+"-");
                    sb2.append(time2.substring(4,6)+" ");
                    sb2.append(time2.substring(6,8)+":");
                    sb2.append(time2.substring(8,10)+":");
                    sb2.append(time2.substring(10,12));
                    tvNewTime.setText(sb2.toString());
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * EventBus注解
     */
    @Subscribe
    public void onEvent(EventType eventType){
        switch (eventType.getStatus()){
            //重新扫描蓝牙设备
            case EventStatus.RESUME_SCAN_BLE:
                break;
            //获取采集时间间隔数据
            case EventStatus.NEW_SETTING_CJSJJG:
                tvAsCetime.setText(String.valueOf(eventType.getObject()));
                break;
            //获取发送时间间隔数据
            case EventStatus.NEW_SETTING_FSSJJG:
                etAsFetime.setText(String.valueOf(eventType.getObject()));
                break;
            //获取gprs模式数据
            case EventStatus.SELECT_GRPS:
                tvAsGrps.setText(eventType.getObject().toString());
                break;
            //获取补发次数数据
            case EventStatus.SELECT_SEND_NUM:
                final String num=eventType.getObject().toString();
                tvSendNum.setText(num);
                newSettingTimeAdapter=new NewSettingTimeAdapter(this,Integer.parseInt(num),list);
                listView.setAdapter(newSettingTimeAdapter);
                break;
            default:
                break;
        }
    }


    @Override
    public void getTime(String time, int type) {
        if (type == 3) {
            etAsCstime.setText(time + ":00");
        }
        if (type == 4) {
            etAsFstime.setText(time + ":00");
        }
        if (type == 5) {
            tvNewTime.setText(time);
        }
    }


    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mBroadcastReceiver);
    }
}
