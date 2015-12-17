package com.tqnam.filemanager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.ExplorerItemAdapter;

/**
 * Created by quangnam on 11/13/15.
 * GridViewItem in custom GridView, it'll be measure to make sure all item is equal and wrap content
 */
public class GridViewItem extends RelativeLayout implements Checkable {

    private int mPosition;
    private boolean mChecked;

    public GridViewItem(Context context) {
        this(context, null);
    }

    public GridViewItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    /**
     * @return The current checked state of the view
     */
    @Override
    public boolean isChecked() {
        return mChecked;
    }

    /**
     * Change the checked state of the view
     *
     * @param checked The new checked state
     */
    @Override
    public void setChecked(boolean checked) {
        if (checked != mChecked) {
            // Check state changed, change background
            if (checked) {
                setBackgroundResource(R.drawable.state_item_bg);
            } else {
                setBackgroundResource(ExplorerItemAdapter.mDefaultThemeBackgroundID);
            }
        }

        ExplorerItemAdapter.ViewHolder holder = (ExplorerItemAdapter.ViewHolder) getTag(R.string.item_key_tag_viewholder);
        setSelected(checked);
        mChecked = checked;

    }

    /**
     * Change the checked state of the view to the inverse of its current state
     */
    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

}
