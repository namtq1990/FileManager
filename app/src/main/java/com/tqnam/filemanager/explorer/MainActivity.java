package com.tqnam.filemanager.explorer;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tqnam.filemanager.BaseActivity;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.fileExplorer.ListFileFragment;
import com.tqnam.filemanager.preference.PreferenceFragment;

/**
 * Activity container
 * First design for file explorer, may be add setting, too.
 */

public class MainActivity extends BaseActivity {

    private ViewHolder mViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    /**
     * Init function for activity.
     * Inflate view and Fragment
     */
    private void init() {
        mViewHolder = new ViewHolder();

        mViewHolder.mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewHolder.mPager = (ViewPager) findViewById(R.id.pager);
        mViewHolder.mTab = (TabLayout) findViewById(R.id.appbar_tab);
        mViewHolder.mBtnAddFile = (FloatingActionButton) findViewById(R.id.btn_add);
        setSupportActionBar(mViewHolder.mToolbar);
        mViewHolder.mPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
        mViewHolder.mTab.setupWithViewPager(mViewHolder.mPager);
        mViewHolder.mBtnAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingActionButton btn = (FloatingActionButton) v;
                if (btn.getDrawable() instanceof Animatable) {
                    Animatable animatable = (Animatable) btn.getDrawable();
                    animatable.start();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mViewHolder.mFragmentListFile != null
                && mViewHolder.mFragmentListFile.isResumed()) {
            // TODO update fragment List is showing

            mViewHolder.mFragmentListFile.onBackPressed();
        }
    }

    private class PageAdapter extends FragmentPagerAdapter {

        private final int mLocalFileFragmentIndex = 0;
        private final int mPrefFragmentIndex      = 1;

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position) {
                case mLocalFileFragmentIndex:
                    fragment = new ListFileFragment();
                    mViewHolder.mFragmentListFile = (ListFileFragment) fragment;
                    break;
                case mPrefFragmentIndex:
                    fragment = new PreferenceFragment();
                    break;
                default:
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //TODO Add string to xml
            switch (position) {
                case mLocalFileFragmentIndex:
                    return "LOCAL";
                case mPrefFragmentIndex:
                    return "PREFERENCE";
                default:
                    return null;
            }
        }
    }

    private class ViewHolder {
        ListFileFragment     mFragmentListFile;
        Toolbar              mToolbar;
        TabLayout            mTab;
        ViewPager            mPager;
        FloatingActionButton mBtnAddFile;
    }
}
