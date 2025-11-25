
package com.appsease.status.saver.extensions

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import com.google.android.material.color.MaterialColors
import com.appsease.status.saver.R

val Context.isNightModeEnabled: Boolean
    get() = resources.configuration.run {
        this.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

fun Context.getGeneralThemeRes(): Int =
    if (isNightModeEnabled && preferences().isJustBlack()) R.style.Theme_WhatSave_Black else R.style.Theme_WhatSave

fun Context.primaryColor() =
    MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary, Color.TRANSPARENT)

fun Context.surfaceColor(fallback: Int = Color.TRANSPARENT) =
    MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface, fallback)