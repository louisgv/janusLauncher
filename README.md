# Janus, a FOSS Android Launcher

The goal of Janus is to be a launcher that is:
1. Minimal
2. Made with Kotlin
3. Gesture based
4. Open source

---

## Prerequisite

Building Janus requires:

- git
- Android Studio Canary ("Android 3" as of 2017-08-05T06:25:29.933Z)
- python3, pip3, virtualenv, virtualenvwrapper (Check NOTE)

---

# Setup

1. The launcher module

	- Download and setup Android Studio Canary:  [link](https://developer.android.com/studio/preview/index.html)
 	- Open the android studio project in `/launcher`

2. The model module

	- Setup `tensorhack` as described in `NOTE`
	- Play with stuffs in `model`

---

## Hacking

### Setup git with ssh

Follow this guide: [link](https://help.github.com/articles/connecting-to-github-with-ssh/)

### Contribution method

1. Fork the repo into your remote
2. Hack
3. Make a pull request here or the community driven organization repo.

---

## Announcements

+ 2017-08-15T05:41:05.891Z: App filtering added
+ 2017-08-13T02:36:29.212Z: Drawing pad added
+ 2017-08-07T05:16:44.862Z: Initialize tensorflow project with readme and notes
+ 2017-08-06T23:37:54.234Z: Refactor into a monorepo project with two module, one for launcher and one for the model.
+ 2017-08-05T05:41:49.454Z: Launcher with apps initialized
+ 2017-08-03T06:48:30.239Z: Initialize the project

---

## References:

+ [Deploying a tensorflow model to android](https://medium.com/joytunes/deploying-a-tensorflow-model-to-android-69d04d1b0cba)
+ [Basic Painting with Views](https://github.com/codepath/android_guides/wiki/Basic-Painting-with-Views)
+ [Creating a- deep learning ios-app with keras and tensorflow by @stratospark](http://blog.stratospark.com/creating-a-deep-learning-ios-app-with-keras-and-tensorflow.html)
+ [EMNIST github repository](https://github.com/Coopss/EMNIST)
+ [EMNIST paper](https://arxiv.org/abs/1702.05373)
+ [Build a custom launcher on Android by Ashraff Hathibelagal](https://code.tutsplus.com/tutorials/build-a-custom-launcher-on-android--cms-21358)
+ [How to Write Custom Launcher App in Android by Arnab Chakraborty](http://arnab.ch/blog/2013/08/how-to-write-custom-launcher-app-in-android/)

---

## License:

MIT
