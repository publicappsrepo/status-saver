package com.appsease.status.saver.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insetMessage(messageEntity: MessageEntity): Long

    @Delete
    fun removeMessage(messageEntity: MessageEntity)

    @Delete
    fun removeMessages(messages: List<MessageEntity>)

    @Query("DELETE FROM received_messages WHERE received_from = :sender")
    fun deleteConversation(sender: String)

    @Query("SELECT received_from AS name," +
            "COUNT(message_id) AS message_count," +
            "message_content AS latest_message," +
            "MAX(received_time) AS latest_message_time " +
            "FROM received_messages GROUP BY received_from ORDER BY latest_message_time DESC")
    fun queryConversations(): LiveData<List<Conversation>>

    @Query("SELECT * FROM received_messages WHERE received_from = :sender ORDER BY received_time DESC")
    fun queryMessages(sender: String): LiveData<List<MessageEntity>>

    @Query("DELETE FROM received_messages")
    fun clearMessages()
}