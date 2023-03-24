package io.github.guowenlong.coroutinebus

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*

/**
 * Description: CoroutineBus的扩展类
 * Author:      郭文龙
 * Date:        2023/3/22 0:41
 * Email:       guowenlong20000@sina.com
 */


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
) {
    return CoroutineBus.subscribeByLifecycleTo(
        id,
        T::class.java,
        isSticky,
        replay,
        dispatcher,
        lifecycleOwner,
        callback
    )
}

/**
 * 带LifecycleOwner的订阅事件
 *
 * @param id 订阅者的唯一标识
 * @param clazz 订阅的事件类型
 * @param lifecycleOwner LifecycleOwner
 * @param isSticky 是否接收粘性事件(注意:如果[replay]为0 那么粘性事件将无法使用)
 * @param replay 缓存事件个数 默认缓存100个,如果为0 那么粘性事件将无法使用
 * @param dispatcher 指定协程的调度器
 * @param callback 回调函数
 */
fun <T : Any> CoroutineBus.subscribeByLifecycleTo(
    id: Any,
    clazz: Class<T>,
    isSticky: Boolean,
    replay: Int,
    dispatcher: CoroutineDispatcher,
    lifecycleOwner: LifecycleOwner,
    callback: suspend (event: T) -> Unit
) {
    lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    subscribeTo(id, clazz, isSticky, replay, dispatcher, callback)
                }
                Lifecycle.Event.ON_DESTROY -> {
                    unsubscribe(id, clazz)
                    lifecycleOwner.lifecycle.removeObserver(this)
                }
                else -> {}
            }
        }
    })
}

/**
 * 发送事件
 * 注意:如果在订阅前就已经先发送事件 那么创建[event]的数据源将缓存[replay]个
 *
 * @param event 事件
 * @param replay 缓存事件的数量
 * @param dispatcher 指定协程的调度器 默认在主线程 接受者接收的默认线程也是主线程
 */
fun <T : Any> post(event: T, replay: Int, dispatcher: CoroutineDispatcher = Dispatchers.Main) {
    CoroutineScope(Job() + dispatcher).launch {
        CoroutineBus.getOrPutProducers(clazz = event.javaClass, replay = replay).also {
            it.emit(event)
        }
    }
}