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
fun TextInputFields(input: MutableMap<String, String>, onChange: (String, String) -> Unit, errorFields: Set<String>) {
    OutlinedTextField(
        value = input["text"] ?: "",
        onValueChange = { onChange("text", it) },
        label = { Text(stringResource(R.string.label_text)) },
        isError = "text" in errorFields,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun UrlInputFields(input: MutableMap<String, String>, onChange: (String, String) -> Unit, errorFields: Set<String>) {
    OutlinedTextField(
        value = input["text"] ?: "",
        onValueChange = { onChange("text", it) },
        label = { Text(stringResource(R.string.label_url)) },
        isError = "text" in errorFields,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun EncryptedInputFields(input: MutableMap<String, String>, onChange: (String, String) -> Unit, errorFields: Set<String>) {
    OutlinedTextField(
        value = input["text"] ?: "",
        onValueChange = { onChange("text", it) },
        label = { Text(stringResource(R.string.label_encrypted_text)) },
        isError = "text" in errorFields,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun EmailInputFields(input: MutableMap<String, String>, onChange: (String, String) -> Unit, errorFields: Set<String>) {
    Column {
        OutlinedTextField(
            value = input["email"] ?: "",
            onValueChange = { onChange("email", it) },
            label = { Text(stringResource(R.string.label_email_address)) },
            isError = "text" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["subject"] ?: "",
            onValueChange = { onChange("subject", it) },
            label = { Text(stringResource(R.string.label_email_subject)) },
            isError = "text" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["body"] ?: "",
            onValueChange = { onChange("body", it) },
            label = { Text(stringResource(R.string.label_email_body)) },
            isError = "text" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiInputFields(input: MutableMap<String, String>, onChange: (String, String) -> Unit, errorFields: Set<String>) {
    val securityOptions = listOf("WPA", "WEP", "nopass")
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = input["ssid"] ?: "",
            onValueChange = { onChange("ssid", it) },
            label = { Text(stringResource(R.string.wifi_ssid)) },
            isError = "text" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["password"] ?: "",
            onValueChange = { onChange("password", it) },
            label = { Text(stringResource(R.string.password)) },
            isError = "text" in errorFields,
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
                            val selected = it
                            input["security"] = selected
                            expanded = false
                            onChange("security", selected)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GeoInputFields(input: MutableMap<String, String>, onChange: (String, String) -> Unit, errorFields: Set<String>) {
    Column {
        OutlinedTextField(
            value = input["lat"] ?: "",
            onValueChange = { onChange("lat", it) },
            label = { Text(stringResource(R.string.latitude)) },
            isError = "text" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["lon"] ?: "",
            onValueChange = { onChange("lon", it) },
            label = { Text(stringResource(R.string.longitude)) },
            isError = "text" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PhoneInputFields(input: MutableMap<String, String>, onChange: (String, String) -> Unit, errorFields: Set<String>) {
    OutlinedTextField(
        value = input["phone"] ?: "",
        onValueChange = { onChange("phone", it) },
        label = { Text(stringResource(R.string.phone_number)) },
        isError = "text" in errorFields,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SmsInputFields(input: MutableMap<String, String>, onChange: (String, String) -> Unit, errorFields: Set<String>) {
    Column {
        OutlinedTextField(
            value = input["phone"] ?: "",
            onValueChange = { onChange("phone", it) },
            label = { Text(stringResource(R.string.recipient_phone_number)) },
            isError = "text" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["message"] ?: "",
            onValueChange = { onChange("message", it) },
            label = { Text(stringResource(R.string.message)) },
            isError = "text" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun VCardInputFields(input: MutableMap<String, String>, onChange: (String, String) -> Unit, errorFields: Set<String>) {
    Column {
        OutlinedTextField(
            value = input["firstName"] ?: "",
            onValueChange = { onChange("firstName", it) },
            label = { Text(stringResource(R.string.first_name)) },
            isError = "text" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["lastName"] ?: "",
            onValueChange = { onChange("lastName", it) },
            label = { Text(stringResource(R.string.last_name)) },
            isError = "text" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["phone"] ?: "",
            onValueChange = { onChange("phone", it) },
            label = { Text(stringResource(R.string.phone_number)) },
            isError = "text" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["email"] ?: "",
            onValueChange = { onChange("email", it) },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["company"] ?: "",
            onValueChange = { onChange("company", it) },
            label = { Text(stringResource(R.string.company)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EventInputFields(input: MutableMap<String, String>, onChange: (String, String) -> Unit, errorFields: Set<String>) {
    Column {
        OutlinedTextField(
            value = input["title"] ?: "",
            onValueChange = { onChange("title", it) },
            label = { Text(stringResource(R.string.event_title)) },
            isError = "text" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["start"] ?: "",
            onValueChange = { onChange("start", it) },
            label = { Text(stringResource(R.string.start_date_time)) },
            isError = "text" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["end"] ?: "",
            onValueChange = { onChange("end", it) },
            label = { Text(stringResource(R.string.end_date_time)) },
            isError = "text" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = input["location"] ?: "",
            onValueChange = { onChange("location", it) },
            label = { Text(stringResource(R.string.location)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}