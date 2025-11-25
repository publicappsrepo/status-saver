package com.appsease.status.saver

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.crossfade
import coil3.video.VideoFrameDecoder
import com.appsease.status.saver.extensions.getDefaultDayNightMode
import com.appsease.status.saver.extensions.migratePreferences
import com.appsease.status.saver.extensions.packageInfo
import com.appsease.status.saver.extensions.preferences
import com.appsease.status.saver.session.SessionManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun getApp(): App = App.instance

class App : Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()
        instance = this

        preferences().migratePreferences()

        startKoin {
            androidContext(this@App)
            modules(appModules)
        }

        SessionManager.getInstance().initSessionManager(this)

        AppCompatDelegate.setDefaultNightMode(preferences().getDefaultDayNightMode())
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }

    val versionName: String
        get() = packageManager.packageInfo().versionName ?: "0"

    companion object {
        internal lateinit var instance: App
            private set

        fun getFileProviderAuthority(): String = instance.packageName + ".file_provider"
    }
}