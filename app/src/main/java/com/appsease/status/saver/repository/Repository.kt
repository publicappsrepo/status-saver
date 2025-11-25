
package com.appsease.status.saver.repository

import androidx.lifecycle.LiveData
import com.appsease.status.saver.database.Conversation
import com.appsease.status.saver.database.MessageEntity
import com.appsease.status.saver.model.Country
import com.appsease.status.saver.model.SavedStatus
import com.appsease.status.saver.model.ShareData
import com.appsease.status.saver.model.Status
import com.appsease.status.saver.model.StatusQueryResult
import com.appsease.status.saver.model.StatusType

interface Repository {
    fun statusIsSaved(status: Status): LiveData<Boolean>
    suspend fun statuses(type: StatusType): StatusQueryResult
    suspend fun savedStatuses(): StatusQueryResult
    suspend fun savedStatuses(type: StatusType): StatusQueryResult
    suspend fun shareStatus(status: Status): ShareData
    suspend fun shareStatuses(statuses: List<Status>): ShareData
    suspend fun saveStatus(status: Status, saveName: String?): SavedStatus?
    suspend fun saveStatuses(statuses: List<Status>): List<SavedStatus>
    suspend fun deleteStatus(status: Status): Boolean
    suspend fun deleteStatuses(statuses: List<Status>): Int
    suspend fun removeStatus(status: Status)
    suspend fun removeStatuses(statuses: List<Status>)
    suspend fun allCountries(): List<Country>
    suspend fun defaultCountry(): Country
    fun defaultCountry(country: Country)
    fun listConversations(): LiveData<List<Conversation>>
    fun receivedMessages(sender: Conversation): LiveData<List<MessageEntity>>
    suspend fun insertMessage(message: MessageEntity): Long
    suspend fun removeMessage(message: MessageEntity)
    suspend fun removeMessages(messages: List<MessageEntity>)
    suspend fun deleteConversations(conversations: List<Conversation>)
    suspend fun clearMessages()
}

class RepositoryImpl(
    private val statusesRepository: StatusesRepository,
    private val countryRepository: CountryRepository,
    private val messageRepository: MessageRepository
) : Repository {

    override fun statusIsSaved(status: Status): LiveData<Boolean> = statusesRepository.statusIsSaved(status)

    override suspend fun statuses(type: StatusType): StatusQueryResult = statusesRepository.statuses(type)

    override suspend fun savedStatuses(): StatusQueryResult = statusesRepository.savedStatuses()

    override suspend fun savedStatuses(type: StatusType): StatusQueryResult = statusesRepository.savedStatuses(type)

    override suspend fun shareStatus(status: Status): ShareData = statusesRepository.share(status)

    override suspend fun shareStatuses(statuses: List<Status>): ShareData = statusesRepository.share(statuses)

    override suspend fun saveStatus(status: Status, saveName: String?): SavedStatus? = statusesRepository.save(status, saveName)

    override suspend fun saveStatuses(statuses: List<Status>): List<SavedStatus> =
        statusesRepository.save(statuses)

    override suspend fun deleteStatus(status: Status): Boolean = statusesRepository.delete(status)

    override suspend fun deleteStatuses(statuses: List<Status>): Int = statusesRepository.delete(statuses)

    override suspend fun removeStatus(status: Status) = statusesRepository.removeFromDatabase(status)

    override suspend fun removeStatuses(statuses: List<Status>) = statusesRepository.removeFromDatabase(statuses)

    override suspend fun allCountries(): List<Country> = countryRepository.allCountries()

    override suspend fun defaultCountry(): Country = countryRepository.defaultCountry()

    override fun defaultCountry(country: Country) = countryRepository.defaultCountry(country)

    override fun listConversations(): LiveData<List<Conversation>> = messageRepository.listConversations()

    override fun receivedMessages(sender: Conversation): LiveData<List<MessageEntity>> {
        return messageRepository.listMessages(sender.name)
    }

    override suspend fun insertMessage(message: MessageEntity) = messageRepository.insertMessage(message)

    override suspend fun removeMessage(message: MessageEntity) = messageRepository.removeMessage(message)

    override suspend fun removeMessages(messages: List<MessageEntity>) = messageRepository.removeMessages(messages)

    override suspend fun deleteConversations(senders: List<Conversation>) =
        messageRepository.deleteConversations(senders.map { it.name })

    override suspend fun clearMessages() = messageRepository.clearMessages()
}