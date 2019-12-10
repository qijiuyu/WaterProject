package com.water.project.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.water.project.R;
import com.water.project.activity.new_version.New_SettingActivity;
import com.water.project.adapter.MainMenuAdapter;
import com.water.project.application.MyApplication;
import com.water.project.bean.Menu;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ble.SendBleDataManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class MainActivity extends BaseActivity{

    @BindView(R.id.tv_am_scan)
    TextView tvAmScan;
    @BindView(R.id.listView)
    RecyclerView listView;
    @BindView(R.id.tv_about)
    TextView tvAbout;
    // 按两次退出
    protected long exitTime = 0;
    //蓝牙参数
    public static BleService bleService = null;
    public static BluetoothAdapter mBtAdapter = null;
    //存储菜单
    private List<Menu> menuList = new ArrayList<>();
    private MainMenuAdapter mainMenuAdapter;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (MyApplication.spUtil.getString("stopAPP").equals("12345water54321")) {
            System.exit(0);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        //设置菜单数据
        setMenuList();
        //删除缓存
        deleteCache();
        initService();//注册蓝牙服务
        setPush();
    }


    /**
     * 初始化控件
     */
    private void initView() {
        tvAbout.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this, 2);
        listView.setLayoutManager(gridLayoutManager);
        mainMenuAdapter=new MainMenuAdapter(this, menuList, new MainMenuAdapter.OnItemClickListener() {
            public void onItemClick(int position) {
                final String menuName=menuList.get(position).getName();
                switch (menuName){
                    case "实时数据":
                        setClass(GetDataActivity.class);
                        break;
                    case "校测水位数据":
                        setClass(CheckActivity.class);
                        break;
                    case "网络连接设置":
                        setClass(NetSettingActivity.class);
                        break;
                    case "参数设置":
                        final int code = BleUtils.getVersion(MainActivity.this);
                        if (code == 2 || code==3) {
                            setClass(New_SettingActivity.class);
                        } else {
                            setClass(SettingActivity.class);
                        }
                        break;
                    case "读取设备记录":
                        setClass(GetRecordActivity.class);
                        break;
                    case "发送数据":
                        setClass(SendDataActivity.class);
                        break;
                    default:
                        break;
                }
            }
        });

    }


    /**
     * 打开蓝牙service
     */
    private void initService() {
        Intent bindIntent = new Intent(this, BleService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            bleService = ((BleService.LocalBinder) rawBinder).getService();
            mBtAdapter = bleService.createBluetoothAdapter();
            SendBleDataManager.getInstance().init(bleService);
            //判断蓝牙是否打开
            BleUtils.isEnabled(MainActivity.this, mBtAdapter);
        }

        public void onServiceDisconnected(ComponentName classname) {
        }
    };


    @OnClick({R.id.tv_am_scan, R.id.tv_about})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //扫描蓝牙
            case R.id.tv_am_scan:
                Intent intent = new Intent(this, SearchBleActivity.class);
                startActivityForResult(intent, 0x001);
                break;
            //关于我们
            case R.id.tv_about:
                setClass(AboutActivity.class);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0x001) {
            //读取版本信息成功
            final int code = BleUtils.getVersion(MainActivity.this);
            if (code==1) {
                menuList.remove(menuList.get(4));
                menuList.remove(menuList.get(5));
            }
            if(code==3){
                menuList.remove(menuList.get(4));
                menuList.remove(menuList.get(5));
                mainMenuAdapter.setNoClickIndex(2);
                mainMenuAdapter.notifyDataSetChanged();
            }
            listView.setAdapter(mainMenuAdapter);
        }
    }


    /**
     * 设置菜单数据
     */
    private void setMenuList() {
        Menu menu=new Menu(R.mipmap.data_icon,"实时数据");
        menuList.add(menu);
        Menu menu2=new Menu(R.mipmap.check_icon,"校测水位数据");
        menuList.add(menu2);
        Menu menu3=new Menu(R.mipmap.net_icon,"网络连接设置");
        menuList.add(menu3);
        Menu menu4=new Menu(R.mipmap.setting_icon,"参数设置");
        menuList.add(menu4);
        Menu menu5=new Menu(R.mipmap.record_icon,"读取设备记录");
        menuList.add(menu5);
        Menu menu6=new Menu(R.mipmap.status_icon,"发送数据");
        menuList.add(menu6);
    }


    /**
     * 删除缓存
     */
    private void deleteCache() {
        MyApplication.spUtil.removeAll();
    }

    // 按两次退出
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showToastView("再按一次退出程序！");
                exitTime = System.currentTimeMillis();
            } else {
                bleService.disconnect();
                try {
                    unbindService(mServiceConnection);
                } catch (Exception e) {

                }
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 200);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 设置推送
     */
    private void setPush() {
        //设置极光推送的别名
        Set<String> tags = new HashSet<>();
        tags.add("com.water.project");
        JPushInterface.setAliasAndTags(getApplicationContext(), "com.water.project", tags, mAliasCallback);
    }


    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        public void gotResult(int code, String alias, Set<String> tags) {
            switch (code) {
                //设置别名成功
                case 0:
                    LogUtils.e("推送设置成功");
                    break;
                //设置别名失败
                case 6002:
                    LogUtils.e("推送设置失败");
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            setPush();
                        }
                    }, 30000);
                    break;
                default:
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != bleService) {
            bleService.disconnect();
        }
    }

}