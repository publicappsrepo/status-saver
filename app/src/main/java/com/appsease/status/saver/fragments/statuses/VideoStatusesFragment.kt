
package com.appsease.status.saver.fragments.statuses

import android.os.Bundle
import android.view.View
import com.appsease.status.saver.model.StatusQueryResult
import com.appsease.status.saver.model.StatusType


class VideoStatusesFragment : StatusesFragment() {

    private val videoType: StatusType = StatusType.VIDEO

    override val lastResult: StatusQueryResult?
        get() = viewModel.getStatuses(videoType).value

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statusesActivity.setSupportActionBar(binding.toolbar)
        binding.collapsingToolbar.setTitle(getString(videoType.nameRes))
        viewModel.getStatuses(videoType).apply {
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
        viewModel.loadStatuses(videoType)
    }
}