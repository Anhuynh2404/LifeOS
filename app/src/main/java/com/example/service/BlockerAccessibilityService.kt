package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import com.example.core.audio.ToneGenerator
import com.example.feature.focus.BlockOverlayActivity

class BlockerAccessibilityService : AccessibilityService() {

    companion object {
        var isServiceRunning = false
            private set

        private var lastBlockTimestamp = 0L
        private const val BLOCK_DEBOUNCE_MS = 1500L
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        isServiceRunning = true
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        val pkgName = event.packageName?.toString() ?: return

        // Ignore system UI and our own app
        if (pkgName == packageName ||
            pkgName == "com.android.systemui" ||
            pkgName == "com.google.android.inputmethod.latin" ||
            pkgName.contains("launcher")
        ) {
            return
        }

        // Check if Pomodoro is currently active
        val prefs = getSharedPreferences("lifeos_focus_state", Context.MODE_PRIVATE)
        val isFocusActive = prefs.getBoolean("is_focus_active", false)
        if (!isFocusActive) {
            return
        }

        // Check remaining time using elapsedRealtime
        val startElapsed = prefs.getLong("start_elapsed_realtime", 0L)
        val targetSeconds = prefs.getInt("target_seconds", 25 * 60)
        val elapsedSeconds = (SystemClock.elapsedRealtime() - startElapsed) / 1000L
        if (elapsedSeconds >= targetSeconds) {
            // Session finished
            prefs.edit().putBoolean("is_focus_active", false).apply()
            return
        }

        // Check if current package is in blocked set
        val blockedAppsString = prefs.getString("blocked_packages", "") ?: ""
        val blockedApps = if (blockedAppsString.isNotBlank()) {
            blockedAppsString.split(",").toSet()
        } else {
            FocusService.DEFAULT_BLOCKED_PACKAGES
        }

        if (blockedApps.contains(pkgName)) {
            val now = SystemClock.uptimeMillis()
            if (now - lastBlockTimestamp > BLOCK_DEBOUNCE_MS) {
                lastBlockTimestamp = now

                // 1. Instantly kick user back to home screen
                performGlobalAction(GLOBAL_ACTION_HOME)

                // 2. Play warning tone
                ToneGenerator.playAlarm(onLoop = false)

                // 3. Record blocked attempt in service and state
                val readableName = getReadableAppName(pkgName)
                FocusService.recordBlockedAttempt(readableName)

                val blockedCount = prefs.getInt("blocked_count", 0) + 1
                prefs.edit().putInt("blocked_count", blockedCount).apply()

                // 4. Launch BlockOverlayActivity on top
                val overlayIntent = Intent(this, BlockOverlayActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("blocked_app_name", readableName)
                    putExtra("blocked_package", pkgName)
                }
                startActivity(overlayIntent)
            }
        }
    }

    override fun onInterrupt() {
        // Accessibility interrupted
    }

    override fun onDestroy() {
        isServiceRunning = false
        super.onDestroy()
    }

    private fun getReadableAppName(pkg: String): String {
        return when {
            pkg.contains("musically") || pkg.contains("trill") -> "TikTok"
            pkg.contains("facebook.katana") || pkg.contains("facebook.lite") -> "Facebook"
            pkg.contains("instagram") -> "Instagram"
            pkg.contains("youtube") -> "YouTube"
            pkg.contains("twitter") || pkg == "com.twitter.android" -> "X / Twitter"
            pkg.contains("shopee") -> "Shopee"
            pkg.contains("netflix") -> "Netflix"
            pkg.contains("reddit") -> "Reddit"
            pkg.contains("snapchat") -> "Snapchat"
            pkg.contains("kgvn") || pkg.contains("freefire") || pkg.contains("pubg") -> "Trò chơi Game"
            else -> "Ứng dụng xao nhãng"
        }
    }
}
