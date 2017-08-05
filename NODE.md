# The source structure

---

# Android SDK update quirk on system low on tmp

My laptop was running low on storage when attempting to upgrade android SDK. Indeed the partition used for tmp was only 1.5GB freed. The solution to this was quite simple. I simply create a tmp directory in another partition, or even external storage drive. Then simply remove the `/tmp` directory with root, then symlink the new `/tmp` like so:

```
sudo rm /tmp
sudo ln -s /path/to/another/tmp /tmp
sudo chmod 777 /tmp
```

This allows Android SDK to upgrade gracefully. However, upon reboot, the `/tmp` directory wasn't mounted correctly and thus the vmlinuz image cannot populate properly, my XServer simply crashed. This was due to the fact that I was using an external drive for my tmp, a silly act. Thus, I had to go to ty1, remove the symlink, and create a new /tmp again. Reboot and things worked:

```
CTRL+ALT+F1 -> login

sudo rm /tmp
sudo mkdir /tmp
sudo chmod 777 /tmp
sudo reboot
```

---

# Using delegate to lazy load things

Working with Android suffers from ancient artifact such as manually handling delegate/promise/asynchronous/background process. To see Kotlin can actually handle this case is a great sign.

Prior in Java, one would have to cache the result of a background call, then check for the cached result in subsequence render.

In Kotlin, the same mechanism is almost impossible, since it does not allow lateinit field that is nullable. However, using a delegate, the cache could be loaded inside the delegate lambda itself. Thus, inside the background loader callback, one can simply return the delegate.

Checkout AppsLoader for a demonstration.
