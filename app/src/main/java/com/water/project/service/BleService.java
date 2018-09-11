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
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import com.water.project.activity.CeshiActivity;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.TimerUtil;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * 蓝牙Service
 */
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
     * 数据交互超时
     */
    public final static String ACTION_INTERACTION_TIMEOUT = "net.zkgd.adminapp.ACTION_INTERACTION_TIMEOUT";

    //发送数据失败
    public final static String ACTION_SEND_DATA_FAIL = "net.zkgd.adminapp.ACTION_SEND_DATA_FAIL";


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
    private long timeOut = 1000 * 8, scanTime = 1000 * 10;
    private TimerUtil timerUtil, startUtil;
    private Handler handler = new Handler();
    //蓝牙名称
    private String bleName;
    //接收回执的数据，进行拼接
    private StringBuffer sb;
    //根据类型发送不同的回执广播
    private int type;

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
    public void scanDevice(String bleName) {
        if (mBluetoothAdapter == null) {
            return;
        }
        this.bleName=bleName;
        //先关闭扫描
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        LogUtils.e("开始扫描蓝牙");
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        //开启扫描蓝牙计时器
        startUtil();
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if(null==device){
                return;
            }
            if(TextUtils.isEmpty(device.getName())){
                return;
            }
            LogUtils.e("搜索到蓝牙：" + device.getName() + "___" + device.getAddress());
            if(TextUtils.isEmpty(bleName)){
                intent.setAction(ACTION_SCAN_SUCCESS);
                intent.putExtra("bleName",device.getName());
                intent.putExtra("bleMac",device.getAddress());
                sendBroadcast(intent);
            }else{
                final Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
                if(null!=ble){
                    if(ble.getBleName().equals(device.getName())){
                        //停止扫描
                        stopScan(mLeScanCallback);
                        //关闭扫描计时器
                        startUtil.stop();
                        //连接蓝牙
                        handler.post(new Runnable() {
                            public void run() {
                                connect(device.getAddress());
                            }
                        });
                    }
                }
            }

        }
    };


    /**
     * 停止蓝牙扫描
     */
    private void stopScan(BluetoothAdapter.LeScanCallback mLeScanCallback){
        if(null!=mBluetoothAdapter){
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    /**
     * 扫描10秒钟
     */
    private void startUtil() {
        startUtil = new TimerUtil(scanTime, new TimerUtil.TimerCallBack() {
            public void onFulfill() {
                //停止扫描
                stopScan(mLeScanCallback);
                //关闭扫描计时器
                startUtil.stop();
                //发送扫描不到该蓝牙设备的广播
                if(!TextUtils.isEmpty(bleName)){
                    broadcastUpdate(ACTION_NO_DISCOVERY_BLE);
                }
            }
        });
        startUtil.start();
    }

    /**
     * 连接指定蓝牙
     * @param address 蓝牙的地址
     */
    public boolean connect(final String address) {
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
    public void enableTXNotification() {
        try {
            if (mBluetoothGatt == null) {
                LogUtils.e("初始化通道错误:mBluetoothGatt=====null");
                disconnect();
                return;
            }
            BluetoothGattService RxService = mBluetoothGatt.getService(RX_SERVICE_UUID);
            if (RxService == null) {
                LogUtils.e("初始化通道错误:BluetoothGattService=====null");
                disconnect();
                return;
            }
            BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(TX_CHAR_UUID);
            if (TxChar == null) {
                LogUtils.e("初始化通道错误:BluetoothGattCharacteristic=====null");
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
    public boolean writeRXCharacteristic(List<String> list, final int type) {
        sb=new StringBuffer();
        this.type=type;
        isSuccess=true;
        try {
            BluetoothGattService RxService = mBluetoothGatt.getService(RX_SERVICE_UUID);
            if (RxService == null) {
                LogUtils.e("传输数据：BluetoothGattService==null");
                disconnect();
                return false;
            }
            final BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(RX_CHAR_UUID);
            if (RxChar == null) {
                disconnect();
                LogUtils.e("传输数据：BluetoothGattCharacteristic==null");
                return false;
            }

            StringBuffer stringBuffer=new StringBuffer();
            for (int i=0;i<list.size();i++){
                stringBuffer.append(list.get(i));
            }
            LogUtils.e("发送的命令是："+stringBuffer.toString());
            //循环发送数据
            for (int i=0;i<list.size();i++){
                   RxChar.setValue(list.get(i).getBytes());
                   //开启超时计时器
                   startTimeOut();
                  //下发命令
                  boolean b=mBluetoothGatt.writeCharacteristic(RxChar);
                  if(!b){
                    isSuccess=false;
                    break;
                  }
                  //延时5毫秒
                  new Thread().sleep(5);
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
                broadcastUpdate(ACTION_GATT_DISCONNECTED,status);
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
            LogUtils.e("接收到的数据是："+data);
            if(data.contains("GD")){
                sb.append(data);
                if(data.contains("OK")){
                    //关闭超时计时器
                    stopTimeOut();
                    if(type==1){
                        LogUtils.e("回执的完整数据是："+sb.toString());
                        broadcastUpdate(ACTION_DATA_AVAILABLE, sb.toString().replace(">OK",""));
                    }else{
                        LogUtils.e("回执的完整数据是："+sb.toString());
                        broadcastUpdate(ACTION_DATA_AVAILABLE2, sb.toString().replace(">OK",""));
                    }
                }
                return;
            }

            if(sb.length()>0){
                sb.append(data);
                if(data.contains("OK")){
                    //关闭超时计时器
                    stopTimeOut();
                    if(type==1){
                        LogUtils.e("回执的完整数据是："+sb.toString());
                        broadcastUpdate(ACTION_DATA_AVAILABLE, sb.toString().replace(">OK",""));
                    }else{
                        LogUtils.e("回执的完整数据是："+sb.toString());
                        broadcastUpdate(ACTION_DATA_AVAILABLE2, sb.toString().replace(">OK",""));
                    }
                }else if(sb.toString().contains("GDCURRENT")){
                    if(data.contains(";")){
                        //关闭超时计时器
                        stopTimeOut();
                        if(type==1){
                            LogUtils.e("回执的完整数据是："+sb.toString());
                            broadcastUpdate(ACTION_DATA_AVAILABLE, sb.toString());
                        }else{
                            LogUtils.e("回执的完整数据是："+sb.toString());
                            broadcastUpdate(ACTION_DATA_AVAILABLE2, sb.toString());
                        }
                    }
                }
            }
        }
    };


    /**
     * 开始计时超时时间
     **/
    private synchronized void startTimeOut() {
        stopTimeOut();
        timerUtil = new TimerUtil(timeOut, new TimerUtil.TimerCallBack() {
            public void onFulfill() {
                LogUtils.e("发送超时广播");
                broadcastUpdate(ACTION_INTERACTION_TIMEOUT);
            }
        });
        timerUtil.start();
    }

    /**
     * 发送命令超时计时
     **/
    public void stopTimeOut() {
        if (timerUtil != null) {
            timerUtil.stop();
        }
    }


    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }

    public int getConnectionState() {
        return connectionState;
    }

    /**
     * 设置数据交互超时时间，默认10s
     */
    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }
}
