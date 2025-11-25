
package com.appsease.status.saver.extensions

import android.net.Uri
import androidx.core.content.FileProvider
import com.appsease.status.saver.App
import com.appsease.status.saver.getApp
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.math.log10
import kotlin.math.pow

fun File.canonicalOrAbsolutePath(): String {
    val canonical = runCatching { this.canonicalPath }
    if (canonical.isFailure) {
        return absolutePath
    }
    return canonical.getOrThrow()
}

fun File.getUri(): Uri = FileProvider.getUriForFile(getApp().applicationContext, App.getFileProviderAuthority(), this)

fun Long.hasElapsedTwentyFourHours(): Boolean {
    return (System.currentTimeMillis() - this) >= TimeUnit.HOURS.toMillis(24L)
}

fun File.isOldFile() = lastModified().hasElapsedTwentyFourHours()

fun Long.toFileSize(): String {
    if (this <= 0) {
        return "0 bytes"
    }
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    return String.format(
        "%s %s",
        DecimalFormat("#,##0.#").format(this / 1024.0.pow(digitGroups.toDouble())),
        units[digitGroups]
    )
}