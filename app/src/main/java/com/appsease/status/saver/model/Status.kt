
package com.appsease.status.saver.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
open class Status(
    open val type: StatusType,
    open val name: String,
    open val fileUri: Uri,
    open val dateModified: Long,
    open val size: Long,
    open val clientPackage: String?,
    open val isSaved: Boolean
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Status) return false

        if (type != other.type) return false
        if (name != other.name) return false
        if (fileUri != other.fileUri) return false
        if (dateModified != other.dateModified) return false
        if (size != other.size) return false
        if (clientPackage != other.clientPackage) return false
        if (isSaved != other.isSaved) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + fileUri.hashCode()
        result = 31 * result + dateModified.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + (clientPackage?.hashCode() ?: 0)
        result = 31 * result + isSaved.hashCode()
        return result
    }
}