
package com.appsease.status.saver.fragments.binding

import com.appsease.status.saver.databinding.FragmentOnboardBinding

class OnboardBinding(binding: FragmentOnboardBinding) {
    val subtitle = binding.subtitle
    val directoryPermissionView = binding.directoryPermissionView.root
    val grantStorageButton = binding.storagePermissionView.grantButton
    val grantDirectoryAccessButton = binding.directoryPermissionView.grantAccessButton
    val revokeDirectoryAccessButton = binding.directoryPermissionView.revokeAccessButton
    val listDirectoriesButton = binding.directoryPermissionView.listDirectoriesButton
    val continueButton = binding.continueButton
    val nestedScrollView = binding.nestedScrollView
}