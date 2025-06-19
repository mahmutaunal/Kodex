package com.mahmutalperenunal.kodex.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.mahmutalperenunal.kodex.R
import com.mahmutalperenunal.kodex.utils.*
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun FormattedMultilineContent(content: String) {
    val parsed = parseMultilineContent(content)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        parsed.forEach { (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$label:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(2f)
                )
            }
        }
    }
}

@Composable
fun ClickableUrlText(url: String) {
    val context = LocalContext.current

    Text(
        text = url,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
        modifier = Modifier.clickable {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        }
    )
}

@SuppressLint("StringFormatInvalid")
@Composable
fun ActionButtonsForContent(content: String, onDismiss: () -> Unit) {
    val context = LocalContext.current

    when (detectContentType(content)) {
        ContentType.URL -> {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, content.toUri())
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_link), contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.open_web_site))
            }
        }

        ContentType.EMAIL -> {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:$content".toUri()
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_mail), contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.send_email))
            }
        }

        ContentType.PHONE -> {
            val phoneNumber = content.removePrefix("tel:")
            ActionButton(context.getString(R.string.phone_call), Icons.Default.Phone) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = "tel:$phoneNumber".toUri()
                }
                context.startActivity(intent)
            }
        }

        ContentType.SMS -> {
            val parts = content.removePrefix("smsto:").split(":", limit = 2)
            val number = parts.getOrNull(0) ?: ""
            val message = parts.getOrNull(1) ?: ""

            ActionButton(context.getString(R.string.send_sms), painterResource(R.drawable.ic_chat)) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "smsto:$number".toUri()
                    putExtra("sms_body", message)
                }
                context.startActivity(intent)
            }
        }

        ContentType.GEO -> {
            ActionButton(context.getString(R.string.open_in_maps), Icons.Default.Place) {
                val intent = Intent(Intent.ACTION_VIEW, content.toUri())
                context.startActivity(intent)
            }
        }

        ContentType.WIFI -> {
            ActionButton(context.getString(R.string.see_wifi), painterResource(R.drawable.ic_wifi)) {
                Toast.makeText(
                    context,
                    context.getString(R.string.wifi_ssid, extractSSID(content, context)),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        ContentType.VCARD -> {
            ActionButton(context.getString(R.string.see_person), Icons.Default.Person) {
                Toast.makeText(
                    context,
                    context.getString(R.string.detected_person),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        ContentType.EVENT -> {
            ActionButton(context.getString(R.string.add_to_calendar), painterResource(R.drawable.ic_event)) {
                try {
                    val lines = content.lines().associate {
                        val parts = it.split(":", limit = 2)
                        parts[0].trim() to parts.getOrNull(1)?.trim().orEmpty()
                    }

                    val intent = Intent(Intent.ACTION_INSERT).apply {
                        type = "vnd.android.cursor.item/event"
                        putExtra(Intent.EXTRA_TITLE, lines["SUMMARY"])
                        putExtra("beginTime", parseEventTime(lines["DTSTART"]))
                        putExtra("endTime", parseEventTime(lines["DTEND"]))
                        putExtra("eventLocation", lines["LOCATION"])
                    }

                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, context.getString(R.string.unable_to_parse_event), Toast.LENGTH_SHORT).show()
                }
            }
        }

        ContentType.PLAIN -> {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.close))
            }
        }
    }
}

@Composable
fun ActionButton(text: String, icon: Painter, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(painter = icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    ActionButton(text, painterResourceFrom(icon), onClick)
}

@Composable
fun painterResourceFrom(imageVector: ImageVector): Painter {
    return rememberVectorPainter(imageVector)
}

fun parseEventTime(value: String?): Long {
    if (value == null) return System.currentTimeMillis()
    return try {
        val formatter = when {
            value.length > 8 -> SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())
            else -> SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        }
        formatter.timeZone = TimeZone.getDefault()
        formatter.parse(value)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}