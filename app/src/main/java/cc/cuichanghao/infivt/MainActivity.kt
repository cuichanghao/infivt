package cc.cuichanghao.infivt

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import cc.cuichanghao.library.InfinitePagerAdapter
import cc.cuichanghao.library.RecyclerTabLayout

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = MainAdapter(supportFragmentManager)
        val wrappedAdapter = InfinitePagerAdapter(adapter)

        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = wrappedAdapter

        val recyclerTabLayout = findViewById<RecyclerTabLayout>(R.id.tab_layout)
        recyclerTabLayout.setUpWithViewPager(viewPager)
    }
}
