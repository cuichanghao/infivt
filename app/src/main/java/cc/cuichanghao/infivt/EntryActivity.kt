package cc.cuichanghao.infivt

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

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

    fun onStartChangableFragmentAdapter(view: View) {
        startActivity(Intent(this, MainActivityChangeablePager::class.java))
    }

    fun onStartWithoutTab(view: View){
        startActivity(Intent(this, MainActivityWithoutTab::class.java))
    }
}
