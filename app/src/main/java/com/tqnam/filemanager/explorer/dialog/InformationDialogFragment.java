package com.tqnam.filemanager.explorer.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quangnam.baseframework.BaseDialog;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.adapter.SimpleArrayAdapter;
import com.tqnam.filemanager.model.ItemInformation;
import com.tqnam.filemanager.utils.ViewUtils;

import java.util.Date;
import java.util.Locale;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_information, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog_Single)
                .setTitle(R.string.information_property)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setView(rootView);
        initView(rootView);

        return builder.create();
    }

    private void initView(final View rootView) {

        mViewHolder = new ViewHolder();
        ButterKnife.bind(mViewHolder, rootView);

        mViewHolder.listName.setAdapter(new SimpleArrayAdapter<String>(mInformation.getName()) {

            @Override
            public void formatLabel(TextView tv, String label) {
                tv.setTextIsSelectable(true);
                tv.setText(String.format(" - %1s", label));
            }
        });
        mViewHolder.listName.setLayoutManager(new LinearLayoutManager(getActivity()));
        mViewHolder.listName.setHasFixedSize(true);

        setPath(mInformation.getPath());
        setSize(mInformation.getFileSize());
        setModifiedDate(mInformation.getModifiedTime());
        setRead(mInformation.canRead());
        setWrite(mInformation.canWrite());
        setExecutable(mInformation.canExecute());

        updateRoot();

        rootView.post(new Runnable() {
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
                String.format(Locale.ENGLISH, "%d [%s]", size, Formatter.formatFileSize(getAppContext(), size)),
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
