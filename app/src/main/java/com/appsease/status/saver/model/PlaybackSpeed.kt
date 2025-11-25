
package com.appsease.status.saver.model

import com.appsease.status.saver.R


enum class PlaybackSpeed(val labelRes: Int, val iconRes: Int, val speed: Float) {
    UltraSlow(R.string.ultra_slow, R.drawable.ic_speed_025_24dp, 0.25f),
    VerySlow(R.string.very_slow, R.drawable.ic_speed_05x_24px, 0.50f),
    Slow(R.string.slow, R.drawable.ic_speed_07x_24px, 0.75f),
    Normal(R.string.normal_speed, R.drawable.ic_speed_1x_24dp, 1f),
    Fast(R.string.fast, R.drawable.ic_speed_125_24px, 1.25f),
    VeryFast(R.string.very_fast, R.drawable.ic_speed_15x_24px, 1.50f),
    UltraFast(R.string.ultra_fast, R.drawable.ic_speed_175_24dp, 1.75f);

    fun next(): PlaybackSpeed {
        val values = PlaybackSpeed.entries
        val currentIndex = values.indexOf(this)
        val nextIndex = (currentIndex + 1) % values.size
        return values[nextIndex]
    }
}