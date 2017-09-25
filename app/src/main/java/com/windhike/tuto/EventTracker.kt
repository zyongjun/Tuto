package com.windhike.tuto

import com.umeng.analytics.MobclickAgent

/**
 * author:gzzyj on 2017/9/25 0025.
 * email:zhyongjun@windhike.cn
 */
object EventTracker{
    fun trackClickAlbum(position:Int) {
        val map = mutableMapOf<String,String>(Pair("position","$position"))
        MobclickAgent.onEvent(TutoApplication.getInstance(), "click_ablum", map)
    }

    fun trackClickAnno(position: Int) {
        val map = mutableMapOf<String,String>(Pair("position","$position"))
        MobclickAgent.onEvent(TutoApplication.getInstance(), "click_anno", map)
    }

    fun trackCreateText(text: String) {
        val map = mutableMapOf<String,String>(Pair("text",text))
        MobclickAgent.onEvent(TutoApplication.getInstance(), "create_text", map)
    }

    fun trackOpenFloat(isOpen: Boolean) {
        val map = mutableMapOf<String,String>(Pair("text","$isOpen"))
        MobclickAgent.onEvent(TutoApplication.getInstance(), "open_float", map)
    }
}