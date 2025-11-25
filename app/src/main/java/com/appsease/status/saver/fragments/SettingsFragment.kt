
package com.appsease.status.saver.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.transition.MaterialFadeThrough
import com.appsease.status.saver.R
import com.appsease.status.saver.databinding.FragmentSettingsBinding
import com.appsease.status.saver.extensions.IsSAFRequired
import com.appsease.status.saver.extensions.PREFERENCE_ANALYTICS_ENABLED
import com.appsease.status.saver.extensions.PREFERENCE_GRANT_PERMISSIONS
import com.appsease.status.saver.extensions.PREFERENCE_JUST_BLACK_THEME
import com.appsease.status.saver.extensions.PREFERENCE_LANGUAGE
import com.appsease.status.saver.extensions.PREFERENCE_QUICK_DELETION
import com.appsease.status.saver.extensions.PREFERENCE_STATUSES_LOCATION
import com.appsease.status.saver.extensions.PREFERENCE_THEME_MODE
import com.appsease.status.saver.extensions.PREFERENCE_USE_CUSTOM_FONT
import com.appsease.status.saver.extensions.applyBottomWindowInsets
import com.appsease.status.saver.extensions.findActivityNavController
import com.appsease.status.saver.extensions.getDefaultDayNightMode
import com.appsease.status.saver.extensions.hasR
import com.appsease.status.saver.extensions.isNightModeEnabled
import com.appsease.status.saver.extensions.openWeb
import com.appsease.status.saver.extensions.whichFragment
import com.appsease.status.saver.fragments.base.BaseFragment
import com.appsease.status.saver.preferences.DefaultClientPreference
import com.appsease.status.saver.preferences.DefaultClientPreferenceDialog
import com.appsease.status.saver.preferences.SaveLocationPreference
import com.appsease.status.saver.preferences.SaveLocationPreferenceDialog
import com.appsease.status.saver.preferences.StoragePreference
import com.appsease.status.saver.preferences.StoragePreferenceDialog
import com.appsease.status.saver.rating.MaterialRating

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSettingsBinding.bind(view)
        postponeEnterTransition()
        enterTransition = MaterialFadeThrough().addTarget(view)
        reenterTransition = MaterialFadeThrough().addTarget(view)
        view.doOnPreDraw { startPostponedEnterTransition() }
        statusesActivity.setSupportActionBar(binding.toolbar)

        var settingsFragment: SettingsFragment? = whichFragment(R.id.settings_container)
        if (settingsFragment == null) {
            settingsFragment = SettingsFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.settings_container, settingsFragment)
                .commit()
        } else {
            settingsFragment.invalidatePreferences()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateMenu(menu, menuInflater)
        menu.clear()
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preferences)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            listView.applyBottomWindowInsets()

            findPreference<Preference>("about")?.setOnPreferenceClickListener {
                findNavController().navigate(R.id.aboutFragment)
                true
            }
            findPreference<Preference>("privacy_policy")?.setOnPreferenceClickListener {
                requireContext().openWeb("https://sites.google.com/view/privacypolicyreestatussaver/home")
                true
            }

            findPreference<Preference>("rateus")?.setOnPreferenceClickListener {
                val feedBackDialog = MaterialRating()
                feedBackDialog.show(childFragmentManager, "rating")
                true
            }
            invalidatePreferences()
        }

        override fun onDisplayPreferenceDialog(preference: Preference) {
            when (preference) {
                is SaveLocationPreference -> {
                    SaveLocationPreferenceDialog().show(childFragmentManager, "SAVE_LOCATION")
                    return
                }

                is DefaultClientPreference -> {
                    DefaultClientPreferenceDialog().show(childFragmentManager, "INSTALLED_CLIENTS")
                    return
                }

                is StoragePreference -> {
                    StoragePreferenceDialog().show(childFragmentManager, "STORAGE_DIALOG")
                    return
                }
            }
            super.onDisplayPreferenceDialog(preference)
        }

        fun invalidatePreferences() {
            findPreference<Preference>(PREFERENCE_THEME_MODE)
                ?.setOnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                    val themeName = newValue as String
                    AppCompatDelegate.setDefaultNightMode(getDefaultDayNightMode(themeName))
                    true
                }
            findPreference<SwitchPreferenceCompat>(PREFERENCE_JUST_BLACK_THEME)
                ?.apply {
                    isEnabled = requireContext().isNightModeEnabled
                    setOnPreferenceChangeListener { _, _ ->
                        requireActivity().recreate()
                        true
                    }
                }
            findPreference<Preference>(PREFERENCE_USE_CUSTOM_FONT)
                ?.setOnPreferenceChangeListener { _, _ ->
                    requireActivity().recreate()
                    true
                }
            findPreference<Preference>(PREFERENCE_LANGUAGE)?.setOnPreferenceChangeListener { _, newValue ->
                val languageName = newValue as String
                if (languageName == "auto") {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
                } else {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageName))
                }
                true
            }

            if (IsSAFRequired) {
                findPreference<Preference>(PREFERENCE_STATUSES_LOCATION)?.isVisible = false
                findPreference<Preference>(PREFERENCE_GRANT_PERMISSIONS)?.apply {
                    isVisible = true
                    setOnPreferenceClickListener {
                        findActivityNavController(R.id.main_container)
                            .navigate(R.id.onboardFragment, bundleOf("isFromSettings" to true))
                        true
                    }
                }
                if (hasR()) {
                    findPreference<Preference>(PREFERENCE_QUICK_DELETION)?.isVisible = false
                }
            }
        }
    }
}