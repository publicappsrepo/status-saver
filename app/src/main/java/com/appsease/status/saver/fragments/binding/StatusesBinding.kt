
package com.appsease.status.saver.fragments.binding

import com.appsease.status.saver.databinding.FragmentStatusesBinding

class StatusesBinding(binding: FragmentStatusesBinding) {
    val toolbar = binding.toolbar
    val collapsingToolbar = binding.collapsingToolbarLayout
    val swipeRefreshLayout = binding.swipeRefreshLayout
    val recyclerView = binding.recyclerView
    val emptyView = binding.emptyView.root
    val emptyTitle = binding.emptyView.emptyTitle
    val emptyText = binding.emptyView.emptyText
    val emptyButton = binding.emptyView.emptyButton
}