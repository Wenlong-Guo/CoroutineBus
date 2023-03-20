package io.github.guowenlong.coroutinebus

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Description:
 * Author:      郭文龙
 * Date:        2023/3/20 1:10
 * Email:       guowenlong20000@sina.com
 */
object CoroutineBus {

    private val subscribers = ConcurrentHashMap<Class<*>, ConcurrentHashMap<Any, Job>>()

    private val producers = ConcurrentHashMap<Class<*>, MutableSharedFlow<*>>()

    inline fun <reified T : Any> subscribe(
        id: Any,
        isSticky: Boolean = false,
        noinline callback: suspend (event: T) -> Unit
    ) {
        return subscribeTo(id, T::class.java, isSticky, callback)
    }

    inline fun <reified T : Any> subscribeByLifecycle(
        id: Any,
        lifecycleOwner: LifecycleOwner,
        isSticky: Boolean = false,
        noinline callback: suspend (event: T) -> Unit
    ) {
        return subscribeByLifecycleTo(id, T::class.java, isSticky, lifecycleOwner, callback)
    }

    fun <T : Any> unsubscribe(id: Any, clazz: Class<T>) {
        subscribers[clazz]?.remove(id)?.cancel()
    }

    fun <T : Any> post(event: T) {
        CoroutineScope(Job() + Dispatchers.Default).launch {
            getOrPutProducers(clazz = event.javaClass).also {
                it.emit(event)
            }.emit(event)
        }
    }

    fun <T : Any> subscribeTo(
        id: Any,
        clazz: Class<T>,
        isSticky: Boolean,
        callback: suspend (event: T) -> Unit
    ) {
        if (subscribers.containsKey(clazz) && subscribers[clazz]?.containsKey(id) == true)
            throw IllegalArgumentException("Already subscribed for event type: $clazz")

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throw throwable
        }

        val job = CoroutineScope(Job() + Dispatchers.Default + exceptionHandler).launch {
            getOrPutProducers(clazz, isSticky)
                .drop(if (isSticky) 0 else 1)
                .filterNotNull()
                .collect {
                    withContext(Dispatchers.Main) {
                        callback(it)
                    }
                }
        }
        getOrPutSubscribers(id, clazz, job)
    }

    fun <T : Any> subscribeByLifecycleTo(
        id: Any,
        clazz: Class<T>,
        isSticky: Boolean,
        lifecycleOwner: LifecycleOwner,
        callback: suspend (event: T) -> Unit
    ) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_CREATE -> {
                        Log.e("nei","subscribeByLifecycle")
                        subscribeTo(id, clazz, isSticky, callback)
                    }
                    Lifecycle.Event.ON_DESTROY -> {
                        Log.e("nei","UnsubscribeByLifecycle")
                        unsubscribe(id, clazz)
                        lifecycleOwner.lifecycle.removeObserver(this)
                    }
                    else -> {}
                }
            }
        })
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getOrPutProducers(
        clazz: Class<T>,
        isSticky: Boolean = true
    ): MutableSharedFlow<T> =
        producers.getOrPut(clazz) { MutableSharedFlow<T>(replay = if (isSticky) 1 else 0) } as MutableSharedFlow<T>

    private fun <T : Any> getOrPutSubscribers(
        id: Any? = null,
        clazz: Class<T>,
        job: Job
    ) {
        id?.let { key ->
            subscribers.getOrPut(clazz) { ConcurrentHashMap() }.let {
                it[key] = job
                subscribers[clazz] = it
            }
        }
    }
}