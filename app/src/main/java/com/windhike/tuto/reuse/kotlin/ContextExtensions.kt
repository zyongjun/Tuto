package cn.ibos.extensions

import android.content.Context
import android.widget.Toast

/**
 * author: gzzyj on 2017/2/7.
 * email:zhyongjun@windhike.cn
 */
fun Context.showToast(text:String){
    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
}

fun Context.showToast(sRid:Int){
    Toast.makeText(this,getString(sRid),Toast.LENGTH_SHORT).show()
}