
package com.appsease.status.saver.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
class Conversation(
    val name: String,
    @ColumnInfo(name = "message_count")
    val messageCount: Int,
    @ColumnInfo(name = "latest_message")
    val latestMessage: String,
    @ColumnInfo(name = "latest_message_time")
    val latestMessageTime: Long
) : Parcelable {

    val id: Long
        get() = name.hashCode().toLong()
}