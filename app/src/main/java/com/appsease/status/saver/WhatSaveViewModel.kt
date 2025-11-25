package com.appsease.status.saver

import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.appsease.status.saver.database.Conversation
import com.appsease.status.saver.database.MessageEntity
import com.appsease.status.saver.extensions.blacklistMessageSender
import com.appsease.status.saver.extensions.getAllInstalledClients
import com.appsease.status.saver.extensions.lastUpdateId
import com.appsease.status.saver.extensions.preferences
import com.appsease.status.saver.model.Country
import com.appsease.status.saver.model.Status
import com.appsease.status.saver.model.StatusQueryResult
import com.appsease.status.saver.model.StatusQueryResult.ResultCode
import com.appsease.status.saver.model.StatusType
import com.appsease.status.saver.model.WaClient
import com.appsease.status.saver.model.WaDirectory
import com.appsease.status.saver.mvvm.DeletionResult
import com.appsease.status.saver.mvvm.PlaybackState
import com.appsease.status.saver.mvvm.SaveResult
import com.appsease.status.saver.mvvm.ShareResult
import com.appsease.status.saver.repository.Repository
import com.appsease.status.saver.storage.Storage
import com.appsease.status.saver.storage.StorageDevice
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.EnumMap

class WhatSaveViewModel(
    private val repository: Repository,
    private val httpClient: HttpClient,
    private val storage: Storage
) : ViewModel() {

    private val liveDataMap = newStatusesLiveDataMap()

    private val _playbackState = MutableStateFlow(PlaybackState.Empty)
    val playbackState = _playbackState.asStateFlow()

    private val savedStatuses = MutableLiveData(StatusQueryResult.Idle)
    private val installedClients = MutableLiveData<List<WaClient>>()
    private val storageDevices = MutableLiveData<List<StorageDevice>>()
    private val countries = MutableLiveData<List<Country>>()
    private val selectedCountry = MutableLiveData<Country>()

    private val unlockMessageView = MutableLiveData(false)

    override fun onCleared() {
        super.onCleared()
        liveDataMap.clear()
    }

    fun unlockMessageView() {
        unlockMessageView.value = true
    }

    fun getMessageViewLockObservable(): LiveData<Boolean> = unlockMessageView

    fun getInstalledClients(): LiveData<List<WaClient>> = installedClients

    fun getStorageDevices(): LiveData<List<StorageDevice>> = storageDevices

    fun getCountriesObservable(): LiveData<List<Country>> = countries

    fun getSelectedCountryObservable(): LiveData<Country> = selectedCountry

    fun getCountries() = countries.value ?: arrayListOf()

    fun getSelectedCountry() = selectedCountry.value

    fun getSavedStatuses(): LiveData<StatusQueryResult> = savedStatuses

    fun getStatuses(type: StatusType): LiveData<StatusQueryResult> {
        return liveDataMap.getOrCreateLiveData(type)
    }

    fun loadClients() = viewModelScope.launch(IO) {
        val result = getApp().getAllInstalledClients()
        installedClients.postValue(result)
    }

    fun loadStorageDevices() = viewModelScope.launch(IO) {
        storageDevices.postValue(storage.storageVolumes)
    }

    fun loadCountries() = viewModelScope.launch(IO) {
        val result = repository.allCountries()
        countries.postValue(result)
    }

    fun loadSelectedCountry() = viewModelScope.launch(IO) {
        selectedCountry.postValue(repository.defaultCountry())
    }

    fun setSelectedCountry(country: Country) = viewModelScope.launch(IO) {
        repository.defaultCountry(country)
        selectedCountry.postValue(country)
    }

    fun loadStatuses(type: StatusType) = viewModelScope.launch(IO) {
        val liveData = liveDataMap[type]
        if (liveData != null) {
            liveData.postValue(liveData.value?.copy(code = ResultCode.Loading) ?: StatusQueryResult(ResultCode.Loading))
            liveData.postValue(repository.statuses(type))
        }
    }

    fun loadSavedStatuses() = viewModelScope.launch(IO) {
        savedStatuses.postValue(savedStatuses.value?.copy(code = ResultCode.Loading) ?: StatusQueryResult(ResultCode.Loading))
        savedStatuses.postValue(repository.savedStatuses())
    }

    fun reloadAll() {
        StatusType.entries.forEach {
            loadStatuses(it)
        }
        loadSavedStatuses()
    }

    fun statusIsSaved(status: Status): LiveData<Boolean> = repository.statusIsSaved(status)

    fun shareStatus(status: Status): LiveData<ShareResult> = liveData(IO) {
        emit(ShareResult(isLoading = true))
        val data = repository.shareStatus(status)
        emit(ShareResult(data = data))
    }

    fun shareStatuses(statuses: List<Status>): LiveData<ShareResult> = liveData(IO) {
        emit(ShareResult(isLoading = true))
        val data = repository.shareStatuses(statuses)
        emit(ShareResult(data = data))
    }

    fun saveStatus(status: Status, saveName: String? = null): LiveData<SaveResult> = liveData(IO) {
        emit(SaveResult(isSaving = true))
        val result = repository.saveStatus(status, saveName)
        emit(SaveResult.single(result))
    }

    fun saveStatuses(statuses: List<Status>): LiveData<SaveResult> = liveData(IO) {
        emit(SaveResult(isSaving = true))
        val savedStatuses = repository.saveStatuses(statuses)
        val savedUris = savedStatuses.map { it.fileUri }
        emit(SaveResult(statuses = savedStatuses, uris = savedUris, saved = savedStatuses.size))
    }

    fun deleteStatus(status: Status): LiveData<DeletionResult> = liveData(IO) {
        emit(DeletionResult(isDeleting = true))
        val result = repository.deleteStatus(status)
        emit(DeletionResult.single(status, result))
    }

    fun deleteStatuses(statuses: List<Status>): LiveData<DeletionResult> = liveData(IO) {
        emit(DeletionResult(isDeleting = true))
        val result = repository.deleteStatuses(statuses)
        emit(DeletionResult(statuses = statuses, deleted = result))
    }

    fun removeStatus(status: Status) = viewModelScope.launch(IO) {
        repository.removeStatus(status)
        reloadAll()
    }

    fun removeStatuses(statuses: List<Status>) = viewModelScope.launch(IO) {
        repository.removeStatuses(statuses)
        reloadAll()
    }

    fun messageSenders(): LiveData<List<Conversation>> =
        repository.listConversations()

    fun receivedMessages(sender: Conversation): LiveData<List<MessageEntity>> =
        repository.receivedMessages(sender)

    fun deleteMessage(message: MessageEntity) = viewModelScope.launch(IO) {
        repository.removeMessage(message)
    }

    fun deleteConversations(conversations: List<Conversation>, addToBlacklist: Boolean = false) = viewModelScope.launch(IO) {
        repository.deleteConversations(conversations)
        if (addToBlacklist) conversations.forEach {
            getApp().preferences().blacklistMessageSender(it.name)
        }
    }

    fun deleteMessages(messages: List<MessageEntity>) = viewModelScope.launch(IO) {
        repository.removeMessages(messages)
    }

    fun deleteAllMessages() = viewModelScope.launch(IO) {
        repository.clearMessages()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun createDeleteRequest(context: Context, statuses: List<Status>): LiveData<PendingIntent> = liveData(IO) {
        val uris = statuses.map { it.fileUri }
        if (uris.isNotEmpty()) {
            emit(MediaStore.createDeleteRequest(context.contentResolver, uris))
        }
    }

    fun getReadableDirectoryPaths(directories: List<WaDirectory>): LiveData<Array<String>> = liveData(IO) {
        val paths = directories.map { it.createPrettyPath(storage) }
            .toTypedArray()

        emit(paths)
    }

    fun preparePlayback(statuses: List<Status>, startPosition: Int) {
        _playbackState.value = PlaybackState(statuses, startPosition)
    }

    fun updatePlayback(position: Int) {
        _playbackState.value.takeUnless { it == PlaybackState.Empty }?.let { currentPlayback ->
            _playbackState.value = currentPlayback.copy(startPosition = position)
        }
    }

    private val SilentHandler = CoroutineExceptionHandler { _, _ -> }
}

internal typealias StatusesLiveData = MutableLiveData<StatusQueryResult>
internal typealias StatusesLiveDataMap = EnumMap<StatusType, StatusesLiveData>

internal fun newStatusesLiveDataMap() = StatusesLiveDataMap(StatusType::class.java)

internal fun StatusesLiveDataMap.getOrCreateLiveData(type: StatusType): StatusesLiveData =
    getOrPut(type) { StatusesLiveData(StatusQueryResult.Idle) }