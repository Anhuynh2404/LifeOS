package com.example.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import kotlinx.coroutines.*
import kotlin.math.sin

object ToneGenerator {
    private var activeJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playAlarm(onLoop: Boolean = true) {
        playToneSequence(
            notes = doubleArrayOf(523.25, 659.25, 783.99, 987.77, 1046.50),
            onLoop = onLoop
        )
    }

    fun playAlarmWithRingtone(context: Context, ringtoneName: String, onLoop: Boolean = true) {
        stop()

        // 1. Custom URI or phone storage audio file
        if (ringtoneName.startsWith("content://") || ringtoneName.startsWith("file://") || ringtoneName.startsWith("custom:")) {
            val uriStr = if (ringtoneName.startsWith("custom:")) {
                ringtoneName.substringAfter("custom:").substringBefore("|")
            } else ringtoneName

            try {
                val uri = Uri.parse(uriStr)
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setDataSource(context, uri)
                    isLooping = onLoop
                    prepare()
                    start()
                }
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (ringtoneName.contains("mặc định", ignoreCase = true)) {
            try {
                val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setDataSource(context, defaultUri)
                    isLooping = onLoop
                    prepare()
                    start()
                }
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 2. Built-in synthesized tones
        val notes = when {
            ringtoneName.contains("quân đội", ignoreCase = true) ->
                doubleArrayOf(392.0, 523.25, 659.25, 783.99, 1046.50, 783.99, 523.25)
            ringtoneName.contains("hùng tráng", ignoreCase = true) ->
                doubleArrayOf(440.0, 554.37, 659.25, 880.0, 1108.73)
            ringtoneName.contains("chim hót", ignoreCase = true) ->
                doubleArrayOf(1200.0, 1500.0, 1800.0, 1350.0, 1600.0)
            else ->
                doubleArrayOf(523.25, 659.25, 783.99, 987.77, 1046.50)
        }

        playToneSequence(notes, onLoop)
    }

    private fun playToneSequence(notes: DoubleArray, onLoop: Boolean) {
        stop()
        activeJob = scope.launch {
            val sampleRate = 44100
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf)
                .build()

            track.play()

            try {
                while (isActive) {
                    for (freq in notes) {
                        if (!isActive) break
                        val duration = 0.22
                        val numSamples = (duration * sampleRate).toInt()
                        val buffer = ShortArray(numSamples)
                        for (i in 0 until numSamples) {
                            val time = i.toDouble() / sampleRate
                            val envelope = (1.0 - (i.toDouble() / numSamples))
                            val sample = sin(2 * Math.PI * freq * time) * envelope
                            buffer[i] = (sample * 28000).toInt().toShort()
                        }
                        track.write(buffer, 0, buffer.size)
                        delay(50)
                    }
                    delay(250)
                    if (!onLoop) break
                }
            } finally {
                track.stop()
                track.release()
            }
        }
    }

    fun playMeditationBell() {
        stop()
        activeJob = scope.launch {
            val sampleRate = 44100
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf)
                .build()

            track.play()

            val duration = 4.0
            val numSamples = (duration * sampleRate).toInt()
            val buffer = ShortArray(numSamples)
            val f1 = 432.0
            val f2 = 864.0
            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                val env = Math.exp(-3.0 * (i.toDouble() / numSamples))
                val sample = (0.7 * sin(2 * Math.PI * f1 * time) + 0.3 * sin(2 * Math.PI * f2 * time)) * env
                buffer[i] = (sample * 25000).toInt().toShort()
            }
            track.write(buffer, 0, buffer.size)
            track.stop()
            track.release()
        }
    }

    fun playSuccessChime() {
        activeJob = scope.launch {
            val sampleRate = 44100
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf)
                .build()

            track.play()
            val notes = doubleArrayOf(587.33, 880.0, 1174.66)
            for (freq in notes) {
                val duration = 0.12
                val numSamples = (duration * sampleRate).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val envelope = 1.0 - (i.toDouble() / numSamples)
                    val sample = sin(2 * Math.PI * freq * time) * envelope
                    buffer[i] = (sample * 22000).toInt().toShort()
                }
                track.write(buffer, 0, buffer.size)
            }
            delay(400)
            track.stop()
            track.release()
        }
    }

    fun stop() {
        activeJob?.cancel()
        activeJob = null
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
    }
}
