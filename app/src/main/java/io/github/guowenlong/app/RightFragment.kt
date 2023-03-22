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
import kotlinx.coroutines.Dispatchers

/**
 * Description:
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
            io.github.guowenlong.coroutinebus.CoroutineBus.post(RandomNumberEvent(22))
        }

        io.github.guowenlong.coroutinebus.CoroutineBus.subscribeByLifecycle(
            this, isSticky = true, lifecycleOwner = this, dispatcher = Dispatchers.Main
        ) { event: RandomNumberEvent ->
            binding.tvContent.text = "${binding.tvContent.text} \n ${event.number}"
            Toast.makeText(requireContext(), "any: $event", Toast.LENGTH_SHORT).show()
            Log.e("count", "New random number: ${event.number}")
        }
    }
}