package io.github.guowenlong.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.guowenlong.app.databinding.ActivityGuideBinding

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

    private val event = RandomNumberEvent(22)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
//        findViewById<TextView>(R.id.tv_title).setOnClickListener {
//            val newRandomNumber = Random.nextInt(0, 100)
//            CoroutineBus.post(event)
//            Log.e("MainActivity", "send")
//        }
//
//        findViewById<ImageView>(R.id.iv_logo).setOnClickListener {
//            CoroutineBus.subscribe(
//                this,
//            ) { event: RandomNumberEvent ->
//                Toast.makeText(
//                    this@GuideActivity,
//                    "New random number: ${event.number}",
//                    Toast.LENGTH_SHORT
//                ).show()
//                Log.e("GuideActivity", "New random number: ${event.number}")
//            }
//        }
    }
}