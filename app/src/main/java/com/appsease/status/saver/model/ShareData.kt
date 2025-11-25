
package com.appsease.status.saver.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat
import com.appsease.status.saver.R


data class ShareData(val data: Set<Uri> = emptySet(), val mimeTypes: Set<String> = emptySet()) {

    val hasData: Boolean
        get() = data.isNotEmpty() && mimeTypes.isNotEmpty()

    val mimeType: String
        get() = mimeTypes.singleOrNull() ?: "*/*"

    constructor(data: Uri, mimeType: String) : this(setOf(data), setOf(mimeType))

    fun createIntent(context: Context): Intent {
        val builder = ShareCompat.IntentBuilder(context)
            .setType(mimeType)
            .setChooserTitle(context.getString(R.string.share_with))
        if (data.size == 1) {
            builder.setStream(data.single())
        } else if (data.size > 1) for (uri in data) {
            builder.addStream(uri)
        }
        builder.intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return builder.createChooserIntent()
    }

    companion object {
        val Empty = ShareData()
    }
}