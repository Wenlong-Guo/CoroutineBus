package io.github.guowenlong.coroutinebus.android

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Description:
 * Author:      郭文龙
 * Date:        2023/3/22 0:41
 * Email:       guowenlong20000@sina.com
 */

inline fun <reified T : Any> Bus.subscribeByLifecycle(
    id: Any,
    lifecycleOwner: LifecycleOwner,
    isSticky: Boolean = false,
    dispatcher: CoroutineDispatcher,
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

fun <T : Any> Bus.subscribeByLifecycleTo(
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