package cc.cuichanghao.infivt

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class EntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        title = "Infinite Viewpager With TabLayout"
    }

    fun onStartNormally(view: View) {
        startActivity(Intent(this, MainActivityNormally::class.java))
    }

    fun onStartOval(view: View) {
        startActivity(Intent(this, MainActivityOval::class.java))
    }

    fun onStartPartialRect(view: View) {
        startActivity(Intent(this, MainActivityPartialRect::class.java))
    }

    fun onStartRect(view: View) {
        startActivity(Intent(this, MainActivityRect::class.java))
    }
}
