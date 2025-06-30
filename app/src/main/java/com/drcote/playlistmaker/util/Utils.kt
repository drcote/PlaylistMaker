package com.drcote.playlistmaker.util

import android.content.res.Resources
import android.util.DisplayMetrics

fun dpToPixel(dp: Float): Int {
    val metrics: DisplayMetrics = Resources.getSystem().displayMetrics
    return (dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT.toFloat())).toInt()
}