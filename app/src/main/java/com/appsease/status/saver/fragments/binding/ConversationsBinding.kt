
package com.appsease.status.saver.fragments.binding

import com.appsease.status.saver.databinding.FragmentConversationsBinding

class ConversationsBinding(binding: FragmentConversationsBinding) {
    val toolbar = binding.toolbar
    val scrollView = binding.scrollView
    val switchWithContainer = binding.conversationsContent.switchView
    val recyclerView = binding.conversationsContent.recyclerView
    val emptyView = binding.conversationsContent.emptyConversationsView.root
    val emptyTitle = binding.conversationsContent.emptyConversationsView.text1
    val emptyText = binding.conversationsContent.emptyConversationsView.text2
    val progressIndicator = binding.conversationsContent.emptyConversationsView.progressIndicator
}