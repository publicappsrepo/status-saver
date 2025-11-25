
package com.appsease.status.saver.model

import android.net.Uri
import android.os.Parcelable
import com.appsease.status.saver.extensions.canonicalOrAbsolutePath
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
class SavedStatus(
    override val type: StatusType,
    override val name: String,
    override val fileUri: Uri,
    override val dateModified: Long,
    override val size: Long,
    private val path: String?
) : Status(type, name, fileUri, dateModified, size, null, true), Parcelable {

    fun hasFile(): Boolean = !path.isNullOrBlank()

    fun getFile(): File {
        checkNotNull(path)
        return File(path)
    }

    fun getFilePath(): String {
        return getFile().canonicalOrAbsolutePath()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SavedStatus) return false
        if (!super.equals(other)) return false

        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (path?.hashCode() ?: 0)
        return result
    }
}