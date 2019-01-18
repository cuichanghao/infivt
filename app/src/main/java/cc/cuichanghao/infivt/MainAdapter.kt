package cc.cuichanghao.infivt

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import cc.cuichanghao.library.FragmentCachePagerAdapter

class MainAdapter(fm: FragmentManager) : FragmentCachePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return CachedFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
    }

    override fun getCount() = CachedFragment.list.size

    override fun getPageTitle(position: Int) = CachedFragment.list[position]
}