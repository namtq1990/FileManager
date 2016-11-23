package com.tqnam.filemanager.explorer.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.quangnam.baseframework.BaseActivity;
import com.quangnam.baseframework.BaseFragment;
import com.quangnam.baseframework.Config;
import com.quangnam.baseframework.DialogUtil;
import com.tqnam.filemanager.Application;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.view.Preview;

import rx.Subscription;
import rx.functions.Action1;

public class PreviewFragment extends BaseFragment implements Preview.OnRemoveListener,
        Preview.OnStateChangeListener, BaseActivity.OnBackPressedListener,
        View.OnClickListener {
    public static final String TAG      = "PreviewFragment";
    public static final String ARG_ITEM = "item";

    // The item to display in this preview
    private ItemExplorer mItem;

    // The view that display item
    private Preview      mPreview;

    // The subsciption to the load item content observable
    private Subscription mLoadDataSubscription;

    // Action when load content is done
    private Action1<Object> mLoadDataAction = new Action1<Object>() {
        @Override
        public void call(Object o) {
            View contentView;

            switch (mItem.getFileType()) {
                case ItemExplorer.FILE_TYPE_IMAGE:
                    contentView = new ImageView(mPreview.getContext());
                    Bitmap bitmap = (Bitmap) o;
                    ((ImageView) contentView).setImageBitmap(bitmap);
                    break;
                default:
                    contentView = new Button(mPreview.getContext());
                    //TODO Test
                    ((Button) contentView).setText("Test");
                    break;
            }

            mPreview.setContentView(contentView);
        }
    };

    public void setItem(ItemExplorer item) {
        mItem = item;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mItem = savedInstanceState.getParcelable(ARG_ITEM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO move preview to Debug version
        if (Config.DEBUG) {
            mPreview = new Preview(inflater.getContext());
            loadPreview((BaseActivity) inflater.getContext());

            mPreview.setOnStateChangeListener(this);
            mPreview.setOnRemoveListener(this);
            mPreview.setOnClickListener(this);

//            BaseActivity activity = (BaseActivity) inflater.getContext();
//            activity.requestFocusFragment(this);
            requestFocusFragment();
        }

        return mPreview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_ITEM, mItem);
    }

    public void loadPreview(BaseActivity activity) {
        if (Config.DEBUG) {
            //TODO move preview feature to Debug version
            FragmentDataStorage dataFragment = (FragmentDataStorage) activity.getDataFragment();

        } else {
            if (activity != null) {
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mItem.getExtension());

                if (mimeType == null) {
                    selectMimeTypeDialog(activity);
                } else {
                    openFileByType(mimeType);
                }
            }
        }
    }

    @Override
    public void onRemovePreview() {
        getFragmentManager().beginTransaction()
                .remove(this)
                .commit();
    }

    @Override
    public void onStateChanged(int oldState, int newState) {
        BaseActivity activity = (BaseActivity) getActivity();

        if (newState == Preview.STATE_MAXIMUM) {
            requestFocusFragment();
        } else {
            removeFocusRequest();
        }
    }

    @Override
    public boolean onBackPressed() {
        mPreview.minimize();

        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == mPreview) {
            mPreview.maximum();
        }
    }

    private void openFileByType(String mimeType) {
        Activity activity = Application.getInstance().getCurActivity();

        if (activity != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(mItem.getUri());
            //            Intent chooserIntent = Intent.createChooser(intent, "Choose an application to open with:");

            intent.setDataAndType(mItem.getUri(), mimeType);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                BaseActivity curActivity = (BaseActivity) Application.getInstance().getCurActivity();
                if (curActivity != null)
                    curActivity.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(Application.getInstance(), "Cann't find application", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private void selectMimeTypeDialog(Context context) {
        String[] typeList = context.getResources().getStringArray(R.array.arr_open_file);
        AlertDialog.Builder builder = DialogUtil.makeSelectDialog(context, "Open as", typeList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // Open as text
                        openFileByType("text/*");
                        break;
                    case 1:
                        // Open as image
                        openFileByType("image/*");
                        break;
                    case 2:
                        // Open as audio
                        openFileByType("audio/*");
                        break;
                    case 3:
                        // Open as Video
                        openFileByType("video/*");
                        break;
                    case 4:
                        // Open as other type
                        openFileByType("*/*");
                        break;
                }
            }
        });

        builder.create().show();
    }
}
