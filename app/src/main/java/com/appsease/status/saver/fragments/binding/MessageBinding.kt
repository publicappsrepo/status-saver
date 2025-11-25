
package com.appsease.status.saver.fragments.binding

import com.appsease.status.saver.databinding.FragmentMessageANumberBinding

class MessageBinding(binding: FragmentMessageANumberBinding) {
    val toolbar = binding.toolbar
    val scrollView = binding.scrollView
    val phoneInputLayout = binding.messageANumberContent.phoneNumberInputLayout
    val phoneNumber = binding.messageANumberContent.phoneNumber
    val message = binding.messageANumberContent.message
    val shareButton = binding.messageANumberContent.shareButton
    val sendButton = binding.messageANumberContent.sendButton
}