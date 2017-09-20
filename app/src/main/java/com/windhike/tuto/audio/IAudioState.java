package com.windhike.tuto.audio;


/**
 * author: zyongjun on 2017/3/18 0018.
 * email: zhyongjun@windhike.cn
 */

public abstract class IAudioState {
    public IAudioState() {
    }

    public void enter() {
    }

    abstract void handleMessage(AudioStateMessage var1);
}
