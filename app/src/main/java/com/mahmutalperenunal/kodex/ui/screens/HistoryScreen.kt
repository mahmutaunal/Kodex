package com.mahmutalperenunal.kodex.ui.screens

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mahmutalperenunal.kodex.R
import com.mahmutalperenunal.kodex.data.QrContentType
import com.mahmutalperenunal.kodex.utils.QrUtils
import com.mahmutalperenunal.kodex.viewmodel.HistoryViewModel
import com.mahmutalperenunal.kodex.utils.viewModelFactory
import java.text.DateFormat
import java.util.Date

@Composable
fun HistoryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: HistoryViewModel = viewModel(factory = viewModelFactory {
        HistoryViewModel(context.applicationContext as Application)
    })
    val qrList by viewModel.qrHistory.observeAsState(emptyList())

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = stringResource(R.string.history),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            textAlign = TextAlign.Center
        )

        if (qrList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_qr_codes_found),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn {
                items(qrList) { qrItem ->
                    val shareText = when (qrItem.contentType) {
                        QrContentType.ENCRYPTED,
                        QrContentType.WIFI,
                        QrContentType.VCARD,
                        QrContentType.EVENT -> null
                        else -> "${context.getString(R.string.qr_content_label)}\n${qrItem.content}"
                    }

                    val qrBitmap = QrUtils.generateQrCodeForSharing(qrItem.content, Color.BLACK)

                    HistoryItem(
                        qrItem = qrItem,
                        onCopy = { QrUtils.copyQrImageAndTextToClipboard(context, shareText ?: "", qrBitmap) },
                        onShare = { QrUtils.shareQrImageWithText(context, shareText ?: "", qrBitmap) },
                        onDelete = { viewModel.delete(qrItem) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun HistoryItem(
    qrItem: com.mahmutalperenunal.kodex.data.QrEntity,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val isDarkTheme = isSystemInDarkTheme()
    val qrColor = if (isDarkTheme) android.graphics.Color.WHITE else android.graphics.Color.BLACK

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_qr_code)) },
            text = { Text(stringResource(R.string.delete_qr_code_description)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    val qrBitmap: Bitmap = QrUtils.generateQrCodeForPreview(qrItem.content, qrColor)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = qrItem.content,
                maxLines = 3,
                style = MaterialTheme.typography.bodyMedium
            )

            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.qr_code),
                modifier = Modifier
                    .size(200.dp)
                    .padding(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCopy) {
                    Icon(
                        painter = painterResource(id = R.drawable.content_copy),
                        contentDescription = stringResource(R.string.copy)
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
                IconButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = stringResource(R.string.share))
                }
                Spacer(modifier = Modifier.width(24.dp))
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = DateFormat.getDateTimeInstance().format(Date(qrItem.timestamp)),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}