package com.appsease.status.saver.extensions

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isGone
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.appsease.status.saver.R
import com.appsease.status.saver.databinding.DialogProgressBinding
import com.appsease.status.saver.databinding.DialogStatusOptionsBinding
import com.appsease.status.saver.model.Status

private typealias StatusBinding = DialogStatusOptionsBinding
private typealias ViewCallback = (View) -> Unit
private typealias StatusCallback = (Status) -> Unit

fun Context.createProgressDialog(): Dialog {
    val builder = MaterialAlertDialogBuilder(this)
    val binding = DialogProgressBinding.inflate(LayoutInflater.from(builder.context))
    return builder.setView(binding.root).setCancelable(false).create()
}

fun Context.showStatusOptions(menu: StatusMenu): Dialog {
    val status = menu.selection
    val binding = StatusBinding.inflate(LayoutInflater.from(this))
    val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
    bottomSheetDialog.setContentView(binding.root)
    bottomSheetDialog.setOnShowListener {
        binding.image.loadImage(status)
        binding.setupSave(menu.isSaveEnabled) {
            bottomSheetDialog.dismiss()
            menu.onSaveClick?.invoke(status)
        }
        binding.setupDelete(menu.isDeleteEnabled) {
            bottomSheetDialog.dismiss()
            menu.onDeleteClick?.invoke(status)
        }
        binding.setupListeners {
            bottomSheetDialog.dismiss()
            when (it) {
                binding.shareAction -> menu.onShareClick?.invoke(status)
                binding.image -> menu.onPreviewClick?.invoke(status)
            }
        }
    }
    return bottomSheetDialog.also {
        it.show()
    }
}

private fun StatusBinding.setupSave(isSaveEnabled: Boolean, callback: ViewCallback) {
    if (isSaveEnabled)
        saveAction.setOnClickListener(callback)
    else saveAction.isGone = true
}

private fun StatusBinding.setupDelete(isDeleteEnabled: Boolean, callback: ViewCallback) {
    if (isDeleteEnabled)
        deleteAction.setOnClickListener(callback)
    else deleteAction.isGone = true
}

private fun StatusBinding.setupListeners(callback: ViewCallback) {
    shareAction.setOnClickListener(callback)
    image.setOnClickListener(callback)
}

class StatusMenu(
    val statuses: List<Status>,
    val selectedPosition: Int,
    val isSaveEnabled: Boolean,
    val isDeleteEnabled: Boolean
) {
    var onPreviewClick: StatusCallback? = null
    var onSaveClick: StatusCallback? = null
    var onShareClick: StatusCallback? = null
    var onDeleteClick: StatusCallback? = null

    val selection: Status
        get() = statuses[selectedPosition]

    val selectionAsList: List<Status>
        get() = listOf(selection)

    fun createClick(
        onPreviewClick: StatusCallback? = null,
        onSaveClick: StatusCallback? = null,
        onShareClick: StatusCallback? = null,
        onDeleteClick: StatusCallback? = null
    ) = also {
        this.onPreviewClick = onPreviewClick ?: this.onPreviewClick
        this.onSaveClick = onSaveClick ?: this.onSaveClick
        this.onDeleteClick = onDeleteClick ?: this.onDeleteClick
        this.onShareClick = onShareClick ?: this.onShareClick
    }
}