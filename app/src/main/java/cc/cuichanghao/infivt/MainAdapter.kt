package cc.cuichanghao.infivt

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import cc.cuichanghao.library.FragmentStatePagerChangeableAdapter

class MainAdapter(fm: FragmentManager) : FragmentStatePagerChangeableAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var list = listOf<String>()

    fun setData(list: List<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return ContentFragment().apply fragment@ {
            arguments = Bundle().apply {
                Log.d("MainAdapter", "CachedFragment ${list[position]} \n ${this@fragment}")
                putString("title", list[position])
            }
        }
    }

    override fun getItemPosition(`object`: Any): Int {
        val title = (`object` as ContentFragment).arguments?.getString("title")
        val result = list.indexOf(title)
        return if (result == -1) {
            PagerAdapter.POSITION_NONE
        } else {
            result
        }
    }

    override fun getCount() = list.size

    override fun getPageTitle(position: Int) = list[position]
}