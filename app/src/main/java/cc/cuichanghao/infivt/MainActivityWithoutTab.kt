package cc.cuichanghao.infivt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cc.cuichanghao.library.InfinitePagerAdapter
import kotlinx.android.synthetic.main.activity_main_without_tab.*

class MainActivityWithoutTab : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_without_tab)

        val adapter = MainAdapter(supportFragmentManager)
        adapter.setData(list)
        val wrappedAdapter = InfinitePagerAdapter(adapter)

        view_pager.adapter = wrappedAdapter
    }
}