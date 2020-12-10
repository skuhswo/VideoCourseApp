package de.hsworms.videokurse.model

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import de.hsworms.videokurse.R
import de.hsworms.videokurse.data.CourseComplexity
import de.hsworms.videokurse.data.CourseListItem

class CourseNavigationRepository private constructor() {

    lateinit private var fileManager: FileManager

    var coursesCatalog = mutableListOf<CourseListItem>()

    companion object {
        private var INSTANCE: CourseNavigationRepository? = null

        fun initialize(fileManager: FileManager) {
            if (INSTANCE == null) {
                INSTANCE = CourseNavigationRepository()
            }
            INSTANCE?.fileManager = fileManager
            INSTANCE?.readCourseCatalog()
        }

        fun get(): CourseNavigationRepository {
            return INSTANCE
                ?: throw IllegalStateException("LogovidNavigationRepository not initialized.")
        }
    }

    private fun processCatalogJsonFile(jo: JsonObject) {
        val coursesJsonArray: JsonArray<JsonObject>? = jo.array("courses")

        if (coursesJsonArray != null) {
            for (courseJsonObject in coursesJsonArray) {
                val c = CourseListItem(
                    productId = courseJsonObject.string("product_id") ?: "",
                    title = courseJsonObject.string("title") ?: "",
                    description = courseJsonObject.string("description") ?: "",
                    complexity = CourseComplexity.valueOf(courseJsonObject.string("complexity") ?: "ohne"),
                    imageFileURL = courseJsonObject.string("image_file_url") ?: ""
                )
                coursesCatalog.add(c)
            }
        }
    }

    private fun readCourseCatalog() {
        val resId = R.raw.course_catalog
        val inputStream = fileManager.readFileFromAppRessources(resId)

        val parser = Parser.default()
        val jo = parser.parse(inputStream) as JsonObject

        processCatalogJsonFile(jo)
    }

}
