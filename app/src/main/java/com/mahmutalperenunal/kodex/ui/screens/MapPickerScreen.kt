package com.mahmutalperenunal.kodex.ui.screens

import android.location.Geocoder
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mahmutalperenunal.kodex.R
import com.mahmutalperenunal.kodex.viewmodel.SharedLocationViewModel
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import java.util.*

@Composable
fun MapPickerScreen(
    navController: NavController,
    onLocationSelected: (lat: Double, lon: Double, address: String?) -> Unit
) {
    val context = LocalContext.current
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry("generator")
    }
    val sharedLocationViewModel: SharedLocationViewModel = viewModel(parentEntry)

    val selectLocationText = stringResource(id = R.string.select_this_location)

    val mapView = remember {
        MapView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            onCreate(null)
            onStart()
            onResume()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = {
                it.getMapAsync { mapLibreMap ->
                    val styleUrl = "https://demotiles.maplibre.org/style.json"
                    mapLibreMap.setStyle(styleUrl) {
                        mapLibreMap.addOnMapClickListener { point ->
                            selectedLatLng = point
                            mapLibreMap.clear()
                            mapLibreMap.addMarker(MarkerOptions().position(point))
                            true
                        }
                    }
                }
            }
        )

        Button(
            onClick = {
                selectedLatLng?.let { latLng ->
                    val address = try {
                        Geocoder(context, Locale.getDefault())
                            .getFromLocation(latLng.latitude, latLng.longitude, 1)
                            ?.firstOrNull()?.getAddressLine(0)
                    } catch (e: Exception) {
                        null
                    }
                    sharedLocationViewModel.setLocation(latLng.latitude, latLng.longitude, address)
                    onLocationSelected(latLng.latitude, latLng.longitude, address)
                    navController.popBackStack("generator", false)
                }
            },
            enabled = selectedLatLng != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(text = selectLocationText)
        }
    }
}