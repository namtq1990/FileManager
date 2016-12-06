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
import com.quangnam.baseframework.BaseActivity;
import com.quangnam.baseframework.Log;
import com.quangnam.baseframework.exception.SystemException;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.operation.Operation;
import com.tqnam.filemanager.utils.DefaultErrorAction;
import com.tqnam.filemanager.utils.OperatorManager;
import com.tqnam.filemanager.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collection;
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
public class OperatorAdapter extends ExpandableRecyclerAdapter<OperatorAdapter.ParentViewHolder, ChildViewHolder> {

    private BaseActivity mContext;

//    private List<OperatorList> mTotalList;

    /**
     * Primary constructor. Sets up {@link #mParentItemList} and {@link #mItemList}.
     * <p>
     * Changes to {@link #mParentItemList} should be made through add/remove methods in
     * {@link ExpandableRecyclerAdapter}
     *
     * @param parentItemList List of all {@link ParentListItem} objects to be
     *                       displayed in the RecyclerView that this
     *                       adapter is linked to
     */
    public OperatorAdapter(@NonNull List<OperatorList> parentItemList, Context context) {
        super(parentItemList);
        mContext = (BaseActivity) context;
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
            case OperatorManager.CATEGORY_COPY:
                label = "COPY OPERATOR";
                break;
            case OperatorManager.CATEGORY_MOVE:
                label = "MOVE OPERATOR";
                break;
            case OperatorManager.CATEGORY_DELETE:
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

        setupFileName(viewHolder, operation);
        setupStream(operation, viewHolder);

        if (operation.isCancelable() && operation.isExecuting()) {
            viewHolder.addMenuButton(viewHolder.btnCancel);
        } else {
            viewHolder.removeMenuButton(viewHolder.btnCancel);
        }
        if (operation.isAbleToPause() && operation.isExecuting()) {
            viewHolder.addMenuButton(viewHolder.btnPause);
        } else {
            viewHolder.removeMenuButton(viewHolder.btnPause);
        }
        if (operation.isExecuting()) {
            viewHolder.removeMenuButton(viewHolder.btnRestart);
        } else {
            viewHolder.addMenuButton(viewHolder.btnRestart);
        }
        if (operation.isUndoable()) {
            viewHolder.addMenuButton(viewHolder.btnUndo);
        } else {
            viewHolder.removeMenuButton(viewHolder.btnUndo);
        }
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

    private void setProgress(ChildViewHolder viewHolder, int progress) {
        Log.d("Set progress: " + progress);
        if (progress > 100) progress = 100;

        viewHolder.progressBar.setProgress(progress);
        viewHolder.tvProgress.setText(String.format(Locale.ENGLISH, "%1d%%", progress));
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

    public static class OperatorList implements ParentListItem {

        ArrayList<Operation> mList;

        public OperatorList(List<Operation> list) {
            mList = new ArrayList<>(list);
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

    public class ChildViewHolder extends com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder implements Action1<Object> {

        @BindView(R.id.prog_execute)
        ProgressBar progressBar;
        @BindView(R.id.tv_progress) TextView tvProgress;
        @BindView(R.id.list_name)
        RecyclerView listName;
        @BindView(R.id.btn_cancel)
        AppCompatImageButton btnCancel;
        @BindView(R.id.btn_restart) AppCompatImageButton btnRestart;
        @BindView(R.id.btn_pause) AppCompatImageButton btnPause;
        @BindView(R.id.btn_undo) AppCompatImageButton btnUndo;
        @BindView(R.id.tv_from) TextView tvFrom;
        @BindView(R.id.tv_to) TextView tvTo;
        View rootView;

        Operation mCurOperation;
        Subscription curSubscription;

        /**
         * Default constructor.
         *
         * @param itemView The {@link View} being hosted in this ViewHolder
         */
        public ChildViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ButterKnife.bind(this, itemView);
        }

        void removeMenuButton(AppCompatImageButton button) {
//            button.setVisibility(View.GONE);
        }

        void addMenuButton(AppCompatImageButton button) {
            button.setVisibility(View.VISIBLE);
        }

        @Override
        public void call(Object o) {
            if (o instanceof Operation.UpdatableData) {
                update((Operation.UpdatableData) o);
            }
        }

        public void update(Operation.UpdatableData updatableData) {
            Log.d("Updated data: " + updatableData);
            int position = getAdapterPosition();
            Object o = getListItem(position);

            if (o instanceof Operation) {
                if (updatableData.getOperatorHashcode() == o.hashCode()) {
                    // This is right operator, so update this view
                    if (progressBar.getProgress() != (int)updatableData.getProgress()) {
                        setProgress(this, (int) updatableData.getProgress());
                    }
                }
            }
        }

        @OnClick(R.id.btn_cancel)
        public void onClickCancel() {
            if (mCurOperation != null) {
                //TODO Cancel operator
            }
        }

        // TODO unbind with operator
    }

    public class ParentViewHolder extends com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder {
        @BindView(R.id.tv_label) TextView label;
        @BindView(R.id.ic_arrow)
        ImageView arrow;

        /**
         * Default constructor.
         *
         * @param itemView The {@link View} being hosted in this ViewHolder
         */
        public ParentViewHolder(View itemView) {
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
                public void onAnimationStart(Animator animation) {}

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
                    if (!expanded) {
                        arrow.setImageResource(R.drawable.ic_action_collapse);
                    } else {
                        arrow.setImageResource(R.drawable.ic_action_expand);
                    }
                }
            });
            animator.playSequentially(fadeOut, fadeIn);
            animator.start();
        }
    }
}
