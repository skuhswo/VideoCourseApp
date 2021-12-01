package de.hsworms.videokurse.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import de.hsworms.videokurse.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val EXTRA_VIDEO_URL = "video_id"
private const val EXTRA_STARTING_POS = "starting_pos"


@AndroidEntryPoint
class VideoPlayerActivity @Inject constructor(
):
    AppCompatActivity(),
    Player.Listener {

    private var startingPos = 0L
    private var videoUrl: String = ""

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var playerControlView: PlayerControlView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video_player)

        val extras = intent.extras
        if (extras != null) {
            videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL) ?: ""
            startingPos = intent.getLongExtra(EXTRA_STARTING_POS, 0)
        }

        playerView = findViewById(R.id.video_view)
        playerControlView = findViewById(R.id.player_control_view)

        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        playerControlView.player = player

        startVideo()
    }

    private fun startVideo() {
        val mediaItem: MediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
        player.seekTo(startingPos)
    }

    companion object {
        fun newIntent(
            packageContext: Context,
            videoUrl: String,
            startingPos: Long,
        ): Intent {

            return Intent(packageContext, VideoPlayerActivity::class.java).apply {
                putExtra(EXTRA_VIDEO_URL, videoUrl)
                putExtra(EXTRA_STARTING_POS, startingPos)
            }
        }
    }

}

