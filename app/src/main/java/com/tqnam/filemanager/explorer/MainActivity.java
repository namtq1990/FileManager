package com.tqnam.filemanager.explorer;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.tqnam.filemanager.BaseActivity;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.fileExplorer.ListFileFragment;

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
        ListFileFragment listFile = (ListFileFragment) getSupportFragmentManager()
                .findFragmentByTag(ListFileFragment.TAG);

        if (listFile == null) {
            listFile = new ListFileFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_list_container, listFile, ListFileFragment.TAG)
                    .commit();
        }

        mViewHolder.mFragmentListFile = listFile;
        mViewHolder.mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mViewHolder.mToolbar);
    }

    @Override
    public void onBackPressed() {
        if (mViewHolder.mFragmentListFile != null
                && mViewHolder.mFragmentListFile.isResumed()) {
            // TODO update fragment List is showing

            mViewHolder.mFragmentListFile.onBackPressed();
        }
    }

    private class ViewHolder {
        ListFileFragment mFragmentListFile;
        Toolbar mToolbar;
    }
}
