# ========== Core Module ProGuard Rules ==========

# Data models — must not be obfuscated (used by Gson/Room serialization)
-keep class com.dicoding.moviecatalogue.core.data.source.remote.response.** { *; }
-keep class com.dicoding.moviecatalogue.core.data.source.local.entity.** { *; }
-keep class com.dicoding.moviecatalogue.core.domain.model.** { *; }

# Repository, DataSource, UseCase implementations
-keep class com.dicoding.moviecatalogue.core.data.** { *; }
-keep class com.dicoding.moviecatalogue.core.domain.** { *; }

# DI modules
-keep class com.dicoding.moviecatalogue.core.di.** { *; }

# Utility classes
-keep class com.dicoding.moviecatalogue.core.utils.** { *; }

# Shared UI (Adapter, Item model)
-keep class com.dicoding.moviecatalogue.core.ui.** { *; }

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
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ========== Room ==========
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-dontwarn androidx.room.paging.**

# ========== SQLCipher ==========
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-dontwarn net.sqlcipher.**

# ========== Kotlin Coroutines ==========
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# ========== Koin ==========
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# ========== Source file info for crash reports ==========
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
