
package com.appsease.status.saver.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.appsease.status.saver.R
import com.appsease.status.saver.extensions.blacklistedSenders
import com.appsease.status.saver.extensions.formattedAsHtml
import com.appsease.status.saver.extensions.preferences
import com.appsease.status.saver.extensions.whitelistMessageSender
import com.appsease.status.saver.getApp

class BlacklistedSenderDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val blacklisted = preferences().blacklistedSenders()?.toTypedArray()
        if (blacklisted.isNullOrEmpty()) {
            return MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.blacklisted_senders)
                .setMessage(R.string.no_blacklisted_senders)
                .setPositiveButton(android.R.string.ok, null)
                .create()
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.blacklisted_senders)
            .setItems(blacklisted) { _: DialogInterface, which: Int ->
                removeItem(blacklisted[which])
            }
            .setPositiveButton(R.string.close_action, null)
            .create()
    }

    private fun removeItem(name: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.remove_x_from_the_blacklist, name).formattedAsHtml())
            .setPositiveButton(R.string.yes_action) { _: DialogInterface, _: Int ->
                getApp().preferences().whitelistMessageSender(name)
            }
            .setNegativeButton(R.string.no_action, null)
            .show()
    }
}