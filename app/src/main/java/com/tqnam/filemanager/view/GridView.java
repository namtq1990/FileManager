package com.tqnam.filemanager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;

/**
 * Created by quangnam on 11/19/15.
 * <p/>
 * Custom GridView that wrap content of every row
 * It must be combined with GridViewItem
 */
public class GridView extends android.widget.GridView {
    public static SparseArray<Integer> mMaxRowHeight = new SparseArray<>();
    public static int mNumColumn;

    public GridView(Context context) {
        this(context, null);
    }

    public GridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mNumColumn = getNumColumns();
        mMaxRowHeight.clear();              // Clear list height to sure row height is always updated
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
