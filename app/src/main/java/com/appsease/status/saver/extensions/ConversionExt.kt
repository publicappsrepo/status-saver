
package com.appsease.status.saver.extensions

import org.ocpsoft.prettytime.PrettyTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

fun Long.time(maxPrettyTime: Long = 1, maxPrettyTimeUnit: TimeUnit = TimeUnit.HOURS, useTimeFormat: Boolean = false): String {
    val date = Date(this)
    val minElapsedHours = maxPrettyTimeUnit.toMillis(maxPrettyTime)
    if ((System.currentTimeMillis() - this) >= minElapsedHours) {
        val dateFormat = if (useTimeFormat) {
            SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        } else {
            SimpleDateFormat.getDateInstance(DateFormat.SHORT)
        }
        return dateFormat.format(date)
    }
    return prettyTime()
}

fun Long.prettyTime(): String = PrettyTime().format(Date(this))

inline fun <reified T : Enum<T>> String.toEnum() =
    enumValues<T>().firstOrNull { it.name.equals(this, ignoreCase = true) }