package com.mahmutalperenunal.kodex.ui.screens

import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.ResultPoint
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.*
import com.mahmutalperenunal.kodex.R
import com.mahmutalperenunal.kodex.data.QrEntity
import com.mahmutalperenunal.kodex.data.QrType
import com.mahmutalperenunal.kodex.ui.components.RequestCameraPermission
import com.mahmutalperenunal.kodex.utils.CryptoUtils
import com.mahmutalperenunal.kodex.utils.viewModelFactory
import com.mahmutalperenunal.kodex.viewmodel.HistoryViewModel
import com.mahmutalperenunal.kodex.ui.components.ScannerResultBottomSheet

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
    val showBottomSheet = remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                val result = decodeQRCodeFromBitmap(bitmap)
                result?.let {
                    scannedText = it
                    showBottomSheet.value = true
                } ?: Toast.makeText(context, R.string.no_qr_found, Toast.LENGTH_SHORT).show()
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, context.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

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
                showBottomSheet.value = true
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
        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                factory = { scannerView },
                modifier = Modifier.fillMaxSize()
            )

            FloatingActionButton(
                onClick = {
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                            galleryLauncher.launch("image/*")
                        }
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            galleryLauncher.launch("image/*")
                        }
                        else -> {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_image), contentDescription = stringResource(R.string.select_from_gallery))
            }
        }
    }

    if (showBottomSheet.value && scannedText != null) {
        ScannerResultBottomSheet(
            content = scannedText!!,
            onDismiss = {
                showBottomSheet.value = false
                scannedText = null
                scannerView.resume()
            }
        )
    }
}

fun decodeQRCodeFromBitmap(bitmap: Bitmap): String? {
    val width = bitmap.width
    val height = bitmap.height
    val intArray = IntArray(width * height)
    bitmap.getPixels(intArray, 0, width, 0, 0, width, height)

    val source = RGBLuminanceSource(width, height, intArray)
    val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

    return try {
        val reader = MultiFormatReader()
        val result = reader.decode(binaryBitmap)
        val rawText = result.text

        try {
            CryptoUtils.decrypt(rawText)
        } catch (e: Exception) {
            rawText
        }

    } catch (e: Exception) {
        null
    }
}