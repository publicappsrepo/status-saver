
package com.appsease.status.saver.fragments.statuses

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.appsease.status.saver.R
import com.appsease.status.saver.adapter.StatusAdapter
import com.appsease.status.saver.model.StatusQueryResult


class SavedStatusesFragment : StatusesFragment() {

    override val lastResult: StatusQueryResult?
        get() = viewModel.getSavedStatuses().value

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statusesActivity.setSupportActionBar(binding.toolbar)
        binding.collapsingToolbar.setTitle(getString(R.string.saved_label))
        viewModel.getSavedStatuses().apply {
            observe(viewLifecycleOwner) { result ->
                data(result)
            }
        }.also { liveData ->
            if (liveData.value == StatusQueryResult.Idle) {
                onRefresh()
            }
        }
    }

    override fun createAdapter(): StatusAdapter =
        StatusAdapter(
            requireActivity(),
            this,
            isSaveEnabled = false,
            isDeleteEnabled = true,
            isWhatsAppIconEnabled = false
        )

    override fun onRefresh() {
        viewModel.loadSavedStatuses()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {}
}