package com.water.project.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.water.project.R;
import com.water.project.activity.menu6.SendDataActivity;
import com.water.project.activity.new_version.MoreSettingActivity;
import com.water.project.activity.new_version.New_SettingActivity;
import com.water.project.adapter.MainMenuAdapter;
import com.water.project.application.MyApplication;
import com.water.project.bean.Menu;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DataCleanManager;
import com.water.project.utils.PermissionCallBack;
import com.water.project.utils.PermissionUtil;
import com.water.project.utils.ble.BleObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class MainActivity extends BaseActivity{

    @BindView(R.id.tv_am_scan)
    TextView tvAmScan;
    @BindView(R.id.listView)
    RecyclerView listView;
    @BindView(R.id.tv_about)
    TextView tvAbout;
    // 按两次退出
    protected long exitTime = 0;
    //存储菜单
    private List<Menu> menuList = new ArrayList<>();
    private MainMenuAdapter mainMenuAdapter;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        //设置菜单数据
        setMenuList();
        //删除缓存
        deleteCache();
        //注册蓝牙服务
        BleObject.getInstance().getBleService(this,null);
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
                    case "数据记录和状态记录":
                        setClass(GetRecordActivity.class);
                        break;
                    case "发送数据":
                        setClass(SendDataActivity.class);
                        break;
                    case "多路实时数据":
                        setClass(GetMoreDataActivity.class);
                         break;
                    case "多路参数设置":
                        setClass(MoreSettingActivity.class);
                         break;
                    default:
                        break;
                }
            }
        });
    }


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
            Menu menu4 = null,menu5=null;
            for (int i=0;i<menuList.size();i++){
                  if(menuList.get(i).getName().equals("数据记录和状态记录")){
                      menu4=menuList.get(i);
                  }
                if(menuList.get(i).getName().equals("发送数据")){
                    menu5=menuList.get(i);
                }
            }
            if (code==1) {
                menuList.remove(menu4);
                menuList.remove(menu5);
            }
            if(code==3){
                menuList.remove(menu4);
                menuList.remove(menu5);
                mainMenuAdapter.setNoClickIndex(2);
            }

            listView.setAdapter(mainMenuAdapter);
            mainMenuAdapter.notifyDataSetChanged();
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
        Menu menu5=new Menu(R.mipmap.record_icon,"数据记录和状态记录");
        menuList.add(menu5);
        Menu menu6=new Menu(R.mipmap.status_icon,"发送数据");
        menuList.add(menu6);
        Menu menu7=new Menu(R.mipmap.data_icon,"多路实时数据");
        menuList.add(menu7);
        Menu menu8=new Menu(R.mipmap.setting_icon,"多路参数设置");
        menuList.add(menu8);
    }


    /**
     * 删除缓存
     */
    private void deleteCache() {
        MyApplication.spUtil.removeAll();
        //清理缓存
        DataCleanManager.clearAllCache(MainActivity.this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        /**
         * 开启权限
         */
        PermissionUtil.getPermission(this, new PermissionCallBack() {
            public void onSuccess() {

            }

            @Override
            public void onFail() {

            }
        });
    }

    // 按两次退出
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showToastView("再按一次退出程序！");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleObject.getInstance().disconnect();
    }
}