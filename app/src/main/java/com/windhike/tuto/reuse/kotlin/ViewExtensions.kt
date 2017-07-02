package com.windhike.tuto.reuse.kotlin

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View

/**
 * author: gzzyj on 2017/2/6.
 * email:zhyongjun@windhike.cn
 */
//fun View.setCompatBackgroud(drawable: Drawable){
//
//}

val View.ctx: Context
        get() = context

val Dialog.ctx: Context
    get() = context
