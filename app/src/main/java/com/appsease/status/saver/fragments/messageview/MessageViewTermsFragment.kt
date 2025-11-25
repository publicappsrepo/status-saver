
package com.appsease.status.saver.fragments.messageview

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.appsease.status.saver.R
import com.appsease.status.saver.databinding.FragmentMessageviewTermsBinding
import com.appsease.status.saver.extensions.applyBottomWindowInsets
import com.appsease.status.saver.extensions.formattedAsHtml
import com.appsease.status.saver.extensions.hasR
import com.appsease.status.saver.extensions.openSettings
import com.appsease.status.saver.extensions.startActivitySafe
import com.appsease.status.saver.fragments.base.BaseFragment
import com.appsease.status.saver.service.MessageCatcherService

class MessageViewTermsFragment : BaseFragment(R.layout.fragment_messageview_terms) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMessageviewTermsBinding.bind(view)
        statusesActivity.setSupportActionBar(binding.toolbar)
        binding.text1.text = getText()
        binding.continueButton.setOnClickListener {
            if (hasR()) {
                val componentName = ComponentName(requireContext(), MessageCatcherService::class.java)
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
                    .putExtra(Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME, componentName.flattenToString())
                startActivitySafe(intent)
            } else {
                requireContext().openSettings(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS, packageName = null)
            }
            findNavController().popBackStack()
        }
        binding.scrollView.applyBottomWindowInsets()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateMenu(menu, menuInflater)
        menu.clear()
    }

    private fun getText(): CharSequence {
        val terms = resources.getStringArray(R.array.message_view_terms)
        val sb = StringBuilder()
        for (i in terms.indices) {
            sb.append("${i + 1}: ").append(terms[i])
            if (i < terms.size - 1)
                sb.append("<br/><br/>")
        }
        return sb.toString().formattedAsHtml()
    }
}