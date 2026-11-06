package com.indievlc.player

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

class MainActivity : AppCompatActivity() {

    private lateinit var libVLC: LibVLC
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var videoLayout: VLCVideoLayout
    private lateinit var btnPlayPause: Button
    private lateinit var seekBar: SeekBar
    private lateinit var timeDisplay: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var isTrackingTouch = false

    private val openDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            playMedia(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoLayout = findViewById(R.id.video_layout)
        btnPlayPause = findViewById(R.id.btn_play_pause)
        seekBar = findViewById(R.id.seek_bar)
        timeDisplay = findViewById(R.id.time_display)

        val args = ArrayList<String>().apply {
            add("--aout=opensles")
            add("--audio-time-stretch")
            add("-vvv")
        }
        libVLC = LibVLC(this, args)
        mediaPlayer = MediaPlayer(libVLC)

        findViewById<Button>(R.id.btn_choose).setOnClickListener {
            openDocument.launch(arrayOf("video/*"))
        }

        btnPlayPause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                btnPlayPause.text = "Play"
            } else {
                mediaPlayer.play()
                btnPlayPause.text = "Pause"
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && mediaPlayer.length > 0) {
                    val position = progress.toFloat() / 1000f
                    mediaPlayer.position = position
                }
            }
            override fun onStartTrackingTouch(sb: SeekBar?) { isTrackingTouch = true }
            override fun onStopTrackingTouch(sb: SeekBar?) { isTrackingTouch = false }
        })

        // Polling loop to update seekbar progress
        handler.post(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying && !isTrackingTouch) {
                    val progress = (mediaPlayer.position * 1000).toInt()
                    seekBar.progress = progress
                    val current = formatTime(mediaPlayer.time)
                    val total = formatTime(mediaPlayer.length)
                    timeDisplay.text = "$current / $total"
                }
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun playMedia(uri: Uri) {
        val media = Media(libVLC, uri).apply {
            setHWDecoderEnabled(true, false)
        }
        mediaPlayer.media = media
        media.release()

        mediaPlayer.attachViews(videoLayout, null, false, false)
        mediaPlayer.play()
        btnPlayPause.text = "Pause"
    }

    private fun formatTime(millis: Long): String {
        val totalSeconds = (millis / 1000).coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
        mediaPlayer.detachViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        libVLC.release()
    }
}
