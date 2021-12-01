package de.hsworms.videokurse.data

import java.util.*

enum class TargetType {
    VIDEO,
    SUBMENU
}

data class NavigationListItem (
    val id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var toolbarTitle: String = "",
    var targetType: TargetType = TargetType.SUBMENU,
    var videoId:  String = "",
    var starting: Long = 0L,
    var newSection: Boolean = false
) {
}

