package com.water.project.activity;

import android.annotation.TargetApi;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.activity.menu3.SetSIM2Activity;
import com.water.project.activity.menu3.SetSIMActivity;
import com.water.project.application.MyApplication;
import com.water.project.bean.BindService;
import com.water.project.bean.Ble;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.Util;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.BleObject;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;

public class NetSettingActivity extends BaseActivity implements View.OnClickListener{

    private ImageView img1,img2,img3,imgClearApn,imgClearPort1,imgClearPort2,imgClearPort3,imgClearIp1,imgClearIp2,imgClearIp3;
    private EditText etAddress1,etAddress2,etAddress3,etApn,etIp1,etIp2,etIp3,etPort1,etPort2,etPort3;
    private TextView tvAddress1,tvAddress2,tvAddress3,tvIp1,tvIp2,tvIp3,tvPort1,tvPort2,tvPort3;
    private DialogView dialogView;
    private Handler mHandler=new Handler();
    //下发命令的编号
    private int SEND_STATUS;
    //读取的数据
    private String strData;
    //密码存储
    private char[] arr;
    //当前页面是否在显示
    private boolean isShowActivity=true;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_net);
        initView();
        register();
        sendData(BleContant.SEND_GET_CODE_PHONE);
    }


    /**
     * 初始化
     */
    private void initView(){
        TextView textView=(TextView)findViewById(R.id.tv_head);
        textView.setText("网络连接设置");
        img1=(ImageView)findViewById(R.id.img_an1);
        img2=(ImageView)findViewById(R.id.img_an2);
        img3=(ImageView)findViewById(R.id.img_an3);
        imgClearApn=(ImageView)findViewById(R.id.img_clear_apn);
        imgClearPort1=(ImageView)findViewById(R.id.img_clear_port1);
        imgClearPort2=(ImageView)findViewById(R.id.img_clear_port2);
        imgClearPort3=(ImageView)findViewById(R.id.img_clear_port3);
        imgClearIp1=(ImageView)findViewById(R.id.img_clear_ip1);
        imgClearIp2=(ImageView)findViewById(R.id.img_clear_ip2);
        imgClearIp3=(ImageView)findViewById(R.id.img_clear_ip3);
        etAddress1=(EditText)findViewById(R.id.et_address1);
        etAddress2=(EditText)findViewById(R.id.et_address2);
        etAddress3=(EditText)findViewById(R.id.et_address3);
        etApn=(EditText)findViewById(R.id.et_apn);
        etIp1=(EditText)findViewById(R.id.et_ip1);
        etIp2=(EditText)findViewById(R.id.et_ip2);
        etIp3=(EditText)findViewById(R.id.et_ip3);
        etPort1=(EditText)findViewById(R.id.et_port1);
        etPort2=(EditText)findViewById(R.id.et_port2);
        etPort3=(EditText)findViewById(R.id.et_port3);
        tvAddress1=(TextView)findViewById(R.id.tv_address1);
        tvAddress2=(TextView)findViewById(R.id.tv_address2);
        tvAddress3=(TextView)findViewById(R.id.tv_address3);
        tvIp1=(TextView)findViewById(R.id.tv_ip1);
        tvIp2=(TextView)findViewById(R.id.tv_ip2);
        tvIp3=(TextView)findViewById(R.id.tv_ip3);
        tvPort1=(TextView)findViewById(R.id.tv_port1);
        tvPort2=(TextView)findViewById(R.id.tv_port2);
        tvPort3=(TextView)findViewById(R.id.tv_port3);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        imgClearApn.setOnClickListener(this);
        imgClearPort1.setOnClickListener(this);
        imgClearPort2.setOnClickListener(this);
        imgClearPort3.setOnClickListener(this);
        imgClearIp1.setOnClickListener(this);
        imgClearIp2.setOnClickListener(this);
        imgClearIp3.setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);
        findViewById(R.id.tv_red).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);

        etApn.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    imgClearApn.setVisibility(View.VISIBLE);
                }else{
                    imgClearApn.setVisibility(View.GONE);
                }
            }
        });

        etPort1.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    imgClearPort1.setVisibility(View.VISIBLE);
                }else{
                    imgClearPort1.setVisibility(View.GONE);
                }
            }
        });

        etPort2.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    imgClearPort2.setVisibility(View.VISIBLE);
                }else{
                    imgClearPort2.setVisibility(View.GONE);
                }
            }
        });

        etPort3.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    imgClearPort3.setVisibility(View.VISIBLE);
                }else{
                    imgClearPort3.setVisibility(View.GONE);
                }
            }
        });

        etIp1.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    imgClearIp1.setVisibility(View.VISIBLE);
                }else{
                    imgClearIp1.setVisibility(View.GONE);
                }
            }
        });

        etIp2.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    imgClearIp2.setVisibility(View.VISIBLE);
                }else{
                    imgClearIp2.setVisibility(View.GONE);
                }
            }
        });

        etIp3.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    imgClearIp3.setVisibility(View.VISIBLE);
                }else{
                    imgClearIp3.setVisibility(View.GONE);
                }
            }
        });
    }


    /**
     * 发送蓝牙命令
     */
    private BleService bleService;
    private void sendData(final int SEND_STATUS) {
        this.SEND_STATUS=SEND_STATUS;
        bleService= BleObject.getInstance().getBleService(this, new BindService() {
            @Override
            public void onSuccess() {
                sendData(SEND_STATUS);
            }
        });
        if(bleService==null){
            return;
        }
        if(SEND_STATUS==BleContant.SEND_GET_CODE_PHONE){
            DialogUtils.showProgress(NetSettingActivity.this,"正在读取参数设置...");
        }else{
            DialogUtils.showProgress(NetSettingActivity.this,"正在设置参数信息...");
        }
        //如果蓝牙连接断开，就扫描重连
        if(bleService.connectionState==bleService.STATE_DISCONNECTED){
            //扫描并重连蓝牙
            final Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
            if(null!=ble){
                DialogUtils.showProgress(NetSettingActivity.this,"扫描并连接蓝牙设备...");
                bleService.scanDevice(ble.getBleName());
            }
            return;
        }
        SendBleStr.sendBleData(this,SEND_STATUS);
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
            if(!isShowActivity){
                return;
            }
            switch (intent.getAction()){
                //扫描不到指定蓝牙设备
                case BleService.ACTION_NO_DISCOVERY_BLE:
                    DialogUtils.closeProgress();
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
                    DialogUtils.closeProgress();
                     dialogView = new DialogView(mContext, "蓝牙连接断开，请靠近设备进行连接!","重新连接", "取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            DialogUtils.showProgress(NetSettingActivity.this,"蓝牙连接中...");
                            mHandler.postDelayed(new Runnable() {
                                public void run() {
                                    Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
                                    bleService.connect(ble.getBleMac());
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
                    DialogUtils.closeProgress();
                    final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA).replace(">OK","");
                    if(SEND_STATUS==BleContant.SEND_GET_CODE_PHONE){
                        if(data.startsWith("GDSETMAS")){
                            //进入设置北斗中心号码的页面
                            Intent intent1=new Intent(NetSettingActivity.this, SetSIM2Activity.class);
                            intent1.putExtra("data",data);
                            startActivity(intent1);
                            finish();
                        }else if(data.startsWith("GDBDCEN")){
                            //进入设置北斗中心号码的页面
                            Intent intent1=new Intent(NetSettingActivity.this, SetSIMActivity.class);
                            intent1.putExtra("data",data);
                            startActivity(intent1);
                            finish();
                        }else{
                            //解析并显示回执的数据
                            showData(data);
                        }
                    }else{
                        showToastView("设置成功！");
                    }
                    SEND_STATUS=BleContant.NOT_SEND_DATA;
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "接收数据超时！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS);
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "下发命令失败！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS);
                        }
                    }, null);
                    dialogView.show();
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
     * 解析并显示数据
     */
    private  void showData(String msg){
       try {
           strData=msg;
           String[] strings=msg.split(";");
           if(null==strings || strings.length==0){
               return;
           }
           final String strAddress=strings[0];
           etAddress1.setText(strAddress.substring(3,5));
           etAddress2.setText(strAddress.substring(5,7));
           etAddress3.setText(strAddress.substring(7,9));

           if(etAddress1.getText().toString().trim().equals("00")){
               close1(img1);
           }else{
               open1(img1);
           }

           if(etAddress2.getText().toString().trim().equals("00")){
               close2(img2);
           }else{
               open2(img2);
           }

           if(etAddress3.getText().toString().trim().equals("00")){
               close3(img3);
           }else{
               open3(img3);
           }

           //展示ip及端口号
           String strIp;
           String[] ips;
           strIp=strings[2];
           ips=strIp.split(",");
           etIp1.setText(ips[0]);
           etPort1.setText(ips[1]);

           strIp=strings[3];
           ips=strIp.split(",");
           etIp2.setText(ips[0]);
           etPort2.setText(ips[1]);

           strIp=strings[4];
           ips=strIp.split(",");
           etIp3.setText(ips[0]);
           etPort3.setText(ips[1]);

           //显示APN
           etApn.setText(strings[5]);
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_clear_apn:
                 etApn.setText(null);
                 break;
            case R.id.img_clear_port1:
                 etPort1.setText(null);
                 break;
            case R.id.img_clear_port2:
                etPort2.setText(null);
                break;
            case R.id.img_clear_port3:
                etPort3.setText(null);
                break;
            case R.id.img_clear_ip1:
                etIp1.setText(null);
                break;
            case R.id.img_clear_ip2:
                etIp2.setText(null);
                break;
            case R.id.img_clear_ip3:
                etIp3.setText(null);
                break;
            //保存
            case R.id.tv_save:
                final String address1=etAddress1.getText().toString().trim();
                final String address2=etAddress2.getText().toString().trim();
                final String address3=etAddress3.getText().toString().trim();
                final String ip1=etIp1.getText().toString().trim();
                final String ip2=etIp2.getText().toString().trim();
                final String ip3=etIp3.getText().toString().trim();
                final String port1=etPort1.getText().toString().trim();
                final String port2=etPort2.getText().toString().trim();
                final String port3=etPort3.getText().toString().trim();
                final String apn=etApn.getText().toString().trim();
                if(TextUtils.isEmpty(address1)){
                    showToastView("请输入连接1的主站地址！");
                    return;
                }
                if(TextUtils.isEmpty(address2)){
                    showToastView("请输入连接2的主站地址！");
                    return;
                }
                if(TextUtils.isEmpty(address3)){
                    showToastView("请输入连接3的主站地址！");
                    return;
                }
                if(TextUtils.isEmpty(ip1)){
                    showToastView("请输入第一个IP地址！");
                    return;
                }
                if(Util.judgeContainsStr(ip1)){
                    if(Util.isInteger(ip1.substring(0,1))){
                        dialogView = new DialogView(mContext, "第一个IP地址为域名，域名首字母不能是数字！", "知道了",null, new View.OnClickListener() {
                            public void onClick(View v) {
                                dialogView.dismiss();
                            }
                        }, null);
                        dialogView.show();
                        return;
                    }
                }

                if(TextUtils.isEmpty(ip2)){
                    showToastView("请输入第二个IP地址！");
                    return;
                }

                if(Util.judgeContainsStr(ip2)){
                    if(Util.isInteger(ip2.substring(0,1))){
                        dialogView = new DialogView(mContext, "第二个IP地址为域名，域名首字母不能是数字！", "知道了",null, new View.OnClickListener() {
                            public void onClick(View v) {
                                dialogView.dismiss();
                            }
                        }, null);
                        dialogView.show();
                        return;
                    }
                }

                if(TextUtils.isEmpty(ip3)){
                    showToastView("请输入第三个IP地址！");
                    return;
                }

                if(Util.judgeContainsStr(ip3)){
                    if(Util.isInteger(ip3.substring(0,1))){
                        dialogView = new DialogView(mContext, "第三个IP地址为域名，域名首字母不能是数字！", "知道了",null, new View.OnClickListener() {
                            public void onClick(View v) {
                                dialogView.dismiss();
                            }
                        }, null);
                        dialogView.show();
                        return;
                    }
                }

                if(TextUtils.isEmpty(port1)){
                    showToastView("请输入第一个端口号！");
                    return;
                }
                if(TextUtils.isEmpty(port2)){
                    showToastView("请输入第二个端口号！");
                    return;
                }
                if(TextUtils.isEmpty(port3)){
                    showToastView("请输入第三个端口号！");
                    return;
                }
                if(TextUtils.isEmpty(apn)){
                    showToastView("请输入APN！");
                    return;
                }
                final View pwdView= LayoutInflater.from(mContext).inflate(R.layout.pop_pwd,null);
                dialogPop(pwdView,true);
                final EditText etPwd=(EditText)pwdView.findViewById(R.id.editHide);
                final TextView tv1 = (TextView) pwdView.findViewById(R.id.t1);
                final TextView tv2 = (TextView)pwdView. findViewById(R.id.t2);
                final TextView tv3 = (TextView)pwdView. findViewById(R.id.t3);
                final TextView tv4 = (TextView)pwdView. findViewById(R.id.t4);
                final TextView tv5 = (TextView)pwdView. findViewById(R.id.t5);
                final TextView tv6 = (TextView) pwdView.findViewById(R.id.t6);
                etPwd.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void afterTextChanged(Editable s) {
                        arr = s.toString().toCharArray();
                        tv1.setText(null);
                        tv2.setText(null);
                        tv3.setText(null);
                        tv4.setText(null);
                        tv5.setText(null);
                        tv6.setText(null);
                        if (TextUtils.isEmpty(s.toString())) {
                            tv1.setBackground(getResources().getDrawable(R.drawable.scan_share));
                        }
                        for (int i = 0; i < arr.length; i++) {
                            if (i == 0) {
                                tv1.setText(String.valueOf(arr[0]));
                                tv1.setBackgroundColor(getResources().getColor(R.color.color_1fc37f));
                                tv2.setBackground(getResources().getDrawable(R.drawable.scan_share));
                            } else if (i == 1) {
                                tv2.setText(String.valueOf(arr[1]));
                                tv2.setBackgroundColor(getResources().getColor(R.color.color_1fc37f));
                                tv3.setBackground(getResources().getDrawable(R.drawable.scan_share));
                            } else if (i == 2) {
                                tv3.setText(String.valueOf(arr[2]));
                                tv3.setBackgroundColor(getResources().getColor(R.color.color_1fc37f));
                                tv4.setBackground(getResources().getDrawable(R.drawable.scan_share));
                            } else if (i == 3) {
                                tv4.setText(String.valueOf(arr[3]));
                                tv4.setBackgroundColor(getResources().getColor(R.color.color_1fc37f));
                                tv5.setBackground(getResources().getDrawable(R.drawable.scan_share));
                            } else if (i == 4) {
                                tv5.setText(String.valueOf(arr[4]));
                                tv5.setBackgroundColor(getResources().getColor(R.color.color_1fc37f));
                                tv6.setBackground(getResources().getDrawable(R.drawable.scan_share));
                            } else if (i == 5) {
                                tv6.setText(String.valueOf(arr[5]));
                                tv6.setBackgroundColor(getResources().getColor(R.color.color_1fc37f));
                            }
                        }
                    }
                });
                //确定
                pwdView.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String strPwd=etPwd.getText().toString().trim();
                        if(TextUtils.isEmpty(strPwd)){
                            showToastView("请输入密码！");
                        }else if(!strPwd.equals("100012")){
                            showToastView("输入的密码错误！");
                        }else{
                            closeDialog();
                            SendBleStr.setIpPort(strData,address1,address2,address3,ip1,ip2,ip3,port1,port2,port3,apn);
                            sendData(BleContant.SET_IP_PORT);
                        }
                    }
                });
                //取消
                pwdView.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        closeDialog();
                    }
                });
                break;
            //读取
            case R.id.tv_red:
                sendData(BleContant.SEND_GET_CODE_PHONE);
                break;
            case R.id.img_an1:
                if(null==v.getTag()){
                    return;
                }
                if(v.getTag().toString().equals("1")){
                    close1(v);
                }else{
                    open1(v);
                }
                break;
            case R.id.img_an2:
                if(null==v.getTag()){
                    return;
                }
                if(v.getTag().toString().equals("1")){
                    close2(v);
                }else{
                    open2(v);
                }
                break;
            case R.id.img_an3:
                if(null==v.getTag()){
                    return;
                }
                if(v.getTag().toString().equals("1")){
                    close3(v);
                }else{
                    open3(v);
                }
                break;
            case R.id.lin_back:
                finish();
                break;
            default:
                break;
        }
    }


    private void close1(View v){
        v.setTag(0);
        img1.setImageDrawable(getResources().getDrawable(R.mipmap.close_icon));
        setColor(tvAddress1,0);
        setColor(tvIp1,0);
        setColor(tvPort1,0);
        setColor2(etAddress1,0);
        setColor2(etIp1,0);
        setColor2(etPort1,0);
        etAddress1.setText("00");
    }


    private void close2(View v){
        v.setTag(0);
        img2.setImageDrawable(getResources().getDrawable(R.mipmap.close_icon));
        setColor(tvAddress2,0);
        setColor(tvIp2,0);
        setColor(tvPort2,0);
        setColor2(etAddress2,0);
        setColor2(etIp2,0);
        setColor2(etPort2,0);
        etAddress2.setText("00");
    }


    private void close3(View v){
        v.setTag(0);
        img3.setImageDrawable(getResources().getDrawable(R.mipmap.close_icon));
        setColor(tvAddress3,0);
        setColor(tvIp3,0);
        setColor(tvPort3,0);
        setColor2(etAddress3,0);
        setColor2(etIp3,0);
        setColor2(etPort3,0);
        etAddress3.setText("00");
    }


    private void open1(View v){
        v.setTag(1);
        img1.setImageDrawable(getResources().getDrawable(R.mipmap.open_icon));
        setColor(tvAddress1,1);
        setColor(tvIp1,1);
        setColor(tvPort1,1);
        setColor2(etAddress1,1);
        setColor2(etIp1,1);
        setColor2(etPort1,1);
        if(!etAddress1.getText().toString().trim().equals("00")){
            return;
        }
        etAddress1.setText("16");
    }


    private void open2(View v){
        v.setTag(1);
        img2.setImageDrawable(getResources().getDrawable(R.mipmap.open_icon));
        setColor(tvAddress2,1);
        setColor(tvIp2,1);
        setColor(tvPort2,1);
        setColor2(etAddress2,1);
        setColor2(etIp2,1);
        setColor2(etPort2,1);
        if(!etAddress2.getText().toString().trim().equals("00")){
            return;
        }
        etAddress2.setText("16");
    }


    private void open3(View v){
        v.setTag(1);
        img3.setImageDrawable(getResources().getDrawable(R.mipmap.open_icon));
        setColor(tvAddress3,1);
        setColor(tvIp3,1);
        setColor(tvPort3,1);
        setColor2(etAddress3,1);
        setColor2(etIp3,1);
        setColor2(etPort3,1);
        if(!etAddress3.getText().toString().trim().equals("00")){
            return;
        }
        etAddress3.setText("16");
    }

    private void setColor(TextView textView,int type){
        if(type==0){
            textView.setTextColor(getResources().getColor(R.color.color_AEAEAE));
        }else{
            textView.setTextColor(getResources().getColor(android.R.color.black));
        }
    }


    private void setColor2(EditText editText,int type){
        if(type==0){
            editText.setTextColor(getResources().getColor(R.color.color_AEAEAE));
            editText.setCursorVisible(false);
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
        }else{
            editText.setTextColor(getResources().getColor(R.color.color_1fc37f));
            editText.setFocusable(true);
            editText.setCursorVisible(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isShowActivity=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isShowActivity=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
