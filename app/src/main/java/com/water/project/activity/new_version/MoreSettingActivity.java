package com.water.project.activity.new_version;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.water.project.R;
import com.water.project.activity.BaseActivity;
import com.water.project.adapter.MoreSettingCodeAdapter;
import com.water.project.adapter.MoreSettingTanTouAdapter;
import com.water.project.adapter.NewSettingTimeAdapter;
import com.water.project.application.MyApplication;
import com.water.project.bean.BindService;
import com.water.project.bean.Ble;
import com.water.project.bean.MoreCode;
import com.water.project.bean.MoreTanTou;
import com.water.project.bean.SelectObject;
import com.water.project.bean.SelectTime;
import com.water.project.bean.eventbus.EventStatus;
import com.water.project.bean.eventbus.EventType;
import com.water.project.presenter.new_device.New_SettingPresenter;
import com.water.project.service.BleService;
import com.water.project.utils.BuglyUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.BleObject;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.CustomListView;
import com.water.project.view.DialogView;
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
    @BindView(R.id.tv_code_title)
    TextView tvCodeTitle;
    @BindView(R.id.list_code)
    RecyclerView listCode;
    @BindView(R.id.list_tantou)
    RecyclerView listTantou;
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

    private int m;//0(默认)表示采集探头, 1表示采集北斗设备数据
    private int nn; //总回路数
    private int redCodeNum=1;//读取统一编码的次数
    private int redTanTouNum=1;//读取探头埋深的次数
    private int setCodeNum=0; //设置统一编码的次数
    private int setTanTouNum=0;//设置探头埋深的次数

    private List<MoreCode> codeList=new ArrayList<>();//存储读取的统一编码数据
    private List<MoreTanTou> tantouList=new ArrayList<>();//存储读取的探头埋深数据

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
            case BleContant.RED_MORE_SETTING_CODE:
            case BleContant.SEND_GET_CODE_PHONE:
            case BleContant.RED_MORE_SETTING_TANTOU:
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


    @OnClick({R.id.lin_back, R.id.tv_road_num, R.id.tv_setting_road, R.id.tv_get_road, R.id.tv_setting_code, R.id.tv_get_code, R.id.tv_setting_tantou, R.id.tv_get_tantou, R.id.et_as_cstime, R.id.tv_as_cetime, R.id.tv_setting_cj, R.id.tv_get_cj, R.id.et_as_fstime, R.id.et_as_fetime, R.id.tv_as_grps, R.id.tv_send_num, R.id.tv_setting_fs, R.id.tv_get_fs, R.id.tv_new_time, R.id.tv_setting_time, R.id.tv_get_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lin_back:
                finish();
                break;
            //选择采集路数
            case R.id.tv_road_num:
                 new SelectRoadView(this, new SelectObject() {
                     @Override
                     public void onSuccess(Object object) {
                         tvRoadNum.setText((String)object);

                         //根据路数变更‘统一编码’和‘探头’的列数
                         listCode.setLayoutManager(new LinearLayoutManager(activity));
                         listCode.setAdapter(new MoreSettingCodeAdapter(activity,codeList,Integer.parseInt(object.toString()),m));

                         listTantou.setLayoutManager(new LinearLayoutManager(activity));
                         listTantou.setAdapter(new MoreSettingTanTouAdapter(activity,tantouList,Integer.parseInt(object.toString())));
                     }
                 }).show();
                break;
            //设置采集路数
            case R.id.tv_setting_road:
                SendBleStr.setSetCaiJiRoad(m,tvRoadNum.getText().toString().trim());
                sendData(BleContant.SET_CAIJI_ROAD,2);
                break;
            //读取采集路数
            case R.id.tv_get_road:
                sendData(BleContant.RED_CAIJI_ROAD,2);
                break;
            //设置统一编码
            case R.id.tv_setting_code:
                 boolean isTrue=true;
                 for (int i=0;i<codeList.size();i++){
                      if(TextUtils.isEmpty(codeList.get(i).getCode()) || TextUtils.isEmpty(codeList.get(i).getOther())){
                          isTrue=false;
                          ToastUtil.showLong("请完善统一编码第"+(i+1)+"路数据");
                          break;
                      }
                 }
                 if(!isTrue){
                     return;
                 }
                 setCodeNum=0;
                 SendBleStr.setSetMoreSettingCode(setCodeNum,codeList.get(setCodeNum));
                 sendData(BleContant.SET_MORE_SETTING_CODE,2);
                break;
            //读取统一编码
            case R.id.tv_get_code:
                redCodeNum=1;
                codeList.clear();
                SendBleStr.setRedMoreSettingCode(redCodeNum);
                sendData(BleContant.RED_MORE_SETTING_CODE,2);
                break;
            //设置探头埋深
            case R.id.tv_setting_tantou:
                boolean isTrue1=true;
                for (int i=0;i<tantouList.size();i++){
                     if(TextUtils.isEmpty(tantouList.get(i).getMaishen())){
                         isTrue1=false;
                         ToastUtil.showLong("请完善探头埋深第"+(i+1)+"路数据");
                         break;
                     }
                    if(!tantouList.get(i).getMaishen().startsWith("+") && !tantouList.get(i).getMaishen().startsWith("-")){
                        isTrue1=false;
                        ToastUtil.showLong("探头埋深开头输入+或-符号");
                        break;
                    }
                }
                if(!isTrue1){
                    return;
                }
                setTanTouNum=0;
                SendBleStr.setSetMoreSettingTanTou(setTanTouNum,tantouList.get(setTanTouNum));
                sendData(BleContant.SET_MORE_SETTING_TANTOU,2);
                break;
            //读取探头埋深
            case R.id.tv_get_tantou:
                redTanTouNum=1;
                tantouList.clear();
                SendBleStr.setRedMoreSettingTanTou(redTanTouNum);
                sendData(BleContant.RED_MORE_SETTING_TANTOU,2);
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
                            case BleContant.RED_CAIJI_ROAD:
                                 SendBleStr.setRedMoreSettingCode(redCodeNum);
                                 sendData(BleContant.RED_MORE_SETTING_CODE,1);
                                 break;
                            case BleContant.RED_MORE_SETTING_CODE:
                                 if(redCodeNum<nn){
                                     SendBleStr.setRedMoreSettingCode(++redCodeNum);
                                     sendData(BleContant.RED_MORE_SETTING_CODE,1);
                                 }else{
                                     SendBleStr.setRedMoreSettingTanTou(redTanTouNum);
                                     sendData(BleContant.RED_MORE_SETTING_TANTOU,1);
                                 }
                                break;
                            case BleContant.RED_MORE_SETTING_TANTOU:
                                if(redTanTouNum<nn){
                                    SendBleStr.setRedMoreSettingTanTou(++redTanTouNum);
                                    sendData(BleContant.RED_MORE_SETTING_TANTOU,1);
                                }else{
                                    sendData(BleContant.SEND_CAI_JI_PIN_LU,1);
                                }
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

                        switch (SEND_STATUS){
                            case BleContant.RED_CAIJI_ROAD:
                            case BleContant.SEND_CAI_JI_PIN_LU:
                            case BleContant.SEND_FA_SONG_PIN_LU:
                            case BleContant.RED_DEVICE_TIME:
                                 DialogUtils.closeProgress();
                                //解析并显示回执的数据
                                showData(data);
                                SEND_STATUS=BleContant.NOT_SEND_DATA;
                                 break;
                            case BleContant.RED_MORE_SETTING_CODE://读取多路编码
                                 //解析并显示回执的数据
                                 showData(data);
                                 if(redCodeNum<nn){
                                    SendBleStr.setRedMoreSettingCode(++redCodeNum);
                                    sendData(BleContant.RED_MORE_SETTING_CODE,2);
                                 }else{
                                     DialogUtils.closeProgress();
                                     SEND_STATUS=BleContant.NOT_SEND_DATA;
                                 }
                                 break;
                            case BleContant.RED_MORE_SETTING_TANTOU://读取多路探头埋深
                                 //解析并显示回执的数据
                                 showData(data);
                                 if(redTanTouNum<nn){
                                     SendBleStr.setRedMoreSettingTanTou(++redTanTouNum);
                                     sendData(BleContant.RED_MORE_SETTING_TANTOU,1);
                                 }else{
                                     DialogUtils.closeProgress();
                                     SEND_STATUS=BleContant.NOT_SEND_DATA;
                                 }
                                 break;
                             default:
                                  if(SEND_STATUS==BleContant.SET_MORE_SETTING_CODE && setCodeNum<(codeList.size()-1)){
                                     setCodeNum++;
                                     SendBleStr.setSetMoreSettingCode(setCodeNum,codeList.get(setCodeNum));
                                     sendData(BleContant.SET_MORE_SETTING_CODE,2);

                                 }else if(SEND_STATUS==BleContant.SET_MORE_SETTING_TANTOU && setTanTouNum<(tantouList.size()-1)){
                                     setTanTouNum++;
                                     SendBleStr.setSetMoreSettingTanTou(setTanTouNum,tantouList.get(setTanTouNum));
                                     sendData(BleContant.SET_MORE_SETTING_TANTOU,2);

                                 }else{
                                     DialogUtils.closeProgress();
                                     if(SEND_STATUS==BleContant.SET_CAIJI_ROAD){
                                         //采集路数设置成功后，要把全局的路数设置为最新的
                                         nn=Integer.parseInt(tvRoadNum.getText().toString());
                                     }
                                     dialogView = new DialogView(dialogView,mContext, "参数设置成功！", "确定",null, new View.OnClickListener() {
                                         public void onClick(View v) {
                                             dialogView.dismiss();
                                         }
                                     }, null);
                                     dialogView.show();
                                     SEND_STATUS=BleContant.NOT_SEND_DATA;
                                 }
                                 break;
                        }
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
                     data=data.replace("GDMETERNUMR","");
                     String[] msg=data.split(",");
                     m=Integer.parseInt(msg[0]);
                     nn=Integer.parseInt(msg[1]);
                     tvRoadNum.setText(msg[1]);
                     if(m==0){
                         tvCodeTitle.setText("统一编码\n探头ID号");
                     }else{
                         tvCodeTitle.setText("统一编码\n北斗SIM卡号");
                     }
                     break;
                //显示统一编码
                case BleContant.RED_MORE_SETTING_CODE:
                    BuglyUtils.uploadBleMsg("统一编码数据："+data);
                     String[] msg2=data.split(",");
                     MoreCode moreCode=new MoreCode();
                     moreCode.setCode(msg2[1]);
                     moreCode.setOther(msg2[2]);
                     codeList.add(moreCode);
                     listCode.setLayoutManager(new LinearLayoutManager(activity));
                     listCode.setAdapter(new MoreSettingCodeAdapter(this,codeList,codeList.size(),m));
                    break;
                //显示探头埋深
                case BleContant.RED_MORE_SETTING_TANTOU:
                    BuglyUtils.uploadBleMsg("探头埋深数据："+data);
                    final String[] msg3=data.split(",");
                    MoreTanTou moreTanTou=new MoreTanTou();
                    moreTanTou.setMaishen(msg3[1]);
                    moreTanTou.setMidu(msg3[2]);
                    moreTanTou.setPianyi(msg3[3]);
                    tantouList.add(moreTanTou);
                    listTantou.setLayoutManager(new LinearLayoutManager(activity));
                    listTantou.setAdapter(new MoreSettingTanTouAdapter(this,tantouList,tantouList.size()));
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
