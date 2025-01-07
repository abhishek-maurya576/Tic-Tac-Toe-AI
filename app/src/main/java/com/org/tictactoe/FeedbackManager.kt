package com.org.tictactoe

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

class FeedbackManager(private val context: Context) {
    private var moveSound: MediaPlayer? = null
    private var winSound: MediaPlayer? = null
    private var drawSound: MediaPlayer? = null
    private var startButtonSound: MediaPlayer? = null
    
    private val vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    init {
        try {
            moveSound = MediaPlayer.create(context, R.raw.move_sound)
            winSound = MediaPlayer.create(context, R.raw.win_sound)
            drawSound = MediaPlayer.create(context, R.raw.draw_sound)
            startButtonSound = MediaPlayer.create(context, R.raw.start_button)
        } catch (e: Exception) {
            Log.e("FeedbackManager", "Error initializing sound effects", e)
        }
    }

    fun playMoveEffect() {
        try {
            moveSound?.start()
            vibrate(50L)
        } catch (e: Exception) {
            Log.e("FeedbackManager", "Error playing move effect", e)
        }
    }

    fun playWinEffect() {
        try {
            winSound?.start()
            vibratePattern(longArrayOf(0, 100, 100, 100))
        } catch (e: Exception) {
            Log.e("FeedbackManager", "Error playing win effect", e)
        }
    }

    fun playDrawEffect() {
        try {
            drawSound?.start()
            vibratePattern(longArrayOf(0, 50, 50, 50))
        } catch (e: Exception) {
            Log.e("FeedbackManager", "Error playing draw effect", e)
        }
    }

    fun playStartButtonEffect() {
        try {
            startButtonSound?.start()
            vibrate(100L)
        } catch (e: Exception) {
            Log.e("FeedbackManager", "Error playing start button effect", e)
        }
    }

    private fun vibrate(duration: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        } catch (e: Exception) {
            Log.e("FeedbackManager", "Error during vibration", e)
        }
    }

    private fun vibratePattern(pattern: LongArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            Log.e("FeedbackManager", "Error during pattern vibration", e)
        }
    }

    fun release() {
        try {
            moveSound?.release()
            winSound?.release()
            drawSound?.release()
            startButtonSound?.release()
            moveSound = null
            winSound = null
            drawSound = null
            startButtonSound = null
        } catch (e: Exception) {
            Log.e("FeedbackManager", "Error releasing resources", e)
        }
    }
}
