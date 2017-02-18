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

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.quangnam.baseframework.BaseActivity;
import com.tqnam.filemanager.Application;
import com.tqnam.filemanager.Common;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.ExplorerPresenter;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.utils.FileUtil;
import com.tqnam.filemanager.view.GridViewItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Adapter for explorer, must be combined with GridViewItem
 */
public class ExplorerItemAdapter extends ComplexAdapter<ExplorerItemAdapter.ViewHolder> {


    public static int mDefaultThemeBackgroundID;

    private Context mContext;
    private ExplorerPresenter mPresenter;
    private ExplorerItemAdapterListener mListener;
    private String mQuery;
    private Filter mCurFilter;

    public ExplorerItemAdapter(Context context, ExplorerPresenter presenter) {
        mContext = context;
        mPresenter = presenter;
        TypedValue typedValueAttr = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValueAttr, true);
        mDefaultThemeBackgroundID = typedValueAttr.resourceId;

        mCurFilter = new Filter();
    }

    public ArrayList<ItemExplorer> getSelectedList() {
        SparseBooleanArray selectedItem = getSelectedItem();
        int length = selectedItem.size();
        ArrayList<ItemExplorer> selectedList = new ArrayList<>(length);
        List<ItemExplorer> data = mPresenter.getListData();

        for (int i = 0; i < length; i++) {
            int key = selectedItem.keyAt(i);
            selectedList.add(data.get(key));
        }

        return selectedList;
    }

    public void setListener(ExplorerItemAdapterListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);

        return new ViewHolder(v);
    }

    public void setQuery(String query) {
        mQuery = query;

        if (TextUtils.isEmpty(query)) {
            setFilter(null);
        } else {
            setFilter(mCurFilter);
        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ItemExplorer item = (ItemExplorer) getItem(position);
        if (item != null) {
            holder.label.setText(item.getDisplayName());

            if (item.isDirectory()) {
                holder.icon.setImageResource(R.drawable.folder_icon);
            } else {
                int fileType = item.getFileType();
                Context context = holder.panel.getContext();

                switch (fileType) {
                    case ItemExplorer.FILE_TYPE_IMAGE:
                        Application.GlobalData globalData = ((Application) context.getApplicationContext())
                                .getGlobalData();
                        globalData.mImage.load(item.getUri())
                                .resize(globalData.mIconSize, globalData.mIconSize)
                                .placeholder(R.drawable.file_icon)
                                .into(holder.icon);
                        break;
                    default:
                        holder.icon.setImageResource(R.drawable.file_icon);
                        break;
                }

            }
        }

        super.onBindViewHolder((ComplexAdapter.ViewHolder) holder, position);
    }

    @Override
    public void setEnableMultiSelect(boolean isEnable) {
        if (isEnableMultiSelect() != isEnable) {
            if (isEnable) {
                mListener.showContextMenu();
            } else {
                mListener.hideContextMenu();
            }
        }

        super.setEnableMultiSelect(isEnable);
    }

    @Override
    public int getRawDataCount() {
        return mPresenter.getListData().size();
    }

    @Override
    public ItemExplorer getRawDataAt(int position) {
        return mPresenter.getListData().get(position);
    }

    public interface ExplorerItemAdapterListener {
        void clearFilter();
        void openRenameDialog(String item, int position);
        void showContextMenu();
        void hideContextMenu();
    }

    public class ViewHolder extends ComplexAdapter.ViewHolder {
        public TextView     label;
        public ImageView    icon;
        public GridViewItem panel;
        private OnLongClickListener mTextViewLongClick = new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    mListener.openRenameDialog(((TextView) v).getText().toString(), getAdapterPosition());
                }

                return false;
            }
        };

        public ViewHolder(final View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.title_item);
            icon = (ImageView) itemView.findViewById(R.id.icon_item);
            panel = (GridViewItem) itemView;

            label.setOnLongClickListener(mTextViewLongClick);

            BaseActivity activity = (BaseActivity) itemView.getContext();
            activity.subscribe(
                    RxView.clicks(itemView).throttleLast(350, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Void>() {
                                @Override
                                public void call(Void aVoid) {
                                    if (isEnableMultiSelect()) {
                                        toggle();
                                    } else {
                                        mPresenter.openItem((ItemExplorer) getItem(getAdapterPosition()));
                                        if (mQuery != null
                                                && mListener != null) {
                                            mListener.clearFilter();
                                        }
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Common.Log("error when open item " + getItem(getAdapterPosition()));
                                    throwable.printStackTrace();
                                }
                            }));
        }

        @Override
        public boolean isChecked() {
            return panel.isChecked();
        }

        @Override
        public void setChecked(boolean checked) {
            super.setChecked(checked);
            panel.setChecked(checked);
        }
    }

    private class Filter extends ComplexAdapter.Filter {

        @Override
        public Array<ItemExplorer> cloneData(Object data) {
            return new Array<>((List<ItemExplorer>) data);
        }

        @Override
        public void filter(Object... constraint) {
            String query = (String) constraint[0];
            Array<ItemExplorer> list = cloneData(mPresenter.getListData());

            FileUtil.filter(list, query);

            mDataCopied = list;
        }

        @Override
        public void onChanged() {
            filter(mQuery);
        }
    }

    private class Array<T> extends ArrayList<T> implements Collection {

        Array(List<T> list) {
            super(list);
        }
    }

}
