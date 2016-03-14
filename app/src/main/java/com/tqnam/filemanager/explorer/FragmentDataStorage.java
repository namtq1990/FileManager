package com.tqnam.filemanager.explorer;

import android.os.Bundle;

import com.quangnam.baseframework.BaseFragment;

import rx.Observable;

/**
 * Created by quangnam on 3/4/16.
 * Fragment used to a data storage.
 */
public class FragmentDataStorage extends BaseFragment {
    public static final String TAG = "FragmentDataStorage";

    private Bundle mData;
    private ObservableManager mObservableManager;

    public ObservableManager getObservableManager() {
        return mObservableManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mData = new Bundle();
        mObservableManager = new ObservableManager();
    }

    public Bundle getData() {
        return mData;
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
