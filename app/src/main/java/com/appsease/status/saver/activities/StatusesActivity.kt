package com.appsease.status.saver.activities

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewGroupCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.appsease.status.saver.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.appsease.status.saver.WhatSaveViewModel
import com.appsease.status.saver.extensions.STORAGE_PERMISSION_REQUEST
import com.appsease.status.saver.extensions.getGeneralThemeRes
import com.appsease.status.saver.extensions.getPreferredClient
import com.appsease.status.saver.extensions.hasQ
import com.appsease.status.saver.extensions.hasStoragePermissions
import com.appsease.status.saver.extensions.isNightModeEnabled
import com.appsease.status.saver.extensions.isShownOnboard
import com.appsease.status.saver.extensions.openSettings
import com.appsease.status.saver.extensions.preferences
import com.appsease.status.saver.extensions.requestPermissions
import com.appsease.status.saver.extensions.requestWithoutOnboard
import com.appsease.status.saver.extensions.useCustomFont
import com.appsease.status.saver.extensions.whichFragment
import com.appsease.status.saver.interfaces.IPermissionChangeListener
import org.koin.androidx.viewmodel.ext.android.viewModel

open class StatusesActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var windowInsetsController: WindowInsetsControllerCompat

    private val permissionsChangeListeners: MutableList<IPermissionChangeListener?> = ArrayList()
    private var hadPermissions = false

    private val viewModel by viewModel<WhatSaveViewModel>()
    private var globalNavController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTheme()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        hadPermissions = hasStoragePermissions()
        ViewGroupCompat.installCompatInsetsDispatch(window.decorView)
        windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        if (hasQ()) {
            window.isNavigationBarContrastEnforced = false
        }

        setContentView(R.layout.activity_main)

        val navigationHost = whichFragment<NavHostFragment>(R.id.global_container)
        globalNavController = navigationHost.navController.also {
            it.addOnDestinationChangedListener(this)
        }
    }

    private fun setupTheme() {
        setTheme(getGeneralThemeRes())
        if (preferences().useCustomFont()) {
            setTheme(R.style.CustomFontThemeOverlay)
        }
    }

    private fun lightSystemBars(isLight: Boolean = !isNightModeEnabled) {
        windowInsetsController.isAppearanceLightStatusBars = isLight
        windowInsetsController.isAppearanceLightNavigationBars = isLight
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (!hasStoragePermissions()) {
            requestPermissions(preferences().isShownOnboard)
        }
    }

    override fun onResume() {
        super.onResume()
        val hasPermissions = hasStoragePermissions()
        if (hasPermissions != hadPermissions) {
            hadPermissions = hasPermissions
            onHasPermissionsChanged(hasPermissions)
        }
    }

    fun addPermissionsChangeListener(listener: IPermissionChangeListener) {
        permissionsChangeListeners.add(listener)
    }

    fun removePermissionsChangeListener(listener: IPermissionChangeListener) {
        permissionsChangeListeners.remove(listener)
    }

    private fun onHasPermissionsChanged(hasPermissions: Boolean) {
        for (listener in permissionsChangeListeners) {
            listener?.permissionsStateChanged(hasPermissions)
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.mainFragment -> lightSystemBars()
            R.id.playbackFragment -> lightSystemBars(false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.setupWhatsAppMenuItem(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                findNavController(R.id.main_container).navigate(R.id.settingsFragment)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.main_container).popBackStack()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST) {
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        //User has denied from permission dialog
                        MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.permissions_denied_title)
                            .setMessage(R.string.permissions_denied_message)
                            .setPositiveButton(R.string.grant_action) { _: DialogInterface, _: Int ->
                                requestWithoutOnboard()
                            }
                            .setNegativeButton(android.R.string.cancel) { _: DialogInterface, _: Int -> finish() }
                            .setCancelable(false)
                            .show()
                    } else {
                        // User has denied permission and checked never show permission dialog, so you can redirect to Application settings page
                        MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.permissions_denied_title)
                            .setMessage(R.string.permissions_denied_message)
                            .setPositiveButton(R.string.open_settings_action) { _: DialogInterface, _: Int ->
                                openSettings(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            }
                            .setNeutralButton(android.R.string.cancel) { _: DialogInterface, _: Int -> finish() }
                            .setCancelable(false)
                            .show()
                    }
                }
                return
            }
        }
        hadPermissions = true
        onHasPermissionsChanged(true)
    }

    override fun onDestroy() {
        globalNavController?.removeOnDestinationChangedListener(this)
        super.onDestroy()
    }

    private fun Menu.setupWhatsAppMenuItem(activity: FragmentActivity) {
        this.removeItem(R.id.action_launch_client)

        val client = activity.getPreferredClient()
        if (client != null) {
            this.add(
                Menu.NONE, R.id.action_launch_client,
                Menu.FIRST, activity.getString(R.string.launch_x_client, client.displayName)
            )
                .setIcon(R.drawable.ic_open_in_new_24dp)
                .setIntent(client.getLaunchIntent(activity.packageManager))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
    }
}