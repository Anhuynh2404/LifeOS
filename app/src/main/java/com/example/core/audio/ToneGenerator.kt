package com.example.core.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlin.math.sin

object ToneGenerator {
    private var activeJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playAlarm(onLoop: Boolean = true) {
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

            // Rising cheerful morning arpeggio: C5, E5, G5, B5, C6
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 987.77, 1046.50)

            try {
                while (isActive) {
                    for (freq in notes) {
                        if (!isActive) break
                        val duration = 0.25 // seconds
                        val numSamples = (duration * sampleRate).toInt()
                        val buffer = ShortArray(numSamples)
                        for (i in 0 until numSamples) {
                            val time = i.toDouble() / sampleRate
                            val envelope = (1.0 - (i.toDouble() / numSamples)) // decaying envelope
                            val sample = sin(2 * Math.PI * freq * time) * envelope
                            buffer[i] = (sample * 28000).toInt().toShort()
                        }
                        track.write(buffer, 0, buffer.size)
                        delay(60)
                    }
                    delay(300)
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

            // Gentle Tibetan bell harmonic chord: 432 Hz fundamental + gentle overtone
            val duration = 4.0 // 4 seconds resonance
            val numSamples = (duration * sampleRate).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                val envelope = Math.exp(-time * 0.9)
                val sample = (0.7 * sin(2 * Math.PI * 432.0 * time) + 0.3 * sin(2 * Math.PI * 864.0 * time)) * envelope
                buffer[i] = (sample * 24000).toInt().toShort()
            }
            track.write(buffer, 0, buffer.size)
            delay(4000)
            track.stop()
            track.release()
        }
    }

    fun playSuccessChime() {
        scope.launch {
            val sampleRate = 44100
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
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
            val notes = doubleArrayOf(587.33, 880.0, 1174.66) // D5, A5, D6
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
    }
}
