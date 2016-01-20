# ![](app/src/main/res/mipmap-mdpi/ic_launcher.png) Helium for Android [![Circle CI](https://circleci.com/gh/gfx/Android-Helium/tree/master.svg?style=svg)](https://circleci.com/gh/gfx/Android-Helium/tree/master)

Helium (He), which comes from "**H**atebu" + "**E**pitome", is a Hatebu reader for Android.

[![Android app on Google Play](https://developer.android.com/images/brand/en_app_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=com.github.gfx.helium)

## Features

* Shows [Hatena Bookmark](http://b.hatena.ne.jp/) hot-entries
* Shows [Epitome](https://ja.epitomeup.com/) entries

## Libraries Used

This project is intended to employ modern Android libraries, such as:

* Android Support Libraries
  * CardView
  * Design
  * RecyclerView
* OkHttp
* Retrofit
* RxJava / RxT4A
* Google Dagger
* ThreeTenABP
* Glide

## Build

In your local machine:

```sh
# make an apk and install it to the connected device
./gradlew installDebug
```

## Test

The tests runs both on [JVM unit testing](http://tools.android.com/tech-docs/unit-testing-support) with
[Robolectric](http://robolectric.org/) and Android Instrumentation Tests.

To test it, type `./gradlew check` and `./gradlew connectedDebugAndroidTest`.

If you want to use Docker, type the following commands, which is what circleci.yml does:

```sh
docker build -t test .
docker run -it test
```

## Special Thanks

* [monja415](https://github.com/monja415), who has made the very cool launcher icon

## License

This application is free software; you can redistribute it and/or modify it
under the terms of the Apache License 2.0.

* http://www.apache.org/licenses/LICENSE-2.0
