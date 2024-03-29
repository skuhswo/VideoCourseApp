package de.hsworms.videokurse.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import de.hsworms.videokurse.R
import de.hsworms.videokurse.data.TargetType
import de.hsworms.videokurse.viewmodel.CourseNavigationViewModel
import javax.inject.Inject


private const val COURSE_LIST_FRAGMENT = "CourseListFragment"

@AndroidEntryPoint
class MainActivity @Inject constructor(
):
        AppCompatActivity(),
        CourseListFragment.Callbacks,
        NavigationListFragment.Callbacks
{

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var drawerToggle: ActionBarDrawerToggle

    private val viewmodel: CourseNavigationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar

        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.hstr_1, R.string.hstr_1)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        toolbar.title = getString(R.string.toolbar_title_main)

        ensureViewModelInitalized()
    }

    private val onViewModelInitializedObserver = Observer<Boolean> { initialized ->
        if (initialized) {
            val fragment = CourseListFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment, COURSE_LIST_FRAGMENT)
                .commit()
        }
    }

    private fun ensureViewModelInitalized() {
        if ((viewmodel.isInitialized.value == null) || (viewmodel.isInitialized.value == false)) {
            viewmodel.isInitialized.observe(this, onViewModelInitializedObserver)
            viewmodel.initialize()
        }
    }

    override fun onCourseSelected(courseId: String) {
        viewmodel.loadCourseNavigation(courseId)
        onNavigationSelected(courseId)
    }

    override fun onNavigationSelected(navigationItemId: String) {
        val navigationListItem = viewmodel.getNavigationItemForId(navigationItemId)

        if (navigationListItem?.targetType == TargetType.VIDEO) {

            // play video
            val videoId = navigationListItem.videoId
            val videoUrl = viewmodel.getVideoUrlForId(videoId) ?: ""
            val startingPos = navigationListItem.starting

            val intent = VideoPlayerActivity.newIntent(
                this@MainActivity,
                videoUrl,
                startingPos
            )
            startActivity(intent)

        } else {

            // display submenu
            val fragment = NavigationListFragment.newInstance(navigationItemId)

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()

        }
    }

    override fun setToolbarTitle(title: String?) {
        toolbar.title = title ?: ""
    }

    override fun onMoreCoursesButtonSelected() {
        val toast = Toast.makeText(
            application, "'Weitere Kurse' angeklickt.",
            Toast.LENGTH_LONG
        )
        toast.show()
    }

    override fun onResume() {
        super.onResume()
        ensureViewModelInitalized()
    }

}