package com.najaf.bubblevpn

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.View
import android.view.animation.LinearInterpolator

class PowerView(ctx: Context) : View(ctx) {

    private var active = false
    private var pulse = 0f

    private val anim = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 2000
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener { pulse = it.animatedValue as Float; invalidate() }
    }

    private val ring = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 5f }
    private val body = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glow = Paint(Paint.ANTI_ALIAS_FLAG)
    private val icon = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        color = Color.WHITE
    }

    fun update(on: Boolean) {
        active = on
        if (on) anim.start() else { anim.cancel(); pulse = 0f }
        invalidate()
    }

    override fun onDraw(c: Canvas) {
        val cx = width / 2f
        val cy = height / 2f
        val r = minOf(width, height) / 2f * 0.55f
        val main = if (active) Color.parseColor("#4CE08A") else Color.parseColor("#5B6CFF")
        val dark = if (active) Color.parseColor("#13946A") else Color.parseColor("#2A2F7A")

        if (active) {
            for (i in 0..1) {
                val p = (pulse + i * 0.5f) % 1f
                ring.color = main
                ring.alpha = (150 * (1 - p)).toInt()
                c.drawCircle(cx, cy, r + p * r * 0.65f, ring)
            }
        }

        glow.shader = RadialGradient(cx, cy, r * 1.5f,
            intArrayOf(Color.argb(90, Color.red(main), Color.green(main), Color.blue(main)), Color.TRANSPARENT),
            null, Shader.TileMode.CLAMP)
        c.drawCircle(cx, cy, r * 1.5f, glow)

        body.shader = LinearGradient(cx, cy - r, cx, cy + r, main, dark, Shader.TileMode.CLAMP)
        c.drawCircle(cx, cy, r, body)

        icon.strokeWidth = r * 0.11f
        val a = r * 0.38f
        c.drawArc(RectF(cx - a, cy - a, cx + a, cy + a), -55f, 290f, false, icon)
        c.drawLine(cx, cy - a * 1.1f, cx, cy - a * 0.05f, icon)
    }

    override fun onDetachedFromWindow() {
        anim.cancel()
        super.onDetachedFromWindow()
    }
}
