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

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.quangnam.base.BaseActivity;
import com.quangnam.base.Log;
import com.quangnam.base.exception.SystemException;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.dialog.OperationInforDialogFragment;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.operation.Operation;
import com.tqnam.filemanager.utils.DefaultErrorAction;
import com.tqnam.filemanager.utils.OperationManager;
import com.tqnam.filemanager.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by quangnam on 11/15/16.
 * Project FileManager-master
 */
public class OperationAdapter extends ExpandableRecyclerAdapter<OperationAdapter.ParentViewHolder, ChildViewHolder> {

    private BaseActivity mContext;
    private int mControllerButtonSize;        //Size of a button in controller (reload, property, ...)

    //    private List<OperationList> mTotalList;

    /**
     * Primary constructor. Sets up {@link #mParentItemList} and {@link #mItemList}.
     * <p/>
     * Changes to {@link #mParentItemList} should be made through add/remove methods in
     * {@link ExpandableRecyclerAdapter}
     *
     * @param parentItemList List of all {@link ParentListItem} objects to be
     *                       displayed in the RecyclerView that this
     *                       adapter is linked to
     */
    public OperationAdapter(@NonNull List<OperationList> parentItemList, Context context) {
        super(parentItemList);
        mContext = (BaseActivity) context;
        mControllerButtonSize = context.getResources().getDimensionPixelSize(R.dimen.tiny_button_size);
    }

    @Override
    public ParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        LayoutInflater inflater = LayoutInflater.from(parentViewGroup.getContext());
        View rootView = inflater.inflate(R.layout.item_operator_parent, parentViewGroup, false);

