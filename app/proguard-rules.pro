-keepattributes SourceFile,Signature,Exceptions,*Annotation*,*Table
-keepnames class * extends java.lang.Throwable

# Android Support Library
-dontwarn android.support.**
-keep class android.support.** { *; }

# Retrofit and OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# SimpleXML Framework
-dontwarn org.simpleframework.xml.stream.**
-keepclassmembers class * {
    @org.simpleframework.xml.** *;
}

-keep class org.simpleframework.xml.** { *; }

# Helium uses ProGuard only to shrink apk
#-keep class com.github.gfx.android.helium.** { *; }
-keepnames class ** { *; }

# StaticGson
-keep @com.github.gfx.static_gson.annotation.StaticGsonGenerated class * { *; }

-keep public class * implements com.bumptech.glide.module.GlideModule
