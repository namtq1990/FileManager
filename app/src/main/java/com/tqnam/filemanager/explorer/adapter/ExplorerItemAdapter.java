package com.tqnam.filemanager.explorer.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.quangnam.baseframework.BaseActivity;
import com.quangnam.baseframework.SaveBundleListener;
import com.tqnam.filemanager.Application;
import com.tqnam.filemanager.Common;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.ExplorerPresenter;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.utils.SparseBooleanArrayParcelable;
import com.tqnam.filemanager.view.GridViewItem;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Adapter for explorer, must be combined with GridViewItem
 */
public class ExplorerItemAdapter extends RecyclerView.Adapter<ExplorerItemAdapter.ViewHolder> implements SaveBundleListener {

    public static final int STATE_NORMAL       = 0;
    //    public static final int STATE_EDIT         = 1;
    public static final int STATE_MULTI_SELECT = 2;
    private static final String ARG_STATE = ExplorerItemAdapter.class.getSimpleName() + ".state";
    private static final String ARG_SELECTED_LIST = ExplorerItemAdapter.class.getSimpleName() + ".selected_list";
    public static int mDefaultThemeBackgroundID;
    int mState = STATE_NORMAL;

    private Context mContext;
    private SparseBooleanArrayParcelable mSelectedList;
    private ActionMode               mActionMode;
    private ExplorerPresenter mPresenter;
    private ExplorerItemAdapterListener mListener;

    private ActionMode.Callback mActionCallback        = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.action_select_item, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mActionMode = mode;
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_property:
                    int length = mSelectedList.size();
                    ItemExplorer[] selectedList = new ItemExplorer[length];
                    for (int i = 0; i < length; i++) {
                        int key = mSelectedList.keyAt(i);
                        selectedList[i] = mPresenter.getItemDisplayedAt(key);
                    }

                    mListener.onViewProperty(selectedList);
                    break;
            }

//            mActionMode.finish();

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            updateView(null, ExplorerItemAdapter.STATE_NORMAL);
        }
    };

    public ExplorerItemAdapter(Context context, ExplorerPresenter presenter) {
        mContext = context;
        mPresenter = presenter;
        mSelectedList = new SparseBooleanArrayParcelable();
        TypedValue typedValueAttr = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValueAttr, true);
        mDefaultThemeBackgroundID = typedValueAttr.resourceId;

    }

    private void clearSelection() {
        for (int i = 0; i < mSelectedList.size(); i++) {
            notifyItemChanged(mSelectedList.keyAt(i));
        }
        mSelectedList.clear();
    }

    public void updateView(@Nullable View view, int state) {

        if (mState == state)
            return;

        int oldState = mState;
        mState = state;

        if (oldState == STATE_MULTI_SELECT) {
            // Handler destroy multi select state
            clearSelection();
        }

        switch (mState) {
            case STATE_NORMAL:
//                if (view != null) {
//                }

                break;
            case STATE_MULTI_SELECT:
                if (mActionMode == null) {
                    AppCompatActivity activity = (AppCompatActivity) mContext;
                    activity.startSupportActionMode(mActionCallback);
                }

                break;
        }
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

        holder.panel.setChecked(mSelectedList.get(position));
    }

    @Override
    public int getItemCount() {
        return mPresenter.getItemDisplayCount();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putInt(ARG_STATE, mState);
        state.putParcelable(ARG_SELECTED_LIST, mSelectedList);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mSelectedList = savedInstanceState.getParcelable(ARG_SELECTED_LIST);
        int state = savedInstanceState.getInt(ARG_STATE);

        updateView(null, state);
    }

    public interface ExplorerItemAdapterListener {
        void onOpenAction(int position);
        void openRenameDialog(String item, int position);
        void onViewProperty(ItemExplorer[] listSelected);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
        private OnLongClickListener mItemLongClickListener = new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setItemChecked(true);

                if (mState != STATE_MULTI_SELECT) {
                    updateView(v, STATE_MULTI_SELECT);
                }

                return true;
            }
        };

        public ViewHolder(final View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.title_item);
            icon = (ImageView) itemView.findViewById(R.id.icon_item);
            panel = (GridViewItem) itemView;

            label.setOnLongClickListener(mTextViewLongClick);
            panel.setOnLongClickListener(mItemLongClickListener);

            BaseActivity activity = (BaseActivity) itemView.getContext();
            activity.getLocalSubscription().add(
                    RxView.clicks(itemView).throttleLast(350, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Void>() {
                                @Override
                                public void call(Void aVoid) {
                                    switch (mState) {
                                        case STATE_MULTI_SELECT: {
                                            GridViewItem item = (GridViewItem) itemView;
                                            setItemChecked(!item.isChecked());
                                            break;
                                        }
                                        case STATE_NORMAL: {
                                            if (mListener != null) {
                                                mListener.onOpenAction(getAdapterPosition());
                                            }
                                            break;
                                        }
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

        public void setItemChecked(boolean checked) {
            panel.setChecked(checked);

            if (checked) {
                mSelectedList.append(getAdapterPosition(), true);
            } else {
                mSelectedList.delete(getAdapterPosition());
            }

            if (mSelectedList.size() == 0) {
                mActionMode.finish();
            }
        }

    }


}
