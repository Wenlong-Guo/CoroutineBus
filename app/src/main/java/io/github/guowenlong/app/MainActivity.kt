package io.github.guowenlong.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.github.guowenlong.coroutinebus.CoroutineBus
import io.github.guowenlong.coroutinebus.subscribeByLifecycle
import kotlinx.coroutines.Dispatchers

/**
 * Description: 首页
 * Author:      郭文龙
 * Date:        2023/3/20 22:00
 * Email:       guowenlong20000@sina.com
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
