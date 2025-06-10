package com.mahmutalperenunal.kodex.ui.screens

import android.app.Application
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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

@Composable
fun GeneratorScreen(navController: NavHostController) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var encryptEnabled by remember { mutableStateOf(false) }

    val viewModel: HistoryViewModel = viewModel(
        factory = viewModelFactory {
            HistoryViewModel(context.applicationContext as Application)
        }
    )

    val isDarkTheme = isSystemInDarkTheme()
    val qrColor = if (isDarkTheme) android.graphics.Color.WHITE else android.graphics.Color.BLACK

    val MAX_QR_LENGTH = 1000

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.qr_code_generator),
            style = MaterialTheme.typography.headlineSmall
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    if (text.isNotBlank()) {
                        val value = if (encryptEnabled) CryptoUtils.encrypt(it) else it
                        if (value.length > MAX_QR_LENGTH) {
                            qrBitmap = null
                            Toast.makeText(context, context.getString(R.string.the_text_is_too_long), Toast.LENGTH_SHORT).show()
                        } else {
                            try {
                                qrBitmap = QrUtils.generateQrCode(value, qrColor)
                            } catch (e: Exception) {
                                qrBitmap = null
                                Toast.makeText(context, context.getString(R.string.the_data_is_too_big), Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        qrBitmap = null
                    }
                },
                label = { Text(stringResource(R.string.enter_text_to_encode)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Switch(
                    checked = encryptEnabled,
                    onCheckedChange = {
                        encryptEnabled = it
                        qrBitmap = if (text.isNotBlank()) {
                            if (encryptEnabled) {
                                val encrypted = CryptoUtils.encrypt(text)
                                QrUtils.generateQrCode(encrypted, qrColor)
                            } else {
                                QrUtils.generateQrCode(text, qrColor)
                            }
                        } else {
                            null
                        }
                    }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(R.string.encrypt_before_generate),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            qrBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = stringResource(R.string.qr_code_generator),
                    modifier = Modifier
                        .size(250.dp)
                        .padding(vertical = 16.dp)
                )
            }
        }

        Button(
            onClick = {
                if (text.isBlank()) {
                    Toast.makeText(context, context.getString(R.string.text_empty_error), Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val content = if (encryptEnabled) CryptoUtils.encrypt(text) else text
                if (content.length > MAX_QR_LENGTH) {
                    Toast.makeText(context, context.getString(R.string.the_text_is_too_long), Toast.LENGTH_SHORT).show()
                    return@Button
                }
                try {
                    viewModel.insert(QrEntity(content = content, type = QrType.GENERATED))
                    Toast.makeText(context, context.getString(R.string.text_empty_error), Toast.LENGTH_SHORT).show()
                    text = ""
                    qrBitmap = null
                } catch (e: Exception) {
                    Toast.makeText(context, context.getString(R.string.the_data_is_too_big), Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(stringResource(R.string.generate_and_save))
        }
    }
}