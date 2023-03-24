package io.github.guowenlong.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.github.guowenlong.app.databinding.ActivityGuideBinding
import io.github.guowenlong.coroutinebus.CoroutineBus
import io.github.guowenlong.coroutinebus.subscribeByLifecycle
import kotlinx.coroutines.Dispatchers

/**
 * Description: 引导页
 * Author:      郭文龙
 * Date:        2023/3/20 22:00
 * Email:       guowenlong20000@sina.com
 */
class GuideActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityGuideBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        CoroutineBus.subscribeByLifecycle(
            this,
            isSticky = true,
            lifecycleOwner = this,
            dispatcher = Dispatchers.Main

        ) { event: RandomNumEvent ->
            Toast.makeText(
                this@GuideActivity,
                "GuideActivity页面收到随机数: ${event.number}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}