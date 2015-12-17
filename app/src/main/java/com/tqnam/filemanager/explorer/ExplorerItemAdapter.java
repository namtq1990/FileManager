package com.tqnam.filemanager.explorer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
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

import com.tqnam.filemanager.R;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.view.GridViewItem;

/**
 * Adapter for explorer, must be combined with GridViewItem
 */
public class ExplorerItemAdapter extends RecyclerView.Adapter<ExplorerItemAdapter.ViewHolder> {

    public static final int STATE_NORMAL       = 0;
    //    public static final int STATE_EDIT         = 1;
    public static final int STATE_MULTI_SELECT = 2;
    public static int mDefaultThemeBackgroundID;

    int mState = STATE_NORMAL;

    private SparseBooleanArray       mSelectedList;
    private ActionMode               mActionMode;
    private ExplorerPresenter        mPresenter;
    private OnRenameActionListener   mRenameListener;
    private OnOpenItemActionListener mOpenItemListener;

    private View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
//            v.setEnabled(false);
//            v.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    v.setEnabled(true);
//

            switch (mState) {
                case STATE_MULTI_SELECT: {
                    GridViewItem item = (GridViewItem) v;
                    setItemChecked(item, !item.isChecked());
                    break;
                }
                case STATE_NORMAL: {
                    if (mOpenItemListener != null) {
                        GridViewItem item = (GridViewItem) v;
                        mOpenItemListener.onOpenAction(item.getPosition());
                    }
                    break;
                }
            }
//                }
//            }, v.getResources().getInteger(android.R.integer.config_shortAnimTime));
//        }
        }
    };

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
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            updateUI(null, ExplorerItemAdapter.STATE_NORMAL);
            mSelectedList.clear();
        }
    };
    private OnLongClickListener mItemLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            GridViewItem item = (GridViewItem) v;
            setItemChecked(item, true);

            if (mState != STATE_MULTI_SELECT) {
                updateUI(v, STATE_MULTI_SELECT);
            }

            return true;
        }
    };

    public ExplorerItemAdapter(Context context, ExplorerPresenter presenter) {
        mPresenter = presenter;
        mSelectedList = new SparseBooleanArray(getItemCount());
        TypedValue typedValueAttr = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValueAttr, true);
        mDefaultThemeBackgroundID = typedValueAttr.resourceId;
    }

    public void updateUI(View view, int state) {

        if (mState == state)
            return;

        if (mState == STATE_MULTI_SELECT) {
            // Handler destroy multi select state
            for (int i = 0; i < mSelectedList.size(); i++) {
                notifyItemChanged(mSelectedList.keyAt(i));
            }

            mSelectedList.clear();
        }

        switch (state) {
            case STATE_NORMAL:
//                if (view != null) {
//                }

                break;
            case STATE_MULTI_SELECT:
                if (mActionMode == null && view != null) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.startSupportActionMode(mActionCallback);
                }


                break;
        }

        mState = state;
    }

    public void setItemChecked(GridViewItem item, boolean checked) {
        item.setChecked(checked);

        if (checked) {
            mSelectedList.append(item.getPosition(), true);
        } else {
            mSelectedList.delete(item.getPosition());
        }

        if (mSelectedList.size() == 0) {
            mActionMode.finish();
        }
    }

    public void setRenameListener(OnRenameActionListener listener) {
        mRenameListener = listener;
    }

    public void setOpenItemListener(OnOpenItemActionListener listener) {
        mOpenItemListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        ViewHolder holder = new ViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ItemExplorer item = mPresenter.getItemAt(position);
        holder.panel.setPosition(position);
        if (item != null) {
            holder.label.setText(item.getDisplayName());

            if (item.isDirectory()) {
                holder.icon.setImageResource(R.drawable.folder_icon);
            } else holder.icon.setImageResource(R.drawable.file_icon);
        }

        if (mSelectedList.get(position)) {
            holder.panel.setChecked(true);
        } else {
            holder.panel.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return mPresenter.getItemCount();
    }

    public interface OnRenameActionListener {
        void onRenameAction(String item, int position);
    }

    public interface OnOpenItemActionListener {
        void onOpenAction(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView     label;
        public ImageView    icon;
        public GridViewItem panel;
        private OnLongClickListener mTextViewLongClick = new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (mRenameListener != null) {
                    mRenameListener.onRenameAction(((TextView) v).getText().toString(), getAdapterPosition());
                }

                return false;
            }
        };

        public ViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.title_item);
            icon = (ImageView) itemView.findViewById(R.id.icon_item);
            panel = (GridViewItem) itemView;

            label.setOnLongClickListener(mTextViewLongClick);
            panel.setOnClickListener(mItemClickListener);
            panel.setOnLongClickListener(mItemLongClickListener);
            panel.setTag(R.string.item_key_tag_viewholder, this);
        }
    }
}
