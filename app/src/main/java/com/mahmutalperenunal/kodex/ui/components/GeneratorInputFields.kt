package com.mahmutalperenunal.kodex.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mahmutalperenunal.kodex.R
import com.mahmutalperenunal.kodex.viewmodel.SharedLocationViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun TextInputFields(
    input: MutableMap<String, String>,
    onChange: (String, String) -> Unit,
    errorFields: Set<String>
) {
    OutlinedTextField(
        value = input["text"] ?: "",
        onValueChange = { onChange("text", it) },
        label = { Text(stringResource(R.string.label_text)) },
        isError = "text" in errorFields,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun UrlInputFields(
    input: MutableMap<String, String>,
    onChange: (String, String) -> Unit,
    errorFields: Set<String>
) {
    OutlinedTextField(
        value = input["text"] ?: "",
        onValueChange = { onChange("text", it) },
        label = { Text(stringResource(R.string.label_url)) },
        isError = "text" in errorFields,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun EncryptedInputFields(
    input: MutableMap<String, String>,
    onChange: (String, String) -> Unit,
    errorFields: Set<String>
) {
    OutlinedTextField(
        value = input["text"] ?: "",
        onValueChange = { onChange("text", it) },
        label = { Text(stringResource(R.string.label_encrypted_text)) },
        isError = "text" in errorFields,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun EmailInputFields(
    input: MutableMap<String, String>,
    onChange: (String, String) -> Unit,
    errorFields: Set<String>
) {
    Column {
        OutlinedTextField(
            value = input["email"] ?: "",
            onValueChange = { onChange("email", it) },
            label = { Text(stringResource(R.string.label_email_address)) },
            isError = "email" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedTextField(
            value = input["subject"] ?: "",
            onValueChange = { onChange("subject", it) },
            label = { Text(stringResource(R.string.label_email_subject)) },
            isError = "subject" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedTextField(
            value = input["body"] ?: "",
            onValueChange = { onChange("body", it) },
            label = { Text(stringResource(R.string.label_email_body)) },
            isError = "body" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiInputFields(
    input: MutableMap<String, String>,
    onChange: (String, String) -> Unit,
    errorFields: Set<String>
) {
    val securityOptions = listOf("WPA", "WEP", "nopass")
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = input["ssid"] ?: "",
            onValueChange = { onChange("ssid", it) },
            label = { Text(stringResource(R.string.wifi_ssid)) },
            isError = "ssid" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedTextField(
            value = input["password"] ?: "",
            onValueChange = { onChange("password", it) },
            label = { Text(stringResource(R.string.password)) },
            isError = "password" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = input["security"] ?: "WPA",
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.security_type)) },
                isError = "security" in errorFields,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
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
fun GeoInputFields(
    navController: NavController,
    input: MutableMap<String, String>,
    onChange: (String, String) -> Unit,
    errorFields: Set<String>,
    sharedLocationViewModel: SharedLocationViewModel
) {
    val lat by sharedLocationViewModel.latitude.collectAsState()
    val lon by sharedLocationViewModel.longitude.collectAsState()

    LaunchedEffect(lat, lon) {
        if (lat != null && lon != null) {
            onChange("lat", lat.toString())
            onChange("lon", lon.toString())
        }
    }

    Column {
        OutlinedTextField(
            value = input["lat"] ?: "",
            onValueChange = { onChange("lat", it) },
            label = { Text(stringResource(R.string.latitude)) },
            isError = "lat" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedTextField(
            value = input["lon"] ?: "",
            onValueChange = { onChange("lon", it) },
            label = { Text(stringResource(R.string.longitude)) },
            isError = "lon" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("map_picker")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.select_from_map))
        }
    }
}

@Composable
fun PhoneInputFields(
    input: MutableMap<String, String>,
    onChange: (String, String) -> Unit,
    errorFields: Set<String>
) {
    OutlinedTextField(
        value = input["phone"] ?: "",
        onValueChange = { onChange("phone", it) },
        label = { Text(stringResource(R.string.phone_number)) },
        isError = "phone" in errorFields,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SmsInputFields(
    input: MutableMap<String, String>,
    onChange: (String, String) -> Unit,
    errorFields: Set<String>
) {
    Column {
        OutlinedTextField(
            value = input["phone"] ?: "",
            onValueChange = { onChange("phone", it) },
            label = { Text(stringResource(R.string.recipient_phone_number)) },
            isError = "phone" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedTextField(
            value = input["message"] ?: "",
            onValueChange = { onChange("message", it) },
            label = { Text(stringResource(R.string.message)) },
            isError = "message" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun VCardInputFields(
    input: MutableMap<String, String>,
    onChange: (String, String) -> Unit,
    errorFields: Set<String>
) {
    Column {
        OutlinedTextField(
            value = input["firstName"] ?: "",
            onValueChange = { onChange("firstName", it) },
            label = { Text(stringResource(R.string.first_name)) },
            isError = "firstName" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedTextField(
            value = input["lastName"] ?: "",
            onValueChange = { onChange("lastName", it) },
            label = { Text(stringResource(R.string.last_name)) },
            isError = "lastName" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedTextField(
            value = input["phone"] ?: "",
            onValueChange = { onChange("phone", it) },
            label = { Text(stringResource(R.string.phone_number)) },
            isError = "phone" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedTextField(
            value = input["email"] ?: "",
            onValueChange = { onChange("email", it) },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedTextField(
            value = input["company"] ?: "",
            onValueChange = { onChange("company", it) },
            label = { Text(stringResource(R.string.company)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EventInputFields(
    navController: NavController,
    input: MutableMap<String, String>,
    onChange: (String, String) -> Unit,
    errorFields: Set<String>,
    sharedLocationViewModel: SharedLocationViewModel
) {
    val location by sharedLocationViewModel.address.collectAsState()

    LaunchedEffect(location) {
        if (!location.isNullOrBlank()) {
            onChange("location", location!!)
        }
    }

    Column {
        OutlinedTextField(
            value = input["title"] ?: "",
            onValueChange = { onChange("title", it) },
            label = { Text(stringResource(R.string.event_title)) },
            isError = "title" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.width(16.dp))

        DateTimePickerField(
            label = stringResource(R.string.start_date_time),
            value = input["start"] ?: "",
            isError = "start" in errorFields,
            onDateTimeSelected = { onChange("start", it) }
        )

        Spacer(modifier = Modifier.width(16.dp))

        DateTimePickerField(
            label = stringResource(R.string.end_date_time),
            value = input["end"] ?: "",
            onDateTimeSelected = { onChange("end", it) }
        )

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedTextField(
            value = input["location"] ?: "",
            onValueChange = { onChange("location", it) },
            label = { Text(stringResource(R.string.location)) },
            trailingIcon = {
                IconButton(onClick = {
                    navController.navigate("map_picker")
                }) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = stringResource(R.string.select_from_map)
                    )
                }
            },
            isError = "location" in errorFields,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DateTimePickerField(
    label: String,
    value: String,
    isError: Boolean = false,
    onDateTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    val dateFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        isError = isError,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val datePicker = DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, day)

                        val timePicker = TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                calendar.set(Calendar.MINUTE, minute)
                                onDateTimeSelected(dateFormatter.format(calendar.time))
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        )
                        timePicker.show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }
    )
}