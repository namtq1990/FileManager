package com.tqnam.filemanager.explorer;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.quangnam.baseframework.BaseDialog;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.adapter.SimpleArrayAdapter;
import com.tqnam.filemanager.model.ItemInformation;
import com.tqnam.filemanager.utils.ViewUtils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InformationDialogFragment extends BaseDialog {
    public static final String TAG = InformationDialogFragment.class.getSimpleName();
    private static final String ARG_DATA = "data";
    private ViewHolder mViewHolder;
    private ItemInformation mInformation;

    public static InformationDialogFragment newInstance(ItemInformation informationItem) {
        InformationDialogFragment fragment = new InformationDialogFragment();
        Bundle arg = new Bundle();

        arg.putSerializable(ARG_DATA, informationItem);
        fragment.setArguments(arg);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInformation = (ItemInformation) getArguments().getSerializable(ARG_DATA);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_information, container, false);
        mViewHolder = new ViewHolder();
        ButterKnife.bind(mViewHolder, rootView);

        mViewHolder.listName.setAdapter(new SimpleArrayAdapter<String>(mInformation.getName()) {

            @Override
            public void formatLabel(TextView tv, String label) {
                tv.setTextIsSelectable(true);
                tv.setText(String.format(" - %1s", label));
            }
        });
        mViewHolder.listName.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        mViewHolder.listName.setHasFixedSize(true);

        setPath(mInformation.getPath());
        setSize(mInformation.getFileSize());
        setModifiedDate(mInformation.getModifiedTime());
        setRead(mInformation.canRead());
        setWrite(mInformation.canWrite());
        setExecutable(mInformation.canExecute());

        updateRoot();

//        rootView.post(new Runnable() {
//            @Override
//            public void run() {
//                // Make dialog fullscreen in width side
//                if (getDialog() != null) {
//                    Dialog dialog = getDialog();
//                    final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//                    params.width = WindowManager.LayoutParams.MATCH_PARENT;
//                    getDialog().getWindow().setAttributes(params);
//                }
//            }
//        });

        return rootView;
    }

    private void setPath(String path) {
        ViewUtils.formatTitleAndContentTextView(mViewHolder.tvPath,
                getString(R.string.information_location),
                path,
                new Object[] {
                        new StyleSpan(Typeface.BOLD)
                },
                null);
    }

    private void setSize(long size) {
        ViewUtils.formatTitleAndContentTextView(mViewHolder.tvSize,
                getString(R.string.information_size),
                String.valueOf(size),
                new Object[] {
                        new StyleSpan(Typeface.BOLD)
                },
                null);
    }

    private void setModifiedDate(Date date) {
        mViewHolder.tvModDate.setVisibility((date == null) ? View.GONE : View.VISIBLE);
        if (date != null) {
            ViewUtils.formatTitleAndContentTextView(mViewHolder.tvModDate,
                    getString(R.string.information_mod_date),
                    date.toString(),
                    new Object[] {
                            new StyleSpan(Typeface.BOLD)
                    },
                    null);
        }
    }

    private boolean canRootable() {
        return false;
    }

    private void updateRoot() {
        mViewHolder.cbRead.setEnabled(canRootable() && mInformation.canRead() != null);
        mViewHolder.cbWrite.setEnabled(canRootable() && mInformation.canWrite() != null);
        mViewHolder.cbExecute.setEnabled(canRootable() && mInformation.canExecute() != null);
    }

    private void setRead(Boolean canRead) {
        mViewHolder.cbRead.setChecked(canRead != null ? canRead : false);
    }

    private void setWrite(Boolean canWrite) {
        mViewHolder.cbWrite.setChecked(canWrite != null ? canWrite : false);
    }

    private void setExecutable(Boolean canExecute) {
        mViewHolder.cbExecute.setChecked(canExecute != null ? canExecute : false);
    }

    class ViewHolder {
        @BindView(R.id.list_name)
        RecyclerView listName;
        @BindView(R.id.tv_path)
        TextView tvPath;
        @BindView(R.id.tv_total_size)
        TextView tvSize;
        @BindView(R.id.tv_mod_time)
        TextView tvModDate;
        @BindView(R.id.cb_pm_read)
        CheckBox cbRead;
        @BindView(R.id.cb_pm_write)
        CheckBox cbWrite;
        @BindView(R.id.cb_pm_execute)
        CheckBox cbExecute;
    }
}
