package io.github.guowenlong.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.github.guowenlong.app.databinding.FragmentLeftBinding
import io.github.guowenlong.coroutinebus.android.CoroutineBus
import kotlinx.coroutines.Dispatchers

/**
 * Description:
 * Author:      郭文龙
 * Date:        2023/3/20 22:27
 * Email:       guowenlong20000@sina.com
 */
class LeftFragment : Fragment() {

    private val binding by lazy {
        FragmentLeftBinding.inflate(layoutInflater)
    }

    private val event = RandomNumberEvent(22)

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
            CoroutineBus.post(RandomNumberEvent(22))
         }

        binding.btnRegister.setOnClickListener {
            CoroutineBus.subscribe(this, dispatcher = Dispatchers.IO) { event: RandomNumberEvent ->
                binding.tvContent.text = "${binding.tvContent.text} \n ${event.number}"
                Log.e("count", "New random number: ${event.number}")
            }
        }

        binding.btnRegisterSticky.setOnClickListener {
            CoroutineBus.subscribe(this, isSticky = true , dispatcher = Dispatchers.Main) { event: RandomNumberEvent ->
                Toast.makeText(requireContext(), "any: $event", Toast.LENGTH_SHORT).show()
                binding.tvContent.text = "${binding.tvContent.text} \n ${event.number}"
                Log.e("MainActivity", "New random number: ${event.number}")
            }
        }
    }
}