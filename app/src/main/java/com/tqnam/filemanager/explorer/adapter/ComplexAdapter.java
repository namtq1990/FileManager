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
 * Project FileManager-master
 */
public abstract class ComplexAdapter<T extends ComplexAdapter.ViewHolder> extends RecyclerView.Adapter<T>
    implements CheckListener
{
    protected RecyclerView mRecyclerView;
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

    public abstract Object getData();

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

}
