package de.hsworms.videokurse.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.hsworms.videokurse.model.CourseNavigationRepository
import de.hsworms.videokurse.data.CourseListItem
import de.hsworms.videokurse.data.NavigationListItem

class CourseNavigationViewModel(application: Application) : AndroidViewModel(application) {

    private val courseNavigationRepository = CourseNavigationRepository.get()

    lateinit var currentCourse: String

    fun getMyCourses(): List<CourseListItem> {
        return courseNavigationRepository.courseCatalog
    }

    fun getNavigationItemForId(navID: String): NavigationListItem? {
        return courseNavigationRepository.navigationItems[navID]
    }

    fun getNavigationItemsForID(navID: String): MutableList<NavigationListItem> {

        val result = mutableListOf<NavigationListItem>()

        var navItemsIds = courseNavigationRepository.menuHierarchy[navID]

        if (navItemsIds != null) {
            for (id in navItemsIds) {
                val n = courseNavigationRepository.navigationItems[id]
                if (n != null) result.add(n)
            }
        }

        return result
    }

    fun getToolbarTitle(navId: String): String? {
        val n = courseNavigationRepository.navigationItems[navId]
        return n?.toolbarTitle
    }

    fun loadCourseNavigation(courseId: String) {
        courseNavigationRepository.readCurrentCourseData(courseId)
        currentCourse = courseId
    }

    fun getVideoUrlForId(videoId: String) : String? {
        return courseNavigationRepository.videoFileInfos[videoId]?.fileName
    }

}



