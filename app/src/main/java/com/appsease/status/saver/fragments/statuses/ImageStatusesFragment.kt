
package com.appsease.status.saver.fragments.statuses

import android.os.Bundle
import android.view.View
import com.appsease.status.saver.model.StatusQueryResult
import com.appsease.status.saver.model.StatusType


class ImageStatusesFragment : StatusesFragment() {

    private val imageType: StatusType = StatusType.IMAGE

    override val lastResult: StatusQueryResult?
        get() = viewModel.getStatuses(imageType).value

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statusesActivity.setSupportActionBar(binding.toolbar)
        binding.collapsingToolbar.setTitle(getString(imageType.nameRes))
        viewModel.getStatuses(imageType).apply {
            observe(viewLifecycleOwner) { result ->
                data(result)
            }
        }.also { liveData ->
            if (liveData.value == StatusQueryResult.Idle) {
                onRefresh()
            }
        }
    }

    override fun onRefresh() {
        viewModel.loadStatuses(imageType)
    }

}