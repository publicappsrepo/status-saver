
package com.appsease.status.saver.fragments.playback

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.transition.MaterialFadeThrough
import com.appsease.status.saver.R
import com.appsease.status.saver.WhatSaveViewModel
import com.appsease.status.saver.adapter.PlaybackAdapter
import com.appsease.status.saver.databinding.FragmentPlaybackBinding
import com.appsease.status.saver.extensions.applyHorizontalWindowInsets
import com.appsease.status.saver.fragments.base.BaseFragment
import com.appsease.status.saver.mvvm.PlaybackState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PlaybackFragment : BaseFragment(R.layout.fragment_playback), Player.Listener {

    private val viewModel: WhatSaveViewModel by activityViewModel()

    private var _binding: FragmentPlaybackBinding? = null
    private val binding get() = _binding!!

    private var adapter: PlaybackAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaybackBinding.bind(view)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        enterTransition = MaterialFadeThrough().addTarget(view)
        reenterTransition = MaterialFadeThrough().addTarget(view)

        binding.toolbar.applyHorizontalWindowInsets(padding = false)
        statusesActivity.setSupportActionBar(binding.toolbar)
        statusesActivity.supportActionBar?.title = null

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playbackState.first().let { state ->
                if (state != PlaybackState.Empty) {
                    adapter = PlaybackAdapter(this@PlaybackFragment, state.statuses)
                    binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                    binding.viewPager.offscreenPageLimit = 1
                    binding.viewPager.adapter = adapter
                    binding.viewPager.setCurrentItem(state.startPosition, false)
                    binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
                } else {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.updatePlayback(position)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
    }

    override fun onDestroyView() {
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        binding.viewPager.adapter = null
        adapter = null
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_STATUS = "status"
    }
}