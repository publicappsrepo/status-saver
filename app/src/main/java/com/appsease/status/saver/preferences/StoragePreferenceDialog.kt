
package com.appsease.status.saver.preferences

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.appsease.status.saver.R
import com.appsease.status.saver.WhatSaveViewModel
import com.appsease.status.saver.databinding.DialogRecyclerviewBinding
import com.appsease.status.saver.databinding.ItemStorageVolumeBinding
import com.appsease.status.saver.storage.Storage
import com.appsease.status.saver.storage.StorageDevice
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class StoragePreferenceDialog : DialogFragment() {

    private val viewModel: WhatSaveViewModel by activityViewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogRecyclerviewBinding.inflate(layoutInflater)

        viewModel.getStorageDevices().observe(this) {
            if (it.isEmpty()) {
                binding.empty.setText(R.string.no_storage_device_found)
                binding.empty.isVisible = true
                binding.recyclerView.isVisible = false
            } else {
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = Adapter(requireContext(), it)
            }
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.statuses_location_title)
            .setView(binding.root)
            .setNegativeButton(R.string.close_action, null)
            .create().also {
                it.setOnShowListener {
                    viewModel.loadStorageDevices()
                }
            }
    }

    private class Adapter(private val context: Context, private val storageVolumes: List<StorageDevice>) :
        RecyclerView.Adapter<Adapter.ViewHolder>(), KoinComponent {

        private val storage: Storage by inject()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ItemStorageVolumeBinding.inflate(LayoutInflater.from(context), parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val storageVolume = storageVolumes[position]
            holder.radioButton?.text = storageVolume.name
            holder.radioButton?.isChecked = storage.isStatusesLocation(storageVolume)
        }

        override fun getItemCount(): Int = storageVolumes.size

        inner class ViewHolder(binding: ItemStorageVolumeBinding) :RecyclerView.ViewHolder(binding.root),
            View.OnClickListener {
            var radioButton: RadioButton? = binding.radioButton

            override fun onClick(view: View) {
                storage.setStatusesLocation(storageVolumes[layoutPosition])
                notifyDataSetChanged()
            }

            init {
                itemView.setOnClickListener(this)
            }
        }

    }
}