package io.github.guowenlong.coroutinebus

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Description: 用Flow协程实现的事件总线
 * Author:      郭文龙
 * Date:        2023/3/22 0:46
 * Email:       guowenlong20000@sina.com
 */
object CoroutineBus {

    // 用于存储订阅者
    private val subscribers = ConcurrentHashMap<Class<*>, ConcurrentHashMap<Any, Job>>()

    // 用于存储Flow
    private val producers = ConcurrentHashMap<Class<*>, MutableSharedFlow<*>>()

    /**
     * 订阅事件
     *
     * @param id 订阅者的唯一标识
     * @param isSticky 是否接收粘性事件
     * @param dispatcher 指定协程的调度器
     * @param callback 回调函数
     */
    inline fun <reified T : Any> subscribe(
        id: Any,
        isSticky: Boolean = false,
        dispatcher: CoroutineDispatcher,
        noinline callback: suspend (event: T) -> Unit
    ) {
        return subscribeTo(id, T::class.java, isSticky, dispatcher, callback)
    }

    /**
     * 取消订阅
     *
     * @param id 订阅者的唯一标识
     * @param clazz 订阅的事件类型
     */
    fun <T : Any> unsubscribe(id: Any, clazz: Class<T>) {
        subscribers[clazz]?.remove(id)?.cancel()
    }

    /**
     * 发送事件
     *
     * @param event 事件
     * @param dispatcher 指定协程的调度器 默认在主线程 接受者接收的默认线程也是主线程
     */
    fun <T : Any> post(event: T, dispatcher: CoroutineDispatcher = Dispatchers.Main) {
        CoroutineScope(Job() + dispatcher).launch {
            getOrPutProducers(clazz = event.javaClass).also {
                it.emit(event)
            }
        }
    }

    /**
     * 订阅事件
     *
     * @param id 订阅者的唯一标识
     * @param clazz 订阅的事件类型
     * @param isSticky 是否接收粘性事件
     * @param dispatcher 指定协程的调度器
     * @param callback 回调函数
     */
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
                .also {
                    if (it.replayCache.isEmpty()) {
                        it.filterNotNull()
                            .collect {
                                withContext(dispatcher) {
                                    callback(it)
                                }
                            }
                    } else {
                        it.drop(if (isSticky) 0 else 1).collect {
                            withContext(dispatcher) {
                                callback(it)
                            }
                        }
                    }
                }
        }
        getOrPutSubscribers(id, clazz, job)
    }


    /**
     * 获取或创建Flow
     *
     * @param clazz 事件类型
     * @param isSticky 是否接收粘性事件
     */
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