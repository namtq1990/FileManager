package com.tqnam.filemanager.explorer.fragment;

import android.os.Bundle;

import com.quangnam.baseframework.BaseDataFragment;

import rx.Observable;

/**
 * Created by quangnam on 3/4/16.
 * Fragment used to a data storage.
 */
public class FragmentDataStorage extends BaseDataFragment {
    public static final String TAG = "FragmentDataStorage";

    private ObservableManager mObservableManager;

    public ObservableManager getObservableManager() {
        return mObservableManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mObservableManager = new ObservableManager();
    }

    public class ObservableManager {

        private Observable<Object> mLoaderObs;

        public Observable<Object> getLoaderObservable() {
            return mLoaderObs;
        }

        public void updateLoaderObservable(Observable observable) {
            mLoaderObs = observable;
        }
    }
}
