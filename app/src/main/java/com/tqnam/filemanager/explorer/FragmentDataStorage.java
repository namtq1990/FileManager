package com.tqnam.filemanager.explorer;

import android.os.Bundle;

import com.quangnam.baseframework.BaseFragment;

/**
 * Created by quangnam on 3/4/16.
 * Fragment used to a data storage.
 */
public class FragmentDataStorage extends BaseFragment {
    public static final String TAG = "FragmentDataStorage";

    private Bundle mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mData = new Bundle();
    }

    public Bundle getData() {
        return mData;
    }
}
