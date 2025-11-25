
package com.appsease.status.saver.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference


class SaveLocationPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.preference.R.attr.dialogPreferenceStyle
) : DialogPreference(context, attrs, defStyleAttr)