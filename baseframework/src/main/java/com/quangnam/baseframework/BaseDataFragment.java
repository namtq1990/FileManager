package com.quangnam.baseframework;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.HashMap;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by quangnam on 5/13/16.
 */
public class BaseDataFragment extends BaseFragment {
    public static final String TAG = BaseDataFragment.class.getSimpleName();

    private static final String ARG_DATA = "data_saved";

    private Bundle mData;
    private HashMap<String, Object> mOtherData;
    private CompositeSubscription mSubscriptions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mData = new Bundle();
        if (savedInstanceState != null) {
            Bundle data = savedInstanceState.getBundle(ARG_DATA);
            if (data != null) {
                mData.putAll(data);
            }
        }

        mOtherData = new HashMap<>();
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(ARG_DATA, mData);
    }

    public Bundle getData() {
        return mData;
    }

    public HashMap<String, Object> getOtherData() {
        return mOtherData;
    }

    public CompositeSubscription getSubscriptions() {
        return mSubscriptions;
    }
}
