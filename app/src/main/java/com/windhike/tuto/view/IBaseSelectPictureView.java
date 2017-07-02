package com.windhike.tuto.view;

import com.windhike.tuto.reuse.IBaseView;

/**
 * author: zyongjun on 2017/7/1 0001.
 * email: zhyongjun@windhike.cn
 */

public interface IBaseSelectPictureView extends IBaseView {
    void notifyDataSetChanged();
    void sendEmptyMessage(int what);
}
