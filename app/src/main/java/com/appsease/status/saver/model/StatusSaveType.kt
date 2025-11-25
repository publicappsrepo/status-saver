
package com.appsease.status.saver.model

import android.net.Uri
import android.provider.MediaStore

internal enum class StatusSaveType(
    internal val dirName: String,
    internal val fileMimeType: String,
    internal val contentUri: Uri,
    internal val dirTypeProvider: (SaveLocation) -> String
) {
    IMAGE_SAVE(
        dirName = "Saved Image Statuses",
        fileMimeType = "image/jpeg",
        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        dirTypeProvider = { it.imageDir }
    ),
    VIDEO_SAVE(
        dirName = "Saved Video Statuses",
        fileMimeType = "video/mp4",
        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        dirTypeProvider = { it.videoDir }
    );
}