package com.appsease.status.saver.adapter.base

import android.annotation.SuppressLint
import android.content.Context
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.appsease.status.saver.R

@SuppressLint("NotifyDataSetChanged")
abstract class AbsMultiSelectionAdapter<Data, VH : ViewHolder>(
    private val context: Context,
    private val multiMenuRes: Int,
) : RecyclerView.Adapter<VH>(), ActionMode.Callback {

    private var actionMode: ActionMode? = null
    private val checked = ArrayList<Data>()

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        val menuInflater = mode.menuInflater
        menuInflater.inflate(multiMenuRes, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        if (item.itemId == R.id.action_select_all) {
            checkAll()
        } else {
            onMultiSelectionItemClick(item, ArrayList(checked))
            finishActionMode()
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        clearChecked()
        actionMode = null
        onBackPressedCallback.remove()
    }

    protected abstract fun onMultiSelectionItemClick(menuItem: MenuItem, selection: List<Data>)

    protected abstract fun getIdentifier(position: Int): Data?

    protected fun isItemSelected(item: Data) = actionMode != null && checked.contains(item)

    protected fun isMultiSelectionMode() = actionMode != null

    protected fun toggleItemChecked(position: Int): Boolean {
        val identifier = getIdentifier(position) ?: return false
        if (!checked.remove(identifier)) {
            checked.add(identifier)
        }
        notifyItemChanged(position)
        updateCab()
        return true
    }

    private fun checkAll() {
        if (actionMode != null) {
            checked.clear()
            for (i in 0 until itemCount) {
                val identifier = getIdentifier(i) ?: continue
                checked.add(identifier)
            }
            notifyDataSetChanged()
            updateCab()
        }
    }

    private fun clearChecked() {
        checked.clear()
        notifyDataSetChanged()
    }

    private fun updateCab() {
        if (actionMode == null) {
            actionMode = (context as AppCompatActivity).startSupportActionMode(this)
            context.onBackPressedDispatcher.addCallback(onBackPressedCallback)
        }
        val size = checked.size
        if (size <= 0) actionMode?.finish()
        else actionMode?.title = context.getString(R.string.x_selected, size)
    }

    fun finishActionMode() {
        actionMode?.finish()
        clearChecked()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (actionMode != null) {
                actionMode?.finish()
                remove()
            }
        }
    }
}