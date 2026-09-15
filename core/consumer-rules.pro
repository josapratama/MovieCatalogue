# ========== Core Consumer ProGuard Rules ==========
# These rules are exported to all modules that depend on :core

# Domain layer — used by all consuming modules
-keep class com.dicoding.moviecatalogue.core.domain.model.** { *; }
-keep class com.dicoding.moviecatalogue.core.domain.usecase.** { *; }
-keep class com.dicoding.moviecatalogue.core.domain.repository.** { *; }

# Utility classes
-keep class com.dicoding.moviecatalogue.core.utils.** { *; }

# Shared UI components
-keep class com.dicoding.moviecatalogue.core.ui.** { *; }

# Data models
-keep class com.dicoding.moviecatalogue.core.data.source.local.entity.** { *; }
-keep class com.dicoding.moviecatalogue.core.data.source.remote.response.** { *; }

# Retrofit interfaces used transitively
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson serialization annotations
-keepattributes *Annotation*
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# SQLCipher
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-dontwarn net.sqlcipher.**

# Kotlin coroutines
-dontwarn kotlinx.coroutines.**
