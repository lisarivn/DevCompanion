package com.example.devcompanion.feature.devtools.color

import kotlin.math.abs
import kotlin.math.roundToInt

object ColorConverter {

    data class Rgb(
        val red: Int,
        val green: Int,
        val blue: Int
    )

    data class Hsl(
        val hue: Double,
        val saturation: Double,
        val lightness: Double
    )

    fun hexToRgb(hex: String): Result<Rgb> = runCatching {
        val value = hex.trim().removePrefix("#")

        require(value.matches(Regex("[0-9a-fA-F]{6}"))) {
            "HEX must contain exactly 6 hexadecimal digits"
        }

        Rgb(
            red = value.substring(0, 2).toInt(16),
            green = value.substring(2, 4).toInt(16),
            blue = value.substring(4, 6).toInt(16)
        )
    }

    fun rgbToHex(rgb: Rgb): Result<String> = runCatching {
        validateRgb(rgb)

        "#%02X%02X%02X".format(
            rgb.red,
            rgb.green,
            rgb.blue
        )
    }

    fun rgbToHsl(rgb: Rgb): Result<Hsl> = runCatching {
        validateRgb(rgb)

        val r = rgb.red / 255.0
        val g = rgb.green / 255.0
        val b = rgb.blue / 255.0

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min

        val lightness = (max + min) / 2.0

        val saturation = if (delta == 0.0) {
            0.0
        } else {
            delta / (1.0 - abs(2.0 * lightness - 1.0))
        }

        val hue = when {
            delta == 0.0 -> 0.0
            max == r -> 60.0 * (((g - b) / delta) % 6.0)
            max == g -> 60.0 * (((b - r) / delta) + 2.0)
            else -> 60.0 * (((r - g) / delta) + 4.0)
        }

        Hsl(
            hue = (hue + 360.0) % 360.0,
            saturation = saturation * 100.0,
            lightness = lightness * 100.0
        )
    }

    fun hslToRgb(hsl: Hsl): Result<Rgb> = runCatching {
        require(hsl.hue.isFinite() && hsl.hue in 0.0..360.0) {
            "Hue must be between 0 and 360"
        }
        require(
            hsl.saturation.isFinite() &&
                    hsl.saturation in 0.0..100.0
        ) {
            "Saturation must be between 0 and 100"
        }
        require(
            hsl.lightness.isFinite() &&
                    hsl.lightness in 0.0..100.0
        ) {
            "Lightness must be between 0 and 100"
        }

        val h = hsl.hue % 360.0
        val s = hsl.saturation / 100.0
        val l = hsl.lightness / 100.0

        val chroma = (1.0 - abs(2.0 * l - 1.0)) * s
        val x = chroma * (1.0 - abs((h / 60.0) % 2.0 - 1.0))
        val m = l - chroma / 2.0

        val (r, g, b) = when {
            h < 60.0 -> Triple(chroma, x, 0.0)
            h < 120.0 -> Triple(x, chroma, 0.0)
            h < 180.0 -> Triple(0.0, chroma, x)
            h < 240.0 -> Triple(0.0, x, chroma)
            h < 300.0 -> Triple(x, 0.0, chroma)
            else -> Triple(chroma, 0.0, x)
        }

        Rgb(
            red = ((r + m) * 255.0).roundToInt().coerceIn(0, 255),
            green = ((g + m) * 255.0).roundToInt().coerceIn(0, 255),
            blue = ((b + m) * 255.0).roundToInt().coerceIn(0, 255)
        )
    }

    private fun validateRgb(rgb: Rgb) {
        require(
            rgb.red in 0..255 &&
                    rgb.green in 0..255 &&
                    rgb.blue in 0..255
        ) {
            "RGB components must be between 0 and 255"
        }
    }
}
