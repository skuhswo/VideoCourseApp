package de.hsworms.videokurse.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import de.hsworms.videokurse.R
import android.util.Log

private const val EXTRA_VIDEO_URL = "video_id"
private const val EXTRA_STARTING_POS = "starting_pos"


class VideoPlayerActivity :
    AppCompatActivity(),
    Player.EventListener {

    private lateinit var videoSource: MediaSource
    private var startingPos = 0L
    private var videoUrl: String = ""

    private lateinit var player: SimpleExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var playerControlView: PlayerControlView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video_player)

        val extras = intent.getExtras()
        if (extras != null) {
            videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL) ?: ""
            startingPos = intent.getLongExtra(EXTRA_STARTING_POS, 0)
        }

        playerView = findViewById(R.id.video_view)
        playerControlView = findViewById(R.id.player_control_view);

        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player
        playerControlView.player = player

        startVideo()
    }

    fun startVideo() {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            this, Util.getUserAgent(this, "VideoCourseApp")
        )

    Log.d("VP", videoUrl)

        videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(videoUrl))

        player.prepare(videoSource)
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

