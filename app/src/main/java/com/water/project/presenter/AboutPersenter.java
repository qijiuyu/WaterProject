package com.water.project.presenter;

import com.water.project.activity.AboutActivity;
import com.water.project.utils.BleUtils;
import com.water.project.utils.ble.ByteUtil;
import com.water.project.utils.ble.SendBleStr;

import java.util.ArrayList;
import java.util.List;

public class AboutPersenter {

    private AboutActivity activity;
    //头部命令
    private String cmdHead;
    //要发送的命令集合
    public List<String> cmdList=new ArrayList<>();

    public AboutPersenter(AboutActivity activity){
        this.activity=activity;
    }


    /**
     * 解析txt文档里面的数据
     * @param message
     */
    public void parsingData(String message){
        //先清空集合
        cmdList.clear();
        //截取头部命令
        cmdHead=message.substring(0,2);
        if(message.length()==9602){
            cmdList= BleUtils.getSendData(message.substring(2,message.length()),2400);
            SendBleStr.sendTxtContent(ByteUtil.strTo16("GD@#ZKU")+"000401"+cmdHead,cmdList.get(0));
        }else if(message.length()==34){
            cmdList.add(message.substring(2,message.length()));
            SendBleStr.sendTxtContent(ByteUtil.strTo16("GD@#ZKU")+"0111"+cmdHead,cmdList.get(0));
        }else{
            cmdList.add(message.substring(2,message.length()));
            SendBleStr.sendTxtContent(ByteUtil.strTo16("GD@#ZKU")+"0121"+cmdHead,cmdList.get(0));
        }
        cmdList.remove(0);
        //开始发送蓝牙命令
        activity.sendData();
    }


    /**
     * 继续发送大数据
     */
    public boolean sendBigData(){
       if(cmdList.size()==0){
           return false;
       }else{
           String numType = null;
           switch (cmdList.size()){
               case 3:
                    numType="02";
                    break;
               case 2:
                    numType="03";
                    break;
               case 1:
                    numType="04";
                    break;
                default:
                    break;
           }
           SendBleStr.sendTxtContent(ByteUtil.strTo16("GD@#ZKU")+"0004"+numType+cmdHead,cmdList.get(0));
           cmdList.remove(0);
           return true;
       }
    }
}
