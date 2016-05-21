package com.quangnam.baseframework;

import android.support.v7.app.AppCompatActivity;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by quangnam on 11/12/15.
 *
 * Base class Activity use for this application.
 * Use this base class so you can handle life cycle to debug or add functional
 */
public class BaseActivity extends AppCompatActivity {

    private CompositeSubscription mLocalSubs = new CompositeSubscription();
    private BaseFragmentInterface mFocusFragment;

    public BaseFragmentInterface getFocusFragment() {
        return mFocusFragment;
    }

    protected void setFocusFragment(BaseFragmentInterface fragment) {
        if (mFocusFragment != null) {
            mFocusFragment.clearFocus();
        }

        mFocusFragment = fragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLocalSubscription().unsubscribe();
    }

    @Override
    public void onBackPressed() {
        if (mFocusFragment instanceof OnBackPressedListener) {
            ((OnBackPressedListener) mFocusFragment).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public CompositeSubscription getLocalSubscription() {
        return mLocalSubs;
    }

    public BaseDataFragment getDataFragment() {
        return null;
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }
}
