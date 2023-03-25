# CoroutineBus
用Kotlin协程制作的事件总线框架.

[![License](https://img.shields.io/github/license/Wenlong-Guo/CoroutineBus)](https://github.com/Wenlong-Guo/Dimens-Generating/blob/master/LICENSE)
![Api](https://img.shields.io/badge/API-14+-brightgreen.svg)
![Release](https://img.shields.io/github/v/release/Wenlong-Guo/CoroutineBus?include_prereleases)

## [English README](README.md)

# About
协程总线是基于 Kotlin 协程的事件总线。它是一种轻量级、高性能且易于使用的事件总线。它适用于Android，Java和Kotlin项目。

# Download
1. [点击此处下载](https://bintray.com/wenlong-guo/maven/CoroutineBus/_latestVersion)
2. 扫描下面二维码下载

![download](release/QRcode.png)

# 配置Gradle

如果你的项目 Gradle 配置是在 `7.0 以下`，需要在 `build.gradle` 文件中加入
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

如果你的 Gradle 配置是 `7.0 及以上`，则需要在 `settings.gradle` 文件中加入
```groovy
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

在项目 app 模块下的 `build.gradle` 文件中加入远程依赖
```groovy
dependencies {
    implementation 'com.github.Wenlong-Guo:CoroutineBus:2.0.0'
}
```

# Usage

1. 自动订阅
```kotlin
CoroutineBus.subscribeByLifecycle(
    this, isSticky = false, replay = 1, lifecycleOwner = this
) { event: RandomNumEvent ->
    // do something
}
```

2. 发送事件
```kotlin
CoroutineBus.post(RandomNumEvent())
```

3. 取消订阅
```kotlin
CoroutineBus.unsubscribe(this, RandomNumEvent::class.java)
```

# API
1. 自动订阅 自动取消订阅的 订阅功能
```kotlin
/**
 * 带LifecycleOwner的订阅事件
 *
 * @param id 订阅者的唯一标识
 * @param lifecycleOwner LifecycleOwner
 * @param isSticky 是否接收粘性事件(注意:如果[replay]为0 那么粘性事件将无法使用)
 * @param replay 缓存事件个数 默认缓存100个,如果为0 那么粘性事件将无法使用
 * @param dispatcher 指定协程的调度器
 * @param callback 回调函数
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

2. 普通订阅
```kotlin
/**
 * 订阅事件
 *
 * @param id 订阅者的唯一标识
 * @param isSticky 是否接收粘性事件(注意:如果[replay]为0 那么粘性事件将无法使用)
 * @param replay 缓存事件个数.默认缓存100个.如果为0,那么粘性事件将无法使用
 * @param dispatcher 指定协程的调度器
 * @param callback 回调函数
 */
inline fun <reified T : Any> subscribe(
    id: Any,
    isSticky: Boolean = false,
    replay: Int = 100,
    dispatcher: CoroutineDispatcher,
    noinline callback: suspend (event: T) -> Unit
)
```

3. 取消指定id的指定事件的订阅
```kotlin
/**
 * 取消该 [id] 的 [clazz] 事件的订阅
 *
 * @param id 订阅者的唯一标识
 * @param clazz 订阅的事件类型
 */
fun <T : Any> unsubscribe(id: Any, clazz: Class<T>)
```

4. 取消订阅指定id的所有订阅
```kotlin
/**
 * 取消订阅该 [id] 的所有订阅
 *
 * @param id 订阅者的唯一标识
 */
fun unsubscribe(id: Any) 
```

5. 清理所有订阅者和该事件的数据源
```kotlin
/**
 * 清理所有订阅者和该事件的数据源
 *
 * @param clazz 订阅的事件类型
 */
fun <T : Any> cleanEvent(clazz: Class<T>)
```

6. 是否已经注册过该事件
```kotlin
/**
 * 是否已经注册过该事件
 *
 *  @param id 订阅者的唯一标识
 *  @param clazz 订阅的事件类型
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

