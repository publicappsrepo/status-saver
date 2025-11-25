
package com.appsease.status.saver.fragments.base

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.appsease.status.saver.activities.StatusesActivity

abstract class BaseFragment @JvmOverloads constructor(@LayoutRes layoutRes: Int = 0) : Fragment(layoutRes),
    MenuProvider {

    protected val statusesActivity: StatusesActivity
        get() = activity as StatusesActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }
}