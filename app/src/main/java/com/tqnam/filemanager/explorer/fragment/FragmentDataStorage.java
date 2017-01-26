package com.tqnam.filemanager.explorer.fragment;

import android.os.Bundle;

import com.quangnam.baseframework.BaseDataFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by quangnam on 3/4/16.
 * Fragment used to a data storage.
 */
public class FragmentDataStorage extends BaseDataFragment {
    public static final String TAG = "FragmentDataStorage";

    ArrayList<Object> mListEventBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListEventBus = new ArrayList<>();
    }

    public void registerEvent(Object object) {
        if (!EventBus.getDefault().isRegistered(object)) {
            mListEventBus.add(object);
            EventBus.getDefault().register(object);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for (Object object : mListEventBus) {
            EventBus.getDefault().unregister(object);
        }
    }
}
