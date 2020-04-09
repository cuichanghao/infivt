package cc.cuichanghao.infivt

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_changeable_fragment_adapter.*


class MainActivityChangeablePager : AppCompatActivity() {

    private lateinit var adapter: MainAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_changeable_fragment_adapter)

        adapter = MainAdapter(supportFragmentManager)
        adapter.setData(list)

        view_pager.adapter = adapter
        tab_layout.setupWithViewPager(view_pager)
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
                adapter.setData(list2)
            }
        }
        return super.onOptionsItemSelected(item);
    }

}