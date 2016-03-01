package com.tqnam.filemanager.explorer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.quangnam.baseframework.BaseActivity;
import com.quangnam.baseframework.BaseFragment;
import com.tqnam.filemanager.Application;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.view.Preview;

public class PreviewFragment extends BaseFragment implements Preview.OnRemoveListener,
        Preview.OnStateChangeListener, BaseActivity.OnBackPressedListener {
    public static final String TAG      = "PreviewFragment";
    public static final String ARG_URI  = "file_uri";
    public static final String ARG_TYPE = "file_type";
    public static final String ARG_DATA = "file_data";

    private Uri mUri;
    private int mFileType;
    private Preview mPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRestoreFocus(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUri = getArguments().getParcelable(ARG_URI);
        mFileType = getArguments().getInt(ARG_TYPE);
        Context context = container.getContext();
        View contentView;

        switch (mFileType) {
            case ItemExplorer.FILE_TYPE_IMAGE:
                contentView = new ImageView(context);
//                ((ImageView) contentView).setAdjustViewBounds(true);
//                contentView.setBackgroundColor(0x00000000);
                Application app = (Application) context.getApplicationContext();
                app.getGlobalData().mImage
                        .load(mUri)
                        .into((ImageView) contentView);
                break;
            default:
                contentView = new Button(context);
                ((Button) contentView).setText("Test");
                break;
        }
        mPreview = new Preview(inflater.getContext());
        mPreview.setContentView(contentView);
        mPreview.setOnStateChangeListener(this);
        mPreview.setOnRemoveListener(this);
        requestFocus();

        return mPreview;
    }

    @Override
    public void onRemovePreview() {
        getFragmentManager().beginTransaction()
                .remove(this)
                .commit();
    }

    @Override
    public void onStateChanged(int oldState, int newState) {
        if (newState == Preview.STATE_MAXIMUM) {
            requestFocus();
        } else {
            popBackFocus();
        }
    }

    @Override
    public void onBackPressed() {
        mPreview.minimize();
    }
}
