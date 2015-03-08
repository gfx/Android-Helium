# ![](app/src/main/res/mipmap-mdpi/ic_launcher.png) Helium for Android [![Circle CI](https://circleci.com/gh/gfx/Android-Helium/tree/master.svg?style=svg)](https://circleci.com/gh/gfx/Android-Helium/tree/master)

Helium (He), which comes from "**H**atebu" + "**E**pitome", is a Hatebu reader for Android.

[![Android app on Google Play](https://developer.android.com/images/brand/en_app_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=com.github.gfx.helium)

## Features

* Shows [Hatena Bookmark](http://b.hatena.ne.jp/) hot-entries
* Shows [Epitome](https://ja.epitomeup.com/) entries

## Build

In your local machine:

```sh
# make an apk and install it to the connected device
./gradlew installDebug
```

To test it with Docker (what circleci.yml does):
```
docker build -t android_helium .
docker run -it android_helium ./gradlew --stacktrace test build
```

## Special Thanks

* [monja415](https://github.com/monja415), who has made the very cool launcher icon

## License

This application is free software; you can redistribute it and/or modify it
under the ters of the Apache License 2.0.

* http://www.apache.org/licenses/LICENSE-2.0
