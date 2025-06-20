package com.mahmutalperenunal.kodex

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        MobileAds.initialize(this) {}
        MapLibre.getInstance(this, "unused-api-key", WellKnownTileServer.MapTiler)
    }
}