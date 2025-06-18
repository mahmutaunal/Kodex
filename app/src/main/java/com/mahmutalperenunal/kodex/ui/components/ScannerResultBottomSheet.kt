package com.mahmutalperenunal.kodex.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmutalperenunal.kodex.R
import com.mahmutalperenunal.kodex.utils.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerResultBottomSheet(
    content: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isEncrypted = remember(content) {
        runCatching {
            CryptoUtils.decrypt(content) != content
        }.getOrElse { false }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.scan_result),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val multilineTypes = listOf(ContentType.WIFI, ContentType.VCARD, ContentType.PLAIN)
            when {
                detectContentType(content) in multilineTypes && content.contains("\n") || content.startsWith("WIFI:") -> {
                    FormattedMultilineContent(content)
                }

                content.isUrl() -> {
                    ClickableUrlText(content)
                }

                else -> {
                    ExpandableText(content)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isEncrypted) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.copy),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("qr_result", content)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
                }) {
                    Icon(painterResource(R.drawable.content_copy), contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.copy))
                }

                TextButton(onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, content)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, null))
                }) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.share))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ActionButtonsForContent(content, onDismiss)
        }
    }
}