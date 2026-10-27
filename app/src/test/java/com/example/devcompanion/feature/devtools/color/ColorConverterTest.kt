
package com.example.devcompanion.feature.devtools.color

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorConverterTest {

    @Test
    fun hexToRgb_convertsRed() {
        val result = ColorConverter.hexToRgb("#FF0000").getOrThrow()

        assertEquals(ColorConverter.Rgb(255, 0, 0), result)
    }

    @Test
    fun hexToRgb_acceptsWithoutHash() {
        val result = ColorConverter.hexToRgb("00FF00").getOrThrow()

        assertEquals(ColorConverter.Rgb(0, 255, 0), result)
    }

    @Test
    fun hexToRgb_rejectsInvalidHex() {
        assertTrue(ColorConverter.hexToRgb("#GG0000").isFailure)
    }

    @Test
    fun rgbToHex_convertsBlue() {
        val result = ColorConverter.rgbToHex(
            ColorConverter.Rgb(0, 0, 255)
        ).getOrThrow()

        assertEquals("#0000FF", result)
    }

    @Test
    fun rgbToHex_rejectsInvalidValues() {
        val result = ColorConverter.rgbToHex(
            ColorConverter.Rgb(300, 0, 0)
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun rgbToHsl_convertsRed() {
        val result = ColorConverter.rgbToHsl(
            ColorConverter.Rgb(255, 0, 0)
        ).getOrThrow()

        assertEquals(0.0, result.hue, 0.001)
        assertEquals(100.0, result.saturation, 0.001)
        assertEquals(50.0, result.lightness, 0.001)
    }

    @Test
    fun rgbToHsl_convertsGray() {
        val result = ColorConverter.rgbToHsl(
            ColorConverter.Rgb(128, 128, 128)
        ).getOrThrow()

        assertEquals(0.0, result.saturation, 0.001)
    }

    @Test
    fun hslToRgb_convertsGreen() {
        val result = ColorConverter.hslToRgb(
            ColorConverter.Hsl(120.0, 100.0, 50.0)
        ).getOrThrow()

        assertEquals(ColorConverter.Rgb(0, 255, 0), result)
    }

    @Test
    fun hslToRgb_rejectsInvalidSaturation() {
        val result = ColorConverter.hslToRgb(
            ColorConverter.Hsl(120.0, 150.0, 50.0)
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun rgbHslRoundTrip_preservesColor() {
        val original = ColorConverter.Rgb(64, 128, 192)

        val hsl = ColorConverter.rgbToHsl(original).getOrThrow()
        val restored = ColorConverter.hslToRgb(hsl).getOrThrow()

        assertEquals(original, restored)
    }
}
