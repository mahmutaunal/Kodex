package com.mahmutalperenunal.kodex

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mahmutalperenunal.kodex.ui.theme.KodexTheme
import com.mahmutalperenunal.kodex.utils.VersionChecker
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        val isDarkTheme = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> resources.configuration.uiMode and 0x30 == 0x20 // UI_MODE_NIGHT_YES
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window?.insetsController
            controller?.setSystemBarsAppearance(
                if (isDarkTheme) 0 else WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            window?.decorView?.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) or
                        if (!isDarkTheme) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
        }

        lifecycleScope.launch {
            if (isNetworkAvailable()) {
                val updateInfo = VersionChecker.checkForUpdate(applicationContext)
                if (updateInfo != null) {
                    showUpdateDialog(updateInfo)
                }
            }
        }

        setContent {
            KodexTheme {
                KodexApp()
            }
        }
    }

    private fun showUpdateDialog(updateInfo: VersionChecker.UpdateInfo) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.update_dialog_title))
            .setMessage(getString(R.string.update_dialog_message))
            .setPositiveButton(getString(R.string.update_dialog_positive)) { _, _ ->
                lifecycleScope.launch {
                    VersionChecker.startUpdate(this@MainActivity, updateInfo)
                }
            }
            .setNegativeButton(getString(R.string.update_dialog_negative), null)
            .show()
    }

    private fun Context.isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}