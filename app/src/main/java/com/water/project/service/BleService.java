package com.water.project.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
/**
 * 蓝牙Service
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleService extends Service implements Serializable{
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_SERVICE_UUID = UUID.fromString("0000B350-D6D8-C7EC-BDF0-EAB1BFC6BCBC");
    public static final UUID RX_CHAR_UUID = UUID.fromString("0000B352-D6D8-C7EC-BDF0-EAB1BFC6BCBC");
    public static final UUID TX_CHAR_UUID = UUID.fromString("0000B351-D6D8-C7EC-BDF0-EAB1BFC6BCBC");


    /**
     * 蓝牙扫描成功
     */
    public final static String ACTION_SCAN_SUCCESS = "net.zkgd.adminapp.ACTION_SCAN_SUCCESS";

    /**
     * 蓝牙连接成功
     */
    public final static String ACTION_GATT_CONNECTED = "net.zkgd.adminapp.ACTION_GATT_CONNECTED";
    /**
     * 断开连接
     */
    public final static String ACTION_GATT_DISCONNECTED = "net.zkgd.adminapp.ACTION_GATT_DISCONNECTED";
    /**
     * 接收到了数据
     */
    public final static String ACTION_DATA_AVAILABLE = "net.zkgd.adminapp.ACTION_DATA_AVAILABLE";
    /**
     * 接收到了数据
     */
    public final static String ACTION_DATA_AVAILABLE2 = "net.zkgd.adminapp.ACTION_DATA_AVAILABLE2";
    /**
     * 发送接到的数据的KEY
     */
    public final static String ACTION_EXTRA_DATA = "net.zkgd.adminapp.EXTRA_DATA";
    /**
     * 通道建立成功
     */
    public final static String ACTION_ENABLE_NOTIFICATION_SUCCES = "net.zkgd.adminapp.enablenotificationsucces";
    /**
     * 没有发现指定蓝牙
     */
    public final static String ACTION_NO_DISCOVERY_BLE = "net.zkgd.adminapp.ACTION_NO_DISCOVERY_BLE";

    /**
     * 30秒扫描完毕
     */
    public final static String ACTION_SCAM_DEVICE_END = "net.zkgd.adminapp.ACTION_SCAM_DEVICE_END";

    /**
     * 数据交互超时
     */
    public final static String ACTION_INTERACTION_TIMEOUT = "net.zkgd.adminapp.ACTION_INTERACTION_TIMEOUT";

    //发送数据失败
    public final static String ACTION_SEND_DATA_FAIL = "net.zkgd.adminapp.ACTION_SEND_DATA_FAIL";

    //回执的数据有error
    public final static String ACTION_GET_DATA_ERROR="net.zkgd.adminapp.ACTION_GET_DATA_ERROR";


    private Intent intent=new Intent();

    private final IBinder mBinder = new LocalBinder();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice bleDevice;
    public int connectionState = STATE_DISCONNECTED;
    //连接断开
    public static final int STATE_DISCONNECTED = 0;
    //开始连接
    public static final int STATE_CONNECTING = 1;
    //连接成功
    public static final int STATE_CONNECTED = 2;
    //timeOut：发送命令超时         scanTime:扫描蓝牙超时
    public long timeOut = 1000 * 25, scanTime = 1000 * 15;
    private Handler handler = new Handler();
    //蓝牙名称
    private String bleName;
    //接收回执的数据，进行拼接
    private StringBuffer sb;
    //是否重新连接蓝牙
    private boolean isConnect = true;
    /**
     * true：接收完毕后再回执
     * false：每接收一条就回执
     */
    private boolean isTotalSend;

    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    /**
     * 创建蓝牙适配器
     *
     * @return true  successful.
     */
    public BluetoothAdapter createBluetoothAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter;
    }

    /**
     * 扫描蓝牙设备
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void scanDevice(String bleName) {
        if (mBluetoothAdapter == null) {
            return;
        }
        this.bleName=bleName;
        //先关闭扫描
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        //开始扫描蓝牙
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        //开启扫描蓝牙计时器
        startScanListener();
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if(null==device){
                return;
            }
            if(TextUtils.isEmpty(device.getName())){
                return;
            }
            if(TextUtils.isEmpty(bleName)){
                intent.setAction(ACTION_SCAN_SUCCESS);
                intent.putExtra("bleName",device.getName());
                intent.putExtra("bleMac",device.getAddress());
                sendBroadcast(intent);
            }else{
                if(bleName.equals(device.getName())){
                    //停止扫描
                    stopScan();
                    //关闭扫描计时器
                    handler.removeCallbacks(scanRunnable);
                    //连接蓝牙
                    handler.post(new Runnable() {
                        public void run() {
                            connect(device.getAddress());
                        }
                    });
                }
            }

        }
    };


    /**
     * 停止蓝牙扫描
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stopScan(){
        if(null!=mBluetoothAdapter){
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }


    /**
     * 连接指定蓝牙
     * @param address 蓝牙的地址
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean connect(final String address) {
        isConnect=true;
        //停止扫描
        stopScan();
        connectionState = STATE_CONNECTING;
        if (mBluetoothAdapter == null || TextUtils.isEmpty(address)) {
            return false;
        }
        bleDevice = mBluetoothAdapter.getRemoteDevice(address);
        if (bleDevice == null) {
            connectionState = STATE_DISCONNECTED;
            return false;
        }
        LogUtils.e("调connectGatt开始连接");
        mBluetoothGatt = bleDevice.connectGatt(BleService.this, false, mGattCallback);
        return true;
    }

    /**
     * 初始化通道
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void enableTXNotification() {
        try {
            if (mBluetoothGatt == null) {
                disconnect();
                return;
            }
            BluetoothGattService RxService = mBluetoothGatt.getService(RX_SERVICE_UUID);
            if (RxService == null) {
                disconnect();
                return;
            }
            BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(TX_CHAR_UUID);
            if (TxChar == null) {
                disconnect();
                return;
            }
            mBluetoothGatt.setCharacteristicNotification(TxChar, true);
            BluetoothGattDescriptor descriptor = TxChar.getDescriptor(CCCD);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
            broadcastUpdate(ACTION_ENABLE_NOTIFICATION_SUCCES);
        } catch (Exception e) {
            broadcastUpdate(ACTION_GATT_DISCONNECTED);
        }
    }

    /**
     * 断开BluetoothGatt连接
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        LogUtils.e("gatt释放了");
    }

    /**
     * 传输数据
     */
    boolean isSuccess;
    public boolean writeRXCharacteristic(List<String> list,boolean isTotalSend) {
        this.isTotalSend=isTotalSend;
        sb=new StringBuffer();
        isSuccess=true;
        try {
            BluetoothGattService RxService = mBluetoothGatt.getService(RX_SERVICE_UUID);
            if (RxService == null) {
                disconnect();
                return false;
            }
            final BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(RX_CHAR_UUID);
            if (RxChar == null) {
                disconnect();
                return false;
            }

//            StringBuffer stringBuffer=new StringBuffer();
//            for (int i=0;i<list.size();i++){
//                stringBuffer.append(list.get(i));
//            }
//            LogUtils.e("发送的命令是："+stringBuffer.toString());
            //循环发送数据
            for (int i=0,len=list.size();i<len;i++){
                   RxChar.setValue(list.get(i).getBytes());
                  //下发命令
                  boolean b=mBluetoothGatt.writeCharacteristic(RxChar);
                  if(!b){
                      //延时下发
                      Thread.sleep(30);
                      b=mBluetoothGatt.writeCharacteristic(RxChar);
                  }
                  if(!b){
                      //延时下发
                      Thread.sleep(30);
                      b=mBluetoothGatt.writeCharacteristic(RxChar);
                  }
                  if(!b){
                      isSuccess=false;
                      break;
                  }
                  //延时下发
                  Thread.sleep(30);
            }
            if(isSuccess){
                //开启超时计时器
                startTimeOut();
            }
            return isSuccess;
        } catch (Exception e) {
            disconnect();
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 发送广播
     **/
    private void broadcastUpdate(String action) {
        Intent intent = new Intent(action);
        getApplication().sendBroadcast(intent);
    }

    private void broadcastUpdate(String action,int status) {
        Intent intent = new Intent(action);
        intent.putExtra("status",status);
        getApplication().sendBroadcast(intent);
    }

    /**
     * 发送广播（携带接受到的值）
     **/
    private void broadcastUpdate(final String action, final String value) {
        final Intent intent = new Intent(action);
        intent.putExtra(ACTION_EXTRA_DATA, value);
        getApplication().sendBroadcast(intent);
    }


    /**
     * 清楚蓝牙缓存（反射）
     **/
    public boolean refreshDeviceCache() {
        if (mBluetoothGatt != null) {
            try {
                BluetoothGatt localBluetoothGatt = mBluetoothGatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
                if (localMethod != null) {
                    boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                    return bool;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("清楚蓝牙连接缓存异常：" + e.getMessage());
            }
        }
        return false;
    }


    /**
     * 蓝牙连接交互回调
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            LogUtils.e("status="+status+"___________");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                LogUtils.e("蓝牙连接成功");
                connectionState = STATE_CONNECTED;
                //去发现服务
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (null == mBluetoothGatt) {
                            return;
                        }
                        mBluetoothGatt.discoverServices();
                    }
                }, 700);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                LogUtils.e("蓝牙连接断开");
                refreshDeviceCache();
                connectionState = STATE_DISCONNECTED;
                close();
                stopTimeOut();
                if(status==0){
                    //发送蓝牙连接断开的广播
                    broadcastUpdate(ACTION_GATT_DISCONNECTED,status);
                }else{
                    //重新连接蓝牙
                    resumeConnect(status);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //发现服务，建立通道
                enableTXNotification();
            }
        }

        //接收数据
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }

        //接收数据
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic) {
            if(null==characteristic){
                return;
            }
            if (!TX_CHAR_UUID.equals(characteristic.getUuid())) {
                return;
            }
            final String data=characteristic.getStringValue(0);
            if(TextUtils.isEmpty(data)){
                return;
            }

            handler.removeCallbacks(runnable);

            //每接收一条数据就回执
            if(!isTotalSend){
                broadcastUpdate(ACTION_DATA_AVAILABLE2, data);
                handler.postDelayed(runnable,70);
                return;
            }

            if(data.startsWith("GD")){
                sb.append(data);
                handler.postDelayed(runnable,70);
                return;
            }
            if(sb.length()>0){
                sb.append(data);
                handler.postDelayed(runnable,70);
            }


//            if(data.startsWith("GD")){
//                sb.append(data);
//                if(sb.toString().endsWith(">OK")){
//                    broadCastData();
//                    return;
//                }
//                if(sb.toString().startsWith("GDCURRENT") && sb.toString().endsWith(";")){
//                    broadCastData();
//                    return;
//                }
//                if(sb.toString().endsWith("ERROR")){
//                    //广播错误数据
//                    broadCastError();
//                    return;
//                }
//                return;
//            }

//            if(sb.length()>0){
//                sb.append(data);
//                if(sb.toString().endsWith(">OK")){
//                    broadCastData();
//                    return;
//                }
//                if(sb.toString().startsWith("GDCURRENT") && sb.toString().endsWith(";")){
//                    broadCastData();
//                    return;
//                }
//                if(sb.toString().endsWith("ERROR")){
//                    //广播错误数据
//                    broadCastError();
//                    return;
//                }
//            }
        }
    };


    /**
     * 判断是否已接收到所有回执数据
     */
    private Runnable runnable=new Runnable() {
        public void run() {
            //已接收完毕，发给APP界面
            if(sb.toString().endsWith("ERROR")){
                broadCastError();
            }else{
                broadCastData();
            }
        }
    };


    /**
     * 通过广播抛出数据
     */
    private void broadCastData(){
        //关闭超时计时器
        stopTimeOut();
        broadcastUpdate(ACTION_DATA_AVAILABLE, sb.toString());
        sb=new StringBuffer();
    }


    /**
     * 重新连接蓝牙
     * @param status
     */
    public void resumeConnect(int status){
        if(isConnect){
            isConnect=false;
            handler.postDelayed(new Runnable() {
                public void run() {
                    LogUtils.e("重连一次蓝牙");
                    Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
                    connect(ble.getBleMac());
                }
            },500);
            return;
        }
        //发送蓝牙连接断开的广播
        broadcastUpdate(ACTION_GATT_DISCONNECTED,status);
    }


    /**
     * 通过广播抛出数据
     */
    private void broadCastError(){
        //关闭超时计时器
        stopTimeOut();
        broadcastUpdate(ACTION_GET_DATA_ERROR);
        sb=new StringBuffer();
    }


    /**
     * 开始计时超时时间
     **/
    private synchronized void startTimeOut() {
        //关闭接收数据的超时计时器
        stopTimeOut();
        //开启监听超时计时器
        handler.postDelayed(dataRunnable,timeOut);
    }

    /**
     * 关闭接收数据的超时计时器
     **/
    public void stopTimeOut() {
        handler.removeCallbacks(dataRunnable);
    }


    /**
     * 开启扫描蓝牙的超时监听
     */
    private void startScanListener() {
        //关闭扫描计时器
        handler.removeCallbacks(scanRunnable);
        handler.postDelayed(scanRunnable,scanTime);
    }


    /**
     * 扫描蓝牙超时监听
     */
    Runnable scanRunnable=new Runnable() {
        public void run() {
            //停止扫描
            stopScan();
            //发送扫描不到该蓝牙设备的广播
            if(!TextUtils.isEmpty(bleName)){
                broadcastUpdate(ACTION_NO_DISCOVERY_BLE);
            }else{
                broadcastUpdate(ACTION_SCAM_DEVICE_END);
            }
        }
    };


    /**
     * 接收数据超时监听
     */
    Runnable dataRunnable=new Runnable() {
        public void run() {
            LogUtils.e("发送超时广播");
            broadcastUpdate(ACTION_INTERACTION_TIMEOUT);
        }
    };

    public int getConnectionState() {
        return connectionState;
    }

    public void setTimeOut(long time){
        timeOut=time;
    }
}
