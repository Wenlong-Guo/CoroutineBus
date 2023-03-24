package io.github.guowenlong.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.github.guowenlong.app.databinding.FragmentLeftBinding
import io.github.guowenlong.coroutinebus.CoroutineBus
import kotlinx.coroutines.Dispatchers

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
            CoroutineBus.post(RandomNumEvent(22))
        }

        binding.btnRegister.setOnClickListener {
            CoroutineBus.subscribe(this, isSticky = false ,dispatcher = Dispatchers.Main) { event: RandomNumEvent ->
                Log.e("Left", "LeftFragment")
                binding.tvContent.text = "${binding.tvContent.text} \n ${event.number}"
            }
        }

        binding.btnRegisterSticky.setOnClickListener {
            CoroutineBus.subscribe(
                this,
                isSticky = true,
                dispatcher = Dispatchers.Main
            ) { event: RandomNumEvent ->
                Toast.makeText(requireContext(), "sticky: $event", Toast.LENGTH_SHORT).show()
                binding.tvContent.text = "${binding.tvContent.text} \n ${event.number}"
            }
        }
    }
}