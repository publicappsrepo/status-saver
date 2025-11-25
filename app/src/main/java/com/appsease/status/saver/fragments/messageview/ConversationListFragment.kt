
package com.appsease.status.saver.fragments.messageview

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.OVER_SCROLL_NEVER
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialFadeThrough
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils
import com.appsease.status.saver.R
import com.appsease.status.saver.WhatSaveViewModel
import com.appsease.status.saver.adapter.ConversationAdapter
import com.appsease.status.saver.database.Conversation
import com.appsease.status.saver.databinding.FragmentConversationsBinding
import com.appsease.status.saver.dialogs.BlacklistedSenderDialog
import com.appsease.status.saver.dialogs.DeleteConversationDialog
import com.appsease.status.saver.extensions.applyBottomWindowInsets
import com.appsease.status.saver.extensions.bindNotificationListener
import com.appsease.status.saver.extensions.getIntRes
import com.appsease.status.saver.extensions.isMessageViewEnabled
import com.appsease.status.saver.extensions.isNotificationListener
import com.appsease.status.saver.extensions.isNullOrEmpty
import com.appsease.status.saver.extensions.preferences
import com.appsease.status.saver.extensions.requestContext
import com.appsease.status.saver.fragments.base.BaseFragment
import com.appsease.status.saver.fragments.binding.ConversationsBinding
import com.appsease.status.saver.interfaces.IConversationCallback
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ConversationListFragment : BaseFragment(R.layout.fragment_conversations), CompoundButton.OnCheckedChangeListener,
    IConversationCallback {

    private val viewModel: WhatSaveViewModel by activityViewModel()

    private var _binding: ConversationsBinding? = null
    private val binding get() = _binding!!

    private var adapter: ConversationAdapter? = null
    private var swipeManager: RecyclerViewSwipeManager? = null
    private var wrappedAdapter: RecyclerView.Adapter<*>? = null
    private var blockSwitchListener: Boolean = false

    private var isMessageViewEnabled: Boolean
        get() = requireContext().preferences().isMessageViewEnabled
        set(value) {
            requireContext().preferences().isMessageViewEnabled = value
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = FragmentConversationsBinding.bind(view)
        _binding = ConversationsBinding(viewBinding)
        binding.scrollView.applyBottomWindowInsets()
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        enterTransition = MaterialFadeThrough().addTarget(view)
        reenterTransition = MaterialFadeThrough().addTarget(view)

        adapter = ConversationAdapter(requireContext(), arrayListOf(), this).also {
            it.registerAdapterDataObserver(adapterDataObserver)
        }

        binding.toolbar.setTitle(R.string.message_view)
        statusesActivity.setSupportActionBar(binding.toolbar)
        setupSwitch()
        setupRecyclerView()

        viewModel.messageSenders().observe(viewLifecycleOwner) {
            adapter?.data(it)
            updateEmptyView()
        }
    }

    override fun onResume() {
        super.onResume()
        requestContext {
            if (!it.isNotificationListener()) {
                findNavController().popBackStack()
            }
        }
    }

    private val adapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            if (adapter.isNullOrEmpty()) {
                binding.emptyView.isVisible = true
                binding.recyclerView.overScrollMode = OVER_SCROLL_NEVER
            } else {
                binding.emptyView.isVisible = false
                binding.recyclerView.overScrollMode = getIntRes(R.integer.overScrollMode)
            }
        }
    }

    private fun setupSwitch() {
        binding.switchWithContainer.isChecked = isMessageViewEnabled
        binding.switchWithContainer.setOnCheckedChangeListener(this)
    }

    private fun setupRecyclerView() {
        swipeManager = RecyclerViewSwipeManager().also {
            wrappedAdapter = it.createWrappedAdapter(adapter!!)
        }
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            recycleChildrenOnDetach = true
        }
        binding.recyclerView.adapter = wrappedAdapter
        binding.recyclerView.itemAnimator = RefactoredDefaultItemAnimator()
        swipeManager!!.attachRecyclerView(binding.recyclerView)
    }

    private fun updateEmptyView() {
        binding.emptyTitle.setText(R.string.empty)
        binding.emptyText.setText(R.string.no_conversations)
        binding.progressIndicator.hide()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.menu_conversations, menu)
        menu.removeItem(R.id.action_settings)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_blacklisted_senders -> {
                BlacklistedSenderDialog().show(childFragmentManager, "BLACKLISTED_SENDER")
                return true
            }

            R.id.action_clear_messages -> {
                viewModel.deleteAllMessages()
                return true
            }

            else -> return super.onMenuItemSelected(menuItem)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (blockSwitchListener) {
            blockSwitchListener = false
            return
        }
        if (!isChecked) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.disable_message_view_title)
                .setMessage(R.string.disable_message_view_confirmation)
                .setPositiveButton(R.string.yes_action) { _: DialogInterface, _: Int ->
                    isMessageViewEnabled = false
                    viewModel.deleteAllMessages()
                }
                .setNegativeButton(R.string.no_action) { _: DialogInterface, _: Int ->
                    setStateManually(buttonView, true)
                }
                .setOnCancelListener {
                    setStateManually(buttonView, true)
                }
                .show()
        } else {
            isMessageViewEnabled = true
            requestContext {
                if (!it.bindNotificationListener()) {
                    setStateManually(buttonView, false)
                }
            }
        }
    }

    private fun setStateManually(buttonView: CompoundButton, isEnabled: Boolean) {
        blockSwitchListener = true
        buttonView.isChecked = isEnabled
    }

    override fun conversationClick(conversation: Conversation) {
        val arguments = ConversationDetailFragmentArgs.Builder(conversation)
            .build()
            .toBundle()

        findNavController().navigate(R.id.messagesFragment, arguments)
    }

    override fun conversationSwiped(conversation: Conversation) {
        DeleteConversationDialog.create(conversation)
            .show(childFragmentManager, "DELETE_CONVERSATION")
    }

    override fun conversationMultiSelectionClick(item: MenuItem, selection: List<Conversation>) {
        when (item.itemId) {
            R.id.action_delete -> {
                DeleteConversationDialog.create(selection)
                    .show(childFragmentManager, "DELETE_CONVERSATION")
            }
        }
    }

    override fun onPause() {
        swipeManager?.cancelSwipe()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.layoutManager = null
        adapter?.unregisterAdapterDataObserver(adapterDataObserver)
        swipeManager?.release()
        swipeManager = null
        WrapperAdapterUtils.releaseAll(wrappedAdapter)
        wrappedAdapter = null
        adapter = null
    }
}