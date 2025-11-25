
package com.appsease.status.saver

import androidx.room.Room
import com.appsease.status.saver.database.MIGRATION_1_2
import com.appsease.status.saver.database.StatusDatabase
import com.appsease.status.saver.network.ktorHttpClient
import com.appsease.status.saver.repository.CountryRepository
import com.appsease.status.saver.repository.CountryRepositoryImpl
import com.appsease.status.saver.repository.MessageRepository
import com.appsease.status.saver.repository.MessageRepositoryImpl
import com.appsease.status.saver.repository.Repository
import com.appsease.status.saver.repository.RepositoryImpl
import com.appsease.status.saver.repository.StatusesRepository
import com.appsease.status.saver.repository.StatusesRepositoryImpl
import com.appsease.status.saver.storage.Storage
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

private val networkModule = module {
    single {
        ktorHttpClient()
    }
}

private val dataModule = module {
    single {
        Room.databaseBuilder(androidContext(), StatusDatabase::class.java, "statuses.db")
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    factory {
        get<StatusDatabase>().statusDao()
    }

    factory {
        get<StatusDatabase>().messageDao()
    }
}

private val managerModule = module {
    single {
        PhoneNumberUtil.createInstance(androidContext())
    }
    single {
        Storage(androidContext())
    }
}

private val statusesModule = module {
    single {
        CountryRepositoryImpl(androidContext())
    } bind CountryRepository::class

    single {
        StatusesRepositoryImpl(androidContext(), get(), get())
    } bind StatusesRepository::class

    single {
        MessageRepositoryImpl(get())
    } bind MessageRepository::class

    single {
        RepositoryImpl(get(), get(), get())
    } bind Repository::class
}

private val viewModelModule = module {
    viewModel {
        WhatSaveViewModel(get(), get(), get())
    }
}

val appModules = listOf(networkModule, dataModule, managerModule, statusesModule, viewModelModule)