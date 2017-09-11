package com.windhike.fastcoding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * author:gzzyj on 2017/9/11 0011.
 * email:zhyongjun@windhike.cn
 */

public class FadeFragmentActivity extends CommonFragmentActivity{

    public static void start(Context context, String fname, @Nullable Bundle args, int flags){
        try {
            Intent intent = new Intent();
            intent.setClass(context, FadeFragmentActivity.class);
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

    public static void start(Context context,String fname, Bundle args) {
        start(context,fname,args,-1);
    }
}
