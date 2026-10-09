package com.najaf.bubblevpn

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {

    private lateinit var power: PowerView
    private lateinit var status: TextView
    private lateinit var timer: TextView
    private lateinit var card: LinearLayout
    private var on = false
    private var startTime = 0L
    private val handler = Handler(Looper.getMainLooper())

    private val tick = object : Runnable {
        override fun run() {
            val s = (SystemClock.elapsedRealtime() - startTime) / 1000
            timer.text = String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60)
            handler.postDelayed(this, 1000)
        }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun tv(text: String, size: Float, color: String, bold: Boolean = false) =
        TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(Color.parseColor(color))
            gravity = Gravity.CENTER
            if (bold) typeface = Typeface.DEFAULT_BOLD
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.parseColor("#0A0E2A")

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(Color.parseColor("#1B1F5E"), Color.parseColor("#0A0E2A"))
            )
            setPadding(dp(24), dp(56), dp(24), dp(32))
        }

        root.addView(tv("BubbleVPN", 30f, "#FFFFFF", true))
        root.addView(tv("Fast \u2022 Private \u2022 Secure", 13f, "#8E96D9").apply {
            setPadding(0, dp(4), 0, 0)
        })

        power = PowerView(this).apply {
            setOnClickListener { toggle() }
        }
        root.addView(power, LinearLayout.LayoutParams(dp(300), dp(300)).apply {
            topMargin = dp(36)
        })

        status = tv("Tap to connect", 20f, "#C9CEFF", true)
        timer = tv("00:00:00", 15f, "#8E96D9").apply { visibility = View.INVISIBLE }
        root.addView(status)
        root.addView(timer, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(6) })

        root.addView(View(this), LinearLayout.LayoutParams(0, 0, 1f))

        card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1F2570"))
                cornerRadius = dp(20).toFloat()
            }
            setPadding(dp(16), dp(16), dp(16), dp(16))
            weightSum = 3f
        }
        card.addView(statBox("Server", "Bubble-1"), LinearLayout.LayoutParams(0, -2, 1f))
        card.addView(statBox("Protocol", "BubbleX"), LinearLayout.LayoutParams(0, -2, 1f))
        card.addView(statBox("Encryption", "256-bit"), LinearLayout.LayoutParams(0, -2, 1f))
        root.addView(card, LinearLayout.LayoutParams(-1, -2))

        setContentView(root)
    }

    private fun statBox(label: String, value: String) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        addView(tv(label, 11f, "#8E96D9"))
        addView(tv(value, 15f, "#FFFFFF", true))
    }

    override fun onResume() {
        super.onResume()
        if (BubbleService.running && !on) { on = true; startTime = SystemClock.elapsedRealtime() }
        render()
    }

    private fun toggle() {
        if (!on) {
            if (!hasAllFilesAccess()) { askAllFilesAccess(); return }
            if (Build.VERSION.SDK_INT >= 33) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
            startForegroundService(Intent(this, BubbleService::class.java))
            on = true
            startTime = SystemClock.elapsedRealtime()
        } else {
            startService(Intent(this, BubbleService::class.java).setAction("STOP"))
            on = false
        }
        render()
    }

    private fun render() {
        power.update(on)
        handler.removeCallbacks(tick)
        if (on) {
            status.text = "Connected"
            status.setTextColor(Color.parseColor("#4CE08A"))
            timer.visibility = View.VISIBLE
            handler.post(tick)
        } else {
            status.text = "Tap to connect"
            status.setTextColor(Color.parseColor("#C9CEFF"))
            timer.visibility = View.INVISIBLE
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(tick)
        super.onDestroy()
    }

    private fun hasAllFilesAccess(): Boolean =
        if (Build.VERSION.SDK_INT >= 30) Environment.isExternalStorageManager()
        else checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED

    private fun askAllFilesAccess() {
        if (Build.VERSION.SDK_INT >= 30) {
            startActivity(Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                Uri.parse("package:$packageName")
            ))
        } else {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        }
    }
}
