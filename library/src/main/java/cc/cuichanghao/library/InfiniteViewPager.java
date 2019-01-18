package cc.cuichanghao.library;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * A {@link ViewPager} that allows pseudo-infinite paging with a wrap-around effect. Should be used with an {@link
 * InfinitePagerAdapter}.
 */
public class InfiniteViewPager extends ViewPager {

    public InfiniteViewPager(Context context) {
        super(context);
    }

    public InfiniteViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        // offset first element so that we can scroll to the left
        setCurrentItem(getOffsetAmount());
    }

    @Override
    public void setCurrentItem(int item) {
        // offset the current item to ensure there is space to scroll
        setCurrentItem(item, false);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (getAdapter() instanceof InfinitePagerAdapter) {
            InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) getAdapter();

            boolean nearLeftEdge = item <= infAdapter.getRealCount();
            boolean nearRightEdge = item >= infAdapter.getCount() - infAdapter.getRealCount();
            if (nearLeftEdge || nearRightEdge) {
                super.setCurrentItem(getOffsetAmount(), false);
                return;
            }
        }
        super.setCurrentItem(item, smoothScroll);
    }

    /**
     * find center position
     * @return
     */
    public int getOffsetAmount() {
        if (getAdapter().getCount() == 0) {
            return 0;
        }
        if (getAdapter() instanceof InfinitePagerAdapter) {
            InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) getAdapter();
            return infAdapter.getRealCount() * (infAdapter.getNumOfLoops() / 2);
        } else {
            return 0;
        }
    }
}
