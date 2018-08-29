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
import com.water.project.utils.ByteStringHexUtil;
import com.water.project.utils.LogUtils;
import com.water.project.utils.TimerUtil;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * 蓝牙Service
 */
public class BleService extends Service {
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_SERVICE_UUID = UUID.fromString("0000ff12-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_CHAR_UUID = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
    public static final UUID TX_CHAR_UUID = UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb");

    /**
     * 蓝牙连接成功
     */
    public final static String ACTION_GATT_CONNECTED =
            "net.zkgd.adminapp.ACTION_GATT_CONNECTED";
    /**
     * 断开连接
     */
    public final static String ACTION_GATT_DISCONNECTED =
            "net.zkgd.adminapp.ACTION_GATT_DISCONNECTED";
    /**
     * 接收到了数据
     */
    public final static String ACTION_DATA_AVAILABLE =
            "net.zkgd.adminapp.ACTION_DATA_AVAILABLE";
    /**
     * 发送接到的数据的KEY
     */
    public final static String ACTION_EXTRA_DATA =
            "net.zkgd.adminapp.EXTRA_DATA";
    /**
     * 通道建立成功
     */
    public final static String ACTION_ENABLE_NOTIFICATION_SUCCES =
            "net.zkgd.adminapp.enablenotificationsucces";
    /**
     * 没有发现指定蓝牙
     */
    public final static String ACTION_NO_DISCOVERY_BLE =
            "net.zkgd.adminapp.ACTION_NO_DISCOVERY_BLE";
    /**
     * 数据交互超时
     */
    public final static String ACTION_INTERACTION_TIMEOUT =
            "net.zkgd.adminapp.ACTION_INTERACTION_TIMEOUT";

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
    //蓝牙名称
    private String bleName;
    //timeOut：发送命令超时         scanTime:扫描蓝牙超时
    private long timeOut = 1000 * 10, scanTime = 1000 * 10;
    private TimerUtil timerUtil, startUtil;
    private Handler handler = new Handler();

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
     * 扫描并且连接
     *
     * @param bleName 蓝牙名
     */
    public void connectScan(String bleName) {
        if (mBluetoothAdapter == null || TextUtils.isEmpty(bleName)) {
            return;
        }
        this.bleName = bleName;
        stopStartTime();
        //先关闭扫描
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        LogUtils.e("开始扫描蓝牙");
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        //开始扫描蓝牙
        startUtil();
        return;
    }


    private Handler mHandler=new Handler();
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if(null==device){
                return;
            }
            if(null==device.getName()){
                return;
            }
            LogUtils.e("搜索到蓝牙：" + device.getName() + "___" + device.getAddress());
            //BLE Device-3D10C8
            //BLE Device-3D1661
            if ("BLE Device-3D10C8".equals(device.getName())) {
                //关闭扫描计时器
                stopStartTime();
                //停止扫描
                stopScan(mLeScanCallback);
                mHandler.post(new Runnable() {
                    public void run() {
                        //连接
                        connect(device.getAddress());
                    }
                });
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
                stopStartTime();
                broadcastUpdate(ACTION_NO_DISCOVERY_BLE);
            }
        });
        startUtil.start();
    }

    /**
     * 连接指定蓝牙
     *
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
            //建立通道失败，发送没有找到蓝牙广播
            broadcastUpdate(ACTION_NO_DISCOVERY_BLE);
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
     * 读取数据
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtils.e("读取数据:mBluetoothAdapter===null");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }


    /**
     * 传输数据
     */
    public boolean writeRXCharacteristic(String[] msg, boolean isTimeOut) {
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
            final Date curDate = new Date();
            for (int i=0;i<msg.length;i++){
                RxChar.setValue(msg[i].getBytes());
                Thread.sleep(CeshiActivity.time);
                boolean is=mBluetoothGatt.writeCharacteristic(RxChar);
                LogUtils.e("is="+is+"___"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.CHINA).format(curDate));
//                mHandler.postDelayed(new Runnable() {
//                    public void run() {
//
//                    }
//                },10);
            }
            return true;
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
    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        if (TX_CHAR_UUID.equals(characteristic.getUuid())) {
            intent.putExtra(ACTION_EXTRA_DATA, characteristic.getValue());
        }
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
                }, 1000);
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
            } else {
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
                LogUtils.e("onServicesDiscovered 返回状态:" + status);
            }
        }

        //接收数据
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            LogUtils.e(characteristic.getUuid().toString()+".....................");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] txValue = characteristic.getValue();
                if (null != txValue && txValue.length > 0) {
                    stopTimeOut();
                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                }
            }
        }

        //接收数据
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic) {
            LogUtils.e(characteristic.getUuid().toString()+"++++++++++++++");
            String str=characteristic.getStringValue(0);
            LogUtils.e(str+"______________");
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };


    /**
     * 开始计时超时时间
     **/
    private synchronized void startTimeOut() {
        LogUtils.e("开启计时了");
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

    /**
     * 扫描超时计时器
     */
    private void stopStartTime() {
        if (null != startUtil) {
            startUtil.stop();
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
