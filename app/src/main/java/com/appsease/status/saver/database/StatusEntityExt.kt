package com.appsease.status.saver.database

import android.net.Uri
import com.appsease.status.saver.model.SavedStatus
import com.appsease.status.saver.model.Status

private fun Status.getSaveName(i: String?, timeMillis: Long, delta: Int = 0): String {
    var saveName = i
    if (saveName.isNullOrBlank()) {
        return type.getDefaultSaveName(timeMillis, delta)
    }
    if (!saveName.endsWith(type.format)) {
        saveName += type.format
    }
    while (saveName!!.startsWith(".")) {
        saveName = saveName.drop(1)
    }
    return saveName
}

fun Status.toStatusEntity(
    saveName: String?,
    timeMillis: Long = System.currentTimeMillis(),
    delta: Int = 0
) = StatusEntity(
    type = type,
    name = name,
    origin = fileUri,
    dateModified = dateModified,
    size = size,
    client = clientPackage,
    saveName = getSaveName(saveName, timeMillis, delta)
)

fun StatusEntity.toSavedStatus(uri: Uri, path: String?) = SavedStatus(
    type = type,
    name = name!!,
    fileUri = uri,
    dateModified = dateModified,
    size = size,
    path = path
)