
package com.appsease.status.saver.storage

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import androidx.core.content.edit
import androidx.core.content.getSystemService
import com.appsease.status.saver.extensions.PREFERENCE_STATUSES_LOCATION
import com.appsease.status.saver.extensions.hasR
import com.appsease.status.saver.extensions.preferences
import com.appsease.status.saver.getApp
import java.lang.reflect.InvocationTargetException

@SuppressLint("DiscouragedPrivateApi")
class Storage(context: Context) {

    private val preferences = context.preferences()
    private val storageManager = context.getSystemService<StorageManager>()!!

    val externalStoragePath: String
        get() = Environment.getExternalStorageDirectory().absolutePath

    val storageVolumes: List<StorageDevice> by lazy {
        arrayListOf<StorageDevice>().also { newList ->
            try {
                for (sv in storageManager.storageVolumes) {
                    newList.add(
                        StorageDevice(
                            sv.getPath(),
                            sv.getDescription(getApp().applicationContext),
                            sv.state,
                            sv.isPrimary
                        )
                    )
                }
            } catch (t: Throwable) {
                t.message
            }
        }
    }

    val primaryStorageDevice: StorageDevice? by lazy {
        storageVolumes.firstOrNull { it.isPrimary }
    }

    private fun getStorageVolume(path: String): StorageDevice? {
        return storageVolumes.filterNot { it.path == null }.firstOrNull { it.path == path }
    }

    fun getStatusesLocation(): StorageDevice? {
        return preferences.getString(PREFERENCE_STATUSES_LOCATION, null)
            ?.let { getStorageVolume(it) }
    }

    fun setStatusesLocation(storageVolume: StorageDevice) {
        val devicePath = storageVolume.path
        preferences.edit {
            if (devicePath.isNullOrEmpty())
                remove(PREFERENCE_STATUSES_LOCATION)
            else putString(PREFERENCE_STATUSES_LOCATION, devicePath)
        }
    }

    fun isStatusesLocation(storageVolume: StorageDevice): Boolean {
        return getStatusesLocation().let {
            if (it == null || !it.isValid)
                externalStoragePath == storageVolume.path
            else it == storageVolume
        }
    }

    @Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        IllegalAccessException::class
    )
    @SuppressLint("DiscouragedPrivateApi")
    private fun StorageVolume.getPath(): String? {
        return if (hasR()) {
            this.directory?.absolutePath
        } else {
            StorageVolume::class.java.getDeclaredMethod("getPath").invoke(this) as? String
        }
    }
}