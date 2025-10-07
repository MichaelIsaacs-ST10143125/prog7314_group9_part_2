package com.example.links_app

import android.content.Context

object PrefsHelper {
    private const val PREFS = "current_user_prefs"
    private const val KEY_UID = "uid"
    private const val KEY_NAME = "name"
    private const val KEY_USERNAME = "username"
    private const val KEY_EMAIL = "email"

    fun saveCurrentUser(ctx: Context, user: User) {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        p.putString(KEY_UID, user.user_id)
        p.putString(KEY_NAME, user.name)
        p.putString(KEY_USERNAME, user.username)
        p.putString(KEY_EMAIL, user.email)
        p.apply()
    }

    fun clear(ctx: Context) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun getCurrentUser(ctx: Context): User? {
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val uid = prefs.getString(KEY_UID, null) ?: return null
        val name = prefs.getString(KEY_NAME, "") ?: ""
        val username = prefs.getString(KEY_USERNAME, "") ?: ""
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        return User(uid, name, username, email)
    }
}