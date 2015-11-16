package com.jaredlam.tagsview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaredluo on 15/10/14.
 */
public class TagsView extends ViewGroup {

    private static final int DEFAULT_HORIZONTAL_PADDING = 10;
    private static final int DEFAULT_VERTICAL_PADDING = 10;

    private int mHorizontalPadding = DEFAULT_HORIZONTAL_PADDING;
    private int mVerticalPadding = DEFAULT_VERTICAL_PADDING;
    private boolean mWillShiftFillGap = false;

    private int mCurrentRowHeight = 0;
    private List<Integer> mVisibleChildIndex = new ArrayList<>();

    private LayoutListener mLayoutListener;
    private List<View> mVisibleChildren = new ArrayList<>();

    public TagsView(Context context) {
        this(context, null);
    }

    public TagsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.tagsview_TagsView, defStyleAttr, 0);
        int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attrIndex = a.getIndex(i);
            if (attrIndex == R.styleable.tagsview_TagsView_tagsview_horizontal_padding) {
                mHorizontalPadding = a.getDimensionPixelSize(attrIndex, DEFAULT_HORIZONTAL_PADDING);
            } else if (attrIndex == R.styleable.tagsview_TagsView_tagsview_vertical_padding) {
                mVerticalPadding = a.getDimensionPixelOffset(attrIndex, DEFAULT_VERTICAL_PADDING);
            }
        }
        a.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mVisibleChildren.clear();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        top = top > 0 ? top : 0;

        for (int i = 0; i < mVisibleChildIndex.size(); i++) {
            Point leftTop = layoutChild(i, left, top);
            left = leftTop.x;
            top = leftTop.y;
        }

        if (mLayoutListener != null) {
            mLayoutListener.onLayoutFinished(mVisibleChildren);
        }
    }

    private Point layoutChild(int i, int left, int top) {
        int index = mVisibleChildIndex.get(i);
        View child = getChildAt(index);

        if (left + child.getMeasuredWidth() + getPaddingRight() <= getMeasuredWidth()) {
            mCurrentRowHeight = child.getMeasuredHeight() > mCurrentRowHeight ? child.getMeasuredHeight() : mCurrentRowHeight;
            setChildFrame(child, left, top, child.getMeasuredWidth(), child.getMeasuredHeight());
        } else {
            top += mCurrentRowHeight;
            top += mVerticalPadding;
            left = getPaddingLeft();
            setChildFrame(child, left, top, child.getMeasuredWidth(), child.getMeasuredHeight());
            mCurrentRowHeight = child.getMeasuredHeight();
        }

        left = left + child.getMeasuredWidth() + mHorizontalPadding;

        mVisibleChildren.add(child);

        return new Point(left, top);
    }


    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int childTotalWidth = 0;
        int childMaxHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            childTotalWidth += child.getMeasuredWidth();
            if (child.getMeasuredHeight() > childMaxHeight) {
                childMaxHeight = child.getMeasuredHeight();
            }
        }
        childTotalWidth += ((childCount - 1) * mHorizontalPadding);

        mVisibleChildIndex.clear();

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                if (mWillShiftFillGap) {
                    int left = getPaddingLeft();
                    for (int i = 0; i < getChildCount(); i++) {
                        if (!hasAddedToVisible(i)) {
                            View child = getChildAt(i);
                            int right = left + child.getMeasuredWidth();
                            if (right + getPaddingRight() <= widthSize) {
                                left += (child.getMeasuredWidth() + mHorizontalPadding);
                            } else {
                                for (int j = i + 1; j < getChildCount(); j++) {
                                    if (!hasAddedToVisible(j)) {
                                        View fillChild = getChildAt(j);
                                        right = left + fillChild.getMeasuredWidth();
                                        if (right + getPaddingRight() <= widthSize) {
                                            left += (fillChild.getMeasuredWidth() + mHorizontalPadding);
                                            mVisibleChildIndex.add(j);
                                        }
                                    }
                                }
                                left = getPaddingLeft();
                                left += (child.getMeasuredWidth() + mHorizontalPadding);
                            }

                            mVisibleChildIndex.add(i);
                        }
                    }
                } else {
                    fillVisibleChild(getChildCount());
                }
                break;
            case MeasureSpec.AT_MOST:
                int count = getChildCount();
                fillVisibleChild(count);
                widthSize = childTotalWidth;
                break;
        }

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                heightSize = getPaddingTop();
                int i = 0;
                int rowMaxHeight = 0;
                int left = getPaddingLeft();
                while (i < mVisibleChildIndex.size()) {
                    View child = getChildAt(mVisibleChildIndex.get(i));
                    int right = left + child.getMeasuredWidth();
                    if (right + getPaddingRight() <= widthSize) {
                        rowMaxHeight = rowMaxHeight < child.getMeasuredHeight() ? child.getMeasuredHeight() : rowMaxHeight;
                    } else {
                        heightSize += rowMaxHeight;
                        heightSize += mVerticalPadding;
                        left = getPaddingLeft();
                        rowMaxHeight = child.getMeasuredHeight();
                    }

                    left += (child.getMeasuredWidth() + mHorizontalPadding);
                    i++;
                }
                heightSize += rowMaxHeight;
                heightSize += getPaddingBottom();
                break;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    private boolean hasAddedToVisible(int i) {
        for (Integer index : mVisibleChildIndex) {
            if (i == index) {
                return true;
            }
        }

        return false;
    }

    private void fillVisibleChild(int count) {
        for (int i = 0; i < getChildCount(); i++) {
            if (i < count) {
                mVisibleChildIndex.add(i);
            } else {
                getChildAt(i).setVisibility(View.GONE);
            }
        }
    }

    public void setLayoutListener(LayoutListener mLayoutListener) {
        this.mLayoutListener = mLayoutListener;
    }

    public interface LayoutListener {
        void onLayoutFinished(List<View> visibleChildren);

    }

    public void setWillShiftAndFillGap(boolean mWillShift) {
        this.mWillShiftFillGap = mWillShift;
    }

    public int getHorizontalPadding() {
        return mHorizontalPadding;
    }

    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    public int getVerticalPadding() {
        return mVerticalPadding;
    }

    public void setVerticalPadding(int mVerticalPadding) {
        this.mVerticalPadding = mVerticalPadding;
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        mVisibleChildren.clear();
        mVisibleChildIndex.clear();
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        child.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllViews();
    }
}
