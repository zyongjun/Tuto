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
import android.view.View;
import com.windhike.annotation.model.PreferenceConnector;
import com.windhike.fastcoding.CommonFragmentActivity;
import com.windhike.fastcoding.base.BaseFragment;
import com.windhike.tuto.R;
import com.windhike.fastcoding.widget.PromptManager;
import com.windhike.tuto.widget.FloatButton;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;
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

    @Override
    public void initView() {
        super.initView();
        XiaomiUpdateAgent.update(getActivity(),true);
        mAdapter = new PictureAdapter(getChildFragmentManager());
        viewpager.setAdapter(mAdapter);
        tabs.setupWithViewPager(viewpager);
        fbSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFragmentActivity.start(getActivity(),SettingFragment.class.getName(),null);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(checkFloatWindowPermission()){
            PreferenceConnector.writeBoolean(getActivity(),PreferenceConnector.KEY_FLOAT_OPENED,true);
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
