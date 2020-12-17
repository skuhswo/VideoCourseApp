package de.hsworms.videokurse

import android.app.Application
import de.hsworms.videokurse.model.CourseNavigationRepository
import de.hsworms.videokurse.model.FileManager

class VideoCourseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // initializing repositories
        val localFileManager = FileManager.initialize(applicationContext)
        CourseNavigationRepository.initialize(localFileManager)
    }

}

