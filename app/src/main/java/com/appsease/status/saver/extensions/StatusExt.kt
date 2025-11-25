
package com.appsease.status.saver.extensions

import android.content.Context
import com.appsease.status.saver.R
import com.appsease.status.saver.model.Status
import com.appsease.status.saver.model.StatusType
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val fileDateFormat: DateFormat by lazy {
    SimpleDateFormat("MMM_d_yyyy_HH.mm.ss", Locale.ENGLISH)
}

fun Status.getFormattedDate(context: Context): String {
    val date = Date(dateModified)
    val resLocale = context.resources.configuration.locales[0] ?: Locale.US
    return DateFormat.getDateInstance(DateFormat.MEDIUM, resLocale).format(date)
}

/**
 * Generates and returns a new save name depending on the
 * given [StatusType] format and the current time.
 */
fun getNewSaveName(type: StatusType? = null, timeMillis: Long, delta: Int): String {
    var saveName = String.format("Status_%s", fileDateFormat.format(Date(timeMillis)))
    if (delta > 0) {
        saveName += "-$delta"
    }
    if (type != null) {
        saveName += type.format
    }
    return saveName
}

fun Status.getState(context: Context): String =
    if (isSaved) context.getString(R.string.status_saved) else context.getString(R.string.status_unsaved)

fun StatusType.acceptFileName(fileName: String): Boolean = !fileName.startsWith(".") && fileName.endsWith(this.format)

fun File.getStatusType() = StatusType.entries.firstOrNull { it.acceptFileName(name) }