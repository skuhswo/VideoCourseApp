package de.hsworms.videokurse.data

data class VideoFileInfo(
    val videoId: String = "",
    val fileName: String = "",
    val productId: String = "",
    val title: String = "",
    var fileChapters: MutableList<VideoSection>
) {
}


