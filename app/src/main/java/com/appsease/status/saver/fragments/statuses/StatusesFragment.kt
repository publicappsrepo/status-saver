
package com.appsease.status.saver.fragments.statuses

import android.app.Activity
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.TransactionTooLargeException
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.appsease.status.saver.R
import com.appsease.status.saver.WhatSaveViewModel
import com.appsease.status.saver.adapter.StatusAdapter
import com.appsease.status.saver.databinding.FragmentStatusesBinding
import com.appsease.status.saver.extensions.PREFERENCE_DEFAULT_CLIENT
import com.appsease.status.saver.extensions.PREFERENCE_EXCLUDE_SAVED_STATUSES
import com.appsease.status.saver.extensions.PREFERENCE_STATUSES_LOCATION
import com.appsease.status.saver.extensions.PREFERENCE_WHATSAPP_ICON
import com.appsease.status.saver.extensions.StatusMenu
import com.appsease.status.saver.extensions.createProgressDialog
import com.appsease.status.saver.extensions.dip
import com.appsease.status.saver.extensions.findActivityNavController
import com.appsease.status.saver.extensions.getPreferredClient
import com.appsease.status.saver.extensions.hasR
import com.appsease.status.saver.extensions.isNullOrEmpty
import com.appsease.status.saver.extensions.isQuickDeletion
import com.appsease.status.saver.extensions.isWhatsappIcon
import com.appsease.status.saver.extensions.launchSafe
import com.appsease.status.saver.extensions.preferences
import com.appsease.status.saver.extensions.primaryColor
import com.appsease.status.saver.extensions.requestContext
import com.appsease.status.saver.extensions.requestPermissions
import com.appsease.status.saver.extensions.requestView
import com.appsease.status.saver.extensions.showStatusOptions
import com.appsease.status.saver.extensions.showToast
import com.appsease.status.saver.extensions.startActivitySafe
import com.appsease.status.saver.fragments.base.BaseFragment
import com.appsease.status.saver.fragments.binding.StatusesBinding
import com.appsease.status.saver.interfaces.IPermissionChangeListener
import com.appsease.status.saver.interfaces.IScrollable
import com.appsease.status.saver.interfaces.IStatusCallback
import com.appsease.status.saver.model.Status
import com.appsease.status.saver.model.StatusQueryResult
import com.appsease.status.saver.mvvm.DeletionResult
import org.koin.androidx.viewmodel.ext.android.activityViewModel


