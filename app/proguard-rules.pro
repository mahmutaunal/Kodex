# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

########################################
# ZXing QR Code Library
########################################
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

########################################
# AndroidX Lifecycle / ViewModel / LiveData
########################################
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

########################################
# Jetpack Compose
########################################
# Compose UI (minimal keep)
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

########################################
# Room Database
########################################
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.* class * {*;}

########################################
# Gson / JSON (eğer kullanıyorsan)
########################################
# -keep class com.google.gson.** { *; }
# -dontwarn com.google.gson.**

########################################
# AES / Crypto / javax
########################################
-dontwarn javax.crypto.**
-dontwarn java.security.**

########################################
# FileProvider (QR paylaşımı için önemli)
########################################
-keep public class * extends android.content.ContentProvider

########################################
# Firebase Crashlytics
########################################
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

########################################
# MapLibre SDK
########################################
-keep class org.maplibre.** { *; }
-dontwarn org.maplibre.**

# Native library yükleme
-keep class com.mapbox.** { *; }
-dontwarn com.mapbox.**

# Style JSON parsing
-keepclassmembers class ** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Gson reflection support (eğer MapLibre stil JSON'u parse ederken kullanıyorsa)
-keep class com.google.gson.stream.** { *; }
-dontwarn com.google.gson.stream.**

# MapView lifecycle methods
-keepclassmembers class org.maplibre.android.maps.MapView {
   public void onCreate(android.os.Bundle);
   public void onStart();
   public void onResume();
   public void onPause();
   public void onStop();
   public void onDestroy();
   public void onLowMemory();
   public void onSaveInstanceState(android.os.Bundle);
}