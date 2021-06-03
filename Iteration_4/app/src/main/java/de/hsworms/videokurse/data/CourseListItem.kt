package de.hsworms.videokurse.data

data class CourseListItem (
    var productId: String = "",
    var title: String = "",
    var description: String = "",
    var complexity: CourseComplexity,
    var imageFileURL: String = ""
)  {
}

