package com.mahmutalperenunal.kodex.ui.screens

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mahmutalperenunal.kodex.data.QrEntity
import com.mahmutalperenunal.kodex.data.QrType
import com.mahmutalperenunal.kodex.utils.CryptoUtils
import com.mahmutalperenunal.kodex.utils.QrUtils
import com.mahmutalperenunal.kodex.utils.viewModelFactory
import com.mahmutalperenunal.kodex.viewmodel.HistoryViewModel
import androidx.compose.ui.res.stringResource
import com.mahmutalperenunal.kodex.R
import com.mahmutalperenunal.kodex.data.QrContentType
import com.mahmutalperenunal.kodex.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: HistoryViewModel = viewModel(
        factory = viewModelFactory {
            HistoryViewModel(context.applicationContext as Application)
        }
    )

    val isDarkTheme = isSystemInDarkTheme()
    val qrColor = if (isDarkTheme) android.graphics.Color.WHITE else android.graphics.Color.BLACK

    val MAX_QR_LENGTH = 1000

    var selectedContentType by remember { mutableStateOf(QrContentType.TEXT) }
    val inputFields = remember { mutableStateMapOf<String, String>() }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var encryptEnabled by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    fun updateBitmap() {
        qrBitmap = tryGenerateQr(selectedContentType, inputFields, encryptEnabled, qrColor, context)
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.qr_code_generator),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val maxHeight = this.maxHeight

                Column(
                    modifier = Modifier
                        .let {
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .fillMaxWidth()
                                .heightIn(min = maxHeight)
                        }
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            readOnly = true,
                            value = stringResource(id = selectedContentType.getLabelRes()),
                            onValueChange = {},
                            label = { Text(stringResource(R.string.qr_code_type)) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            QrContentType.entries.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(id = type.getLabelRes())) },
                                    onClick = {
                                        selectedContentType = type
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (selectedContentType) {
                        QrContentType.TEXT -> TextInputFields(inputFields, ::updateBitmap)
                        QrContentType.URL -> UrlInputFields(inputFields, ::updateBitmap)
                        QrContentType.ENCRYPTED -> EncryptedInputFields(inputFields, ::updateBitmap)
                        QrContentType.EMAIL -> EmailInputFields(inputFields, ::updateBitmap)
                        QrContentType.WIFI -> WifiInputFields(inputFields, ::updateBitmap)
                        QrContentType.GEO -> GeoInputFields(inputFields, ::updateBitmap)
                        QrContentType.PHONE -> PhoneInputFields(inputFields, ::updateBitmap)
                        QrContentType.SMS -> SmsInputFields(inputFields, ::updateBitmap)
                        QrContentType.VCARD -> VCardInputFields(inputFields, ::updateBitmap)
                        QrContentType.EVENT -> EventInputFields(inputFields, ::updateBitmap)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = encryptEnabled, onCheckedChange = {
                            encryptEnabled = it
                            updateBitmap()
                        })
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.encrypt_before_generate),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    qrBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = stringResource(R.string.qr_code_generator),
                            modifier = Modifier
                                .size(250.dp)
                                .padding(vertical = 16.dp)
                                .align(Alignment.CenterHorizontally)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Button(onClick = {
                                val content = formatQrContent(selectedContentType, inputFields)
                                val clipboard =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip =
                                    android.content.ClipData.newPlainText("QR Content", content)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.copied_to_clipboard),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                                Text(stringResource(R.string.copy))
                            }
                            Button(onClick = {
                                val content = formatQrContent(selectedContentType, inputFields)
                                val shareIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    putExtra(android.content.Intent.EXTRA_TEXT, content)
                                    type = "text/plain"
                                }
                                context.startActivity(
                                    android.content.Intent.createChooser(
                                        shareIntent,
                                        null
                                    )
                                )
                            }) {
                                Text(stringResource(R.string.share))
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                val content = formatQrContent(selectedContentType, inputFields).let {
                    if (encryptEnabled && selectedContentType == QrContentType.ENCRYPTED) CryptoUtils.encrypt(
                        it
                    ) else it
                }
                if (content.isBlank() || content.length > MAX_QR_LENGTH) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.the_text_is_too_long),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }
                viewModel.insert(
                    QrEntity(
                        content = content,
                        type = QrType.GENERATED,
                        contentType = selectedContentType
                    )
                )
                Toast.makeText(
                    context,
                    context.getString(R.string.saved_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                inputFields.clear()
                qrBitmap = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(stringResource(R.string.generate_and_save))
        }
    }
}

fun tryGenerateQr(
    contentType: QrContentType,
    inputMap: Map<String, String>,
    encrypt: Boolean,
    color: Int,
    context: Context
): Bitmap? {
    val maxLength = 1000
    val rawContent = formatQrContent(contentType, inputMap)
    val finalContent = if (encrypt && contentType == QrContentType.ENCRYPTED)
        CryptoUtils.encrypt(rawContent) else rawContent

    return try {
        if (finalContent.length > maxLength) {
            Toast.makeText(
                context,
                context.getString(R.string.the_text_is_too_long),
                Toast.LENGTH_SHORT
            ).show()
            null
        } else {
            QrUtils.generateQrCode(finalContent, color)
        }
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.the_data_is_too_big), Toast.LENGTH_SHORT)
            .show()
        null
    }
}

fun formatQrContent(contentType: QrContentType, input: Map<String, String>): String {
    return when (contentType) {
        QrContentType.TEXT, QrContentType.URL, QrContentType.ENCRYPTED ->
            input["text"] ?: ""

        QrContentType.EMAIL ->
            "mailto:${input["email"]}?subject=${input["subject"]}&body=${input["body"]}"

        QrContentType.WIFI ->
            "WIFI:S:${input["ssid"]};T:${input["security"]};P:${input["password"]};;"

        QrContentType.GEO ->
            "geo:${input["lat"]},${input["lon"]}"

        QrContentType.PHONE ->
            "tel:${input["phone"]}"

        QrContentType.SMS ->
            "sms:${input["phone"]}?body=${input["message"]}"

        QrContentType.VCARD -> """
            BEGIN:VCARD
            VERSION:3.0
            N:${input["lastName"]};${input["firstName"]}
            FN:${input["firstName"]} ${input["lastName"]}
            TEL:${input["phone"]}
            EMAIL:${input["email"]}
            ORG:${input["company"]}
            END:VCARD
        """.trimIndent()

        QrContentType.EVENT -> """
            BEGIN:VEVENT
            SUMMARY:${input["title"]}
            DTSTART:${input["start"]}
            DTEND:${input["end"]}
            LOCATION:${input["location"]}
            END:VEVENT
        """.trimIndent()
    }
}