
package com.appsease.status.saver.model

import android.os.Build


class RequestedPermissions(private val versions: IntRange, vararg val permissions: String) {

    constructor(version: Int, vararg permissions: String) : this(version..version, *permissions)

    fun isApplicable(): Boolean {
        if (versions.isEmpty()) {
            return false
        }
        val sdk = Build.VERSION.SDK_INT
        return sdk >= versions.first && sdk <= versions.last
    }
}