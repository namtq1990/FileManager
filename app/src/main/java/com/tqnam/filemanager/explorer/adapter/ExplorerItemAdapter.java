package com.tqnam.filemanager.explorer.adapter;

import android.content.Context;
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
import com.tqnam.filemanager.view.GridViewItem;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Adapter for explorer, must be combined with GridViewItem
 */
public class ExplorerItemAdapter extends ComplexAdapter<ExplorerItemAdapter.ViewHolder> {


    public static int mDefaultThemeBackgroundID;

    private ExplorerPresenter mPresenter;
    private ExplorerItemAdapterListener mListener;

    public ExplorerItemAdapter(Context context, ExplorerPresenter presenter) {
//        mContext = context;
        mPresenter = presenter;
        TypedValue typedValueAttr = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValueAttr, true);
        mDefaultThemeBackgroundID = typedValueAttr.resourceId;

    }

    public ArrayList<ItemExplorer> getSelectedList() {
        SparseBooleanArray selectedItem = getSelectedItem();
        int length = selectedItem.size();
        ArrayList<ItemExplorer> selectedList = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            int key = selectedItem.keyAt(i);
            selectedList.add(mPresenter.getItemDisplayedAt(key));
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

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ItemExplorer item = mPresenter.getItemDisplayedAt(position);
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
    public int getItemCount() {
        return mPresenter.getItemDisplayCount();
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

    public interface ExplorerItemAdapterListener {
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
                                        mPresenter.openItem(getAdapterPosition());
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Common.Log("error when open item " + getAdapterPosition());
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

}
