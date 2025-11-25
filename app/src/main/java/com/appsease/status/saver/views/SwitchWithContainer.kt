
package com.appsease.status.saver.views

import android.content.Context
import android.util.AttributeSet
import android.widget.CompoundButton
import android.widget.FrameLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import com.appsease.status.saver.R
import androidx.core.content.withStyledAttributes

class SwitchWithContainer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr), CompoundButton.OnCheckedChangeListener {

    private var onCheckedChangedListener: CompoundButton.OnCheckedChangeListener? = null
    private var cardView: MaterialCardView? = null
    var switch: MaterialSwitch? = null

    var isChecked: Boolean
        get() = switch?.isChecked ?: false
        set(value) {
            switch?.isChecked = value
        }
    var text: CharSequence?
        get() = switch?.text
        set(value) {
            switch?.text = value
        }

    init {
        addView(inflate(context, R.layout.switch_with_container, null))
        cardView = findViewById(R.id.cardView)
        cardView?.checkedIcon = null
        switch = findViewById(R.id.switchWidget)
        switch?.setOnCheckedChangeListener(this)

        context.withStyledAttributes(attrs, R.styleable.SwitchWithContainer, defStyleAttr, 0) {
            text = getString(R.styleable.SwitchWithContainer_android_text)
            isChecked = getBoolean(R.styleable.SwitchWithContainer_android_checked, false)
        }
    }

    fun setOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener?) {
        this.onCheckedChangedListener = listener
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        cardView?.isChecked = isChecked
        onCheckedChangedListener?.onCheckedChanged(buttonView, isChecked)
    }
}