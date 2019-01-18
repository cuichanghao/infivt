/**
 * Copyright (C) 2015 nshmura
 * Copyright (C) 2015 The Android Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.cuichanghao.library;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.LinkedHashSet;

/**
 * Edited by cuichanghao on 2018/04/26.
 */
public class RecyclerTabLayout extends RecyclerView {

    protected static final long DEFAULT_SCROLL_DURATION = 200;
    protected static final float DEFAULT_POSITION_THRESHOLD = 0.6f;
    protected static final float POSITION_THRESHOLD_ALLOWABLE = 0.001f;
    protected static final int DEFAULT_DRAW_INDICATOR_COUNT = 20; //cached indicator count

    protected Paint mIndicatorPaint;
    protected Paint mTabItemIndicatorPaint;
    protected int mTabBackgroundResId;
    protected int mTabOnScreenLimit;
    protected int mTabMinWidth;
    protected int mTabMaxWidth;
    protected int mTabTextAppearance;
    protected int mTabSelectedTextColor;
    protected boolean mTabSelectedTextColorSet;
    protected int mTabPaddingStart;
    protected int mTabPaddingTop;
    protected int mTabPaddingEnd;
    protected int mTabPaddingBottom;
    protected int mTabSelectType;
    protected int mIndicatorHeight;
    protected int mRealItemCount;

    //gather show indicator position
    protected LinkedHashSet<Integer> mIndicatorPositionQueue = new LinkedHashSet(DEFAULT_DRAW_INDICATOR_COUNT);
    protected LinkedHashSet<Integer> mIndicatorTempDeque = new LinkedHashSet();
    protected LinearLayoutManager mLinearLayoutManager;
    protected RecyclerOnScrollListener mRecyclerOnScrollListener;
    protected InfiniteViewPager mViewPager;
    protected Adapter<?> mAdapter;

    protected int mIndicatorPosition;
    protected int mIndicatorGap;
    protected int mIndicatorScroll;
    private int mOldPosition;
    private int mOldScrollOffset;
    protected float mOldPositionOffset;
    protected float mPositionThreshold;
    protected boolean mRequestScrollToTab;
    protected boolean mScrollEnabled;

    private int mPxPaddingOval;
    private int mPxPaddingPartialRectTop;
    private int mPxRound;

    public RecyclerTabLayout(Context context) {
        this(context, null);
    }

    public RecyclerTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        mIndicatorPaint = new Paint();
        mTabItemIndicatorPaint = new Paint();
        mPxPaddingOval = dip(getContext(), 4);
        mPxPaddingPartialRectTop = dip(getContext(), 8);
        mPxRound = dip(getContext(), 25);

