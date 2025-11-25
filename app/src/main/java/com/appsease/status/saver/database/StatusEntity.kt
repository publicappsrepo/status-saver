package com.appsease.status.saver.database

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.appsease.status.saver.model.StatusType

@Entity(tableName = "saved_statuses")
class StatusEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("status_id")
    val id: Long = 0L,
    @ColumnInfo(name = "status_type")
    val type: StatusType,
    @ColumnInfo(name = "original_name")
    val name: String?,
    @ColumnInfo(name = "original_uri")
    val origin: Uri,
    @ColumnInfo(name = "original_date_modified")
    val dateModified: Long,
    @ColumnInfo(name = "original_size")
    val size: Long,
    @ColumnInfo(name = "original_client")
    val client: String?,
    @ColumnInfo(name = "save_name")
    val saveName: String
)