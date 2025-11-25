
package com.appsease.status.saver.extensions

import android.content.Context
import com.appsease.status.saver.model.WaClient

val REGEX_WHATSAPP = """^(?:Android/media/com\.whatsapp/WhatsApp/|WhatsApp/)(?:accounts/\d+/)?Media/\.Statuses$""".toRegex()
val REGEX_BUSINESS = """^(?:Android/media/com\.whatsapp\.w4b/WhatsApp Business/|WhatsApp Business/)(?:accounts/\d+/)?Media/\.Statuses$""".toRegex()

fun Context.getDefaultClient(): WaClient? {
    val clientPackageName = preferences().defaultClientPackageName
    if (!clientPackageName.isNullOrEmpty()) {
        return getClientIfInstalled(clientPackageName)
    }
    return null
}

fun Context.setDefaultClient(client: WaClient?) {
    preferences().defaultClientPackageName = client?.packageName
}

fun Context.getAllInstalledClients() = WaClient.entries.filter { it.isInstalled(this) }

fun Context.getClientIfInstalled(packageName: String?) =
    getAllInstalledClients().firstOrNull { it.packageName == packageName }

fun Context.getPreferredClient() = getDefaultClient() ?: getAllInstalledClients().firstOrNull()

fun List<WaClient>.getPreferred(context: Context): List<WaClient> {
    val preferred = context.getDefaultClient()
    return if (preferred == null) this else filter { it.packageName == preferred.packageName }
}