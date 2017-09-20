package com.windhike.tuto.audio;

/**
 * Created by Administrator on 2017/7/2 0002.
 */

public class AudioStateMessage {
    public int what;
    public Object obj;

    public AudioStateMessage() {
    }

    public static AudioStateMessage obtain() {
        return new AudioStateMessage();
    }
}
