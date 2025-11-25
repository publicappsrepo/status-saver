
package com.appsease.status.saver.model

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.appsease.status.saver.R
import com.appsease.status.saver.extensions.REGEX_BUSINESS
import com.appsease.status.saver.extensions.REGEX_WHATSAPP
import com.appsease.status.saver.extensions.getDrawableCompat
import com.appsease.status.saver.extensions.packageInfo

enum class WaClient(
    val displayName: String,
    val packageName: String,
    private val iconRes: Int,
    val pathRegex: Regex
) {
    WhatsApp(
        "WhatsApp",
        "com.whatsapp",
        R.drawable.icon_wa,
        REGEX_WHATSAPP
    ),
    Business(
        "WhatsApp Business",
        "com.whatsapp.w4b",
        R.drawable.icon_business,
        REGEX_BUSINESS
    );

    fun getIcon(context: Context): Drawable? = context.getDrawableCompat(iconRes)

    fun isInstalled(context: Context): Boolean {
        return try {
            context.packageManager.packageInfo(packageName)
            true
        } catch (ignored: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getLaunchIntent(packageManager: PackageManager): Intent? {
        return packageManager.getLaunchIntentForPackage(packageName)
    }
}