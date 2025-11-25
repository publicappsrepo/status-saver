
package com.appsease.status.saver.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.view.doOnPreDraw
import com.google.android.material.transition.MaterialFadeThrough
import com.appsease.status.saver.App
import com.appsease.status.saver.BuildConfig
import com.appsease.status.saver.R
import com.appsease.status.saver.databinding.FragmentAboutBinding
import com.appsease.status.saver.extensions.applyBottomWindowInsets
import com.appsease.status.saver.extensions.openWeb
import com.appsease.status.saver.fragments.base.BaseFragment

class AboutFragment : BaseFragment(R.layout.fragment_about) {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        enterTransition = MaterialFadeThrough().addTarget(view)
        reenterTransition = MaterialFadeThrough().addTarget(view)
        view.doOnPreDraw { startPostponedEnterTransition() }

        _binding = FragmentAboutBinding.bind(view)
        binding.scrollView.applyBottomWindowInsets()
        binding.toolbar.setTitle(R.string.about_title)
        binding.appVersion.setSummary(getString(R.string.version_x, BuildConfig.VERSION_NAME))
        binding.moreApps.setOnClickListener { statusesActivity.openWeb("https://play.google.com/store/apps/developer?id=Appsease") }
        statusesActivity.setSupportActionBar(binding.toolbar)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateMenu(menu, menuInflater)
        menu.clear()
        menuInflater.inflate(R.menu.menu_about, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_share_app -> {
                ShareCompat.IntentBuilder(requireContext())
                    .setChooserTitle(R.string.share_app)
                    .setText(getString(R.string.app_share))
                    .setType("text/plain")
                    .startChooser()
                true
            }

            else -> super.onMenuItemSelected(menuItem)
        }
    }
}