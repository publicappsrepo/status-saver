package com.appsease.status.saver.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "received_messages",
    indices = [Index("received_time", "received_from", "message_content", unique = true, name = "messages_index")]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "message_id")
    val id: Int = 0,
    @ColumnInfo(name = "client_package")
    val clientPackage: String?,
    @ColumnInfo(name = "received_time")
    val time: Long,
    @ColumnInfo(name = "received_from")
    val senderName: String,
    @ColumnInfo(name = "message_content")
    val content: String
) : Parcelable