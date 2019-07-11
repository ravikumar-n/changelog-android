# Changelog Monitor [![CircleCI](https://circleci.com/gh/ravikumar-n/changelog-android/tree/master.svg?style=svg)](https://circleci.com/gh/ravikumar-n/changelog-android/tree/master) (DISCONTINUED)
**Android app to monitor all your favorite libraries release and their changelog in one place.**

<img src="presentation/src/main/res/mipmap-xxhdpi/ic_launcher_round.png" align="left"
width="200"
    hspace="10" vspace="10">

Changelog Monitor is available for free on the Google Play Store.

Don't miss out any critical update of a library, subscribe and get notified about the new version within 2hrs of its release. 

Currently the app supports 55 widely used libraries. As a free user you can subscribe and monitor 5 libraries. 

<a href="https://play.google.com/store/apps/details?id=com.ravikumar.changelogmonitor">
    <img alt="Get it on Google Play"
        height="80"
        src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" />
</a>
 
### Code Style

 - Formatting - [square/java-code-styles](https://github.com/square/java-code-styles).
 - Run `detektCheck` task before giving PR. (see: [detekt](https://github.com/arturbosch/detekt))

### Project Setup
Follow these steps to setup and run the project:

```
$ git clone git@github.com:ravikumar-n/changelog-android.git
$ cd changelog-android
$ cp .keystore.properties keystore.properties && cp .gradle.properties gradle.properties
```
Ensure you have selected `staginDebug` variant under `BuildVariant` in Android Studio.

NOTE: <i>Your first API request may result in timeout, try again. If some weird issue happens, logout and start afresh.</i>

### Project Members
 - Designer - [Ahila Pillai](http://www.ahilapillai.com/)
 - Developer - [Ravikumar N](https://twitter.com/HappyRavi)
 
### Acknowledgments
 - [Fernando Cejas](https://twitter.com/fernando_cejas) for his awesome work on [Clean Architecture Kotlin](https://github.com/android10/Android-CleanArchitecture-Kotlin/commits/master).
