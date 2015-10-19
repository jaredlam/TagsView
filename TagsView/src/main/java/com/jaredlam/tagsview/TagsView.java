package com.jaredlam.tagsview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaredluo on 15/10/14.
 */
public class TagsView extends ViewGroup {

    private static final int DEFAULT_PADDING = 10;

    public static final int LEFT_TO_RIGHT = 0;
    public static final int RIGHT_TO_LEFT = 1;

    private int mPadding = DEFAULT_PADDING;
    private boolean mWillShift = false;
    private int mOrder = LEFT_TO_RIGHT;

    private List<Integer> mVisibleChildIndex = new ArrayList<>();

    private LayoutListener mLayoutListener;
    private List<View> mVisibleChildren = new ArrayList<>();

    public TagsView(Context context) {
        super(context);
    }

    public TagsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TagsView, defStyleAttr, 0);
        int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attrIndex = a.getIndex(i);
            if (attrIndex == R.styleable.TagsView_padding) {
                mPadding = a.getInt(attrIndex, 0);
            }
        }
        a.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mVisibleChildren.clear();
        if (mOrder == LEFT_TO_RIGHT) {
            int childLeft = 0;
            for (int i = 0; i < mVisibleChildIndex.size(); i++) {
                int index = mVisibleChildIndex.get(i);
                View child = getChildAt(index);
                setChildFrame(child, childLeft, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                childLeft += mPadding;
                childLeft += child.getMeasuredWidth();

                mVisibleChildren.add(child);
            }
        } else if (mOrder == RIGHT_TO_LEFT) {
            int childLeft = 0;
            for (int i = mVisibleChildIndex.size() - 1; i >= 0; i--) {
                int index = mVisibleChildIndex.get(i);
                View child = getChildAt(index);
                setChildFrame(child, childLeft, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                childLeft += mPadding;
                childLeft += child.getMeasuredWidth();

                mVisibleChildren.add(child);
            }
        }

        if (mLayoutListener != null) {
            mLayoutListener.onLayoutFinished(mVisibleChildren);
        }
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
        childTotalWidth += ((childCount - 1) * mPadding);

        mVisibleChildIndex.clear();

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                if (mWillShift) {
                    int actualWidth = 0;
                    for (int i = 0; i < getChildCount(); i++) {
                        View child = getChildAt(i);

                        int currentWidth;
                        if (i == 0) {
                            currentWidth = actualWidth + child.getMeasuredWidth();
                        } else {
                            currentWidth = actualWidth + mPadding + child.getMeasuredWidth();
                        }

                        if (currentWidth <= widthSize) {
                            actualWidth = currentWidth;
                            mVisibleChildIndex.add(i);
                        }
                    }
                } else {
                    int i = getChildCount() - 1;
                    while (i >= 0 && childTotalWidth > widthSize) {
                        View child = getChildAt(i);
                        childTotalWidth -= mPadding;
                        childTotalWidth -= child.getMeasuredWidth();
                        i--;
                    }
                    int count = i + 1;
                    fillVisibleChild(count);
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
                heightSize = childMaxHeight;
                break;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    private void fillVisibleChild(int count) {
        for (int i = 0; i < count; i++) {
            mVisibleChildIndex.add(i);
        }
    }

    public void setOrder(int order) {
        this.mOrder = order;
    }

    public void setLayoutListener(LayoutListener mLayoutListener) {
        this.mLayoutListener = mLayoutListener;
    }

    public interface LayoutListener {
        void onLayoutFinished(List<View> visibleChildren);
    }

    public void setWillShift(boolean mWillShift) {
        this.mWillShift = mWillShift;
    }

    public int getPadding() {
        return mPadding;
    }

    public void setPadding(int mPadding) {
        this.mPadding = mPadding;
    }
}
