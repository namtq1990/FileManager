/*
 * MIT License
 *
 * Copyright (c) 2017 Tran Quang Nam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tqnam.filemanager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.adapter.ExplorerItemAdapter;

/**
 * Created by quangnam on 11/13/15.
 * GridViewItem in custom GridView, it'll be measure to make sure all item is equal and wrap content
 */
public class GridViewItem extends RelativeLayout implements Checkable {

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
