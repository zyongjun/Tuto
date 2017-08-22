package com.windhike.tuto.event

import android.net.Uri

/**
 * author: gzzyj on 2017/4/20.
 * email:zhyongjun@windhike.cn
 */
data class AudioRecordStopEvent(val audioPath: Uri)
data class AnnotationChangedEvent(val des:String)
data class AnnotationShareResult(val code:String)

