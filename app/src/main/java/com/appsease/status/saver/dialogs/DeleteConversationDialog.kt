
package com.appsease.status.saver.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.appsease.status.saver.R
import com.appsease.status.saver.WhatSaveViewModel
import com.appsease.status.saver.database.Conversation
import com.appsease.status.saver.databinding.DialogDeleteConversationBinding
import com.appsease.status.saver.extensions.parcelableList
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class DeleteConversationDialog : DialogFragment() {

    private val viewModel: WhatSaveViewModel by activityViewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogDeleteConversationBinding.inflate(layoutInflater)
        val conversations = requireArguments().parcelableList(EXTRA_CONVERSATIONS, Conversation::class)!!
        val titleRes: Int
        if (conversations.size == 1) {
            titleRes = R.string.delete_conversation_title
            binding.message.setText(R.string.delete_conversation_confirmation)
            binding.blacklistSender.setText(R.string.blacklist_sender)
        } else {
            titleRes = R.string.delete_conversations_title
            binding.message.text = getString(R.string.delete_x_conversations_confirmation, conversations.size)
            binding.blacklistSender.setText(R.string.blacklist_senders)
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(titleRes)
            .setView(binding.root)
            .setPositiveButton(R.string.delete_action) { _: DialogInterface, _: Int ->
                viewModel.deleteConversations(conversations, binding.blacklistSender.isChecked)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    companion object {
        private const val EXTRA_CONVERSATIONS = "extra_conversations"

        fun create(conversation: Conversation) = create(listOf(conversation))

        fun create(conversations: List<Conversation>) =
            DeleteConversationDialog().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(EXTRA_CONVERSATIONS, ArrayList(conversations))
                }
            }
    }
}