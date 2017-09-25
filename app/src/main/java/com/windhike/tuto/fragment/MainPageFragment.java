package com.windhike.tuto.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.windhike.annotation.model.PreferenceConnector;
import com.windhike.fastcoding.CommonFragmentActivity;
import com.windhike.fastcoding.base.BaseFragment;
import com.windhike.tuto.BuildConfig;
import com.windhike.tuto.R;
import com.windhike.fastcoding.widget.PromptManager;
import com.windhike.tuto.widget.FloatButton;
import com.xiaomi.market.sdk.UpdateResponse;
import com.xiaomi.market.sdk.UpdateStatus;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;
import com.xiaomi.market.sdk.XiaomiUpdateListener;
import com.zyongjun.easytouch.service.DrawMenuService;

import butterknife.BindView;

/**
 * author:gzzyj on 2017/7/20 0020.
 * email:zhyongjun@windhike.cn
 */
public class MainPageFragment extends BaseFragment {

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.fbSetting)
    FloatButton fbSetting;
    private PictureAdapter mAdapter;

    @Override
    public int getLayouId() {
        return R.layout.fragment_tab_viewpager;
    }

    private static final String TAG = "MainPageFragment";
    @Override
    public void initView() {
        super.initView();
        checkForUpdate();
        mAdapter = new PictureAdapter(getChildFragmentManager());
        viewpager.setAdapter(mAdapter);
        tabs.setupWithViewPager(viewpager);
        fbSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(),"click_setting");
                CommonFragmentActivity.start(getActivity(),SettingFragment.class.getName(),null);
            }
        });
    }

    private void checkForUpdate() {
        XiaomiUpdateAgent.setUpdateAutoPopup(true);
        XiaomiUpdateAgent.setUpdateListener(new XiaomiUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.STATUS_UPDATE:
                        // 有更新， UpdateResponse为本次更新的详细信息
                        // 其中包含更新信息，下载地址，MD5校验信息等，可自行处理下载安装
                        // 如果希望 SDK继续接管下载安装事宜，可调用
                        XiaomiUpdateAgent.arrange();
                        break;
                    case UpdateStatus.STATUS_NO_UPDATE:
                        // 无更新， UpdateResponse为null
                        break;
                    case UpdateStatus.STATUS_NO_WIFI:
                        // 设置了只在WiFi下更新，且WiFi不可用时， UpdateResponse为null
                        break;
                    case UpdateStatus.STATUS_NO_NET:
                        // 没有网络， UpdateResponse为null
                        break;
                    case UpdateStatus.STATUS_FAILED:
                        // 检查更新与服务器通讯失败，可稍后再试， UpdateResponse为null
                        break;
                    case UpdateStatus.STATUS_LOCAL_APP_FAILED:
                        // 检查更新获取本地安装应用信息失败， UpdateResponse为null
                        break;
                    default:
                        break;
                }
            }
        });
        XiaomiUpdateAgent.update(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if(checkFloatWindowPermission()){
            PreferenceConnector.writeBoolean(getActivity(),PreferenceConnector.KEY_FLOAT_OPENED,true);
            MobclickAgent.onEvent(getActivity(),"open_service");
            getActivity().startService(new Intent(getActivity(), DrawMenuService.class));
        }else{
            showAppSettingPage();
        }
    }

    private void showAppSettingPage() {
        String title = "权限申请";
        String confirm = "去设置";
        String cancel = "取消";
        String appName = getString(R.string.app_name);
        StringBuilder sb = new StringBuilder(String.format("在设置-应用-%s-权限中开启",appName));
        sb.append("允许出现在其他应用上或显示悬浮窗");
        sb.append(",以正常使用本应用");
        PromptManager.getInstance(getActivity()).showDialog(title, sb.toString(), cancel, confirm, new PromptManager.OnClickBtnCallback() {
            @Override
            public void confirmClick() {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse(String.format("package:%s", getActivity().getPackageName())));
                startActivity(intent);
            }

            @Override
            public void cancelClick() {

            }
        });
    }

    public boolean checkFloatWindowPermission(){
        boolean hasPermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //23后的悬浮窗权限属于危险权限 需要手动开启
            if(!Settings.canDrawOverlays(getActivity())) {
                hasPermission = false;
            }
        }else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_DENIED){
            hasPermission = false;
        }
        return hasPermission;
    }

    private static class PictureAdapter extends FragmentPagerAdapter{
        private String[] titles = {"相册","批注"};

        public PictureAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new AlbumListFragment();
            }
            return new AnnotationListFragment();
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

}
