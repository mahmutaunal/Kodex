package com.mahmutalperenunal.kodex.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.*
import com.mahmutalperenunal.kodex.R
import com.mahmutalperenunal.kodex.data.QrEntity
import com.mahmutalperenunal.kodex.data.QrType
import com.mahmutalperenunal.kodex.ui.components.RequestCameraPermission
import com.mahmutalperenunal.kodex.utils.CryptoUtils
import com.mahmutalperenunal.kodex.utils.viewModelFactory
import com.mahmutalperenunal.kodex.viewmodel.HistoryViewModel

@Composable
fun ScannerScreen(navController: NavHostController) {
    var hasPermission by remember { mutableStateOf(false) }

    RequestCameraPermission {
        hasPermission = true
    }

    if (!hasPermission) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.camera_permission_required), textAlign = TextAlign.Center)
        }
        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scannerView = remember { DecoratedBarcodeView(context) }
    var scannedText by remember { mutableStateOf<String?>(null) }

    val viewModel: HistoryViewModel = viewModel(
        factory = viewModelFactory {
            HistoryViewModel(context.applicationContext as Application)
        }
    )

    val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            result?.text?.let {
                val resultText = try {
                    CryptoUtils.decrypt(it)
                } catch (e: Exception) {
                    it
                }

                scannedText = resultText
                scannerView.pause()

                viewModel.insert(
                    QrEntity(
                        content = resultText,
                        type = QrType.SCANNED
                    )
                )
            }
        }

        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
    }

    LaunchedEffect(Unit) {
        scannerView.decodeContinuous(callback)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> scannerView.resume()
                Lifecycle.Event.ON_PAUSE -> scannerView.pause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            scannerView.pause()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { scannerView },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        scannedText?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(it, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    scannerView.resume()
                    scannedText = null
                }) {
                    Text(stringResource(R.string.rescan))
                }
            }
        }
    }
}