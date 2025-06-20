package com.mahmutalperenunal.kodex.ui.screens

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
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
import com.mahmutalperenunal.kodex.viewmodel.GeneratorViewModel
import com.mahmutalperenunal.kodex.viewmodel.SharedLocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: HistoryViewModel = viewModel(
        factory = viewModelFactory {
            HistoryViewModel(context.applicationContext as Application)
        }
    )
    val generatorViewModel: GeneratorViewModel = viewModel()
    val sharedLocationViewModel: SharedLocationViewModel = viewModel()
    val latitude by sharedLocationViewModel.latitude.collectAsState()
    val longitude by sharedLocationViewModel.longitude.collectAsState()

    val isDarkTheme = isSystemInDarkTheme()
    val qrColor = if (isDarkTheme) Color.WHITE else Color.BLACK

    val MAX_QR_LENGTH = 1000

    var selectedContentType by generatorViewModel::selectedContentType
    var qrBitmap by generatorViewModel::qrBitmap
    var encryptEnabled by generatorViewModel::encryptEnabled
    val errorFields by generatorViewModel::errorFields
    val inputFields by generatorViewModel::inputFields
    var expanded by remember { mutableStateOf(false) }

    fun updateBitmap() {
        if (!isInputValid(selectedContentType, inputFields)) {
            qrBitmap = null
            return
        }
        qrBitmap = tryGenerateQr(selectedContentType, inputFields, encryptEnabled, qrColor, context)
    }

    fun onFieldChange(field: String, value: String) {
        inputFields[field] = value
        if (value.isNotBlank()) {
            errorFields.value -= field
        }
        updateBitmap()
    }

    LaunchedEffect(latitude, longitude, selectedContentType) {
        if (selectedContentType == QrContentType.GEO && latitude != null && longitude != null) {
            inputFields["lat"] = latitude.toString()
            inputFields["lon"] = longitude.toString()
            updateBitmap()
        }
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
                                        generatorViewModel.inputFields.clear()
                                        generatorViewModel.qrBitmap = null
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (selectedContentType) {
                        QrContentType.TEXT -> TextInputFields(inputFields, ::onFieldChange, errorFields.value)
                        QrContentType.URL -> UrlInputFields(inputFields, ::onFieldChange, errorFields.value)
                        QrContentType.ENCRYPTED -> EncryptedInputFields(inputFields, ::onFieldChange, errorFields.value)
                        QrContentType.EMAIL -> EmailInputFields(inputFields, ::onFieldChange, errorFields.value)
                        QrContentType.WIFI -> WifiInputFields(inputFields, ::onFieldChange, errorFields.value)
                        QrContentType.GEO -> GeoInputFields(navController, inputFields, ::onFieldChange, errorFields.value, sharedLocationViewModel)
                        QrContentType.PHONE -> PhoneInputFields(inputFields, ::onFieldChange, errorFields.value)
                        QrContentType.SMS -> SmsInputFields(inputFields, ::onFieldChange, errorFields.value)
                        QrContentType.VCARD -> VCardInputFields(inputFields, ::onFieldChange, errorFields.value)
                        QrContentType.EVENT -> EventInputFields(navController, inputFields, ::onFieldChange, errorFields.value, sharedLocationViewModel)
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
                                val rawContent = formatQrContent(context, selectedContentType, inputFields)
                                val content = if (encryptEnabled || selectedContentType == QrContentType.ENCRYPTED) CryptoUtils.encrypt(rawContent) else rawContent

                                val shareText: String? = when (selectedContentType) {
                                    QrContentType.ENCRYPTED,
                                    QrContentType.WIFI,
                                    QrContentType.VCARD,
                                    QrContentType.EVENT -> null
                                    else -> {
                                        "${context.getString(R.string.qr_content_label)}\n$content"
                                    }
                                }

                                QrUtils.copyToClipboard(context, shareText ?: "")
                            }) {
                                Text(stringResource(R.string.copy))
                            }
                            Button(onClick = {
                                val rawContent = formatQrContent(context, selectedContentType, inputFields)
                                val content = if (encryptEnabled || selectedContentType == QrContentType.ENCRYPTED) CryptoUtils.encrypt(rawContent) else rawContent

                                val shareText: String? = when (selectedContentType) {
                                    QrContentType.ENCRYPTED,
                                    QrContentType.WIFI,
                                    QrContentType.VCARD,
                                    QrContentType.EVENT -> null
                                    else -> {
                                        "${context.getString(R.string.qr_content_label)}\n$content"
                                    }
                                }

                                val shareBitmap = QrUtils.generateQrCodeForSharing(content, Color.BLACK)

                                QrUtils.shareQrImageWithText(context, shareText ?: "", shareBitmap)
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
                val requiredFields = getRequiredFieldsForType(selectedContentType)
                val missingFields = requiredFields.filter { inputFields[it].isNullOrBlank() }.toSet()

                errorFields.value = missingFields

                if (missingFields.isNotEmpty()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.please_fill_required_fields),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                val content = formatQrContent(context, selectedContentType, inputFields).let {
                    if (encryptEnabled || selectedContentType == QrContentType.ENCRYPTED) CryptoUtils.encrypt(it) else it
                }

                if (content.length > MAX_QR_LENGTH) {
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
    val rawContent = formatQrContent(context, contentType, inputMap)
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
            QrUtils.generateQrCodeForPreview(finalContent, color)
        }
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.the_data_is_too_big), Toast.LENGTH_SHORT)
            .show()
        null
    }
}

