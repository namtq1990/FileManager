package com.tqnam.filemanager.explorer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

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

        // Init Fragment
//        ListFileFragment listFile = (ListFileFragment) getSupportFragmentManager()
//                .findFragmentByTag(ListFileFragment.TAG);
//
//        if (listFile == null) {
//            listFile = new ListFileFragment();
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_list_container, listFile, ListFileFragment.TAG)
//                    .commit();
//        }

//        mViewHolder.mFragmentListFile = listFile;
        mViewHolder.mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewHolder.mPager = (ViewPager) findViewById(R.id.pager);
        mViewHolder.mTab = (TabLayout) findViewById(R.id.appbar_tab);
        setSupportActionBar(mViewHolder.mToolbar);
        mViewHolder.mPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
        mViewHolder.mTab.setupWithViewPager(mViewHolder.mPager);
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
        private final int mPrefFragmentIndex = 1;

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
        ListFileFragment mFragmentListFile;
        Toolbar          mToolbar;
        TabLayout        mTab;
        ViewPager        mPager;
    }
}
