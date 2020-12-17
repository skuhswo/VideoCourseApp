package de.hsworms.videokurse.model

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import de.hsworms.videokurse.R
import de.hsworms.videokurse.data.*

class CourseNavigationRepository private constructor() {

    lateinit private var fileManager: FileManager

    var courseCatalog = mutableListOf<CourseListItem>()

    // current course
    var productId: String = ""

    // mapping from id to navigation item object
    var navigationItems = mutableMapOf<String, NavigationListItem>()

    // mapping from id to ids of sub-navigation items
    var menuHierarchy = mutableMapOf<String, MutableList<String>>()

    // mapping from video id to video file info object
    var videoFileInfos = mutableMapOf<String, VideoFileInfo>()

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
                courseCatalog.add(c)
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

    private fun processVideoFiles(
        videoFilesJsonArray: JsonArray<JsonObject>,
        title: String,
        product_id: String
    ) {

        for (vf in videoFilesJsonArray) {

            val videoId = vf.string("videoID") ?: ""
            val fileName = vf.string("fileName") ?: ""

            val chaptersJsonArray: JsonArray<JsonObject>? = vf.array("chapters")
            var chapters = mutableListOf<VideoSection>()
            if (chaptersJsonArray != null) {
                for (sh in chaptersJsonArray) {
                    val chapterId = sh.string("id") ?: ""
                    val chapterTitle = sh.string("title") ?: ""
                    val from = sh.long("from") ?: 0L
                    val to = sh.long("to") ?: 0L
                    val videoSection = VideoSection(chapterId, chapterTitle, from, to)
                    chapters.add(videoSection)
                }
            }

            val thisVideoFileInfo = VideoFileInfo(
                videoId,
                fileName,
                product_id,
                title,
                chapters
            )

            videoFileInfos[videoId] = thisVideoFileInfo
        }
    }

    private fun processSubmenu(submenuJsonArray: JsonArray<JsonObject>, id: String) {

        val submenuNavigationItems = mutableListOf<String>()

        for (sj in submenuJsonArray) {

            val n = NavigationListItem()

            val subId = n.id

            val title = sj.string("title") ?: ""
            n.title = title

            n.toolbarTitle = sj.string("toolbarTitle") ?: title

            val newSection = sj.boolean("newSection") ?: false
            n.newSection = newSection

            val subsubmenuJsonArray: JsonArray<JsonObject>? = sj.array("submenu")
            if (subsubmenuJsonArray != null) { // submenu is present
                processSubmenu(subsubmenuJsonArray, subId)
                n.targetType = TargetType.SUBMENU
            } else { // no submenu
                n.targetType = TargetType.VIDEO
                val videoId = sj.string("videoID") ?: ""
                val starting = sj.long("startingPos") ?: 0L
                n.videoId = videoId
                n.starting = starting
            }

            navigationItems[subId] = n
            submenuNavigationItems.add(subId)
        }

        menuHierarchy[id] = submenuNavigationItems

    }

    private fun processJsonIndexFile(jo: JsonObject?, title: String) {
        // add check if local version can still be used

        //val toolbarTitle = jo?.string("toolbarTitle") ?: title

        val videoFilesArray: JsonArray<JsonObject>? = jo?.array("videoFiles")
        if (videoFilesArray != null) {
            processVideoFiles(videoFilesArray, title, productId)
        }

        val submenuJsonArray: JsonArray<JsonObject>? = jo?.array("submenu")
        if (submenuJsonArray != null) {
            processSubmenu(submenuJsonArray, productId)
        }
    }

    fun readCurrentCourseData(courseId: String) {

        // currently hard coded for testing, has to be removed later on
        val resId = R.raw.course_index_file
        val inputStream = fileManager.readFileFromAppRessources(resId)
        val parser = Parser.default()
        val jo = parser.parse(inputStream) as JsonObject

        navigationItems.clear()
        videoFileInfos.clear()

        val c = courseCatalog.find { it.productId == courseId }
        productId = c?.productId ?: ""
        val title = c?.title ?: ""

        val n = NavigationListItem(
            toolbarTitle = c?.title ?: ""
        )

        if (c != null)
            navigationItems[c.productId] = n

        processJsonIndexFile(jo, title)
    }

}