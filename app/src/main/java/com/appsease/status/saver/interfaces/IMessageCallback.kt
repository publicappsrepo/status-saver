
package com.appsease.status.saver.interfaces

import android.view.MenuItem
import com.appsease.status.saver.database.MessageEntity

interface IMessageCallback {
    fun messageClick(message: MessageEntity)
    fun messageSwiped(message: MessageEntity)
    fun messageMultiSelectionClick(item: MenuItem, selection: List<MessageEntity>)
}