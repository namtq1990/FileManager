package com.quangnam.baseframework;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class BaseDataFragment extends BaseFragment {
    public static final String TAG = BaseDataFragment.class.getSimpleName();


    private HashMap<String, Object> mOtherData;

    public BaseDataFragment() {
        Bundle arg = new Bundle();
        setArguments(arg);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mOtherData = new HashMap<>();
    }

    public Bundle getData() {
        return getArguments();
    }

    public HashMap<String, Object> getOtherData() {
        return mOtherData;
    }

}
