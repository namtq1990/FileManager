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
import android.view.ViewGroup;
import android.widget.Checkable;

import com.quangnam.base.exception.SystemException;
import com.tqnam.filemanager.utils.SparseBooleanArrayParcelable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

interface CheckListener {
    void onCheckedChange(ComplexAdapter.ViewHolder viewHolder, boolean checked);
}

/**
 * Created by quangnam on 11/23/16.
 * This Adapter add multiselect mode and filter to support {@link RecyclerView}
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ComplexAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements CheckListener
{
    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_HEADER = 1;
    public static final int VIEW_TYPE_FOOTER = 2;

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
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    /**
     * Define header view. If you don't use header, return null here.
     * Note: If header enabled, position of {@link ViewHolder} will be increase to 1
     * because index of 0 is header.
     */
    public View getHeaderView() {
        return null;
    }

    /**
     * Define footer view. If you don't use footer, return null here.
     * Note: If footer enabled, position of {@link ViewHolder} will be increase to 1
     * because last index is footer.
     */
    public View getFooterView() {
        return null;
    }

    public boolean useHeader() {
        return getHeaderView() != null;
    }

    public boolean useFooter() {
        return getFooterView() != null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_FOOTER:
                return onCreateFooterHolder(viewGroup, viewType);
            case VIEW_TYPE_HEADER:
                return onCreateHeaderHolder(viewGroup, viewType);
            default:
                return onCreateContentHolder(viewGroup, viewType);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).setChecked(mSelectedList.get(position));
            ((ViewHolder) holder).mCheckListener = this;

            onBindContent((ViewHolder) holder, position);
        } else if (holder instanceof HeaderHolder) {
            onBindHeader((HeaderHolder) holder, position);
        } else if (holder instanceof FooterHolder) {
            onBindFooter((FooterHolder) holder, position);
        }
    }

    public void onBindHeader(final HeaderHolder holder, int position) {}

    public void onBindFooter(final FooterHolder holder, int position) {}

    public abstract void onBindContent(final ViewHolder holder, int position);

    public abstract ViewHolder onCreateContentHolder(ViewGroup viewGroup, int viewType);

    public HeaderHolder onCreateHeaderHolder(ViewGroup viewGroup, int viewType) {
        return new HeaderHolder(getHeaderView());
    }

    public FooterHolder onCreateFooterHolder(ViewGroup viewGroup, int viewType) {
        return new FooterHolder(getFooterView());
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
        int contentPosition = position;
        if (useHeader())
            contentPosition--;

        if (mFilter != null) {
            return mFilter.mDataCopied.get(contentPosition);
        }

        return getRawDataAt(contentPosition);
    }

    @Override
    public final int getItemCount() {
        int itemCount;
        if (mFilter != null) {
            itemCount = mFilter.mDataCopied.size();
        } else {
            itemCount = getRawDataCount();
        }

        if (useHeader())
            itemCount++;
        if (useFooter()) {
            itemCount++;
        }

        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (useHeader() && position == 0)
            return VIEW_TYPE_HEADER;
        if (useFooter() && position == getItemCount() - 1)
            return VIEW_TYPE_FOOTER;

        return VIEW_TYPE_NORMAL;
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

    public static class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public static class FooterHolder extends RecyclerView.ViewHolder {

        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * Class to apply filter to this adapter. You must call {@link #performFilter()}} function
     * in method {@link #onChanged()} or any method that change data that you want.
     * <BR>
     *     It also copy your source data to a {@link Collection} to be a mask to provide data for
     *     this adapter.
     */
    public static abstract class Filter extends RecyclerView.AdapterDataObserver {
        protected List mDataCopied;
        private Object mQuery;

        public List cloneData(Object data) {
            if (!(data instanceof Cloneable)) {
                throw new SystemException(SystemException.RK_API, "" + data
                        + " couldn't be cloneable, so implement this function to clone data.");
            }

            if (!(data instanceof List)) {
                throw new SystemException(SystemException.RK_API, "" + data
                        + " isn't a list, so you have to implement this function to clone this data to list");
            }

            try {
                Method cloneMethod = data.getClass().getMethod("clone");
                cloneMethod.setAccessible(true);
                return (List) cloneMethod.invoke(data);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Perform filter action. You should update query by {@link #setQuery(Object)}
         * and get query by {@link #getQuery()}. You should update directly in this list,
         * It's cloned data.
         */
        public abstract List filter(List list, Object query);

        public abstract Object getOriginalData();

        public Object getQuery() {
            return mQuery;
        }

        public void setQuery(Object query) {
            mQuery = query;
        }

        @Override
        public void onChanged() {
            performFilter();
        }

        private void performFilter() {
            mDataCopied = cloneData(getOriginalData());
            mDataCopied = filter(mDataCopied, getQuery());
        }
    }

}
