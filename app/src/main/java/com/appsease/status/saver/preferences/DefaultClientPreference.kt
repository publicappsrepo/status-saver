
package com.appsease.status.saver.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference

class DefaultClientPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.preference.R.attr.preferenceStyle
) : DialogPreference(context, attrs, defStyleAttr)