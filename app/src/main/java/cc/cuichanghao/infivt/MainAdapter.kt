package cc.cuichanghao.infivt

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import cc.cuichanghao.library.FragmentCachePagerAdapter

class MainAdapter(fm: FragmentManager) : FragmentCachePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return CachedWebFragment().apply {
            arguments = Bundle().apply {
                putInt("identifier", position)
            }
        }
    }

    override fun getCount() = CachedWebFragment.map.size

    override fun getPageTitle(position: Int) = CachedWebFragment.map[position].first
}