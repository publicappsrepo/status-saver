
package com.appsease.status.saver.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * https://github.com/Baseflow/PhotoView/issues/708#issuecomment-1116960531
 */
class PhotoViewWrapper @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    private var isParentInterceptionDisallowed = false

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        isParentInterceptionDisallowed = disallowIntercept
        if (disallowIntercept) {
            // PhotoView wants to disallow parent interception, let it be.
            parent.requestDisallowInterceptTouchEvent(true) // don't ban wrapper itself
        } else {
            // PhotoView wants to allow parent interception, we need to re-check it.
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // always false when up or cancel event,
        // which will allow parent interception normally.
        val isMultiTouch = ev.pointerCount > 1

        // re-check if it's multi touch
        parent.requestDisallowInterceptTouchEvent(isParentInterceptionDisallowed || isMultiTouch)
        return false
    }
}