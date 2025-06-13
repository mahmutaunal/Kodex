package com.mahmutalperenunal.kodex

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.mahmutalperenunal.kodex.ui.theme.KodexTheme
import com.mahmutalperenunal.kodex.utils.VersionChecker
import kotlinx.coroutines.launch

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

        window.decorView.post {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    if (isDarkTheme) 0 else WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                window.decorView.systemUiVisibility =
                    (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) or
                            if (!isDarkTheme) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
            }
        }

        setContent {
            KodexTheme {
                val showUpdateDialog = remember { mutableStateOf(false) }
                val updateInfo = remember { mutableStateOf<VersionChecker.UpdateInfo?>(null) }

                LaunchedEffect(Unit) {
                    if (isNetworkAvailable()) {
                        val result = VersionChecker.checkForUpdate(applicationContext)
                        if (result != null) {
                            updateInfo.value = result
                            showUpdateDialog.value = true
                        }
                    }
                }

                KodexApp()

                if (showUpdateDialog.value && updateInfo.value != null) {
                    UpdateDialog(
                        onConfirm = {
                            showUpdateDialog.value = false
                            lifecycleScope.launch {
                                VersionChecker.startUpdate(this@MainActivity, updateInfo.value!!)
                            }
                        },
                        onDismiss = {
                            showUpdateDialog.value = false
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun UpdateDialog(
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = stringResource(id = R.string.update_dialog_positive))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(id = R.string.update_dialog_negative))
                }
            },
            title = {
                Text(text = stringResource(id = R.string.update_dialog_title))
            },
            text = {
                Text(text = stringResource(id = R.string.update_dialog_message))
            }
        )
    }

    private fun Context.isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}