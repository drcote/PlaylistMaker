package com.drcote.playlistmaker.util

object ClickDebouncer {
    private const val WINDOW_MS = 1000L // 1 сек
    @Volatile private var lastClick = 0L
    fun tryClick(): Boolean {
        val now = System.currentTimeMillis()
        return if (now - lastClick >= WINDOW_MS) {
            lastClick = now; true
        } else false
    }
}