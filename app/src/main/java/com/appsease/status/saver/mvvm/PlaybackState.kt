
package com.appsease.status.saver.mvvm

import com.appsease.status.saver.model.Status

data class PlaybackState(val statuses: List<Status>, val startPosition: Int) {
    companion object {
        val Empty = PlaybackState(emptyList(), -1)
    }
}