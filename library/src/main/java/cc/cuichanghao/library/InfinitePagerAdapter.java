package cc.cuichanghao.library;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;

/**
 * A PagerAdapter that wraps around another PagerAdapter to handle paging wrap-around.
 */
public class InfinitePagerAdapter extends PagerAdapter {

    private static final int DEFAULT_NUM_OF_LOOPS = 1000; //enough 1000 loop
    private int numOfLoops = DEFAULT_NUM_OF_LOOPS;
    private int selectedPosition = 0;
    private PagerAdapter adapter;

    public InfinitePagerAdapter(PagerAdapter adapter) {
        this.adapter = adapter;
    }

    public void setNumOfLoops(int numOfLoops) {
        this.numOfLoops = numOfLoops;
    }

    public int getNumOfLoops() {
        return numOfLoops;
    }

    @Override
    public int getCount() {
        if (getRealCount() == 0) {
            return 0;
        }
        // warning: scrolling to very high values (1,000,000+) results in
        // strange drawing behaviour

        return numOfLoops * getRealCount();
    }

    /**
     * @return the {@link #getCount()} result of the wrapped adapter
     */
    public int getRealCount() {
        return adapter.getCount();
    }

    public void willBePageSelect(int selectPosition) {
        this.selectedPosition = selectPosition;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int virtualPosition = position % getRealCount();
        // only expose virtual position to the inner adapter
        return adapter.instantiateItem(container, virtualPosition);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        int distance = Math.abs((selectedPosition - position) % getRealCount());
        if( distance > 1 && distance < (getRealCount() - 1)) {
            adapter.destroyItem(container, position % getRealCount(), object);
        }
    }

    /*
     * Delegate rest of methods directly to the inner adapter.
     */

    @Override
    public void finishUpdate(ViewGroup container) {
        adapter.finishUpdate(container);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return adapter.isViewFromObject(view, object);
    }

    @Override
    public void restoreState(Parcelable bundle, ClassLoader classLoader) {
        adapter.restoreState(bundle, classLoader);
    }

    @Override
    public Parcelable saveState() {
        return adapter.saveState();
    }

    @Override
    public void startUpdate(ViewGroup container) {
        adapter.startUpdate(container);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        int virtualPosition = position % getRealCount();
        return adapter.getPageTitle(virtualPosition);
    }

    @Override
    public float getPageWidth(int position) {
        return adapter.getPageWidth(position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        adapter.setPrimaryItem(container, position, object);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        adapter.unregisterDataSetObserver(observer);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        adapter.registerDataSetObserver(observer);
    }

    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
        super.notifyDataSetChanged();
    }

    public void notifyDataSetChangedWithoutSubAdapter() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return adapter.getItemPosition(object);
    }

}
