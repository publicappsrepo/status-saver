package com.appsease.status.saver.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.appsease.status.saver.fragments.playback.PlaybackFragment
import com.appsease.status.saver.fragments.playback.image.ImageFragment
import com.appsease.status.saver.fragments.playback.video.VideoFragment
import com.appsease.status.saver.model.Status
import com.appsease.status.saver.model.StatusType

class PlaybackAdapter(
    private val fragment: Fragment,
    private val statuses: List<Status>
) : FragmentStateAdapter(fragment) {

    private val fragmentFactory: FragmentFactory = fragment
        .childFragmentManager
        .fragmentFactory

    override fun createFragment(position: Int): Fragment {
        val status = statuses[position]
        val playbackType = PlaybackFragmentType.entries.first {
            it.type == status.type
        }
        return fragmentFactory.instantiate(
            fragment.requireContext().classLoader,
            playbackType.className
        ).apply { arguments = bundleOf(PlaybackFragment.EXTRA_STATUS to status) }
    }

    override fun getItemCount(): Int = statuses.size

    enum class PlaybackFragmentType(val className: String, val type: StatusType) {
        IMAGE_VIEWER(ImageFragment::class.java.name, StatusType.IMAGE),
        VIDEO_PLAYER(VideoFragment::class.java.name, StatusType.VIDEO)
    }
}