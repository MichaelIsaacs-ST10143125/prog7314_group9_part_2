package com.example.links_app.utils

import android.content.Context
import android.graphics.Color

fun getMessageColor(context: Context): Int {
    val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    return prefs.getInt("message_color", Color.parseColor("#1DB453")) // default green
}
