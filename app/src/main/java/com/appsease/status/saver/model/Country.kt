
package com.appsease.status.saver.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Country internal constructor(
    @SerialName("country_code")
    val code: Int,
    @SerialName("iso_code")
    val isoCode: String,
    @SerialName("display_name")
    val displayName: String
) {
    fun getId(): String = String.format("%s %s", isoCode, getFormattedCode())

    fun getFormattedCode() = String.format("+%d", code)
}