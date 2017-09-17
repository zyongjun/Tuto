package com.windhike.tuto.reuse;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionSet;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.util.AttributeSet;

/**
 * author:gzzyj on 2017/9/17 0017.
 * email:zhyongjun@windhike.cn
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DetailTransition extends TransitionSet {
    public DetailTransition() {
        init();
    }

    // 允许资源文件使用
    public DetailTransition(Context context, AttributeSet attrs) {
        super();
        init();
    }

    private void init() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds());
//                addTransition(new ChangeTransform()).
//                addTransition(new ChangeImageTransform());
    }
}


