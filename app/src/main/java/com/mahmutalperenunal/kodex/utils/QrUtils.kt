package com.mahmutalperenunal.kodex.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.mahmutalperenunal.kodex.R
import java.io.File
import java.io.FileOutputStream

object QrUtils {

    fun generateQrCodeForPreview(text: String, color: Int): Bitmap {
        val writer = QRCodeWriter()
        val hints = mapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L)
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = createBitmap(width, height)

        val backgroundColor = Color.TRANSPARENT

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixelColor = if (bitMatrix[x, y]) color else backgroundColor
                bitmap[x, y] = pixelColor
            }
        }
        return bitmap
    }

    fun generateQrCodeForSharing(text: String, color: Int): Bitmap {
        val writer = QRCodeWriter()
        val hints = mapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L)
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = createBitmap(width, height)

        val backgroundColor = Color.WHITE

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixelColor = if (bitMatrix[x, y]) color else backgroundColor
                bitmap[x, y] = pixelColor
            }
        }
        return bitmap
    }

    fun copyQrImageAndTextToClipboard(context: Context, content: String, bitmap: Bitmap) {
        try {
            val file = File(context.cacheDir, "copied_qr_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newUri(context.contentResolver, "QR Image", uri).apply {
                addItem(ClipData.Item(content))
            }
            clipboard.setPrimaryClip(clipData)

            Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, context.getString(R.string.copy_failed), Toast.LENGTH_SHORT).show()
        }
    }

    fun shareQrImageWithText(context: Context, text: String, bitmap: Bitmap) {
        try {
            val file = File(context.cacheDir, "qr_code.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, context.getString(R.string.share_qr_content))
            context.startActivity(chooser)

        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.share_failed), Toast.LENGTH_SHORT).show()
        }
    }
}