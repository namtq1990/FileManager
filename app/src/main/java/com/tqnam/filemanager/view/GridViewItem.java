package com.tqnam.filemanager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by quangnam on 11/13/15.
 * GridViewItem in custom GridView, it'll be measure to make sure all item is equal and wrap content
 */
public class GridViewItem extends RelativeLayout {

    private int mPosition;

    public GridViewItem(Context context) {
        this(context, null);
    }

    public GridViewItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (GridView.mNumColumn > 1 && GridView.mMaxRowHeight != null) {
            int rowIndex = mPosition / GridView.mNumColumn;
            int measureHeight = getMeasuredHeight();

            if (GridView.mMaxRowHeight.get(rowIndex, 0) < measureHeight) {
                GridView.mMaxRowHeight.put(rowIndex, measureHeight);
            }

            setMeasuredDimension(getMeasuredWidth(), GridView.mMaxRowHeight.get(rowIndex));
        }
    }
}
