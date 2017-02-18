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

package com.tqnam.filemanager.explorer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Checkable;

import com.tqnam.filemanager.utils.SparseBooleanArrayParcelable;

interface CheckListener {
    void onCheckedChange(ComplexAdapter.ViewHolder viewHolder, boolean checked);
}

/**
 * Created by quangnam on 11/23/16.
 * This Adapter add multiselect mode and filter to support {@link RecyclerView}
 */
public abstract class ComplexAdapter<T extends ComplexAdapter.ViewHolder> extends RecyclerView.Adapter<T>
    implements CheckListener
{
    protected RecyclerView mRecyclerView;
    private Filter mFilter;
    private boolean mEnableMultiSelect = false;
    private SparseBooleanArrayParcelable mSelectedList;

    public ComplexAdapter() {
        mSelectedList = new SparseBooleanArrayParcelable();
    }

    public SparseBooleanArrayParcelable getSelectedItem() {
        return mSelectedList;
    }

    public void setSelectedList(SparseBooleanArrayParcelable list) {
        mSelectedList = list;

        if (mSelectedList != null
                && mSelectedList.size() > 0) {
            setEnableMultiSelect(true);
        } else {
            setEnableMultiSelect(false);
        }
    }

    public Filter getFilter() {
        return mFilter;
    }

    /**
     * Set a filter to this adapter. If adapter have a filter, you must use {@link #getItem(int)}}
     * to get the data for each item. It'll return the data from filter if it've a filter.
     * <br>
     * Otherwise, if you need source data, use {@link #getRawDataAt(int)} instead.
     * After set filter, if you need to filter, you should call {@link #notifyDataSetChanged()}
     * or other method to notify data and apply that filter.
     *
     */
    public void setFilter(Filter filter) {
        if (mFilter != null) {
            unregisterAdapterDataObserver(mFilter);
        }

        mFilter = filter;

        if (mFilter != null) {
            registerAdapterDataObserver(mFilter);
        }
    }

    public boolean isEnableMultiSelect() {
        return mEnableMultiSelect;
    }

    public void setEnableMultiSelect(boolean isEnable) {
        if (mEnableMultiSelect == isEnable) {
            return;
        }

        mEnableMultiSelect = isEnable;

        for (int i = 0; i < mSelectedList.size(); i++) {
            if (mRecyclerView != null)
            notifyItemChanged(mSelectedList.keyAt(i));
        }
        if (!isEnable) {
            mSelectedList.clear();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setChecked(mSelectedList.get(position));
        holder.mCheckListener = this;
    }

    @Override
    public void onCheckedChange(ViewHolder viewHolder, boolean checked) {
        if (checked) {
            mSelectedList.append(viewHolder.getAdapterPosition(), true);
        } else {
            mSelectedList.delete(viewHolder.getAdapterPosition());
        }

        if (mSelectedList.size() == 0) {
            setEnableMultiSelect(false);
        } else {
            setEnableMultiSelect(true);
        }
    }

    public abstract int getRawDataCount();
    public abstract Object getRawDataAt(int position);

    public final Object getItem(int position) {
        if (mFilter != null) {
            return mFilter.mDataCopied.get(position);
        }

        return getRawDataAt(position);
    }

    @Override
    public final int getItemCount() {
        if (mFilter != null)
            return mFilter.mDataCopied.size();

        return getRawDataCount();
    }

    public interface Collection {
        int size();
        Object get(int position);
        boolean remove(Object item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements Checkable {

        CheckListener mCheckListener;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setChecked(true);
                    return true;
                }
            });
        }

        @Override
        public boolean isChecked() {
            return false;
        }

        @Override
        public void setChecked(boolean checked) {
            if (isChecked() != checked
                    && mCheckListener != null) {
                mCheckListener.onCheckedChange(this, checked);
            }
        }

        @Override
        public void toggle() {
            setChecked(!isChecked());
        }
    }

    /**
     * Class to apply filter to this adapter. You must call {@link #filter(Object...)} function
     * in method {@link #onChanged()} or any method that change data that you want.
     * <BR>
     *     It also copy your source data to a {@link Collection} to be a mask to provide data for
     *     this adapter.
     */
    public static abstract class Filter extends RecyclerView.AdapterDataObserver {
        protected Collection mDataCopied;

        public abstract Collection cloneData(Object data);
        public abstract void filter(Object... constraint);

        public void setSource(Object source) {
            mDataCopied = cloneData(source);
        }

        @Override
        public abstract void onChanged();
    }

}
