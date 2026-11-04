package com.example.core.qr

import android.content.Context
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object LaptopQrHelper {

    private const val PREFS_NAME = "lifeos_qr_prefs"
    private const val KEY_DEVICE_SECRET = "device_secret"
    private const val TIME_STEP_SECONDS = 30L

    fun getOrCreateDeviceSecret(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var secret = prefs.getString(KEY_DEVICE_SECRET, null)
        if (secret.isNullOrBlank()) {
            val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            secret = (1..16).map { allowedChars.random() }.joinToString("")
            prefs.edit().putString(KEY_DEVICE_SECRET, secret).apply()
        }
        return secret
    }

    fun generateCurrentDynamicCode(secret: String, offsetSteps: Long = 0L): String {
        val currentStep = (System.currentTimeMillis() / 1000L / TIME_STEP_SECONDS) + offsetSteps
        val dataToSign = "$secret:$currentStep"
        val hmac = hmacSha256(dataToSign, secret).take(8).uppercase()
        return "LIFEOS_DYNAMIC:$secret:$currentStep:$hmac"
    }

    fun getStaticQrCode(secret: String): String {
        return "LIFEOS_STATIC:$secret"
    }

    fun validateQrPayload(scannedPayload: String, secret: String): Boolean {
        val clean = scannedPayload.trim()

        // 1. Static QR validation (Unified & Permanent Laptop QR)
        if (clean.equals("LIFEOS_STATIC:$secret", ignoreCase = true) || clean.equals(secret, ignoreCase = true)) {
            return true
        }

        // 2. Dynamic QR validation (allow ±1 step of 30 seconds for clock drift)
        if (clean.startsWith("LIFEOS_DYNAMIC:")) {
            val parts = clean.split(":")
            if (parts.size == 4) {
                val scannedSecret = parts[1]
                val scannedStep = parts[2].toLongOrNull() ?: return false
                val scannedHmac = parts[3]

                if (scannedSecret != secret) return false

                val currentStep = System.currentTimeMillis() / 1000L / TIME_STEP_SECONDS
                // Allow current step and ±1 step (90 seconds total window)
                if (Math.abs(currentStep - scannedStep) <= 1) {
                    val expectedData = "$secret:$scannedStep"
                    val expectedHmac = hmacSha256(expectedData, secret).take(8).uppercase()
                    return scannedHmac.equals(expectedHmac, ignoreCase = true)
                }
            }
        }

        return false
    }

    private fun hmacSha256(data: String, key: String): String {
        return try {
            val sha256Hmac = Mac.getInstance("HmacSHA256")
            val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256")
            sha256Hmac.init(secretKey)
            val signedBytes = sha256Hmac.doFinal(data.toByteArray(Charsets.UTF_8))
            signedBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            MessageDigest.getInstance("MD5").digest((data + key).toByteArray()).joinToString("") { "%02x".format(it) }
        }
    }

    fun generateHtmlWebPage(secret: String): String {
        return """<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LifeOS - Mã QR Báo Thức Laptop</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background: linear-gradient(135deg, #0F172A 0%, #1E1B4B 100%);
            color: #F8FAFC;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        .container {
            background: rgba(30, 41, 59, 0.85);
            backdrop-filter: blur(16px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 28px;
            padding: 40px;
            max-width: 480px;
            width: 100%;
            text-align: center;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
        }
        h1 { font-size: 24px; font-weight: 800; margin-bottom: 8px; color: #38BDF8; }
        p.subtitle { font-size: 14px; color: #94A3B8; margin-bottom: 24px; }
        .qr-card {
            background: #FFFFFF;
            padding: 24px;
            border-radius: 20px;
            display: inline-block;
            box-shadow: 0 10px 25px rgba(0,0,0,0.3);
            margin-bottom: 20px;
        }
        #qrcode { display: flex; justify-content: center; }
        .timer-box { margin-bottom: 20px; }
        .progress-bar {
            width: 100%;
            height: 6px;
            background: rgba(255,255,255,0.1);
            border-radius: 3px;
            overflow: hidden;
            margin-top: 8px;
        }
        .progress-fill {
            height: 100%;
            background: #38BDF8;
            width: 100%;
            transition: width 1s linear;
        }
        .secret-box {
            background: rgba(15, 23, 42, 0.6);
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 12px;
            padding: 12px;
            font-size: 13px;
            color: #CBD5E1;
            margin-top: 16px;
        }
        .secret-box code { color: #F59E0B; font-weight: bold; font-size: 15px; }
        .instructions {
            font-size: 12px;
            color: #64748B;
            margin-top: 20px;
            line-height: 1.5;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>💻 LifeOS Laptop Wake-Up</h1>
        <p class="subtitle">Quét mã QR bằng điện thoại để tắt chuông báo thức</p>

        <div class="qr-card">
            <div id="qrcode"></div>
        </div>

        <div class="timer-box">
            <div style="display: flex; justify-content: space-between; font-size: 13px; font-weight: 600;">
                <span>Mã xoay vòng chống chụp ảnh</span>
                <span id="countdown-text" style="color: #38BDF8;">30s</span>
            </div>
            <div class="progress-bar">
                <div id="progress-fill" class="progress-fill"></div>
            </div>
        </div>

        <div class="secret-box">
            Mã thiết bị: <code id="secret-display">$secret</code>
        </div>

        <p class="instructions">
            💡 Mở trang này trước khi đi ngủ trên màn hình laptop.<br>
            Sáng mai, bạn bắt buộc phải rời giường để quét mã tắt chuông!
        </p>
    </div>

    <script>
        const SECRET = "$secret";
        const STEP_SECONDS = 30;
        let qr = null;

        // Offline pseudo-HMAC for pure standalone operation
        function computeToken(data, secret) {
            let hash = 0;
            const combined = data + secret;
            for (let i = 0; i < combined.length; i++) {
                hash = ((hash << 5) - hash) + combined.charCodeAt(i);
                hash |= 0;
            }
            return Math.abs(hash).toString(16).padStart(8, '0').toUpperCase().substring(0, 8);
        }

        function updateQR() {
            const nowSeconds = Math.floor(Date.now() / 1000);
            const step = Math.floor(nowSeconds / STEP_SECONDS);
            const remaining = STEP_SECONDS - (nowSeconds % STEP_SECONDS);

            const token = computeToken(SECRET + ":" + step, SECRET);
            const payload = "LIFEOS_DYNAMIC:" + SECRET + ":" + step + ":" + token;

            const qrContainer = document.getElementById("qrcode");
            qrContainer.innerHTML = "";
            new QRCode(qrContainer, {
                text: payload,
                width: 220,
                height: 220,
                colorDark: "#0F172A",
                colorLight: "#FFFFFF",
                correctLevel: QRCode.CorrectLevel.M
            });

            document.getElementById("countdown-text").innerText = remaining + "s";
            document.getElementById("progress-fill").style.width = ((remaining / STEP_SECONDS) * 100) + "%";
        }

        updateQR();
        setInterval(updateQR, 1000);
    </script>
</body>
</html>"""
    }
}
