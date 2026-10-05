package com.example.mymovieapp

import android.content.Context

object IntroManager {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LAUNCH_COUNT = "launch_count"

    fun shouldShowIntro(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val count = prefs.getInt(KEY_LAUNCH_COUNT, 0) + 1
        prefs.edit().putInt(KEY_LAUNCH_COUNT, count).apply()
        return count % 3 == 0
    }
}