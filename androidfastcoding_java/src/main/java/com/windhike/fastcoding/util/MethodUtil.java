package com.windhike.fastcoding.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * author:gzzyj on 2017/9/8 0008.
 * email:zhyongjun@windhike.cn
 */

public class MethodUtil {

    public static void close(Closeable closeable) {
        try {
            if(closeable!=null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
