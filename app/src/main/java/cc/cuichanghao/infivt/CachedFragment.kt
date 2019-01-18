package cc.cuichanghao.infivt

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_cached.*

/**
 * Example Fragment class that shows an position inside a TextView.
 */
class CachedFragment : Fragment() {

    private var position: Int = 0

    companion object {
        val list = listOf("A", "B-LargeTitle", "C", "D", "E")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        position = args!!.getInt("position")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_cached, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text.text = list[position]
    }
}
