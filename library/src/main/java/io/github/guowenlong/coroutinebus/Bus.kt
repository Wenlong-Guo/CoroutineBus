package io.github.guowenlong.coroutinebus

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import java.util.concurrent.ConcurrentHashMap

/**
 * Description:
 * Author:      郭文龙
 * Date:        2023/3/22 0:46
 * Email:       guowenlong20000@sina.com
 */
open class Bus {

    private val subscribers = ConcurrentHashMap<Class<*>, ConcurrentHashMap<Any, Job>>()

    private val producers = ConcurrentHashMap<Class<*>, MutableSharedFlow<*>>()

    inline fun <reified T : Any> subscribe(
        id: Any,
        isSticky: Boolean = false,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        noinline callback: suspend (event: T) -> Unit
    ) {
        return subscribeTo(id, T::class.java, isSticky, dispatcher, callback)
    }

    fun <T : Any> unsubscribe(id: Any, clazz: Class<T>) {
        subscribers[clazz]?.remove(id)?.cancel()
    }

    fun <T : Any> post(event: T) {
        CoroutineScope(Job() + Dispatchers.Default).launch {
            getOrPutProducers(clazz = event.javaClass).also {
                it.emit(event)
            }
        }
    }

    fun <T : Any> subscribeTo(
        id: Any,
        clazz: Class<T>,
        isSticky: Boolean,
        dispatcher: CoroutineDispatcher,
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
                    withContext(dispatcher) {
                        callback(it)
                    }
                }
        }
        getOrPutSubscribers(id, clazz, job)
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