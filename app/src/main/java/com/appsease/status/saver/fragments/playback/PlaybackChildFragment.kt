
package com.appsease.status.saver.fragments.playback

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.appsease.status.saver.R
import com.appsease.status.saver.WhatSaveViewModel
import com.appsease.status.saver.extensions.createProgressDialog
import com.appsease.status.saver.extensions.hasR
import com.appsease.status.saver.extensions.launchSafe
import com.appsease.status.saver.extensions.showToast
import com.appsease.status.saver.extensions.startActivitySafe
import com.appsease.status.saver.fragments.playback.PlaybackFragment.Companion.EXTRA_STATUS
import com.appsease.status.saver.model.SavedStatus
import com.appsease.status.saver.model.Status
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.properties.Delegates


abstract class PlaybackChildFragment(layoutRes: Int) : Fragment(layoutRes), View.OnClickListener {

    protected val viewModel: WhatSaveViewModel by activityViewModel()

    protected abstract val saveButton: MaterialButton
    protected abstract val shareButton: MaterialButton
    protected abstract val deleteButton: MaterialButton

    protected var status: Status by Delegates.notNull()

    private lateinit var deletionRequestLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val progressDialog by lazy { requireContext().createProgressDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        status = BundleCompat.getParcelable(requireArguments(), EXTRA_STATUS, Status::class.java)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        deletionRequestLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    viewModel.removeStatus(status)
                    showToast(R.string.deletion_success)
                    findNavController().popBackStack()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        viewModel.statusIsSaved(status).observe(viewLifecycleOwner) { isSaved ->
            this.isSaved = isSaved || (status is SavedStatus)
        }
        saveButton.setOnClickListener(this)
        shareButton.setOnClickListener(this)
        deleteButton.setOnClickListener(this)
        deleteButton.isEnabled = (status is SavedStatus)
    }

    private var isSaved: Boolean = false
        set(value) {
            field = value
            if (value) {
                saveButton.setText(R.string.saved_label)
                saveButton.setIconResource(R.drawable.ic_round_check_24dp)
            } else {
                saveButton.setText(R.string.save_action)
                saveButton.setIconResource(R.drawable.ic_save_alt_24dp)
            }
        }

    override fun onClick(v: View) {
        when (v) {
            saveButton -> {
                if (!isSaved) {
                    viewModel.saveStatus(status).observe(viewLifecycleOwner) { result ->
                        saveButton.isEnabled = !result.isSaving
                        if (result.isSuccess) {
                            showToast(R.string.saved_successfully)
                            viewModel.reloadAll()
                        } else if (!result.isSaving) {
                            showToast(R.string.failed_to_save)
                        }
                    }
                }
            }

            shareButton -> {
                viewModel.shareStatus(status).observe(viewLifecycleOwner) { result ->
                    shareButton.isEnabled = !result.isLoading
                    if (result.isLoading) {
                        progressDialog.show()
                    } else {
                        progressDialog.dismiss()
                        if (result.isSuccess) {
                            startActivitySafe(result.data.createIntent(requireContext()))
                        }
                    }
                }
            }

            deleteButton -> {
                if (hasR()) {
                    viewModel.createDeleteRequest(requireContext(), listOf(status))
                        .observe(viewLifecycleOwner) {
                            deletionRequestLauncher.launchSafe(
                                IntentSenderRequest.Builder(it).build()
                            )
                        }
                } else {
                    MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.delete_saved_status_title)
                        .setMessage(R.string.this_saved_status_will_be_permanently_deleted)
                        .setPositiveButton(R.string.delete_action) { _: DialogInterface, _: Int ->
                            viewModel.deleteStatus(status).observe(viewLifecycleOwner) { result ->
                                if (result.isSuccess) {
                                    showToast(R.string.deletion_success)
                                    viewModel.reloadAll()
                                    findNavController().popBackStack()
                                } else {
                                    showToast(R.string.deletion_failed)
                                }
                            }
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }
            }
        }
    }
}