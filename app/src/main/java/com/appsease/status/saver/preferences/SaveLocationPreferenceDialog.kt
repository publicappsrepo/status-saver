
package com.appsease.status.saver.preferences

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.appsease.status.saver.R
import com.appsease.status.saver.databinding.DialogSaveLocationBinding
import com.appsease.status.saver.extensions.check
import com.appsease.status.saver.extensions.preferences
import com.appsease.status.saver.extensions.saveLocation
import com.appsease.status.saver.model.SaveLocation


class SaveLocationPreferenceDialog : DialogFragment(), View.OnClickListener {

    private var _binding: DialogSaveLocationBinding? = null
    private val binding get() = _binding!!

    private var selectedLocation: SaveLocation? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogSaveLocationBinding.inflate(layoutInflater)
        binding.dcimOption.setOnClickListener(this)
        binding.fileTypeOption.setOnClickListener(this)
        setSaveLocation(preferences().saveLocation)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.save_location_title)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                selectedLocation?.let {
                    preferences().saveLocation = it
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    private fun setSaveLocation(location: SaveLocation) {
        selectedLocation = location
        when (location) {
            SaveLocation.DCIM -> {
                binding.dcimRadio.check(true)
                binding.fileTypeRadio.check(false)
            }

            SaveLocation.ByFileType -> {
                binding.dcimRadio.check(false)
                binding.fileTypeRadio.check(true)
            }
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.dcimOption -> setSaveLocation(SaveLocation.DCIM)
            binding.fileTypeOption -> setSaveLocation(SaveLocation.ByFileType)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}