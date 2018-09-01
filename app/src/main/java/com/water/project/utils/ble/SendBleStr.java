package com.water.project.utils.ble;

/**
 * Created by Administrator on 2018/9/1.
 */

public class SendBleStr {

    //查询统一编码，SIM卡号
    public static final String GET_CODE_PHONE="GDGET";

    //查询探头埋深
    public static final String GET_TANTOU="GDLINER";

    public static void sendBleData(int status){
        switch (status){
            //查询统一编码，SIM卡号
            case BleContant.SEND_GET_CODE_PHONE:
                 SendBleDataManager.getInstance().sendData(GET_CODE_PHONE,true);
                 break;

        }
    }

}