        getAttributes(context, attrs, defStyle);
        mLinearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollHorizontally() {
                return mScrollEnabled;
            }
        };
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(mLinearLayoutManager);
        setItemAnimator(null);
        mPositionThreshold = DEFAULT_POSITION_THRESHOLD;
        setRefreshIndicatorWithScroll(true);
    }

    private void getAttributes(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.rtl_RecyclerTabLayout,
                defStyle, R.style.rtl_RecyclerTabLayout);
        setIndicatorColor(a.getColor(R.styleable
                .rtl_RecyclerTabLayout_rtl_tabIndicatorColor, 0));
        setIndicatorHeight(a.getDimensionPixelSize(R.styleable
                .rtl_RecyclerTabLayout_rtl_tabIndicatorHeight, 0));

        mTabTextAppearance = a.getResourceId(R.styleable.rtl_RecyclerTabLayout_rtl_tabTextAppearance,
                R.style.rtl_RecyclerTabLayout_Tab);

        mTabPaddingStart = mTabPaddingTop = mTabPaddingEnd = mTabPaddingBottom = a
                .getDimensionPixelSize(R.styleable.rtl_RecyclerTabLayout_rtl_tabPadding, 0);
        mTabPaddingStart = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingStart, mTabPaddingStart);
        mTabPaddingTop = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingTop, mTabPaddingTop);
        mTabPaddingEnd = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingEnd, mTabPaddingEnd);
        mTabPaddingBottom = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingBottom, mTabPaddingBottom);
        mTabSelectType = a.getInt(R.styleable.rtl_RecyclerTabLayout_rtl_selectType, -1);

        if (a.hasValue(R.styleable.rtl_RecyclerTabLayout_rtl_tabSelectedTextColor)) {
            mTabSelectedTextColor = a
                    .getColor(R.styleable.rtl_RecyclerTabLayout_rtl_tabSelectedTextColor, 0);
            mTabSelectedTextColorSet = true;
        }

        mTabOnScreenLimit = a.getInteger(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabOnScreenLimit, 0);
        if (mTabOnScreenLimit == 0) {
            mTabMinWidth = a.getDimensionPixelSize(
                    R.styleable.rtl_RecyclerTabLayout_rtl_tabMinWidth, 0);
            mTabMaxWidth = a.getDimensionPixelSize(
                    R.styleable.rtl_RecyclerTabLayout_rtl_tabMaxWidth, 0);
        }

        mTabBackgroundResId = a
                .getResourceId(R.styleable.rtl_RecyclerTabLayout_rtl_tabBackground, 0);
        mScrollEnabled = a.getBoolean(R.styleable.rtl_RecyclerTabLayout_rtl_scrollEnabled, true);
        a.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener);
            mRecyclerOnScrollListener = null;
        }
        super.onDetachedFromWindow();
    }


    public void setIndicatorColor(int color) {
        mIndicatorPaint.setColor(color);
        mTabItemIndicatorPaint.setColor(color);
    }

    public void setIndicatorHeight(int indicatorHeight) {
        mIndicatorHeight = indicatorHeight;
    }

    public void setAutoSelectionMode(boolean autoSelect) {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener);
            mRecyclerOnScrollListener = null;
        }
        if (autoSelect) {
            mRecyclerOnScrollListener = new RecyclerOnScrollListener(this, mLinearLayoutManager);
            addOnScrollListener(mRecyclerOnScrollListener);
        }
    }

    public void setPositionThreshold(float positionThreshold) {
        mPositionThreshold = positionThreshold;
    }

    public void setRefreshIndicatorWithScroll(boolean autoRefreshIndicator) {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener);
            mRecyclerOnScrollListener = null;
        }
        if (autoRefreshIndicator) {
            mRecyclerOnScrollListener = new RecyclerOnScrollListener(this, mLinearLayoutManager);
            addOnScrollListener(mRecyclerOnScrollListener);
        }
    }

    public void setUpWithViewPager(InfiniteViewPager viewPager) {

        DefaultAdapter adapter = new DefaultAdapter(viewPager);
        adapter.setTabPadding(mTabPaddingStart, mTabPaddingTop, mTabPaddingEnd, mTabPaddingBottom);
        adapter.setTabTextAppearance(mTabTextAppearance);
        adapter.setTabSelectedTextColor(mTabSelectedTextColorSet, mTabSelectedTextColor);
        adapter.setTabMaxWidth(mTabMaxWidth);
        adapter.setTabMinWidth(mTabMinWidth);
        adapter.setTabBackgroundResId(mTabBackgroundResId);
        adapter.setTabOnScreenLimit(mTabOnScreenLimit);

        if(viewPager.getAdapter() instanceof InfinitePagerAdapter) {
            mRealItemCount = ((InfinitePagerAdapter) viewPager.getAdapter()).getRealCount();
            adapter.setRealItemCount(mRealItemCount);
        }

        setUpWithAdapter(adapter);
    }

    public void setUpWithAdapter(Adapter<?> adapter) {
        mAdapter = adapter;
        mViewPager = adapter.getViewPager();
        if (mViewPager.getAdapter() == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }
        mViewPager.addOnPageChangeListener(new ViewPagerOnPageChangeListener(this));
        setAdapter(adapter);
        scrollToTab(mViewPager.getCurrentItem());
    }

    public void setCurrentItem(int position, boolean smoothScroll) {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(position, smoothScroll);
            scrollToTab(mViewPager.getCurrentItem());
            return;
        }

        if (smoothScroll && position != mIndicatorPosition) {
            startAnimation(position);

        } else {
            scrollToTab(position);
        }
    }

    public void setCurrentCenterItem(int position) {
        mIndicatorPositionQueue.clear();
        if(mTabOnScreenLimit > 0) {
            mIndicatorPositionQueue.add(position);
        } else {
            for (int i = position - DEFAULT_DRAW_INDICATOR_COUNT; i < position + DEFAULT_DRAW_INDICATOR_COUNT; i++) {
                if ((i - mIndicatorPosition) % mRealItemCount == 0) {
                    mIndicatorPositionQueue.add(i);
                }
            }
        }
        invalidate();
    }

    protected void startAnimation(final int position) {

        float distance = 1;

        View view = mLinearLayoutManager.findViewByPosition(position);
        if (view != null) {
            float currentX = view.getX() + view.getMeasuredWidth() / 2.f;
            float centerX = getMeasuredWidth() / 2.f;
            distance = Math.abs(centerX - currentX) / view.getMeasuredWidth();
        }

        ValueAnimator animator;
        if (position < mIndicatorPosition) {
            animator = ValueAnimator.ofFloat(distance, 0);
        } else {
            animator = ValueAnimator.ofFloat(-distance, 0);
        }
        animator.setDuration(DEFAULT_SCROLL_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollToTab(position, (float) animation.getAnimatedValue(), true);
            }
        });
        animator.start();
    }

    protected void scrollToTab(int position) {
        scrollToTab(position, 0, false);
        mAdapter.setCurrentIndicatorPosition(position);
        mAdapter.notifyDataSetChanged();
    }

    protected void scrollToTab(int position, float positionOffset, boolean fitIndicator) {
        int scrollOffset = 0;
        View selectedView = null;

        int firstVisiblePosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = mLinearLayoutManager.findLastVisibleItemPosition();

        //search visible area the same indicator item
        for(int i = firstVisiblePosition; i <= lastVisiblePosition; i++ ){
            if( (i - position) % mRealItemCount == 0 ){
                selectedView = mLinearLayoutManager.findViewByPosition(i);
                position = i;
                break;
            }
        }

        View nextView = mLinearLayoutManager.findViewByPosition(position + 1);

        if (selectedView != null) {
            int width = getMeasuredWidth();
            float sLeft = (position == 0) ? 0 : width / 2.f - selectedView.getMeasuredWidth() / 2.f; // left edge of selected tab
            float sRight = sLeft + selectedView.getMeasuredWidth(); // right edge of selected tab

            if (nextView != null) {
                float nLeft = width / 2.f - nextView.getMeasuredWidth() / 2.f; // left edge of next tab
                float distance = sRight - nLeft; // total distance that is needed to distance to next tab
                float dx = distance * positionOffset;
                scrollOffset = (int) (sLeft - dx);

                if (position == 0) {
                    float indicatorGap = (nextView.getMeasuredWidth() - selectedView.getMeasuredWidth()) / 2;
                    mIndicatorGap = (int) (indicatorGap * positionOffset);
                    mIndicatorScroll = (int)((selectedView.getMeasuredWidth() + indicatorGap)  * positionOffset);

                } else {
                    float indicatorGap = (nextView.getMeasuredWidth() - selectedView.getMeasuredWidth()) / 2;
                    mIndicatorGap = (int) (indicatorGap * positionOffset);
                    mIndicatorScroll = (int) dx;
                }

            } else {
                scrollOffset = (int) sLeft;
                mIndicatorScroll = 0;
                mIndicatorGap = 0;
            }
            if (fitIndicator) {
                mIndicatorScroll = 0;
                mIndicatorGap = 0;
            }

        } else {
            if (getMeasuredWidth() > 0 && mTabMaxWidth > 0 && mTabMinWidth == mTabMaxWidth) { //fixed size
                int width = mTabMinWidth;
                int offset = (int) (positionOffset * -width);
                int leftOffset = (int) ((getMeasuredWidth() - width) / 2.f);
                scrollOffset = offset + leftOffset;
            }
            mRequestScrollToTab = true;
        }

        updateCurrentIndicatorPosition(position, positionOffset - mOldPositionOffset, positionOffset);
        mIndicatorPosition = position;
        setCurrentCenterItem(mIndicatorPosition);

        stopScroll();

        if (position != mOldPosition || scrollOffset != mOldScrollOffset) {
            mLinearLayoutManager.scrollToPositionWithOffset(position, scrollOffset);
        }
        if (mIndicatorHeight > 0) {
            invalidate();
        }

        mOldPosition = position;
        mOldScrollOffset = scrollOffset;
        mOldPositionOffset = positionOffset;
    }

    protected void updateCurrentIndicatorPosition(int position, float dx, float positionOffset) {
        if (mAdapter == null) {
            return;
        }
        int indicatorPosition = -1;
        if (dx > 0 && positionOffset >= mPositionThreshold - POSITION_THRESHOLD_ALLOWABLE) {
            indicatorPosition = position + 1;

        } else if (dx < 0 && positionOffset <= 1 - mPositionThreshold + POSITION_THRESHOLD_ALLOWABLE) {
            indicatorPosition = position;
        }
        if (indicatorPosition >= 0 && indicatorPosition != mAdapter.getCurrentIndicatorPosition()) {
            mAdapter.setCurrentIndicatorPosition(indicatorPosition);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(mIndicatorPositionQueue.size() == 0 ) return;

        mIndicatorTempDeque.clear();

        //元のqueueはそのままに保持しつつ、新しいqueueで回す。
        mIndicatorTempDeque.addAll(mIndicatorPositionQueue);

        for (Integer indicatorPosition : mIndicatorTempDeque) {
            View view = mLinearLayoutManager.findViewByPosition(indicatorPosition);
            if (view == null) {
                if (mRequestScrollToTab) {
                    mRequestScrollToTab = false;
                    scrollToTab(mViewPager.getCurrentItem());
                }
                continue;
            }
            mRequestScrollToTab = false;

            int left;
            int right;
            if (isLayoutRtl()) {
                left = view.getLeft() - mIndicatorScroll - mIndicatorGap;
                right = view.getRight() - mIndicatorScroll + mIndicatorGap;
            } else {
                left = view.getLeft() + mIndicatorScroll - mIndicatorGap;
                right = view.getRight() + mIndicatorScroll + mIndicatorGap;
            }

            int top = getHeight() - mIndicatorHeight;
            int bottom = getHeight();

            canvas.drawRect(left, top, right, bottom, mIndicatorPaint);

            Log.d("RecyclerTabLayout", "left:" + left + ",right:" + right);

            switch (mTabSelectType) {
                case 0:
                    //oval
                    canvas.drawRoundRect(new RectF(left, mPxPaddingOval, right, bottom - mPxPaddingOval), mPxRound, mPxRound, mTabItemIndicatorPaint);
                    break;
                case 1:
                    //rect
                    canvas.drawRect(left, 0, right, top, mTabItemIndicatorPaint);
                    break;
                case 2:
                    //partialRect
                    Path path = RoundedRect(left, mPxPaddingPartialRectTop, right, bottom, 30, 30, true, true, false, false);
                    canvas.drawPath(path, mTabItemIndicatorPaint);
                    break;
            }
        }
    }

    public Path RoundedRect(float left, float top, float right, float bottom, float rx, float ry,
                            boolean tl, boolean tr, boolean br, boolean bl){
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width / 2) rx = width / 2;
        if (ry > height / 2) ry = height / 2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        if (tr)
            path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
        else{
            path.rLineTo(0, -ry);
            path.rLineTo(-rx,0);
        }
        path.rLineTo(-widthMinusCorners, 0);
        if (tl)
            path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
        else{
            path.rLineTo(-rx, 0);
            path.rLineTo(0,ry);
        }
        path.rLineTo(0, heightMinusCorners);

        if (bl)
            path.rQuadTo(0, ry, rx, ry);//bottom-left corner
        else{
            path.rLineTo(0, ry);
            path.rLineTo(rx,0);
        }

        path.rLineTo(widthMinusCorners, 0);
        if (br)
            path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner
        else{
            path.rLineTo(rx,0);
            path.rLineTo(0, -ry);
        }

        path.rLineTo(0, -heightMinusCorners);

        path.close();//Given close, last lineto can be removed.

        return path;
    }

    protected boolean isLayoutRtl() {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    protected static class RecyclerOnScrollListener extends OnScrollListener {

        private static int REFRESH_DISTANCE = 3000;
        protected RecyclerTabLayout mRecyclerTabLayout;
        protected LinearLayoutManager mLinearLayoutManager;

        public RecyclerOnScrollListener(RecyclerTabLayout recyclerTabLayout,
                                        LinearLayoutManager linearLayoutManager) {
            mRecyclerTabLayout = recyclerTabLayout;
            mLinearLayoutManager = linearLayoutManager;
        }

        public int mDx;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mDx += dx;
            if (mDx > REFRESH_DISTANCE) {
                refreshCenterTabForRightScroll();
                mDx = 0;
            } else if(mDx < -REFRESH_DISTANCE) {
                refreshCenterTabForLeftScroll();
                mDx = 0;
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case SCROLL_STATE_IDLE:
                    if (mDx > 0) {
                        refreshCenterTabForRightScroll();
                    } else {
                        refreshCenterTabForLeftScroll();
                    }
                    mDx = 0;
                    break;
                case SCROLL_STATE_DRAGGING:
                case SCROLL_STATE_SETTLING:
            }
        }

        protected void refreshCenterTabForRightScroll() {
            int first = mLinearLayoutManager.findFirstVisibleItemPosition();
            int last = mLinearLayoutManager.findLastVisibleItemPosition();
            int center = mRecyclerTabLayout.getWidth() / 2;

            for (int position = first; position <= last; position++) {
                View view = mLinearLayoutManager.findViewByPosition(position);
                if (view.getLeft() + view.getWidth() >= center) {
                    mRecyclerTabLayout.setCurrentCenterItem(position);
                    break;
                }
            }
        }

        protected void refreshCenterTabForLeftScroll() {
            int first = mLinearLayoutManager.findFirstVisibleItemPosition();
            int last = mLinearLayoutManager.findLastVisibleItemPosition();
            int center = mRecyclerTabLayout.getWidth() / 2;
            for (int position = last; position >= first; position--) {
                View view = mLinearLayoutManager.findViewByPosition(position);
                if (view.getLeft() <= center) {
                    mRecyclerTabLayout.setCurrentCenterItem(position);
                    break;
                }
            }
        }
    }

    protected static class ViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {

        private final RecyclerTabLayout mRecyclerTabLayout;
        private int mScrollState;

        public ViewPagerOnPageChangeListener(RecyclerTabLayout recyclerTabLayout) {
            mRecyclerTabLayout = recyclerTabLayout;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mScrollState != ViewPager.SCROLL_STATE_IDLE) {
                mRecyclerTabLayout.scrollToTab(position, positionOffset, false);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                if (mRecyclerTabLayout.mIndicatorPosition != position) {
                    mRecyclerTabLayout.scrollToTab(position);
                }
            }
        }
    }

    public static abstract class Adapter<T extends RecyclerView.ViewHolder>
            extends RecyclerView.Adapter<T> {

        protected InfiniteViewPager mViewPager;
        protected int mIndicatorPosition;

        public Adapter(InfiniteViewPager viewPager) {
            mViewPager = viewPager;
        }

        public InfiniteViewPager getViewPager() {
            return mViewPager;
        }

        public void setCurrentIndicatorPosition(int indicatorPosition) {
            mIndicatorPosition = indicatorPosition;
        }

        public int getCurrentIndicatorPosition() {
            return mIndicatorPosition;
        }
    }

    public static class DefaultAdapter
            extends Adapter<DefaultAdapter.ViewHolder> {

        protected static final int MAX_TAB_TEXT_LINES = 2;

        protected int mTabPaddingStart;
        protected int mTabPaddingTop;
        protected int mTabPaddingEnd;
        protected int mTabPaddingBottom;
        protected int mTabTextAppearance;
        protected boolean mTabSelectedTextColorSet;
        protected int mTabSelectedTextColor;
        private int mTabMaxWidth;
        private int mTabMinWidth;
        private int mTabBackgroundResId;
        private int mTabOnScreenLimit;
        private int mRealItemCount;

        public DefaultAdapter(InfiniteViewPager viewPager) {
            super(viewPager);
        }

        @SuppressWarnings("deprecation")
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TabTextView tabTextView = new TabTextView(parent.getContext());

            ViewCompat.setPaddingRelative(tabTextView, mTabPaddingStart, mTabPaddingTop,
                    mTabPaddingEnd, mTabPaddingBottom);
            tabTextView.setGravity(Gravity.CENTER);
            tabTextView.setMaxLines(MAX_TAB_TEXT_LINES);
            tabTextView.setEllipsize(TextUtils.TruncateAt.END);

            if (mTabOnScreenLimit > 0) {
                int width = parent.getMeasuredWidth() / mTabOnScreenLimit;
                tabTextView.setMaxWidth(width);
                tabTextView.setMinWidth(width);

            } else {
                if (mTabMaxWidth > 0) {
                    tabTextView.setMaxWidth(mTabMaxWidth);
                }
                tabTextView.setMinWidth(mTabMinWidth);
            }

            tabTextView.setTextAppearance(tabTextView.getContext(), mTabTextAppearance);
            if (mTabSelectedTextColorSet) {
                tabTextView.setTextColor(tabTextView.createColorStateList(
                        tabTextView.getCurrentTextColor(), mTabSelectedTextColor));
            }
            if (mTabBackgroundResId != 0) {
                tabTextView.setBackgroundDrawable(
                        AppCompatResources.getDrawable(tabTextView.getContext(), mTabBackgroundResId));
            }
            tabTextView.setLayoutParams(createLayoutParamsForTabs());

            return new ViewHolder(tabTextView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CharSequence title = getViewPager().getAdapter().getPageTitle(position);
            holder.title.setText(title);
            holder.title.setSelected(getCurrentIndicatorPosition() % mRealItemCount == position % mRealItemCount);
        }

        @Override
        public int getItemCount() {
            return getViewPager().getAdapter().getCount();
        }

        public void setTabPadding(int tabPaddingStart, int tabPaddingTop, int tabPaddingEnd,
                                  int tabPaddingBottom) {
            mTabPaddingStart = tabPaddingStart;
            mTabPaddingTop = tabPaddingTop;
            mTabPaddingEnd = tabPaddingEnd;
            mTabPaddingBottom = tabPaddingBottom;
        }

        public void setTabTextAppearance(int tabTextAppearance) {
            mTabTextAppearance = tabTextAppearance;
        }

        public void setTabSelectedTextColor(boolean tabSelectedTextColorSet,
                                            int tabSelectedTextColor) {
            mTabSelectedTextColorSet = tabSelectedTextColorSet;
            mTabSelectedTextColor = tabSelectedTextColor;
        }

        public void setTabMaxWidth(int tabMaxWidth) {
            mTabMaxWidth = tabMaxWidth;
        }

        public void setTabMinWidth(int tabMinWidth) {
            mTabMinWidth = tabMinWidth;
        }

        public void setTabBackgroundResId(int tabBackgroundResId) {
            mTabBackgroundResId = tabBackgroundResId;
        }

        public void setTabOnScreenLimit(int tabOnScreenLimit) {
            mTabOnScreenLimit = tabOnScreenLimit;
        }

        public void setRealItemCount(int realCount) {
            this.mRealItemCount = realCount;
        }

        protected RecyclerView.LayoutParams createLayoutParamsForTabs() {
            return new RecyclerView.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView title;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView;
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        if (pos != NO_POSITION) {
                            if( (pos - mIndicatorPosition) % mRealItemCount == 0) {
                                return;
                            }

                            int loopDistance = pos - mIndicatorPosition;
                            float percent = (float)loopDistance/mRealItemCount;
                            if(mTabOnScreenLimit == 0 && Math.abs(percent) != 0.5f) {
                                int nearlyTheSameItem = mIndicatorPosition + Math.round(percent) * mRealItemCount;
                                loopDistance = pos - nearlyTheSameItem;
                            }

                            int currentItem = getViewPager().getCurrentItem();
                            getViewPager().setCurrentItem(currentItem + loopDistance, true);
                        }
                    }
                });
            }
        }
    }


    public static class TabTextView extends AppCompatTextView {

        public TabTextView(Context context) {
            super(context);
        }

        public ColorStateList createColorStateList(int defaultColor, int selectedColor) {
            final int[][] states = new int[2][];
            final int[] colors = new int[2];
            states[0] = SELECTED_STATE_SET;
            colors[0] = selectedColor;
            // Default enabled state
            states[1] = EMPTY_STATE_SET;
            colors[1] = defaultColor;
            return new ColorStateList(states, colors);
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            Typeface typeface;
            if(selected) {
                typeface = Typeface.DEFAULT_BOLD;
            } else {
                typeface = Typeface.DEFAULT;
            }
            setTypeface(typeface);
        }
    }

    public int dip(Context context, int value) {
        return (int)(value * context.getResources().getDisplayMetrics().density);
    }
}
