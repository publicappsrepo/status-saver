
package com.appsease.status.saver.mvvm

import android.net.Uri
import com.appsease.status.saver.model.SavedStatus
import com.appsease.status.saver.model.ShareData
import com.appsease.status.saver.model.Status

data class DeletionResult(
    val isDeleting: Boolean = false,
    val statuses: List<Status> = arrayListOf(),
    val deleted: Int = 0
) {
    val isSuccess: Boolean
        get() = statuses.size == deleted

    companion object {
        fun single(status: Status, success: Boolean) =
            DeletionResult(false, listOf(status), if (success) 1 else 0)
    }
}

data class SaveResult(
    val isSaving: Boolean = false,
    val statuses: List<Status> = arrayListOf(),
    val uris: List<Uri> = arrayListOf(),
    val saved: Int = 0
) {
    val isSuccess: Boolean
        get() = statuses.isNotEmpty() && uris.isNotEmpty() && statuses.size == uris.size

    companion object {
        fun single(status: SavedStatus?): SaveResult {
            if (status != null) {
                return SaveResult(
                    isSaving = false,
                    statuses = listOf(status),
                    uris = listOf(status.fileUri),
                    saved = 1
                )
            }
            return SaveResult()
        }
    }
}

data class ShareResult(
    val isLoading: Boolean = false,
    val data: ShareData = ShareData()
) {
    val isSuccess: Boolean
        get() = data.hasData
}