fun formatQrContent(context: Context, contentType: QrContentType, input: Map<String, String>): String {
    return when (contentType) {
        QrContentType.TEXT, QrContentType.URL, QrContentType.ENCRYPTED -> input["text"] ?: ""

        QrContentType.EMAIL -> buildString {
            appendLine("${context.getString(R.string.email)}: ${input["email"]}")
            appendLine("${context.getString(R.string.label_email_subject)}: ${input["subject"]}")
            appendLine("${context.getString(R.string.label_email_body)}: ${input["body"]}")
        }

        QrContentType.WIFI -> buildString {
            appendLine("${context.getString(R.string.wifi_ssid)}: ${input["ssid"]}")
            appendLine("${context.getString(R.string.security_type)}: ${input["security"]}")
            appendLine("${context.getString(R.string.password)}: ${input["password"]}")
        }

        QrContentType.GEO -> buildString {
            appendLine("${context.getString(R.string.latitude)}: ${input["lat"]}")
            appendLine("${context.getString(R.string.longitude)}: ${input["lon"]}")
        }

        QrContentType.PHONE -> "${context.getString(R.string.phone_number)}: ${input["phone"]}"

        QrContentType.SMS -> buildString {
            appendLine("${context.getString(R.string.phone_number)}: ${input["phone"]}")
            appendLine("${context.getString(R.string.message)}: ${input["message"]}")
        }

        QrContentType.VCARD -> buildString {
            appendLine("${context.getString(R.string.name)}: ${input["firstName"]} ${input["lastName"]}")
            appendLine("${context.getString(R.string.phone_number)}: ${input["phone"]}")
            appendLine("${context.getString(R.string.email)}: ${input["email"]}")
            appendLine("${context.getString(R.string.company)}: ${input["company"]}")
        }

        QrContentType.EVENT -> buildString {
            appendLine("${context.getString(R.string.event_title)}: ${input["title"]}")
            appendLine("${context.getString(R.string.start_date_time)}: ${input["start"]}")
            appendLine("${context.getString(R.string.end_date_time)}: ${input["end"]}")
            appendLine("${context.getString(R.string.location)}: ${input["location"]}")
        }
    }
}

fun getRequiredFieldsForType(type: QrContentType): List<String> = when (type) {
    QrContentType.TEXT,
    QrContentType.URL,
    QrContentType.ENCRYPTED -> listOf("text")

    QrContentType.EMAIL -> listOf("email", "subject", "body")

    QrContentType.WIFI -> listOf("ssid", "security", "password")

    QrContentType.GEO -> listOf("lat", "lon")

    QrContentType.PHONE -> listOf("phone")

    QrContentType.SMS -> listOf("phone", "message")

    QrContentType.VCARD -> listOf("firstName", "lastName", "phone")

    QrContentType.EVENT -> listOf("title", "start")
}

fun isInputValid(type: QrContentType, input: Map<String, String>): Boolean {
    val requiredFields = getRequiredFieldsForType(type)
    return requiredFields.all { field -> !input[field].isNullOrBlank() }
}