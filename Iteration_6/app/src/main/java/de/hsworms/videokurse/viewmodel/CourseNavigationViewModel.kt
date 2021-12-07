package de.hsworms.videokurse.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hsworms.videokurse.model.CourseNavigationRepository
import de.hsworms.videokurse.data.CourseListItem
import de.hsworms.videokurse.data.NavigationListItem
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseNavigationViewModel @Inject constructor(
    private val courseNavigationRepository: CourseNavigationRepository
) : ViewModel() {

    private var _initialized = MutableLiveData(false)

    val isInitialized: LiveData<Boolean>
        get() { return _initialized }

    private val onRepoInitializationChangedObserver = Observer<Boolean> {
        _initialized.value = courseNavigationRepository.isInitialized.value
    }

    init {
        courseNavigationRepository.isInitialized.observeForever(onRepoInitializationChangedObserver)
    }

    fun initialize() = viewModelScope.launch {
        courseNavigationRepository.initialize()
    }

    override fun onCleared() {
        courseNavigationRepository.isInitialized.removeObserver(onRepoInitializationChangedObserver)
        super.onCleared()
    }

    lateinit var currentCourse: String

    fun getMyCourses(): List<CourseListItem> {
        return courseNavigationRepository.courseCatalog
    }

    fun getNavigationItemForId(navID: String): NavigationListItem? {
        return courseNavigationRepository.navigationItems[navID]
    }

    fun getNavigationItemsForID(navID: String): MutableList<NavigationListItem> {

        val result = mutableListOf<NavigationListItem>()

        val navItemsIds = courseNavigationRepository.menuHierarchy[navID]

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

    fun loadCourseNavigation(courseId: String) = viewModelScope.launch {
        courseNavigationRepository.readCurrentCourseData(courseId)
        currentCourse = courseId
    }

    fun getVideoUrlForId(videoId: String) : String? {
        return courseNavigationRepository.videoFileInfos[videoId]?.fileName
    }

}



