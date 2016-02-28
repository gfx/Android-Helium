-keepattributes SourceFile,LineNumberTable,Exceptions
-keepnames class * extends java.lang.Throwable

# RxJava
-dontwarn rx.internal.util.unsafe.**
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}

# Android Support Library
-dontwarn android.support.**
-keep class android.support.** { *; }

# Retrofit and OkHttp
-dontwarn com.squareup.okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

# SimpleXML Framework
-dontwarn org.simpleframework.xml.stream.**
-keepclassmembers class * {
    @org.simpleframework.xml.** *;
}

-keep class org.simpleframework.xml.** { *; }

# Helium uses ProGuard only to shrink apk
#-keep class com.github.gfx.android.helium.** { *; }
-keepnames class ** { *; }


# Orma (v2.1.1 will include them)
-dontwarn org.antlr.v4.runtime.**
-dontwarn org.abego.treelayout.**
