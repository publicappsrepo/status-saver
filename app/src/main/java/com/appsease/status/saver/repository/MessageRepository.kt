
package com.appsease.status.saver.repository

import androidx.lifecycle.LiveData
import com.appsease.status.saver.database.Conversation
import com.appsease.status.saver.database.MessageDao
import com.appsease.status.saver.database.MessageEntity

interface MessageRepository {
    fun listConversations(): LiveData<List<Conversation>>
    fun listMessages(sender: String): LiveData<List<MessageEntity>>
    suspend fun insertMessage(message: MessageEntity): Long
    suspend fun removeMessage(message: MessageEntity)
    suspend fun removeMessages(messages: List<MessageEntity>)
    suspend fun deleteConversations(conversations: List<String>)
    suspend fun clearMessages()
}

class MessageRepositoryImpl(private val messageDao: MessageDao) : MessageRepository {

    override fun listConversations(): LiveData<List<Conversation>> =
        messageDao.queryConversations()

    override fun listMessages(sender: String): LiveData<List<MessageEntity>> =
        messageDao.queryMessages(sender)

    override suspend fun insertMessage(message: MessageEntity) =
        messageDao.insetMessage(message)

    override suspend fun removeMessage(message: MessageEntity) =
        messageDao.removeMessage(message)

    override suspend fun removeMessages(messages: List<MessageEntity>) {
        messageDao.removeMessages(messages)
    }

    override suspend fun deleteConversations(conversations: List<String>) {
        if (conversations.isNotEmpty()) for (conversation in conversations) {
            messageDao.deleteConversation(conversation)
        }
    }

    override suspend fun clearMessages() {
        messageDao.clearMessages()
    }
}