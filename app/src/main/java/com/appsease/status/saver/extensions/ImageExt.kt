
package com.appsease.status.saver.extensions

import android.widget.ImageView
import coil3.load
import coil3.request.Disposable
import coil3.video.VideoFrameDecoder
import com.appsease.status.saver.model.Status
import com.appsease.status.saver.model.StatusType

fun ImageView.loadImage(status: Status): Disposable {
    return if (status.type == StatusType.VIDEO) {
        load(status.fileUri) {
            decoderFactory { result, options, _ -> VideoFrameDecoder(result.source, options) }
        }
    } else {
        load(status.fileUri)
    }
}