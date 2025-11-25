
package com.appsease.status.saver.storage

import android.os.Environment


class StorageDevice(val path: String?, val name: String, private val state: String, val isPrimary: Boolean) {

    val isValid: Boolean
        get() = state == Environment.MEDIA_MOUNTED && !path.isNullOrEmpty()

    override fun toString(): String {
        return "StorageDevice{path='$path', name='$name', state=$state, isPrimary=$isPrimary}"
    }
}