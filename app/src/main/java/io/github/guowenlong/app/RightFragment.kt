package io.github.guowenlong.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.github.guowenlong.app.databinding.FragmentRightBinding
import io.github.guowenlong.coroutinebus.CoroutineBus
import io.github.guowenlong.coroutinebus.subscribeByLifecycle
import kotlin.random.Random

/**
 * Description: 右侧Fragment
 * Author:      郭文龙
 * Date:        2023/3/20 22:27
 * Email:       guowenlong20000@sina.com
 */
class RightFragment : Fragment() {

    private val binding by lazy {
        FragmentRightBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSend.setOnClickListener {
            CoroutineBus.post(RandomNumEvent(Random.nextInt(0, 100)))
        }

        //自动跟随生命周期 注册和取消注册
        //onStart的时候注册,onStop的时候取消注册
        //dispatcher = Dispatchers.Main 可以不设置 默认 dispatcher = Dispatchers.Default
        //建议replay设为1
        //如果在不可见的情况下需要接受数据,请不要使用这个函数,可以试用 [CoroutineBus.subscribe] 并自己选择时机取消订阅 否则会内存泄漏
        CoroutineBus.subscribeByLifecycle(
            this, isSticky = false, replay = 1, lifecycleOwner = this
        ) { event: RandomNumEvent ->
            Log.e("Right", "RightFragment页面收到随机数: ${event.number}")
        }
    }
}