abstract class StatusesFragment : BaseFragment(R.layout.fragment_statuses),
    View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener,
    OnRefreshListener, IScrollable, IPermissionChangeListener, IStatusCallback {

    private var _binding: StatusesBinding? = null
    private lateinit var deletionRequestLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val progressDialog by lazy { requireContext().createProgressDialog() }
    private var deletedStatuses = mutableListOf<Status>()

    protected val binding get() = _binding!!
    protected val viewModel by activityViewModel<WhatSaveViewModel>()
    protected var statusAdapter: StatusAdapter? = null

    protected abstract val lastResult: StatusQueryResult?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        _binding = StatusesBinding(FragmentStatusesBinding.bind(view)).apply {
            swipeRefreshLayout.setOnRefreshListener(this@StatusesFragment)
            swipeRefreshLayout.setColorSchemeColors(view.context.primaryColor())

            recyclerView.setPadding(dip(R.dimen.status_item_margin))
            recyclerView.layoutManager = GridLayoutManager(requireActivity(), resources.getInteger(R.integer.statuses_grid_span_count))
            recyclerView.adapter = createAdapter().apply {
                registerAdapterDataObserver(adapterDataObserver)
            }.also { newStatusAdapter ->
                statusAdapter = newStatusAdapter
            }

            emptyButton.setOnClickListener(this@StatusesFragment)
        }
        deletionRequestLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                viewModel.removeStatuses(deletedStatuses)
                showToast(R.string.deletion_success)
            }
        }
        preferences().registerOnSharedPreferenceChangeListener(this)
    }

    protected open fun createAdapter(): StatusAdapter {
        return StatusAdapter(
            requireActivity(),
            this,
            isSaveEnabled = true,
            isDeleteEnabled = false,
            isWhatsAppIconEnabled = preferences().isWhatsappIcon()
        )
    }

    override fun scrollToTop() {
        binding.recyclerView.scrollToPosition(0)
    }

    override fun onStart() {
        super.onStart()
        statusesActivity.addPermissionsChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        statusesActivity.removePermissionsChangeListener(this)
    }

    override fun onClick(view: View) {
        if (view == binding.emptyButton) {
            val resultCode = lastResult?.code
            if (resultCode != StatusQueryResult.ResultCode.Loading) {
                when (resultCode) {
                    StatusQueryResult.ResultCode.PermissionError -> requestPermissions()
                    StatusQueryResult.ResultCode.NotInstalled -> requireActivity().finish()
                    StatusQueryResult.ResultCode.NoStatuses -> requireContext().getPreferredClient()?.let {
                        startActivitySafe(it.getLaunchIntent(requireContext().packageManager))
                    }

                    else -> onRefresh()
                }
            }
        }
    }

    override fun permissionsStateChanged(hasPermissions: Boolean) {
        viewModel.reloadAll()
    }

    override fun multiSelectionItemClick(item: MenuItem, selection: List<Status>) {
        when (item.itemId) {
            R.id.action_share -> shareStatuses(selection)
            R.id.action_save -> saveStatuses(selection)
            R.id.action_delete -> deleteStatuses(selection)
        }
    }

    override fun previewStatusesClick(statuses: List<Status>, startPosition: Int) {
        viewModel.preparePlayback(statuses, startPosition)
        findActivityNavController(R.id.global_container).navigate(R.id.playbackFragment)
    }

    override fun showStatusMenu(menu: StatusMenu) = requestContext { context ->
        context.showStatusOptions(
            menu.createClick(
                onPreviewClick = {
                    previewStatusesClick(menu.statuses, menu.selectedPosition)
                },
                onSaveClick = {
                    saveStatuses(menu.selectionAsList)
                },
                onShareClick = {
                    shareStatuses(menu.selectionAsList)
                },
                onDeleteClick = {
                    deleteStatuses(menu.selectionAsList)
                }
            )
        )
    }

    protected fun data(result: StatusQueryResult) {
        statusAdapter?.statuses = result.statuses
        binding.swipeRefreshLayout.isRefreshing = result.isLoading
        if (result.code.titleRes != 0) {
            binding.emptyTitle.text = getString(result.code.titleRes)
            binding.emptyTitle.isVisible = true
        } else {
            binding.emptyTitle.isVisible = false
        }
        if (result.code.descriptionRes != 0) {
            binding.emptyText.text = getString(result.code.descriptionRes)
            binding.emptyText.isVisible = true
        } else {
            binding.emptyText.isVisible = false
        }
        if (result.code.buttonTextRes != 0) {
            binding.emptyButton.text = getString(result.code.buttonTextRes)
            binding.emptyButton.isVisible = true
        } else {
            binding.emptyButton.isVisible = false
        }
    }

    private fun shareStatuses(statuses: List<Status>) = requestView {
        viewModel.shareStatuses(statuses).observe(viewLifecycleOwner) {
            if (it.isLoading) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
                if (it.isSuccess) {
                    startActivitySafe(it.data.createIntent(requireContext())) { t: Throwable, _ ->
                        if (t is TransactionTooLargeException) {
                            showToast(R.string.unable_to_share_sharing_too_many_files)
                        }
                    }
                }
            }
        }
    }

    private fun saveStatuses(statuses: List<Status>) = requestView { view ->
        viewModel.saveStatuses(statuses).observe(viewLifecycleOwner) { result ->
            if (result.isSaving) {
                Snackbar.make(view, R.string.saving_status, Snackbar.LENGTH_SHORT).show()
            } else {
                if (result.isSuccess) {
                    if (result.saved == 1) {
                        Snackbar.make(view, R.string.saved_successfully, Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(view, getString(R.string.saved_x_statuses, result.saved), Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    viewModel.reloadAll()
                } else {
                    Snackbar.make(view, R.string.failed_to_save, Snackbar.LENGTH_SHORT).show()
                }
            }
            statusAdapter?.isSavingContent = result.isSaving
        }
    }

    private fun deleteStatuses(statuses: List<Status>) = requestView { view ->
        if (hasR()) {
            viewModel.createDeleteRequest(requireContext(), statuses).observe(viewLifecycleOwner) {
                deletedStatuses = statuses.toMutableList()
                deletionRequestLauncher.launchSafe(IntentSenderRequest.Builder(it).build())
            }
        } else {
            if (!preferences().isQuickDeletion()) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.delete_saved_statuses_title)
                    .setMessage(
                        getString(R.string.x_saved_statuses_will_be_permanently_deleted, statuses.size)
                    )
                    .setPositiveButton(R.string.delete_action) { _: DialogInterface, _: Int ->
                        viewModel.deleteStatuses(statuses).observe(viewLifecycleOwner) {
                            it.processDeletionResult()
                        }
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            } else {
                viewModel.deleteStatuses(statuses).observe(viewLifecycleOwner) {
                    it.processDeletionResult()
                }
            }
        }
    }

    private fun DeletionResult.processDeletionResult() = requestView { view ->
        if (isDeleting) {
            Snackbar.make(view, R.string.deleting_please_wait, Snackbar.LENGTH_SHORT).show()
        } else if (isSuccess) {
            Snackbar.make(view, R.string.deletion_success, Snackbar.LENGTH_SHORT).show()
            viewModel.reloadAll()
        } else {
            Snackbar.make(view, R.string.deletion_failed, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        when (key) {
            PREFERENCE_DEFAULT_CLIENT,
            PREFERENCE_STATUSES_LOCATION,
            PREFERENCE_EXCLUDE_SAVED_STATUSES -> onRefresh()

            PREFERENCE_WHATSAPP_ICON -> statusAdapter?.isWhatsAppIconEnabled = sharedPreferences.isWhatsappIcon()
        }
    }

    override fun onPause() {
        super.onPause()
        statusAdapter?.finishActionMode()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        preferences().unregisterOnSharedPreferenceChangeListener(this)
        deletedStatuses.clear()
        statusAdapter?.unregisterAdapterDataObserver(adapterDataObserver)
        statusAdapter = null
    }

    private val adapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            binding.emptyView.isVisible = statusAdapter.isNullOrEmpty()
        }
    }
}