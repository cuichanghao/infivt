package cc.cuichanghao.infivt

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import cc.cuichanghao.library.InfinitePagerAdapter
import kotlinx.android.synthetic.main.activity_main_normally.*


class MainActivityNormally : AppCompatActivity() {

    private lateinit var adapter: MainAdapter
    private lateinit var wrappedAdapter: InfinitePagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_normally)

        adapter = MainAdapter(supportFragmentManager)
        adapter.setData(list)

        wrappedAdapter = InfinitePagerAdapter(adapter)
        view_pager.adapter = wrappedAdapter

        tab_layout.setUpWithViewPager(view_pager)

        if (savedInstanceState == null) {
            tab_layout.setCurrentItem(view_pager.offsetAmount, false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.change_adapter, menu)

        // return true so that the menu pop up is opened
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.change_adapter -> {
                val oldItemTitle = wrappedAdapter.getPageTitle(view_pager.currentItem)

                adapter.setData(list2)
                wrappedAdapter.notifyDataSetChangedWithoutSubAdapter()
                tab_layout.setUpWithViewPager(view_pager)

                var newItemPosition = list2.indexOf(oldItemTitle)
                if (newItemPosition == -1) {
                    newItemPosition = 0
                }

                tab_layout.setCurrentItem(view_pager.offsetAmount + newItemPosition, false)
            }
        }
        return super.onOptionsItemSelected(item);
    }

}