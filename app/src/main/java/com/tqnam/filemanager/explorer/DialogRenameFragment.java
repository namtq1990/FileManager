package com.tqnam.filemanager.explorer;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

import com.quangnam.baseframework.BaseDialog;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.model.ItemExplorer;

/**
 * Created by quangnam on 11/28/15.
 */
public class DialogRenameFragment extends BaseDialog {

    public static final String TAG       = "DialogRenameFragment";
    public static final String ARG_LABEL = "label";
    private static final String ARG_ITEM = "item";

    private ItemExplorer mItem;
    private String     mCurLabel;

    private ViewHolder mHolder;
    private RenameDialogListener mListener;

    public static DialogRenameFragment newInstance(ItemExplorer item, String label) {
        DialogRenameFragment fragment = new DialogRenameFragment();
        Bundle args = new Bundle();

        args.putParcelable(ARG_ITEM, item);
        args.putString(ARG_LABEL, label);
        fragment.setArguments(args);

        return fragment;
    }

    public void setListener(RenameDialogListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurLabel = getArguments().getString(ARG_LABEL, "");
        mItem = getArguments().getParcelable(ARG_ITEM);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        mHolder = new ViewHolder();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS);
//        dialog.getWindow().setBackgroundDrawableResource(R.drawable.panel_background_border_vector);

        View view = inflater.inflate(R.layout.popup_edit_label, container, false);
        mHolder.mEdLabel = (EditText) view.findViewById(R.id.popup_ed_label);
        mHolder.mBtnOK = (ImageButton) view.findViewById(R.id.popup_btn_ok);

        if (savedInstanceState == null)
            mHolder.mEdLabel.setText(mCurLabel);

        mHolder.mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onRename(mItem, mHolder.mEdLabel.getText().toString());
                }

                dismiss();
            }
        });

        return view;
    }

    public interface RenameDialogListener {
        void onRename(ItemExplorer item, String newName);
    }

    private class ViewHolder {
        private EditText    mEdLabel;
        private ImageButton mBtnOK;
    }
}
