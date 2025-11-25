
package com.appsease.status.saver.fragments.playback.image

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import coil3.load
import com.google.android.material.button.MaterialButton
import com.appsease.status.saver.R
import com.appsease.status.saver.databinding.FragmentImageBinding
import com.appsease.status.saver.extensions.Space
import com.appsease.status.saver.extensions.applyWindowInsets
import com.appsease.status.saver.fragments.playback.PlaybackChildFragment


class ImageFragment : PlaybackChildFragment(R.layout.fragment_image) {

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!
    private val imageView get() = binding.image

    override val saveButton: MaterialButton
        get() = binding.playbackActionButton.save

    override val shareButton: MaterialButton
        get() = binding.playbackActionButton.share

    override val deleteButton: MaterialButton
        get() = binding.playbackActionButton.delete

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentImageBinding.bind(view)
        binding.playbackActionButton.root.apply {
            applyWindowInsets(left = true, right = true, bottom = true, addedSpace = Space.viewPadding())
        }
        imageView.load(status.fileUri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}