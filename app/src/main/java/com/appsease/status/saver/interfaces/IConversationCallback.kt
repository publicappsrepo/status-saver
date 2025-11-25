
package com.appsease.status.saver.interfaces

import android.view.MenuItem
import com.appsease.status.saver.database.Conversation

interface IConversationCallback {
    fun conversationClick(conversation: Conversation)
    fun conversationSwiped(conversation: Conversation)
    fun conversationMultiSelectionClick(item: MenuItem, selection: List<Conversation>)
}