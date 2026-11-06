package com.indievlc.player

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

class MainActivity : ComponentActivity() {
    private var libVlc: LibVLC? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var videoLayout: VLCVideoLayout
    private lateinit var status: TextView

    private val pickVideo = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { playVideo(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(5, 5, 5)
        window.navigationBarColor = Color.rgb(5, 5, 5)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 24)
            setBackgroundColor(Color.rgb(5, 5, 5))
        }
        val brand = TextView(this).apply {
            text = "IndieVLC"
            textSize = 30f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER_VERTICAL
        }
        val subtitle = TextView(this).apply {
            text = "Play everything. Your way."
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 4, 0, 18)
        }
        videoLayout = VLCVideoLayout(this)
        videoLayout.setBackgroundColor(Color.BLACK)
        status = TextView(this).apply {
            text = "Your player is ready. Choose a video to begin."
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 16, 0, 16)
        }
        val open = Button(this).apply {
            text = "Choose a video"
            setOnClickListener { pickVideo.launch(arrayOf("video/*", "audio/*")) }
        }
        root.addView(brand)
        root.addView(subtitle)
        root.addView(videoLayout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        root.addView(status)
        root.addView(open)
        setContentView(root)
    }

    private fun playVideo(uri: Uri) {
        try {
            if (libVlc == null) libVlc = LibVLC(this, arrayListOf("--no-drop-late-frames", "--no-skip-frames"))
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer(libVlc!!).also { it.attachViews(videoLayout, null, false, false) }
            }
            val media = Media(libVlc!!, uri).apply { setHWDecoderEnabled(true, false) }
            mediaPlayer?.media = media
            media.release()
            mediaPlayer?.play()
            status.text = "Playing selected media"
        } catch (e: Exception) {
            status.text = "Couldn't open this file: ${e.localizedMessage ?: "unknown error"}"
        }
    }

    override fun onStop() {
        mediaPlayer?.pause()
        super.onStop()
    }

    override fun onDestroy() {
        mediaPlayer?.detachViews()
        mediaPlayer?.release()
        mediaPlayer = null
        libVlc?.release()
        libVlc = null
        super.onDestroy()
    }
}
