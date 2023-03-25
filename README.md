# CoroutineBus
Event Bus By Kotlin Coroutine.

[![License](https://img.shields.io/github/license/Wenlong-Guo/CoroutineBus)](https://github.com/Wenlong-Guo/Dimens-Generating/blob/master/LICENSE)
![Api](https://img.shields.io/badge/API-14+-brightgreen.svg)
![Release](https://img.shields.io/github/v/release/Wenlong-Guo/CoroutineBus?include_prereleases)

## [中文 README](README-zh.md)

# About
CoroutineBus is an event bus based on Kotlin coroutine. It is a lightweight, high-performance, and easy-to-use event bus. It is suitable for Android, Java, and Kotlin projects.

# Download
1. [Click to download](https://bintray.com/wenlong-guo/maven/CoroutineBus/_latestVersion)
2. Scan QR code to download

![download](release/QRcode.png)

# Getting started

If your project's Gradle configuration is below '7.0', you need to add it to the 'build.gradle' file
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

If your Gradle configuration is '7.0 and above', you need to add it to the 'settings.gradle' file
```groovy
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

# Usage

1. Subscribe
```kotlin
CoroutineBus.subscribeByLifecycle(
    this, isSticky = false, replay = 1, lifecycleOwner = this
) { event: RandomNumEvent ->
    // do something
}
```

2. Post
```kotlin
CoroutineBus.post(RandomNumEvent())
```

3. Unsubscribe
```kotlin
CoroutineBus.unsubscribe(this, RandomNumEvent::class.java)
```

# API
1. SubscribeByLifecycle
```kotlin
/**
 * Subscribe to the event automatically and unsubscribe automatically
 *
 * @param id A unique identifier for the subscriber
 * @param lifecycleOwner LifecycleOwner
 * @param isSticky Whether to receive sticky events (note: sticky events will not be available if [replay] is 0)
 * @param replay Number of cached events The default cache is 100, if it is 0, then sticky events will not be available
 * @param dispatcher Specifies the scheduler for the coroutine
 * @param callback Callback functions
 */
inline fun <reified T : Any> CoroutineBus.subscribeByLifecycle(
    id: Any,
    lifecycleOwner: LifecycleOwner,
    isSticky: Boolean = false,
    replay: Int = 100,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    noinline callback: suspend (event: T) -> Unit
)
```

2. Subscribe
```kotlin

/**
 * Subscribe to the event
 *
 * @param id A unique identifier for the subscriber
 * @param isSticky Whether to receive sticky events (note: sticky events will not be available if [replay] is 0)
 * @param replay Number of cached events The default cache is 100, if it is 0, then sticky events will not be available
 * @param dispatcher Specifies the scheduler for the coroutine
 * @param callback Callback functions
 */
inline fun <reified T : Any> subscribe(
    id: Any,
    isSticky: Boolean = false,
    replay: Int = 100,
    dispatcher: CoroutineDispatcher,
    noinline callback: suspend (event: T) -> Unit
)
```

3. Unsubscribe 1
```kotlin
/**
 * Unsubscribe [clazz] events for that [id].
 *
 * @param id A unique identifier for the subscriber
 * @param clazz The event type to which you subscribed
 */
fun <T : Any> unsubscribe(id: Any, clazz: Class<T>)
```

4. Unsubscribe 2
```kotlin
/**
 * Unsubscribe all subscriptions to that [id].
 *
 * @param id A unique identifier for the subscriber
 */
fun unsubscribe(id: Any) 
```

5. clean event
```kotlin
/**
 * Clean up all subscribers and data sources for that event
 *
 * @param clazz The event type to which you subscribed
 */
fun <T : Any> cleanEvent(clazz: Class<T>)
```

6. isSubscribed
```kotlin
/**
 * Whether the event has already been registered
 *
 *  @param id A unique identifier for the subscriber
 *  @param clazz The event type to which you subscribed
 */
fun isSubscribed(id: Any, clazz: Class<*>): Boolean 
```

License
-------

    Copyright 2023 Wenlong Guo

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

