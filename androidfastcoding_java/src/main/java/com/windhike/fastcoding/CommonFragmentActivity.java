package com.windhike.fastcoding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.view.WindowManager;
import com.windhike.fastcoding.base.BaseFragment;
import java.util.List;

/**
 * author:gzzyj on 2017/7/14 0014.
 * email:zhyongjun@windhike.cn
 */

public class CommonFragmentActivity extends BaseFragmentActivity {
    public static final String BUNDLE_KEY_NAME = "name";
    public static final String BUNDLE_KEY_ARGS = "args";
    public static final String BUNDLE_KEY_TRANSLUCENT = "BUNDLE_KEY_TRANSLUCENT";
    public static final String BUNDLE_KEY_FULLSCREEN = "BUNDLE_KEY_FULLSCREEN";
    private boolean isTranslucent;

    protected void onLayoutCreateBefore(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null&&intent.getStringExtra(BUNDLE_KEY_NAME)!=null) {
            Bundle args = intent.getBundleExtra(BUNDLE_KEY_ARGS);
            if(args != null&&args.getBoolean(BUNDLE_KEY_FULLSCREEN,false)){
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        onLayoutCreateBefore(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(getLayoutId());
        Fragment fragment;
        if (intent != null&&intent.getStringExtra(BUNDLE_KEY_NAME)!=null) {
            String fragmentName = intent.getStringExtra(BUNDLE_KEY_NAME);
            Bundle args = intent.getBundleExtra(BUNDLE_KEY_ARGS);
            if(args != null){
                isTranslucent = args.getBoolean(BUNDLE_KEY_TRANSLUCENT);
            }
            if (isTranslucent && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();//设置状态栏透明
                localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            }

            fragment = Fragment.instantiate(this,fragmentName,args);
        }else{
            fragment = createContentFragment();
        }
        if(fragment!=null) {
            replaceFragment(fragment);
        }
    }

    protected Fragment createContentFragment() {
        return null;
    }

    public static void start(Context context, String fname, @Nullable Bundle args, int flags){
        try {
            Intent intent = new Intent();
            intent.setClass(context, CommonFragmentActivity.class);
            intent.putExtra(BUNDLE_KEY_NAME, fname);
            intent.putExtra(BUNDLE_KEY_ARGS, args);
            if(flags != -1){
                intent.addFlags(flags);
            }
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void startWithShare(Context context, String fname, @Nullable Bundle args, ActivityOptionsCompat optionsCompat){
        try {
            Intent intent = new Intent();
            intent.setClass(context, CommonFragmentActivity.class);
            intent.putExtra(BUNDLE_KEY_NAME, fname);
            intent.putExtra(BUNDLE_KEY_ARGS, args);
//            if(flags != -1){
//                intent.addFlags(flags);
//            }
//            context.startActivity(intent);
            ActivityCompat.startActivity(context, intent, optionsCompat.toBundle());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void start(Context context,String fname, Bundle args) {
        start(context,fname,args,-1);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        @SuppressLint("RestrictedApi") List<Fragment> fragments = fm.getFragments();
        if(fragments != null && fragments.size() > 0 && fragments.get(0) instanceof BaseFragment){
            BaseFragment fragment = (BaseFragment)fragments.get(0);
            if(!fragment.onBackPressed()){
                super.onBackPressed();
            }
        }else {
            super.onBackPressed();
        }
    }
}
