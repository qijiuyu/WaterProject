package com.water.project.presenter.check;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.activity.MainActivity;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.utils.SPUtil;
import com.water.project.utils.Util;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;

public class CheckPresenterImpl {

    private Activity activity;
    private CheckPresenter checkPresenter;
    private DialogView dialogView;

    public CheckPresenterImpl(Activity activity,CheckPresenter checkPresenter){
        this.activity=activity;
        this.checkPresenter=checkPresenter;
    }


    /**
     * 水温校测按钮点击
     */
    public void SW_btn(TextView tvYaLi,TextView tv_SW_wucha,EditText et_SW_check){
        final String YaLi=tvYaLi.getText().toString().trim();
        if(YaLi.contains("99999999")){
            dialogView = new DialogView(activity, "传感器故障，无法参与计算校准，请查找原因！", "确认",null, new View.OnClickListener() {
                public void onClick(View v) {
                    dialogView.dismiss();
                }
            }, null);
            dialogView.show();
            return;
        }
        String wuCha=tv_SW_wucha.getText().toString().trim();
        final String strCheck=et_SW_check.getText().toString().trim();
        final int qIndex=strCheck.indexOf(".");
        final int hIndex=strCheck.length()-qIndex-1;
        if(strCheck.indexOf(".")==-1 && strCheck.length()>2){
            checkPresenter.showToast("人工实测水温最多只能输入2位整数！");
        }else if(qIndex>2){
            checkPresenter.showToast("人工实测水温的小数点前面最多只能是2位数");
        }else if(hIndex>2){
            checkPresenter.showToast("人工实测水温的小数点后面最多只能是2位数");
        }else if(TextUtils.isEmpty(wuCha)){
            checkPresenter.showToast("没有误差数据！");
        }else if(Double.parseDouble(wuCha)==0){
            checkPresenter.showToast("水温数据无误差，无需校正！");
        }else{
            checkPresenter.sendData(BleContant.RED_SHUI_WEN_PYL);
        }
    }

