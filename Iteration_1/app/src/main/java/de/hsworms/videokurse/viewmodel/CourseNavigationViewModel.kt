package de.hsworms.videokurse.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.hsworms.videokurse.model.CourseNavigationRepository
import de.hsworms.videokurse.data.CourseListItem

class CourseNavigationViewModel(application: Application) : AndroidViewModel(application) {

    private val coursesNavigationRepository = CourseNavigationRepository.get()

    fun getMyCourses(): List<CourseListItem> {
        return coursesNavigationRepository.coursesCatalog
    }

}

