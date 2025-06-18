package com.mahmutalperenunal.kodex.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mahmutalperenunal.kodex.R

@Composable
fun TextInputFields(input: MutableMap<String, String>, onChange: () -> Unit) {
    OutlinedTextField(
        value = input["text"] ?: "",
        onValueChange = { input["text"] = it; onChange() },
        label = { Text(stringResource(R.string.label_text)) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun UrlInputFields(input: MutableMap<String, String>, onChange: () -> Unit) {
    OutlinedTextField(
        value = input["text"] ?: "",
        onValueChange = { input["text"] = it; onChange() },
        label = { Text(stringResource(R.string.label_url)) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun EncryptedInputFields(input: MutableMap<String, String>, onChange: () -> Unit) {
    OutlinedTextField(
        value = input["text"] ?: "",
        onValueChange = { input["text"] = it; onChange() },
        label = { Text(stringResource(R.string.label_encrypted_text)) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun EmailInputFields(input: MutableMap<String, String>, onChange: () -> Unit) {
    Column {
        OutlinedTextField(
            value = input["email"] ?: "",
            onValueChange = { input["email"] = it; onChange() },
            label = { Text(stringResource(R.string.label_email_address)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["subject"] ?: "",
            onValueChange = { input["subject"] = it; onChange() },
            label = { Text(stringResource(R.string.label_email_subject)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["body"] ?: "",
            onValueChange = { input["body"] = it; onChange() },
            label = { Text(stringResource(R.string.label_email_body)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiInputFields(input: MutableMap<String, String>, onChange: () -> Unit) {
    val securityOptions = listOf("WPA", "WEP", "nopass")
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = input["ssid"] ?: "",
            onValueChange = { input["ssid"] = it; onChange() },
            label = { Text(stringResource(R.string.wifi_ssid)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["password"] ?: "",
            onValueChange = { input["password"] = it; onChange() },
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = input["security"] ?: "WPA",
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.security_type)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                securityOptions.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            input["security"] = it
                            expanded = false
                            onChange()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GeoInputFields(input: MutableMap<String, String>, onChange: () -> Unit) {
    Column {
        OutlinedTextField(
            value = input["lat"] ?: "",
            onValueChange = { input["lat"] = it; onChange() },
            label = { Text(stringResource(R.string.latitude)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["lon"] ?: "",
            onValueChange = { input["lon"] = it; onChange() },
            label = { Text(stringResource(R.string.longitude)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PhoneInputFields(input: MutableMap<String, String>, onChange: () -> Unit) {
    OutlinedTextField(
        value = input["phone"] ?: "",
        onValueChange = { input["phone"] = it; onChange() },
        label = { Text(stringResource(R.string.phone_number)) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SmsInputFields(input: MutableMap<String, String>, onChange: () -> Unit) {
    Column {
        OutlinedTextField(
            value = input["phone"] ?: "",
            onValueChange = { input["phone"] = it; onChange() },
            label = { Text(stringResource(R.string.recipient_phone_number)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["message"] ?: "",
            onValueChange = { input["message"] = it; onChange() },
            label = { Text(stringResource(R.string.message)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun VCardInputFields(input: MutableMap<String, String>, onChange: () -> Unit) {
    Column {
        OutlinedTextField(
            value = input["firstName"] ?: "",
            onValueChange = { input["firstName"] = it; onChange() },
            label = { Text(stringResource(R.string.first_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["lastName"] ?: "",
            onValueChange = { input["lastName"] = it; onChange() },
            label = { Text(stringResource(R.string.last_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["phone"] ?: "",
            onValueChange = { input["phone"] = it; onChange() },
            label = { Text(stringResource(R.string.phone_number)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["email"] ?: "",
            onValueChange = { input["email"] = it; onChange() },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["company"] ?: "",
            onValueChange = { input["company"] = it; onChange() },
            label = { Text(stringResource(R.string.company)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EventInputFields(input: MutableMap<String, String>, onChange: () -> Unit) {
    Column {
        OutlinedTextField(
            value = input["title"] ?: "",
            onValueChange = { input["title"] = it; onChange() },
            label = { Text(stringResource(R.string.event_title)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["start"] ?: "",
            onValueChange = { input["start"] = it; onChange() },
            label = { Text(stringResource(R.string.start_date_time)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["end"] ?: "",
            onValueChange = { input["end"] = it; onChange() },
            label = { Text(stringResource(R.string.end_date_time)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["location"] ?: "",
            onValueChange = { input["location"] = it; onChange() },
            label = { Text(stringResource(R.string.location)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}