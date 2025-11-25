
package com.appsease.status.saver.fragments.playback.video

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.android.material.button.MaterialButton
import com.appsease.status.saver.R
import com.appsease.status.saver.databinding.FragmentVideoBinding
import com.appsease.status.saver.extensions.applyWindowInsets
import com.appsease.status.saver.extensions.bumpPlaybackSpeed
import com.appsease.status.saver.extensions.playbackSpeed
import com.appsease.status.saver.extensions.preferences
import com.appsease.status.saver.extensions.resetPlaybackSpeed
import com.appsease.status.saver.fragments.playback.PlaybackChildFragment
import com.appsease.status.saver.model.PlaybackSpeed


class VideoFragment : PlaybackChildFragment(R.layout.fragment_video), Player.Listener {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!
    private val playerView get() = binding.playerView
    private val speedButton get() = playerView.findViewById<ImageView>(R.id.exo_speed_btn)

    private var player: ExoPlayer? = null
    private var toast: Toast? = null

    override val saveButton: MaterialButton
        get() = playerView.findViewById(R.id.save)

    override val shareButton: MaterialButton
        get() = playerView.findViewById(R.id.share)

    override val deleteButton: MaterialButton
        get() = playerView.findViewById(R.id.delete)

    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVideoBinding.bind(view)
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
        //playerView.controllerAutoShow = false
        playerView.controllerShowTimeoutMs = 3000
        setupControllerInsets()
        setupSpeedButton()
        initPlayer()
    }

    override fun onStart() {
        super.onStart()
        preparePlayer()
    }

    override fun onResume() {
        super.onResume()
        adjustPlaybackSpeed()
        player?.play()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onStop() {
        super.onStop()
        player?.stop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        player?.removeListener(this)
        player?.release()
        player = null
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_READY) {
            playerView.player = player
            if (isResumed) {
                player?.play()
            }
        }
    }

    private fun initPlayer() {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .setUsage(C.USAGE_MEDIA)
            .build()

        player = ExoPlayer.Builder(requireContext())
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()
    }

    private fun preparePlayer() {
        player?.addListener(this)
        player?.setMediaItem(MediaItem.fromUri(status.fileUri))
        player?.repeatMode = Player.REPEAT_MODE_ONE
        player?.prepare()
    }

    private fun setupControllerInsets() {
        val controllerView = playerView.findViewById<View>(R.id.controllerView)
        controllerView.applyWindowInsets(left = true, right = true, bottom = true)
    }

    private fun setupSpeedButton() {
        speedButton.setOnClickListener {
            adjustPlaybackSpeed(preferences().bumpPlaybackSpeed(), showToast = true)
        }
        speedButton.setOnLongClickListener {
            adjustPlaybackSpeed(preferences().resetPlaybackSpeed(), showToast = true)
            true
        }
    }

    private fun adjustPlaybackSpeed(currentSpeed: PlaybackSpeed = preferences().playbackSpeed, showToast: Boolean = false) {
        if (player?.isCommandAvailable(Player.COMMAND_SET_SPEED_AND_PITCH) == true) {
            speedButton.setImageResource(currentSpeed.iconRes)
            player?.playbackParameters = PlaybackParameters(currentSpeed.speed, currentSpeed.speed)
            if (showToast) {
                toast?.cancel()
                toast = Toast.makeText(
                    requireContext(),
                    "${getString(currentSpeed.labelRes)} (${currentSpeed.speed}x)",
                    Toast.LENGTH_SHORT
                ).also { it.show() }
            }
        }
    }
}