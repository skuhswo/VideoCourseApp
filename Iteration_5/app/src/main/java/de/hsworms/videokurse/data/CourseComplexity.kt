package de.hsworms.videokurse.data

data class ComplexityValue (
    var name: String = "",
    var color: String = ""
)  {
}

enum class CourseComplexity(val v: ComplexityValue) {
    all(ComplexityValue("alle", "#FFFFFF")),
    high(ComplexityValue("hoch", "#E34732")),
    medium(ComplexityValue("mittel", "#E0B100")),
    low(ComplexityValue("niedrig", "#099161")),
    NA(ComplexityValue("ohne", "#FFFFFF"))
}


