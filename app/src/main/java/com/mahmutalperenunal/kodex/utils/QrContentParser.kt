package com.mahmutalperenunal.kodex.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mahmutalperenunal.kodex.R

enum class ContentType {
    URL, EMAIL, PHONE, SMS, WIFI, GEO, VCARD, EVENT, PLAIN
}

fun detectContentType(content: String): ContentType {
    return when {
        content.startsWith("http://") || content.startsWith("https://") -> ContentType.URL
        content.contains("@") && content.contains(".") && !content.contains("BEGIN:VCARD") -> ContentType.EMAIL
        content.startsWith("tel:") || content.matches(Regex("^\\+?[0-9]{10,15}$")) -> ContentType.PHONE
        content.startsWith("smsto:") -> ContentType.SMS
        content.startsWith("WIFI:") -> ContentType.WIFI
        content.startsWith("geo:") -> ContentType.GEO
        content.startsWith("BEGIN:VCARD") -> ContentType.VCARD
        content.startsWith("BEGIN:VEVENT") -> ContentType.EVENT
        else -> ContentType.PLAIN
    }
}

fun extractSSID(wifiData: String, context: Context): String {
    val regex = Regex("S:([^;]+)")
    return regex.find(wifiData)?.groupValues?.get(1) ?: context.getString(R.string.unknown)
}

fun String.isUrl(): Boolean {
    return startsWith("http://") || startsWith("https://")
}

@Composable
fun getContentIcon(content: String) = when {
    content.startsWith("http://") || content.startsWith("https://") -> painterResource(R.drawable.ic_link)
    content.contains("@") && content.contains(".") -> painterResource(R.drawable.ic_mail)
    else -> painterResource(R.drawable.ic_link)
}

@Composable
fun parseMultilineContent(content: String): List<Pair<String, String>> {
    val ssidLabel = stringResource(R.string.wifi_ssid)
    val encryptionLabel = stringResource(R.string.encryption_type)
    val passwordLabel = stringResource(R.string.password)
    val contentLabel = stringResource(R.string.content)

    return when {
        content.startsWith("WIFI:") -> {
            val fields = content.removePrefix("WIFI:").removeSuffix(";;").split(";")
            fields.mapNotNull { line ->
                val (key, value) = line.split(":", limit = 2)
                    .let { it.getOrNull(0) to it.getOrNull(1) }
                if (key != null && value != null) {
                    val label = when (key) {
                        "S" -> ssidLabel
                        "T" -> encryptionLabel
                        "P" -> passwordLabel
                        else -> key
                    }
                    label to value
                } else null
            }
        }

        content.startsWith("BEGIN:VCARD") -> {
            content.lines().mapNotNull { line ->
                val (key, value) = line.split(":", limit = 2)
                    .let { it.getOrNull(0) to it.getOrNull(1) }
                if (key != null && value != null) key to value else null
            }
        }

        content.contains("\n") -> {
            content.lines().mapIndexed { index, line ->
                stringResource(R.string.line_number, index + 1) to line
            }
        }

        else -> listOf(contentLabel to content)
    }
}