package cc.cuichanghao.infivt

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import cc.cuichanghao.library.InfinitePagerAdapter
import cc.cuichanghao.library.InfiniteViewPager
import cc.cuichanghao.library.RecyclerTabLayout

class MainActivityPartialRect : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_partialrect)

        val adapter = MainAdapter(supportFragmentManager)
        val wrappedAdapter = InfinitePagerAdapter(adapter)
        wrappedAdapter.setNumOfLoops(1)

        val viewPager = findViewById<InfiniteViewPager>(R.id.view_pager)
        viewPager.adapter = wrappedAdapter

        val recyclerTabLayout = findViewById<RecyclerTabLayout>(R.id.tab_layout)
        recyclerTabLayout.setRefreshIndicatorWithScroll(false)
        recyclerTabLayout.setUpWithViewPager(viewPager)


        if(savedInstanceState == null) {
            recyclerTabLayout.setCurrentItem(viewPager.offsetAmount, false)
        }
    }
}
