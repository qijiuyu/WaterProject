package com.water.project.utils;

import android.app.Activity;
import android.os.Handler;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

public class PermissionUtil {

    /**
     * getPermission 动态获取权限方法
     *
     * @param context 上下文
     */
    public static void getPermission(final Activity context, final PermissionCallBack permissionCallBack){
        if (XXPermissions.isHasPermission(context,
                //所需危险权限可以在此处添加：
                Permission.READ_PHONE_STATE,
                Permission.WRITE_EXTERNAL_STORAGE,
                Permission.READ_EXTERNAL_STORAGE,
                Permission.ACCESS_FINE_LOCATION,
                Permission.ACCESS_COARSE_LOCATION)){
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    permissionCallBack.onSuccess();
                }
            },2000);
        }else {
            XXPermissions.with(context).permission(
                    //同时在此处添加：
                    Permission.READ_PHONE_STATE,
                    Permission.WRITE_EXTERNAL_STORAGE,
                    Permission.READ_EXTERNAL_STORAGE,
                    Permission.ACCESS_FINE_LOCATION,
                    Permission.ACCESS_COARSE_LOCATION
            ).request(new OnPermission() {
                @Override
                public void noPermission(List<String> denied, boolean quick) {
                    permissionCallBack.onSuccess();
                }

                @Override
                public void hasPermission(List<String> granted, boolean isAll) {
                    if (isAll) {//获取成功
                        permissionCallBack.onSuccess();
                    }
                }
            });
        }
    }


    /**
     * 判断某个权限是否已申请
     */
    public static boolean isPermission(Activity context, final PermissionCallBack permissionCallBack, String... permissions){
        if (XXPermissions.isHasPermission(context, permissions)) {
            if(permissionCallBack!=null){
                permissionCallBack.onSuccess();
            }
            return true;
        }else{
            if(permissionCallBack!=null){
                permissionCallBack.onFail();
                //申请某个权限
                applyPermission(context,permissionCallBack,permissions);
            }
            return false;
        }
    }


    /**
     * 申请某个权限
     * @param permissions
     */
    public static void applyPermission(Activity context, final PermissionCallBack permissionCallBack, String... permissions){
        XXPermissions.with(context)
                .permission(permissions)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if(isAll){
                            permissionCallBack.onSuccess();
                        }else{
                            permissionCallBack.onFail();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        permissionCallBack.onFail();
                    }
                });
    }
}
