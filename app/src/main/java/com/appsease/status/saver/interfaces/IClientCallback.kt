
package com.appsease.status.saver.interfaces

import com.appsease.status.saver.model.WaClient

interface IClientCallback {
    fun clientClick(client: WaClient)
    fun checkModeForClient(client: WaClient): Int = MODE_UNCHECKED

    companion object {
        const val MODE_CHECKED = 1
        const val MODE_UNCHECKED = 2
    }
}