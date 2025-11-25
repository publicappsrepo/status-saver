package com.appsease.status.saver.interfaces

import android.view.MenuItem
import com.appsease.status.saver.extensions.StatusMenu
import com.appsease.status.saver.model.Status


interface IStatusCallback {
    fun showStatusMenu(menu: StatusMenu)
    fun previewStatusesClick(statuses: List<Status>, startPosition: Int)
    fun multiSelectionItemClick(item: MenuItem, selection: List<Status>)
}