    /**
     * 水位埋深校测按钮点击
     */
    public void ShuiWei_btn(TextView tvYaLi, TextView tv_MS_wucha, EditText et_MS_check){
        final String YaLi=tvYaLi.getText().toString().trim();
        if(YaLi.contains("99999999")){
            dialogView = new DialogView(activity, "传感器故障，无法参与计算校准，请查找原因！", "确认",null, new View.OnClickListener() {
                public void onClick(View v) {
                    dialogView.dismiss();
                }
            }, null);
            dialogView.show();
            return;
        }
        String wuCha=tv_MS_wucha.getText().toString().trim();
        final String strCheck=et_MS_check.getText().toString().trim();
        final int qIndex=strCheck.indexOf(".");
        final int hIndex=strCheck.length()-qIndex-1;
        if(strCheck.indexOf(".")==-1 && strCheck.length()>4){
            checkPresenter.showToast("人工实测水位埋深最多只能输入4位整数！");
        }else if(qIndex>4){
            checkPresenter.showToast("人工实测水位埋深的小数点前面最多只能是4位数");
        }else if(hIndex>3){
            checkPresenter.showToast("人工实测水位埋深的小数点后面最多只能是3位数");
        }else if(TextUtils.isEmpty(wuCha)){
            checkPresenter.showToast("没有误差数据！");
        }else if(Double.parseDouble(wuCha)==0){
            checkPresenter.showToast("水位埋深数据无误差，无需校正！");
        }else{
            double d=Double.parseDouble(wuCha.replace("-",""))*100;
            if(d>0 && d<10){
                checkPresenter.sendData(BleContant.SEND_CHECK_ERROR);
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
    }



    /**
     * 电导率校测按钮点击
     */
    public void DDL_btn(TextView tvYaLi,TextView tv_DDL_wucha,EditText et_DDL_check){
        final String YaLi=tvYaLi.getText().toString().trim();
        if(YaLi.contains("99999999")){
            dialogView = new DialogView(activity, "传感器故障，无法参与计算校准，请查找原因！", "确认",null, new View.OnClickListener() {
                public void onClick(View v) {
                    dialogView.dismiss();
                }
            }, null);
            dialogView.show();
            return;
        }
        String wuCha=tv_DDL_wucha.getText().toString().trim();
        final String strCheck=et_DDL_check.getText().toString().trim();
        final int qIndex=strCheck.indexOf(".");
        final int hIndex=strCheck.length()-qIndex-1;
        if(strCheck.indexOf(".")==-1 && strCheck.length()>6){
            checkPresenter.showToast("人工实测电导率最多只能输入6位整数！");
        }else if(qIndex>6){
            checkPresenter.showToast("人工实测电导率的小数点前面最多只能是6位数");
        }else if(hIndex>2){
            checkPresenter.showToast("人工实测水温的小数点后面最多只能是2位数");
        }else if(TextUtils.isEmpty(wuCha)){
            checkPresenter.showToast("没有误差数据！");
        }else if(Double.parseDouble(wuCha)==0){
            checkPresenter.showToast("电导率数据无误差，无需校正！");
        }else{
            checkPresenter.sendData(BleContant.RED_DIAN_DAO_LV_PYL);
        }
    }


    /**
     * 弹出提示误差框
     */
    private void showPop(int type,double data){
        final String wuchaStr=Util.setDouble(data,1);
        View view=LayoutInflater.from(activity).inflate(R.layout.pop_check,null);
        final Dialog dialog=Util.dialogPop(activity,view);
        ImageView imageView=(ImageView)view.findViewById(R.id.img_pc);
        TextView textView=(TextView)view.findViewById(R.id.tv_pc);
        if(type==1){
            textView.setText("人工实测水位埋深大于设备采集水位埋深"+wuchaStr+"cm，需要将线夹子向下调整"+wuchaStr+"cm，线夹子调整完成，请重新读取实时数据。");
            imageView.setImageDrawable(activity.getResources().getDrawable(R.mipmap.pop_check_down));
        }else{
            textView.setText("人工实测水位埋深小于设备采集水位埋深"+wuchaStr+"cm，需要将线夹子向上调整"+wuchaStr+"cm，线夹子调整完成，请重新读取实时数据。");
            imageView.setImageDrawable(activity.getResources().getDrawable(R.mipmap.pop_check_up));
        }
        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    /**
     * 误差过大的提示
     */
    private void showNotCheckPop(){
        View view=LayoutInflater.from(activity).inflate(R.layout.pop_not_check,null);
        final Dialog dialog=Util.dialogPop(activity,view);
        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    /**
     * 重新扫描蓝牙
     * @param SEND_STATUS
     */
    public void resumeScan(final int SEND_STATUS){
        checkPresenter.clearLoding();
        if(null!=dialogView){
            dialogView.dismiss();
        }
        dialogView = new DialogView(activity, "扫描不到该蓝牙设备，请靠近设备再进行扫描！", "重新扫描","取消", new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
                checkPresenter.sendData(SEND_STATUS);
            }
        }, null);
        dialogView.show();
    }


    /**
     * 蓝牙连接断开
     */
    public void bleDisConnect(){
        checkPresenter.clearLoding();
        if(null!=dialogView){
            dialogView.dismiss();
        }
        dialogView = new DialogView(activity, "蓝牙连接断开，请靠近设备进行连接!","重新连接", "取消", new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
                checkPresenter.showLoding("蓝牙连接中...");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
                        MainActivity.bleService.connect(ble.getBleMac());
                    }
                },100);
            }
        }, null);
        dialogView.show();
    }


    /**
     * 读取数据超时
     */
    public void timeOut(final int SEND_STATUS){
        checkPresenter.clearLoding();
        if(null!=dialogView){
            dialogView.dismiss();
        }
        dialogView = new DialogView(activity, "接收数据超时！", "重试","取消", new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
                checkPresenter.sendData(SEND_STATUS);
            }
        }, null);
        dialogView.show();
    }


    /**
     * 下发命令失败
     */
    public void sendCmdFail(final int SEND_STATUS){
        checkPresenter.clearLoding();
        if(null!=dialogView){
            dialogView.dismiss();
        }
        dialogView = new DialogView(activity, "下发命令失败！", "重试","取消", new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
                checkPresenter.sendData(SEND_STATUS);
            }
        }, null);
        dialogView.show();
    }
}
