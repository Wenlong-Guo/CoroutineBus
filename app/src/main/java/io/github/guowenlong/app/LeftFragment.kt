package io.github.guowenlong.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.github.guowenlong.app.databinding.FragmentLeftBinding
import io.github.guowenlong.coroutinebus.CoroutineBus
import kotlinx.coroutines.Dispatchers
import kotlin.random.Random

/**
 * Description: 左侧Fragment
 * Author:      郭文龙
 * Date:        2023/3/20 22:27
 * Email:       guowenlong20000@sina.com
 */
class LeftFragment : Fragment() {

    private val binding by lazy {
        FragmentLeftBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSend.setOnClickListener {
            CoroutineBus.post(RandomNumEvent(Random.nextInt(0, 100)))
        }

        //同一个ID不可以注册多次
        binding.btnRegister.setOnClickListener {
            if (CoroutineBus.isSubscribed(this, RandomNumEvent::class.java)) {
                CoroutineBus.unsubscribe(this, RandomNumEvent::class.java)
                Toast.makeText(requireContext(), "如果同一[id]多次注册会抛异常,已经反注册上一个订阅者并绑定了新的订阅者", Toast.LENGTH_SHORT).show()
            }
            CoroutineBus.subscribe(
                this,//改为 LeftFragment::class.java 也可以,便于跨界面取消订阅
                isSticky = false,
                replay = 100,
                dispatcher = Dispatchers.Main
            ) { event: RandomNumEvent ->
                binding.tvContent.text = "${binding.tvContent.text} \n 非粘性事件 收到 ${event.number}"
            }
        }

        //同一个ID不可以注册多次
        binding.btnRegisterSticky.setOnClickListener {
            if (CoroutineBus.isSubscribed(this, RandomNumEvent::class.java)) {
                CoroutineBus.unsubscribe(this, RandomNumEvent::class.java)
                Toast.makeText(requireContext(), "如果同一[id]多次注册会抛异常,已经反注册上一个订阅者并绑定了新的订阅者", Toast.LENGTH_SHORT).show()
            }
            CoroutineBus.subscribe(
                this,//改为 LeftFragment::class.java 也可以,便于跨界面取消订阅
                isSticky = true,
                replay = 100,
                dispatcher = Dispatchers.Main
            ) { event: RandomNumEvent ->
                binding.tvContent.text = "${binding.tvContent.text} \n 粘性事件 收到 : ${event.number}"
            }
        }
        binding.btnUnregister.setOnClickListener {
            CoroutineBus.unsubscribe(this, RandomNumEvent::class.java)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CoroutineBus.unsubscribe(this)
    }
}