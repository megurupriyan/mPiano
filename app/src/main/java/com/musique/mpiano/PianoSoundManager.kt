package com.musique.mpiano

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlin.math.PI
import kotlin.math.sin

class PianoSoundManager {

    private val sampleRate = 44100
    private var bufferSize = AudioTrack.getMinBufferSize(
        sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT
    ) * 10
    private val audioTrack = AudioTrack(
        AudioManager.STREAM_MUSIC,
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufferSize,
        AudioTrack.MODE_STREAM
    )

    private var isPlaying = false

    private var prevNote: String? = null

    fun playSound(note: String) {
//        if (!prevNote.isNullOrEmpty() && prevNote != note)
//            stopImmediately()
        if (isPlaying) {
            return
        }

        val frequency = notesFrequencies[note] ?: return
        val angularFrequency = 2 * PI * frequency / sampleRate
        val samples = ShortArray(bufferSize)
        prevNote = note
        isPlaying = true
        startTime = System.currentTimeMillis()
        audioTrack.play()

        Thread {
            while (isPlaying) {
                for (i in 0 until bufferSize) {
                    val sample = (sin(i * angularFrequency) * Short.MAX_VALUE).toInt().toShort()
                    samples[i] = sample
                }
                audioTrack.write(samples, 0, bufferSize)
            }
        }.start()
    }

    private var startTime: Long = 0

    fun stopSound() {
        val endTime = System.currentTimeMillis()
        val actualDuration = endTime - startTime
        if (actualDuration > 100) {
            stopImmediately()
            return
        }

        val delayMillis = 100L - actualDuration
        Handler(Looper.getMainLooper()).postDelayed({
            stopImmediately()
        }, delayMillis)
    }

    private fun stopImmediately() {
        if (!isPlaying) return
        isPlaying = false
        audioTrack.stop()
        audioTrack.flush()
        Log.e("", "stopImmediately ")
    }

    fun release() {
        isPlaying = false
        audioTrack.release()
    }

}
