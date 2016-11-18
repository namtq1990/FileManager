package com.tqnam.filemanager.explorer.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import com.quangnam.baseframework.Log;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.model.CopyFileOperator;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.Operator;
import com.tqnam.filemanager.utils.ViewUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by quangnam on 11/15/16.
 * Project FileManager-master
 */
public class OperatorAdapter extends ExpandableRecyclerAdapter<OperatorAdapter.ParentViewHolder, ChildViewHolder> {
    public static final int INDEX_COPY = 0;
    public static final int INDEX_MOVE = 1;
    public static final int INDEX_DELETE = 2;
    public static final int INDEX_COMPRESS = 3;

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
    public OperatorAdapter(@NonNull List<OperatorList> parentItemList) {
        super(parentItemList);
//        mTotalList = parentItemList;
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

        switch (position) {
            case INDEX_COPY:
                label = "COPY OPERATOR";
                break;
            case INDEX_MOVE:
                label = "MOVE OPERATOR";
                break;
            case INDEX_DELETE:
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
        Operator operator = (Operator) childListItem;
        final ChildViewHolder viewHolder = (ChildViewHolder) childViewHolder;

        setupFileName(viewHolder, operator);
        setupOperator(operator);
        Observable<Object> observable = operator.execute();
        observable.flatMap(new Func1<Object, Observable<Operator.UpdatableData>>() {

            @Override
            public Observable<Operator.UpdatableData> call(Object o) {
                if (o instanceof Operator.UpdatableData)
                    return Observable.just((Operator.UpdatableData) o);

                return Observable.empty();
            }
        }).subscribe(viewHolder);
//                .subscribe(new Subscriber<Operator.UpdatableData>() {
//            @Override
//            public void onCompleted() {}
//
//            @Override
//            public void onError(Throwable e) {
//            }
//
//            @Override
//            public void onNext(Operator.UpdatableData updatableData) {
//                viewHolder.call(updatableData);
//                request(1);
//            }
//
//            @Override
//            public void onStart() {
//                super.onStart();
//                request(1);
//            }
//        });

        if (operator.isCancelable() && operator.isExecuting()) {
            viewHolder.addMenuButton(viewHolder.btnCancel);
        } else {
            viewHolder.removeMenuButton(viewHolder.btnCancel);
        }
        if (operator.isAbleToPause() && operator.isExecuting()) {
            viewHolder.addMenuButton(viewHolder.btnPause);
        } else {
            viewHolder.removeMenuButton(viewHolder.btnPause);
        }
        if (operator.isExecuting()) {
            viewHolder.removeMenuButton(viewHolder.btnRestart);
        } else {
            viewHolder.addMenuButton(viewHolder.btnRestart);
        }
        if (operator.isUndoable()) {
            viewHolder.addMenuButton(viewHolder.btnUndo);
        } else {
            viewHolder.removeMenuButton(viewHolder.btnUndo);
        }
        if (operator.isUpdatable()) {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            setProgress(viewHolder, 50);
        } else {
            viewHolder.progressBar.setVisibility(View.INVISIBLE);
        }
        if (operator.getSourcePath() != null) {
            ViewUtils.formatTitleAndContentTextView(viewHolder.tvFrom,
                    "From: ",
                    operator.getSourcePath(),
                    null,
                    null);
        }
        if (operator.getDestinationPath() != null) {
            ViewUtils.formatTitleAndContentTextView(viewHolder.tvTo,
                    "To: ",
                    operator.getDestinationPath(),
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

    private void setupFileName(ChildViewHolder viewHolder, Operator operator) {
        if (operator.getData() != null) {
            List<Object> data = new ArrayList<>();

            if (operator.getData() instanceof Collection) {
                data.addAll((Collection<?>) operator.getData());
            } else {
                data.add(operator.getData());
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

    private void setupOperator(Operator operator) {
        if (operator instanceof CopyFileOperator) {
            ((CopyFileOperator) operator).setRetryFlatMap(new Func1<Throwable, Observable<?>>() {
                @Override
                public Observable<?> call(Throwable throwable) {
                    if (throwable instanceof IOException) {
                        // TODO Implement check permission
                        return Observable.just(null);
                    }

                    return Observable.error(throwable);
                }
            });
        }
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

        ArrayList<Operator> mList;

        public OperatorList(List<Operator> list) {
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

    public class ChildViewHolder extends com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder implements Action1<Operator.UpdatableData> {

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
            button.setVisibility(View.INVISIBLE);
        }

        void addMenuButton(AppCompatImageButton button) {
            button.setVisibility(View.VISIBLE);
        }

        @Override
        public void call(Operator.UpdatableData updatableData) {
            Log.d("Updated data: " + updatableData);
            int position = getAdapterPosition();
            Object o = getListItem(position);

            if (o instanceof Operator) {
                if (updatableData.getOperatorHashcode() == o.hashCode()) {
                    // This is right operator, so update this view
                    if (progressBar.getProgress() != (int)updatableData.getProgress()) {
                        setProgress(this, (int) updatableData.getProgress());
                    }
                }
            }
        }
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