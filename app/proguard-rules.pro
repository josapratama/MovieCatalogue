# ========== App Module ProGuard Rules ==========

# Keep all presentation layer classes (Activities, Fragments, ViewModels)
-keep class com.dicoding.moviecatalogue.presentation.** { *; }
-keep class com.dicoding.moviecatalogue.di.** { *; }
-keep class com.dicoding.moviecatalogue.MainActivity { *; }
-keep class com.dicoding.moviecatalogue.MyApplication { *; }

# ========== Kotlin ==========
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# ========== Kotlin Coroutines ==========
-keepclassmembers class kotlinx.coroutines.** { *; }
-keep class kotlinx.coroutines.android.** { *; }
-dontwarn kotlinx.coroutines.**

# ========== Retrofit ==========
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# ========== OkHttp ==========
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# ========== Gson ==========
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ========== Room ==========
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ========== SQLCipher ==========
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-dontwarn net.sqlcipher.**

# ========== Koin ==========
-keep class org.koin.** { *; }
-keep class org.koin.core.** { *; }
-keep class org.koin.android.** { *; }
-dontwarn org.koin.**

# ========== Coil ==========
-keep class coil.** { *; }
-dontwarn coil.**

# ========== AndroidX ==========
-keep class androidx.lifecycle.** { *; }
-keep class androidx.navigation.** { *; }
-dontwarn androidx.lifecycle.**

# ========== Attributes for reflection / stack traces ==========
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
