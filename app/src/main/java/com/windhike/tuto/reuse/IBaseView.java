package com.windhike.tuto.reuse;

import android.content.Context;

/**
 * author: zyongjun on 2017/6/30 0030.
 * email: zhyongjun@windhike.cn
 */

public interface IBaseView {
    Context getViewContext();
    ToolbarBuilder getToolbarBuilder();
    void showWaitingDialog(Context context);
    void dismissOpDialog();
    void hideViewByIds(int... ids);
    void showViewByIds(int... ids);
}