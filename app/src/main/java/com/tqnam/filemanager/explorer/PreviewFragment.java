package com.tqnam.filemanager.explorer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.quangnam.baseframework.BaseActivity;
import com.quangnam.baseframework.BaseFragment;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.view.Preview;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

public class PreviewFragment extends BaseFragment implements Preview.OnRemoveListener,
        Preview.OnStateChangeListener, BaseActivity.OnBackPressedListener,
        View.OnClickListener {
    public static final String TAG      = "PreviewFragment";
    public static final String ARG_ITEM = "item";

    private ItemExplorer mItem;
    private Preview      mPreview;
    private Subscription mSubsLoader;
    private Action1<Object> mActionLoadData = new Action1<Object>() {
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
        setRestoreFocus(true);

        if (savedInstanceState != null) {
            mItem = savedInstanceState.getParcelable(ARG_ITEM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPreview = new Preview(inflater.getContext());
        loadPreview();

        mPreview.setOnStateChangeListener(this);
        mPreview.setOnRemoveListener(this);
        mPreview.setOnClickListener(this);
        requestFocus();

        return mPreview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_ITEM, mItem);
    }

    public void loadPreview() {
        FragmentDataStorage dataFragment = (FragmentDataStorage) getFragmentManager()
                .findFragmentByTag(FragmentDataStorage.TAG);
        Observable<Object> observable = dataFragment.getObservableManager().getLoaderObservable();

        if (mSubsLoader != null) {
            mSubsLoader.unsubscribe();
        }

        BaseActivity activity = (BaseActivity) getActivity();
        if (activity != null) {
            mSubsLoader = observable.subscribe(mActionLoadData);
            activity.getLocalSubscription().add(mSubsLoader);
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

    @Override
    public void onClick(View v) {
        if (v == mPreview) {
            mPreview.maximum();
        }
    }
}
