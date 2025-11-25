package com.appsease.status.saver.fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.appsease.status.saver.R
import com.appsease.status.saver.extensions.applyHorizontalWindowInsets
import com.appsease.status.saver.extensions.applyWindowInsets
import com.appsease.status.saver.extensions.currentFragment
import com.appsease.status.saver.extensions.getBottomInsets
import com.appsease.status.saver.extensions.hide
import com.appsease.status.saver.extensions.requireWindow
import com.appsease.status.saver.extensions.show
import com.appsease.status.saver.extensions.whichFragment
import com.appsease.status.saver.fragments.statuses.StatusesFragment
import com.appsease.status.saver.rating.MaterialRating
import com.appsease.status.saver.session.SessionManager
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigationrail.NavigationRailView


class MainFragment : Fragment(R.layout.fragment_main),
    NavigationBarView.OnItemReselectedListener,
    NavController.OnDestinationChangedListener {

    private lateinit var contentView: FrameLayout
    private lateinit var navigationView: NavigationBarView
    private lateinit var childNavController: NavController

    private var windowInsets: WindowInsetsCompat? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentView = view.findViewById(R.id.main_container)
        contentView.applyHorizontalWindowInsets(left = false)
        navigationView = view.findViewById(R.id.navigation_view)
        navigationView.setOnItemReselectedListener(this)
        if (navigationView is NavigationRailView) {
            navigationView.applyWindowInsets(top = true, left = true)
        }

        ViewCompat.setOnApplyWindowInsetsListener(requireWindow().decorView) { _, insets ->
            insets.also { windowInsets = it }
        }

        SessionManager.getInstance().setAppOpenCount(SessionManager.getInstance().appOpenCount + 1)

        if (SessionManager.getInstance().appOpenCount >= 3 && !SessionManager.getInstance().isRateGiven) {
            val feedBackDialog = MaterialRating()
            feedBackDialog.show(childFragmentManager, "rating")
        }

        childNavController = whichFragment<NavHostFragment>(R.id.main_container).navController
        childNavController.addOnDestinationChangedListener(this)
        navigationView.setupWithNavController(childNavController)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.imagesFragment,
            R.id.videosFragment,
            R.id.savedFragment,
            R.id.toolsFragment -> hideBottomBar(false)

            else -> hideBottomBar(true)
        }
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        val currentFragment = currentFragment(R.id.main_container)
        if (currentFragment is StatusesFragment) {
            currentFragment.scrollToTop()
        }
    }

    override fun onDestroyView() {
        childNavController.removeOnDestinationChangedListener(this)
        super.onDestroyView()
    }

    private fun hideBottomBar(hide: Boolean) {
        if (hide) navigationView.hide() else navigationView.show()
        if (navigationView is NavigationRailView) return
        val navHeight = resources.getDimensionPixelSize(R.dimen.bottom_nav_height)
        val navHeightWithInsets = navHeight + windowInsets.getBottomInsets()
        contentView.updatePadding(bottom = if (!hide) navHeightWithInsets else 0)
    }
}