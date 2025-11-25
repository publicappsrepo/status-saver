
package com.appsease.status.saver.preferences

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.appsease.status.saver.R
import com.appsease.status.saver.WhatSaveViewModel
import com.appsease.status.saver.adapter.ClientAdapter
import com.appsease.status.saver.databinding.DialogRecyclerviewBinding
import com.appsease.status.saver.extensions.getDefaultClient
import com.appsease.status.saver.extensions.setDefaultClient
import com.appsease.status.saver.extensions.showToast
import com.appsease.status.saver.interfaces.IClientCallback
import com.appsease.status.saver.model.WaClient
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class DefaultClientPreferenceDialog : DialogFragment(), OnShowListener, IClientCallback {

    private var _binding: DialogRecyclerviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WhatSaveViewModel by activityViewModel()

    private lateinit var clientAdapter: ClientAdapter
    private var defaultClient: WaClient? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRecyclerviewBinding.inflate(layoutInflater)
        binding.empty.setText(R.string.wa_is_not_installed_title)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = ClientAdapter(binding.root.context, R.layout.item_client, this).apply {
            registerAdapterDataObserver(adapterDataObserver)
        }.also {
            clientAdapter = it
        }

        defaultClient = requireContext().getDefaultClient()
        viewModel.getInstalledClients().observe(this) { installedClients ->
            clientAdapter.setClients(installedClients)
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.default_client_title)
            .setView(binding.root)
            .setNegativeButton(R.string.close_action, null)
            .create().also {
                it.setOnShowListener(this)
            }
    }

    private val adapterDataObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            binding.empty.isVisible = clientAdapter.itemCount == 0
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun clientClick(client: WaClient) {
        defaultClient = if (client == defaultClient) null else client
        if (defaultClient == null) {
            showToast(R.string.default_client_cleared)
        } else {
            showToast(getString(R.string.x_is_the_default_client_now, client.displayName))
        }
        requireContext().setDefaultClient(defaultClient)
        clientAdapter.notifyDataSetChanged()
    }

    override fun checkModeForClient(client: WaClient): Int {
        return if (client == defaultClient) IClientCallback.MODE_CHECKED else IClientCallback.MODE_UNCHECKED
    }

    override fun onShow(dialogInterface: DialogInterface) {
        viewModel.loadClients()
    }

    override fun onDismiss(dialog: DialogInterface) {
        clientAdapter.unregisterAdapterDataObserver(adapterDataObserver)
        super.onDismiss(dialog)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}