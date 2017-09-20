package com.windhike.tuto.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import com.windhike.fastcoding.base.BaseFragment;
import com.windhike.tuto.R;
import com.windhike.fastcoding.widget.PromptManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * author:gzzyj on 2017/7/18 0018.
 * email:zhyongjun@windhike.cn
 */
public abstract class BasePermissionFragment extends BaseFragment {
    private  static final int REQUEST_PERMISSION_CODE = 1024;

    protected void onGranted() {

    }

    /**
     * 小米上跳设置页面时候自动调用MIUI的授权页面，拒绝后才会弹窗
     *
     * read phone state权限为什么自动拥有
     * - TODO
     * 弹窗需要处理调用MUI的授权页面
     * @param lackedPermission
     */
    protected void onDenied(final List<String> lackedPermission) {
        HashMap<String,String> permissionMap = new HashMap();
        permissionMap.put(Manifest.permission.RECORD_AUDIO,"麦克风");
        permissionMap.put(Manifest.permission.CAMERA,"相机");
        permissionMap.put(Manifest.permission.READ_PHONE_STATE,"电话");
        permissionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,"存储空间");
        permissionMap.put(Manifest.permission.ACCESS_FINE_LOCATION,"定位");
        permissionMap.put(Manifest.permission.SYSTEM_ALERT_WINDOW,"打开悬浮窗");
        String title = "权限申请";
        String confirm = "去设置";
        String cancel = "取消";
        String appName = getString(R.string.app_name);
        StringBuilder sb = new StringBuilder(String.format("在设置-应用-%s-权限中开启",appName));
        boolean flag = false;
        String join = "、";
        for(String permissionName:lackedPermission){
            if (flag) {
                sb.append(join);
            }
            flag=true;
            sb.append(permissionMap.get(permissionName));
        }
        sb.append("权限,以正常使用本应用");
        PromptManager.getInstance(getActivity()).showDialog(title, sb.toString(), cancel, confirm, new PromptManager.OnClickBtnCallback() {
            @Override
            public void confirmClick() {
//                Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
//                startActivity(intent);
//                if ((lackedPermission.size() ==1) && lackedPermission.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
//                    startActivity(intent);
//                }else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                    startActivity(intent);
//                }
                getActivity().finish();
            }

            @Override
            public void cancelClick() {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private List<String> getLackedPermissions(String ...permissions) {
        List<String> lackedPermission = new ArrayList();
        Context context = getContext();
        for(String permission:permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(),permission) != PackageManager.PERMISSION_GRANTED) {
                lackedPermission.add(permission);
            }
        }
        return lackedPermission;
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void checkAndRequestPermission(String ...permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onGranted();
        }
        if (permissions.length == 0) {
            onGranted();
            return;
        }
        List<String> lackedPermission = getLackedPermissions(permissions);
        if (lackedPermission.size() == 0) {
            onGranted();
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, REQUEST_PERMISSION_CODE);
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if(hasAllPermissionsGranted(grantResults)) {
                onGranted();
            }else{
                onDenied(getLackedPermissions(permissions));
            }
        }
    }

}
