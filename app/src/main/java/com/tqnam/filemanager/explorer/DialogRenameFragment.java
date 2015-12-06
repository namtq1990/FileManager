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

import com.tqnam.filemanager.BaseDialog;
import com.tqnam.filemanager.R;

/**
 * Created by quangnam on 11/28/15.
 */
public class DialogRenameFragment extends BaseDialog {

    public static final String TAG       = "DialogRenameFragment";
    public static final String ARG_LABEL = "label";

    private String     mCurLabel;
    private ViewHolder mHolder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurLabel = getArguments().getString(ARG_LABEL, "");
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

        return view;
    }

    private class ViewHolder {
        private EditText    mEdLabel;
        private ImageButton mBtnOK;
    }
}
