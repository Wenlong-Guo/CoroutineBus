package io.github.guowenlong.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val event = RandomNumberEvent(22)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        findViewById<Button>(R.id.buttonTest).setOnClickListener {
////            lifecycleScope.launch {
////                // skip retained event and await next one
////                val event = GlobalBus.getFlow<RandomNumberEvent>().drop(1).take(1).first()
////                Toast.makeText(
////                    this@MainActivity,
////                    "Received event with number: ${event.number}",
////                    Toast.LENGTH_SHORT
////                ).show()
////            }
//        }

//

//        findViewById<Button>(R.id.register_sticky).setOnClickListener {
//            CoroutineBus.subscribe(this, isSticky = true) { event: RandomNumberEvent ->
//                Toast.makeText(this@MainActivity, "any: $event", Toast.LENGTH_SHORT).show()
//                Log.e("MainActivity", "New random number: ${event.number}")
//            }
//        }
//
//        findViewById<Button>(R.id.unregister).setOnClickListener {
//            CoroutineBus.unsubscribe(this, RandomNumberEvent::class.java)
//            Toast.makeText(this@MainActivity, "unregister", Toast.LENGTH_SHORT).show()
//        }

    }
}
