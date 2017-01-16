package com.tqnam.filemanager.explorer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quangnam.baseframework.BaseActivity;
import com.quangnam.baseframework.BaseDataFragment;
import com.quangnam.baseframework.BaseDialog;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.adapter.SimpleArrayAdapter;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.operation.Operation;
import com.tqnam.filemanager.model.operation.propertyView.OperationPropertyView;
import com.tqnam.filemanager.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by quangnam on 1/4/17.
 * Project FileManager-master
 */
public class OperationInforDialogFragment extends BaseDialog {
    public static final String ARG_OPERATION = "operation";

    public static final String TAG = OperationInforDialogFragment.class.getSimpleName();

    private ViewHolder mHolder;
    private BaseDataFragment mData;
    private Operation mOperation;

    public static OperationInforDialogFragment newInstance(final Operation operation,
                                                           final BaseDataFragment dataFragment) {
        OperationInforDialogFragment dialog = new OperationInforDialogFragment();
        String argOperation = getArgOperation(dialog.hashCode());
        dataFragment.getOtherData().put(argOperation, operation);

        return dialog;
    }

    public static String getArgOperation(int hashcode) {
        return ARG_OPERATION + hashcode;
    }

    public Operation getOperation() {
        if (mOperation == null) {
            mOperation = (Operation) mData.getOtherData()
                    .get(getArgOperation(getSavedHashcode() == -1 ? hashCode() : getSavedHashcode()));
        }

        return mOperation;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mData = (BaseDataFragment) getFragmentManager()
                .findFragmentByTag(BaseDataFragment.TAG);
        mOperation = getOperation();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mData = ((BaseActivity) getActivity()).getDataFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mData.getOtherData().put(getArgOperation(hashCode()), mOperation);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mHolder = new ViewHolder();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog_Single);
        View layout = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_operation_info, null);
        builder.setView(layout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

        ButterKnife.bind(mHolder, layout);
        initView();

        layout.post(new Runnable() {
            @Override
            public void run() {
                if (getDialog() != null) {
                    AlertDialog curDialog = (AlertDialog) getDialog();

                    Button button = curDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button
                            .getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    button.setLayoutParams(params);
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOperation.getPropertyView() != null) {
            mOperation.getPropertyView().unBindView(mHolder);
        }
    }

    private void initView() {
        mHolder.mListFile.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHolder.mListFile.setHasFixedSize(true);

        if (mOperation.getPropertyView() != null) {
            mOperation.getPropertyView().bindView(mHolder);
        }
    }

    public class ViewHolder implements OperationPropertyView.ViewHolder {

        @BindView(R.id.list_operation_files)
        public RecyclerView mListFile;

        @BindView(R.id.tv_operation_from)
        public TextView mSource;

        @BindView(R.id.tv_operation_to)
        public TextView mDestination;

        @BindView(R.id.tv_operation_progress)
        public TextView mProgress;

        @BindView(R.id.tv_operation_speed)
        public TextView mSpeed;

        @BindView(R.id.tv_operation_size)
        public TextView mSize;

        public SimpleArrayAdapter<ItemExplorer> mAdapter;

        public void setListData(ItemExplorer[] data) {
            if (data == null) {
                ((View) mListFile.getParent()).setVisibility(View.GONE);
            } else {
                mAdapter = new SimpleArrayAdapter<ItemExplorer>(data) {

                    @Override
                    public void formatLabel(TextView tv, String label) {
                        label = " - " + label;
                        tv.setLayoutParams(
                                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT)
                        );
                        super.formatLabel(tv, label);
                    }

                    @Override
                    public String getLabelFromData(ItemExplorer data) {
                        return data.getPath();
                    }
                };
                mListFile.setAdapter(mAdapter);
            }
        }

        public void setSource(String source) {
            if (source == null) {
                mSource.setVisibility(View.GONE);
            } else {
                ViewUtils.formatTitleAndContentTextView(mSource,
                        getString(R.string.from),
                        source,
                        new Object[]{
                                new StyleSpan(Typeface.BOLD)
                        },
                        null);
            }
        }

        public void setDestination(String destination) {
            if (destination == null) {
                mDestination.setVisibility(View.GONE);
            } else {
                ViewUtils.formatTitleAndContentTextView(mDestination,
                        getString(R.string.destination),
                        destination,
                        new Object[]{
                                new StyleSpan(Typeface.BOLD)
                        },
                        null);
            }
        }

        public void setProgress(Integer progress) {
            if (progress == null) {
                mProgress.setVisibility(View.GONE);
            } else {
                ViewUtils.formatTitleAndContentTextView(mProgress,
                        getString(R.string.progress),
                        String.valueOf(progress),
                        new Object[]{
                                new StyleSpan(Typeface.BOLD)
                        },
                        null);
            }
        }

        public void setSpeed(Integer speed) {
            if (speed == null) {
                mSpeed.setVisibility(View.GONE);
            } else {
                ViewUtils.formatTitleAndContentTextView(mSpeed,
                        getString(R.string.speed),
                        String.valueOf(speed),
                        new Object[]{
                                new StyleSpan(Typeface.BOLD)
                        },
                        null);
            }
        }

        public void setSize(Long size) {
            if (size == null) {
                mSize.setVisibility(View.GONE);
            } else {
                ViewUtils.formatTitleAndContentTextView(mSize,
                        getString(R.string.information_size),
                        String.valueOf(size),
                        new Object[]{
                                new StyleSpan(Typeface.BOLD)
                        },
                        null);
            }
        }
    }

}