        return new ParentViewHolder(rootView);
    }

    @Override
    public com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        LayoutInflater inflater = LayoutInflater.from(childViewGroup.getContext());
        View rootView = inflater.inflate(R.layout.item_operator, childViewGroup, false);

        return new ChildViewHolder(rootView);
    }

    @Override
    public void onBindParentViewHolder(ParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        String label;

        List parentList = getParentItemList();
        int index = parentList.indexOf(parentListItem);

        switch (index) {
            case OperationManager.CATEGORY_COPY:
                label = "COPY OPERATOR";
                break;
            case OperationManager.CATEGORY_MOVE:
                label = "MOVE OPERATOR";
                break;
            case OperationManager.CATEGORY_DELETE:
                label = "DELETE OPERATOR";
                break;
            default:
                label = "OTHERS OPERATION";
                break;
        }

        parentViewHolder.label.setText(label);
    }

    @Override
    public void onBindChildViewHolder(com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder childViewHolder, int position, Object childListItem) {
        Operation operation = (Operation) childListItem;
        final ChildViewHolder viewHolder = (ChildViewHolder) childViewHolder;

        viewHolder.unBind();
        setupFileName(viewHolder, operation);
        setupStream(operation, viewHolder);

        viewHolder.setupCancelButton();
        viewHolder.setupResumeButton();
        viewHolder.setupRestartButton();
        viewHolder.setupUndoButton();
        if (operation.isUpdatable()) {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
        } else {
            viewHolder.progressBar.setVisibility(View.INVISIBLE);
        }
        if (operation.getSourcePath() != null) {
            ViewUtils.formatTitleAndContentTextView(viewHolder.tvFrom,
                    "From: ",
                    operation.getSourcePath(),
                    null,
                    null);
        }
        if (operation.getDestinationPath() != null) {
            ViewUtils.formatTitleAndContentTextView(viewHolder.tvTo,
                    "To: ",
                    operation.getDestinationPath(),
                    null,
                    null);
        }

        int parentPosition = getParentPosition(position);
        if ((position - parentPosition) % 2 == 0) {
            viewHolder.rootView.setBackgroundResource(R.color.blue_light);
        } else {
            viewHolder.rootView.setBackgroundResource(R.color.white);
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof ChildViewHolder) {
            ((ChildViewHolder) holder).unBind();
        }
    }

    private void setProgress(ChildViewHolder viewHolder, int progress) {
        if (progress > 100) progress = 100;

        viewHolder.progressBar.setProgress(progress);
    }

    private void setupFileName(ChildViewHolder viewHolder, Operation operation) {
        if (operation.getData() != null) {
            List<Object> data = new ArrayList<>();

            if (operation.getData() instanceof Collection) {
                data.addAll((Collection<?>) operation.getData());
            } else {
                data.add(operation.getData());
            }

            SimpleArrayAdapter<Object> adapter = new SimpleArrayAdapter<Object>(data.toArray()) {
                @Override
                public void formatLabel(TextView tv, String label) {
                    tv.setText(String.format(" - %1s", label));
                }

                @Override
                public String getLabelFromData(Object data) {
                    if (data instanceof ItemExplorer)
                        return ((ItemExplorer) data).getDisplayName();
                    else return data.toString();
                }
            };

            viewHolder.listName.setAdapter(adapter);
            viewHolder.listName.setLayoutManager(new LinearLayoutManager(viewHolder.listName.getContext()));
            viewHolder.listName.setHasFixedSize(true);
        }
    }

    private void setupStream(Operation operation, ChildViewHolder viewHolder) {
        Observable<?> observable = null;
        if (operation.isUpdatable()) {
            Operation.UpdatableData data = operation.getUpdateData();
            if (data.isError()) {
                observable = Observable.just(data);
            }

            data.registerStateChangeListener(viewHolder);
        }
        if (observable == null) {
            observable = operation.execute();
        }

        viewHolder.mCurOperation = operation;
        if (viewHolder.curSubscription != null
                && viewHolder.curSubscription.isUnsubscribed()) {
            viewHolder.curSubscription.unsubscribe();
        }
        viewHolder.curSubscription = observable.subscribe(viewHolder, new DefaultErrorAction() {

            @Override
            public void onError(int errCode, SystemException e) {
                super.onError(errCode, e);
            }

            @Override
            public Context getContext() {
                return mContext;
            }
        });


    }

    public int getParentPosition(int position) {
        final int type_parent = 0;
        int i;

        for (i = position; i >= 0; i--) {
            int viewType = getItemViewType(i);

            if (viewType == type_parent) {
                break;
            }
        }

        return i;
    }

    public static class OperationList implements ParentListItem {

        ArrayList<Operation> mList;

        public OperationList(List<Operation> list) {
            mList = (ArrayList<Operation>) list;
        }

        @Override
        public List<?> getChildItemList() {
            return mList;
        }

        @Override
        public boolean isInitiallyExpanded() {
            return true;
        }
    }

    class ChildViewHolder extends com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder
            implements Action1<Object>, Operation.OnStateChangeListener {
        private static final int CONTROLLER_BUTTON_ROW_SIZE = 3;        // Number of button per rows

        @BindView(R.id.prog_execute)
        ProgressBar progressBar;
        @BindView(R.id.tv_progress)
        TextView tvProgress;
        @BindView(R.id.list_name)
        RecyclerView listName;
        @BindView(R.id.btn_cancel)
        AppCompatImageButton btnCancel;
        @BindView(R.id.btn_info)
        AppCompatImageButton btnInfo;
        @BindView(R.id.btn_restart)
        AppCompatImageButton btnRestart;
        @BindView(R.id.btn_pause)
        AppCompatImageButton btnPause;
        @BindView(R.id.btn_start)
        AppCompatImageButton btnStart;
        @BindView(R.id.btn_undo)
        AppCompatImageButton btnUndo;
        @BindView(R.id.tv_from)
        TextView tvFrom;
        @BindView(R.id.tv_to)
        TextView tvTo;
        @BindView(R.id.layout_controller)
        ViewGroup layoutController;
        @BindView(R.id.space)
        View space;
        View rootView;

        Operation mCurOperation;
        Subscription curSubscription;
        HashSet<Integer> mVisibleButton;

        /**
         * Default constructor.
         *
         * @param itemView The {@link View} being hosted in this ViewHolder
         */
        ChildViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ButterKnife.bind(this, itemView);

            mVisibleButton = new HashSet<>(layoutController.getChildCount());
            for (int i = 0; i < layoutController.getChildCount(); i++) {
                View child = layoutController.getChildAt(i);
                if (child instanceof AppCompatImageButton) {
                    // Add all button in controller layout to visible button
                    addMenuButton((AppCompatImageButton) child);
                }
            }
        }

        void removeMenuButton(AppCompatImageButton button) {
            mVisibleButton.remove(button.getId());

            if (mVisibleButton.size() < CONTROLLER_BUTTON_ROW_SIZE) {
                space.getLayoutParams().width
                        = (CONTROLLER_BUTTON_ROW_SIZE - mVisibleButton.size()) * mControllerButtonSize;
                space.setVisibility(View.VISIBLE);

                if (space.getParent() != layoutController)
                    layoutController.addView(space, space.getLayoutParams());
            }

            layoutController.removeView(button);
        }

        void addMenuButton(AppCompatImageButton button) {
            int index = mVisibleButton.size();
            if (button.getTag() != null) {
                index = Math.min(Integer.valueOf((String) button.getTag()), index);
            }
            addMenuButton(button, index);
        }

        void addMenuButton(AppCompatImageButton button, int index) {
            if (button.getId() != View.NO_ID)
                mVisibleButton.add(button.getId());
            if (mVisibleButton.size() >= CONTROLLER_BUTTON_ROW_SIZE) {
                layoutController.removeView(space);
            }

            if (button.getParent() != layoutController) {
                layoutController.addView(button, index, button.getLayoutParams());
            }
        }

        @Override
        public void call(Object o) {
            if (o instanceof Operation.UpdatableData) {
                update((Operation.UpdatableData) o);
            }
        }

        void update(Operation.UpdatableData updatableData) {
            Log.d("Updated data: " + updatableData);
            int position = getAdapterPosition();
            Object o = getListItem(position);

            if (o instanceof Operation) {
                if (updatableData.getOperatorHashcode() == o.hashCode()) {
                    // This is right operator, so update this view
                    if (progressBar.getProgress() != updatableData.getProgress()) {
                        setProgress(this, updatableData.getProgress());
                    }

                    setupStatus();
                }
            }
        }

        @OnClick(R.id.btn_cancel)
        void onClickCancel() {
            if (mCurOperation != null
                    && mCurOperation.isCancelable()) {
                ((Operation.ICancel) mCurOperation).cancel();
            }
        }

        @OnClick(R.id.btn_info)
        void onClickInfo() {
            OperationInforDialogFragment fragment = OperationInforDialogFragment
                    .newInstance(mCurOperation, mContext.getDataFragment());
            fragment.show(mContext.getSupportFragmentManager(), OperationInforDialogFragment.TAG);
        }

        @OnClick({R.id.btn_pause, R.id.btn_start})
        void onClickPause() {
            if (mCurOperation.isAbleToPause()) {
                Operation.IPause pauseControl = (Operation.IPause) mCurOperation;
                pauseControl.setRunning(!pauseControl.isRunning());
            }
        }

        @OnClick(R.id.btn_undo)
        void onClickUndo() {
            if (mCurOperation.isUndoable()) {
                Operation.IRevert revertControl = (Operation.IRevert) mCurOperation;
                revertControl.revert();
            }
        }

        void setupResumeButton() {
            Operation.UpdatableData data = mCurOperation.getUpdateData();
            if (mCurOperation.isAbleToPause()
                    && (data.getStateValue(Operation.OperationState.STATE_RUNNING)
                    || data.getStateValue(Operation.OperationState.STATE_PAUSE)
                    || data.getState() == 0
            )) {

                Operation.IPause pauseControl = (Operation.IPause) mCurOperation;
                if (pauseControl.isRunning()) {
                    int index = layoutController.indexOfChild(btnStart);
                    removeMenuButton(btnStart);
                    addMenuButton(btnPause, index);
                } else {
                    int index = layoutController.indexOfChild(btnPause);
                    removeMenuButton(btnPause);
                    addMenuButton(btnStart, index);
                }
            } else {
                removeMenuButton(btnPause);
                removeMenuButton(btnStart);
            }
        }

        void setupCancelButton() {
            if (mCurOperation != null
                    && mCurOperation.isCancelable()
                    && (mCurOperation.getUpdateData().getStateValue(Operation.OperationState.STATE_PAUSE)
                    || mCurOperation.getUpdateData().getStateValue(Operation.OperationState.STATE_RUNNING)
            )) {
                addMenuButton(btnCancel);
            } else {
                removeMenuButton(btnCancel);
            }
        }

        void setupRestartButton() {
            //TODO not implemented
            if (mCurOperation != null
                    && mCurOperation.isRestartable()) {
                addMenuButton(btnRestart);
            } else {
                removeMenuButton(btnRestart);
            }
        }

        void setupUndoButton() {
            if (mCurOperation != null
                    && mCurOperation.isUndoable()) {
                addMenuButton(btnUndo);
            } else {
                removeMenuButton(btnUndo);
            }
        }

        void setupStatus() {
            if (mCurOperation != null) {
                Operation.UpdatableData data = mCurOperation.getUpdateData();
                if (data.getStateValue(Operation.OperationState.STATE_RUNNING)) {
                    tvProgress.setText(String.format(Locale.ENGLISH, "%1d%%", data.getProgress()));
                } else if (data.getStateValue(Operation.OperationState.STATE_PAUSE)) {
                    tvProgress.setText(R.string.paused);
                } else if (data.getStateValue(Operation.OperationState.STATE_CANCELLED)) {
                    tvProgress.setText(R.string.cancelled);
                } else if (data.getStateValue(Operation.OperationState.STATE_ERROR)) {
                    tvProgress.setText(R.string.error);
                } else if (data.getStateValue(Operation.OperationState.STATE_FINISHED)) {
                    tvProgress.setText(R.string.finished);
                }
                if (data.getStateValue(Operation.OperationState.STATE_UNDO)) {
                    tvProgress.setText(R.string.undoing);
                }
            }
        }

        @Override
        public void onStateChanging(int mode, boolean newValue) {
            if (mode == Operation.OperationState.STATE_UNDO && newValue) {
                //Undoing
                List curOperationList = OperationManager.getInstance().getOperatorList(mCurOperation.getCategory());
                int index = curOperationList.indexOf(mCurOperation);
                if (index != -1) {
                    curOperationList.remove(mCurOperation);
                    notifyChildItemRemoved(getParentPosition(getAdapterPosition()), index);
                }
            }
        }

        @Override
        public void onStateChanged(int mode, boolean oldValue, boolean newValue) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupResumeButton();
                    setupCancelButton();
                    setupStatus();
                }
            });
        }

        void unBind() {
            if (mCurOperation != null
                    && mCurOperation.getUpdateData() != null) {
                mCurOperation.getUpdateData().unregisterAllStateChangeListener();
            }

            if (curSubscription != null
                    && !curSubscription.isUnsubscribed()) {
                curSubscription.unsubscribe();
            }
        }
    }

    class ParentViewHolder extends com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder {
        @BindView(R.id.tv_label)
        TextView label;
        @BindView(R.id.ic_arrow)
        ImageView arrow;

        /**
         * Default constructor.
         *
         * @param itemView The {@link View} being hosted in this ViewHolder
         */
        ParentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onExpansionToggled(final boolean expanded) {
            AnimatorSet animator = new AnimatorSet();
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(arrow, "alpha", 0)
                    .setDuration(100);
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(arrow, "alpha", 1)
                    .setDuration(100);
            fadeOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    updateArrow();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    updateArrow();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                void updateArrow() {
                    arrow.setImageResource(expanded ? R.drawable.ic_action_expand : R.drawable.ic_action_collapse);
                }
            });
            animator.playSequentially(fadeOut, fadeIn);
            animator.start();
        }

    }
}
