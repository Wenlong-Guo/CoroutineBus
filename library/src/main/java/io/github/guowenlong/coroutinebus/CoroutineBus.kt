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
    ) {
        return subscribeTo(id, T::class.java, isSticky, replay, dispatcher, callback)
    }

    /**
     * 取消该 [id] 的 [clazz] 事件的订阅
     *
     * @param id 订阅者的唯一标识
     * @param clazz 订阅的事件类型
     */
    fun <T : Any> unsubscribe(id: Any, clazz: Class<T>) {
        subscribers[clazz]?.remove(id)?.cancel()
    }

    /**
     * 取消订阅该 [id] 的所有订阅
     *
     * @param id 订阅者的唯一标识
     */
    fun unsubscribe(id: Any) {
        subscribers.forEach { (_, value) ->
            value.remove(id)?.cancel()
        }
    }

    /**
     * 清理所有订阅者和该事件的数据源
     *
     * @param clazz 订阅的事件类型
     */
    fun <T : Any> cleanEvent(clazz: Class<T>) {
        subscribers[clazz]?.forEach {
            it.value.cancel()
        }
        subscribers[clazz]?.clear()
        producers.remove(clazz)
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
     * 是否已经注册过该事件
     *
     *  @param id 订阅者的唯一标识
     *  @param clazz 订阅的事件类型
     */
    fun isSubscribed(id: Any, clazz: Class<*>): Boolean {
        return subscribers[clazz]?.containsKey(id) == true
    }

    /**
     * 订阅事件
     *
     * @param id 订阅者的唯一标识
     * @param clazz 订阅的事件类型
     * @param isSticky 是否接收粘性事件(注意:如果[replay]为0 那么粘性事件将无法使用)
     * @param replay 缓存事件个数 默认缓存100个,如果为0 那么粘性事件将无法使用
     * @param dispatcher 指定协程的调度器
     * @param callback 回调函数
     */
    fun <T : Any> subscribeTo(
        id: Any,
        clazz: Class<T>,
        isSticky: Boolean,
        replay: Int,
        dispatcher: CoroutineDispatcher,
        callback: suspend (event: T) -> Unit
    ) {
        if (subscribers.containsKey(clazz) && subscribers[clazz]?.containsKey(id) == true)
            throw IllegalArgumentException("Already subscribed for event type: $clazz")

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throw throwable
        }

        val job = CoroutineScope(Job() + Dispatchers.Default + exceptionHandler).launch {
            getOrPutProducers(clazz, replay)
                .also {
                    if (it.replayCache.isEmpty()) {
                        it.filterNotNull()
                            .collect {
                                withContext(dispatcher) {
                                    callback(it)
                                }
                            }
                    } else {
                        if (isSticky) {
                            it.filterNotNull()
                                .collect {
                                    withContext(dispatcher) {
                                        callback(it)
                                    }
                                }
                        } else {
                            it.drop(it.replayCache.size)
                                .collect {
                                    withContext(dispatcher) {
                                        callback(it)
                                    }
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
     * @param replay 缓存大小
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrPutProducers(
        clazz: Class<T>,
        replay: Int = 100
    ): MutableSharedFlow<T> =
        producers.getOrPut(clazz) { MutableSharedFlow<T>(replay) } as MutableSharedFlow<T>

    fun <T : Any> getOrPutSubscribers(
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