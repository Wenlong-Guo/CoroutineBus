package io.github.guowenlong.coroutinebus

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

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
 * @param isSticky 是否接收粘性事件
 * @param dispatcher 指定协程的调度器
 * @param callback 回调函数
 */
inline fun <reified T : Any> CoroutineBus.subscribeByLifecycle(
    id: Any,
    lifecycleOwner: LifecycleOwner,
    isSticky: Boolean = false,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    noinline callback: suspend (event: T) -> Unit
) {
    return CoroutineBus.subscribeByLifecycleTo(
        id,
        T::class.java,
        isSticky,
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
 * @param isSticky 是否接收粘性事件
 * @param dispatcher 指定协程的调度器
 * @param callback 回调函数
 */
fun <T : Any> CoroutineBus.subscribeByLifecycleTo(
    id: Any,
    clazz: Class<T>,
    isSticky: Boolean,
    dispatcher: CoroutineDispatcher,
    lifecycleOwner: LifecycleOwner,
    callback: suspend (event: T) -> Unit
) {
    lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    subscribeTo(id, clazz, isSticky, dispatcher, callback)
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