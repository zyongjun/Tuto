package cn.ibos.extensions

import android.util.Log

/**
 * author: gzzyj on 2017/2/7.
 * email:zhyongjun@windhike.cn
 */
inline fun <reified T> T.le(s:String){
    Log.e(T::class.simpleName.toString(),s)
}