package com.windhike.tuto.view;


import com.windhike.tuto.reuse.IBaseView;

/**
 * author: zyongjun on 2017/3/12 0012.
 * email: zhyongjun@windhike.cn
 */

public interface IBaseRecordsView extends IBaseView {
    void updateEmptyView(boolean isVisible);
    void notifyDataChanged();
}
