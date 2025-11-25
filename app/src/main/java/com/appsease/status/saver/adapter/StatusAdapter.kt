@file:Suppress("LeakingThis")

package com.appsease.status.saver.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.ActionMode
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.appsease.status.saver.R
import com.appsease.status.saver.adapter.base.AbsMultiSelectionAdapter
import com.appsease.status.saver.databinding.ItemStatusBinding
import com.appsease.status.saver.extensions.StatusMenu
import com.appsease.status.saver.extensions.getClientIfInstalled
import com.appsease.status.saver.extensions.getFormattedDate
import com.appsease.status.saver.extensions.getState
import com.appsease.status.saver.extensions.loadImage
import com.appsease.status.saver.interfaces.IStatusCallback
import com.appsease.status.saver.model.Status
import com.appsease.status.saver.model.StatusType
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

@SuppressLint("NotifyDataSetChanged")
open class StatusAdapter(
    protected val activity: FragmentActivity,
    private val callback: IStatusCallback,
    private var isSaveEnabled: Boolean,
    private var isDeleteEnabled: Boolean,
    isWhatsAppIconEnabled: Boolean
) : AbsMultiSelectionAdapter<Status, StatusAdapter.ViewHolder>(activity, R.menu.menu_statuses_selection) {

    var statuses: List<Status> by Delegates.observable(ArrayList()) { _: KProperty<*>, _: List<Status>, _: List<Status> ->
        notifyDataSetChanged()
    }
    var isSavingContent by Delegates.observable(false) { _: KProperty<*>, _: Boolean, _: Boolean ->
        notifyDataSetChanged()
    }
    var isWhatsAppIconEnabled by Delegates.observable(isWhatsAppIconEnabled) { _: KProperty<*>, _: Boolean, _: Boolean ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_status, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val status = statuses[position]

        val isSelected = isItemSelected(status)
        if (holder.cardView != null) {
            holder.cardView.isChecked = isSelected
        } else {
            holder.itemView.isActivated = isSelected
        }

        holder.menu?.isGone = isSelected

        holder.image?.loadImage(status)

        if (holder.state != null) {
            if (isSaveEnabled) {
                holder.state.text = status.getState(activity)
            } else {
                holder.state.text = status.getFormattedDate(activity)
            }
        }

        holder.playIcon?.isGone = isSaveEnabled || status.type == StatusType.IMAGE

        if (holder.clientIcon != null) {
            holder.clientIcon.isVisible = false
            holder.clientIcon.setImageDrawable(null)
            if (isWhatsAppIconEnabled) {
                val client = activity.getClientIfInstalled(status.clientPackage)
                if (client != null) {
                    holder.clientIcon.isVisible = true
                    holder.clientIcon.setImageDrawable(client.getIcon(activity))
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return statuses[position].hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return statuses.size
    }

    override fun getIdentifier(position: Int): Status {
        return statuses[position]
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        if (!isSaveEnabled) {
            menu.removeItem(R.id.action_save)
        }
        if (!isDeleteEnabled) {
            menu.removeItem(R.id.action_delete)
        }
        return false
    }

    override fun onMultiSelectionItemClick(menuItem: MenuItem, selection: List<Status>) {
        callback.multiSelectionItemClick(menuItem, selection)
    }

    @SuppressLint("ClickableViewAccessibility")
    open inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener,
        OnLongClickListener {
        val image: ImageView?
        val state: TextView?
        val clientIcon: ImageView?
        val playIcon: ImageView?
        val menu: Button?
        val cardView: MaterialCardView?

        private val currentMenu: StatusMenu
            get() = StatusMenu(statuses, layoutPosition, isSaveEnabled, isDeleteEnabled)

        init {
            val binding = ItemStatusBinding.bind(itemView)
            image = binding.image
            state = binding.state
            cardView = itemView as? MaterialCardView
            cardView?.isCheckable = true
            clientIcon = binding.clientIcon
            playIcon = binding.playIcon
            menu = binding.menu
            menu.setOnClickListener(this)

            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(view: View) {
            when (view) {
                itemView -> {
                    if (!isSavingContent) {
                        if (isMultiSelectionMode()) {
                            toggleItemChecked(layoutPosition)
                        } else {
                            callback.previewStatusesClick(statuses, layoutPosition)
                        }
                    }
                }
                menu -> {
                    callback.showStatusMenu(currentMenu)
                }
            }
        }

        override fun onLongClick(view: View): Boolean {
            if (!isSavingContent) {
                return toggleItemChecked(layoutPosition)
            }
            return false
        }
    }

    init {
        setHasStableIds(true)
    }
}