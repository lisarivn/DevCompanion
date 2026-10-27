package com.example.devcompanion.feature.devtools.color

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun ColorConverterScreen() {
    val context = LocalContext.current

    var selectedFormat by rememberSaveable {
        mutableStateOf("HEX")
    }

    var hexInput by rememberSaveable {
        mutableStateOf("")
    }

    var redInput by rememberSaveable {
        mutableStateOf("")
    }

    var greenInput by rememberSaveable {
        mutableStateOf("")
    }

    var blueInput by rememberSaveable {
        mutableStateOf("")
    }

    var hueInput by rememberSaveable {
        mutableStateOf("")
    }

    var saturationInput by rememberSaveable {
        mutableStateOf("")
    }

    var lightnessInput by rememberSaveable {
        mutableStateOf("")
    }

    var resultRgb by remember {
        mutableStateOf<ColorConverter.Rgb?>(null)
    }

    var resultHsl by remember {
        mutableStateOf<ColorConverter.Hsl?>(null)
    }

    var resultHex by rememberSaveable {
        mutableStateOf("")
    }

    var errorMessage by rememberSaveable {
        mutableStateOf("")
    }

    fun clearResult() {
        resultRgb = null
        resultHsl = null
        resultHex = ""
        errorMessage = ""
    }

    fun showColor(rgb: ColorConverter.Rgb) {
        resultRgb = rgb
        resultHex = ColorConverter.rgbToHex(rgb).getOrThrow()
        resultHsl = ColorConverter.rgbToHsl(rgb).getOrThrow()
        errorMessage = ""
    }

    fun convert() {
        val conversion = when (selectedFormat) {
            "HEX" -> ColorConverter.hexToRgb(hexInput)

            "RGB" -> {
                val red = redInput.trim().toIntOrNull()
                val green = greenInput.trim().toIntOrNull()
                val blue = blueInput.trim().toIntOrNull()

                if (red == null || green == null || blue == null) {
                    Result.failure(
                        IllegalArgumentException(
                            "Enter valid integer RGB values"
                        )
                    )
                } else {
                    val rgb = ColorConverter.Rgb(red, green, blue)

                    ColorConverter.rgbToHex(rgb).map { rgb }
                }
            }

            else -> {
                val hue = hueInput.trim().toDoubleOrNull()
                val saturation = saturationInput.trim().toDoubleOrNull()
                val lightness = lightnessInput.trim().toDoubleOrNull()

                if (
                    hue == null ||
                    saturation == null ||
                    lightness == null
                ) {
                    Result.failure(
                        IllegalArgumentException(
                            "Enter valid HSL values"
                        )
                    )
                } else {
                    ColorConverter.hslToRgb(
                        ColorConverter.Hsl(
                            hue = hue,
                            saturation = saturation,
                            lightness = lightness
                        )
                    )
                }
            }
        }

        conversion
            .onSuccess { rgb ->
                showColor(rgb)
            }
            .onFailure { error ->
                clearResult()
                errorMessage = error.message ?: "Conversion failed"
            }
    }

    fun copyToClipboard(label: String, value: String) {
        val clipboard = context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager

        clipboard.setPrimaryClip(
            ClipData.newPlainText(label, value)
        )
    }

    val rgb = resultRgb
    val hsl = resultHsl

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Color Converter",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Convert colors between HEX, RGB and HSL.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Input format",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("HEX", "RGB", "HSL").forEach { format ->
                FilterChip(
                    selected = selectedFormat == format,
                    onClick = {
                        selectedFormat = format
                        clearResult()
                    },
                    label = {
                        Text(format)
                    }
                )
            }
        }

        when (selectedFormat) {
            "HEX" -> {
                OutlinedTextField(
                    value = hexInput,
                    onValueChange = {
                        hexInput = it
                        clearResult()
                    },
                    label = { Text("HEX color") },
                    placeholder = { Text("#FF5733") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            "RGB" -> {
                OutlinedTextField(
                    value = redInput,
                    onValueChange = {
                        redInput = it
                        clearResult()
                    },
                    label = { Text("Red (0–255)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = greenInput,
                    onValueChange = {
                        greenInput = it
                        clearResult()
                    },
                    label = { Text("Green (0–255)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = blueInput,
                    onValueChange = {
                        blueInput = it
                        clearResult()
                    },
                    label = { Text("Blue (0–255)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            "HSL" -> {
                OutlinedTextField(
                    value = hueInput,
                    onValueChange = {
                        hueInput = it
                        clearResult()
                    },
                    label = { Text("Hue (0–360)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = saturationInput,
                    onValueChange = {
                        saturationInput = it
                        clearResult()
                    },
                    label = { Text("Saturation (0–100)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = lightnessInput,
                    onValueChange = {
                        lightnessInput = it
                        clearResult()
                    },
                    label = { Text("Lightness (0–100)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = { convert() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Convert")
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (rgb != null && hsl != null) {
            Text(
                text = "Color preview",
                style = MaterialTheme.typography.titleMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    .background(
                        Color(
                            red = rgb.red,
                            green = rgb.green,
                            blue = rgb.blue
                        )
                    )
            )

            Text(
                text = "Conversion results",
                style = MaterialTheme.typography.titleMedium
            )

            val rgbText = "rgb(${rgb.red}, ${rgb.green}, ${rgb.blue})"

            val hslText = String.format(
                Locale.US,
                "hsl(%.1f, %.1f%%, %.1f%%)",
                hsl.hue,
                hsl.saturation,
                hsl.lightness
            )

            ColorResultRow(
                label = "HEX",
                value = resultHex,
                onCopy = {
                    copyToClipboard("HEX Color", resultHex)
                }
            )

            ColorResultRow(
                label = "RGB",
                value = rgbText,
                onCopy = {
                    copyToClipboard("RGB Color", rgbText)
                }
            )

            ColorResultRow(
                label = "HSL",
                value = hslText,
                onCopy = {
                    copyToClipboard("HSL Color", hslText)
                }
            )
        }
    }
}

@Composable
private fun ColorResultRow(
    label: String,
    value: String,
    onCopy: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall
        )

        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(
            onClick = onCopy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Copy $label")
        }
    }
}