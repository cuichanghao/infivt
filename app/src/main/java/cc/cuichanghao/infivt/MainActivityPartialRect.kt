package cc.cuichanghao.infivt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cc.cuichanghao.library.InfinitePagerAdapter
import kotlinx.android.synthetic.main.activity_main_partialrect.*

class MainActivityPartialRect : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_partialrect)

        val adapter = MainAdapter(supportFragmentManager)
        adapter.setData(list)
        val wrappedAdapter = InfinitePagerAdapter(adapter)
        wrappedAdapter.setNumOfLoops(1)


        view_pager.adapter = wrappedAdapter
        tab_layout.setRefreshIndicatorWithScroll(false)
        tab_layout.setUpWithViewPager(view_pager)


        if(savedInstanceState == null) {
            tab_layout.setCurrentItem(view_pager.offsetAmount, false)
        }
    }
}
