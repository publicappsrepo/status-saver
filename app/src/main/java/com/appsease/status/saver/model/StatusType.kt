
package com.appsease.status.saver.model

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore.MediaColumns
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import com.appsease.status.saver.R
import com.appsease.status.saver.extensions.acceptFileName
import com.appsease.status.saver.extensions.getNewSaveName
import java.io.File


enum class StatusType(
    @param:StringRes val nameRes: Int,
    val format: String,
    private val saveType: StatusSaveType
) {
    IMAGE(R.string.type_images, ".jpg", StatusSaveType.IMAGE_SAVE),
    VIDEO(R.string.type_videos, ".mp4", StatusSaveType.VIDEO_SAVE);

    fun getDefaultSaveName(timeMillis: Long, delta: Int): String = getNewSaveName(this, timeMillis, delta = delta)

    val contentUri: Uri get() = saveType.contentUri

    val mimeType: String get() = saveType.fileMimeType

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getRelativePath(location: SaveLocation): String =
        String.format("%s/%s", saveType.dirTypeProvider(location), saveType.dirName)

    fun getSavesDirectory(location: SaveLocation): File =
        File(Environment.getExternalStoragePublicDirectory(saveType.dirTypeProvider(location)), saveType.dirName)

    fun getSavedContentFiles(location: SaveLocation): Array<File> {
        val directory = getSavesDirectory(location)
        return directory.listFiles { _, name -> acceptFileName(name) } ?: emptyArray()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getSavedMedia(contentResolver: ContentResolver): Cursor? {
        val projection = arrayOf(
            MediaColumns._ID,
            MediaColumns.DISPLAY_NAME,
            MediaColumns.DATE_MODIFIED,
            MediaColumns.SIZE,
            MediaColumns.RELATIVE_PATH
        )
        val entries = SaveLocation.entries
        val selection = entries.joinToString(" OR ") { "${MediaColumns.RELATIVE_PATH} LIKE ?" }
        val arguments = entries.map { "%${getRelativePath(it)}%" }.toTypedArray()
        return contentResolver.query(contentUri, projection, selection, arguments, null)
    }
}