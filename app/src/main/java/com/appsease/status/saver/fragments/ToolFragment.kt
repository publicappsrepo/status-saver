
package com.appsease.status.saver.fragments

import android.app.Activity
import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.getSystemService
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.appsease.status.saver.R
import com.appsease.status.saver.WhatSaveViewModel
import com.appsease.status.saver.databinding.FragmentToolBinding
import com.appsease.status.saver.extensions.applyBottomWindowInsets
import com.appsease.status.saver.extensions.isMessageViewEnabled
import com.appsease.status.saver.extensions.isNotificationListener
import com.appsease.status.saver.extensions.launchSafe
import com.appsease.status.saver.extensions.preferences
import com.appsease.status.saver.fragments.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ToolFragment : BaseFragment(R.layout.fragment_tool) {

    private val viewModel: WhatSaveViewModel by activityViewModel()
    private val keyguardManager: KeyguardManager by lazy { requireContext().getSystemService()!! }
    private lateinit var credentialsRequestLauncher: ActivityResultLauncher<Intent>

    private var _binding: FragmentToolBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentToolBinding.bind(view)
        binding.scrollView.applyBottomWindowInsets()
        binding.msgANumber.setOnClickListener {
            findNavController().navigate(R.id.messageFragment)
        }
        binding.messageView.setOnClickListener {
            if (requireContext().isNotificationListener()) {
                openMessageView()
            } else {
                findNavController().navigate(R.id.messageViewTermsFragment)
            }
        }

        statusesActivity.setSupportActionBar(binding.toolbar)
        credentialsRequestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                viewModel.unlockMessageView()
            } else {
                viewModel.getMessageViewLockObservable().removeObserver(credentialObserver)
            }
        }

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
    }

    private fun openMessageView() {
        viewModel.getMessageViewLockObservable().observe(viewLifecycleOwner, credentialObserver)
    }

    @Suppress("DEPRECATION")
    private val credentialObserver = Observer<Boolean> { isUnlocked ->
        if (isUnlocked || !preferences().isMessageViewEnabled) {
            findNavController().navigate(R.id.conversationsFragment)
        } else {
            val credentialsRequestIntel = keyguardManager.createConfirmDeviceCredentialIntent(
                getString(R.string.message_view),
                getString(R.string.confirm_device_credentials)
            )
            if (credentialsRequestIntel != null) {
                credentialsRequestLauncher.launchSafe(credentialsRequestIntel)
            } else {
                viewModel.unlockMessageView()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}