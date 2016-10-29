package com.tqnam.filemanager.explorer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.quangnam.baseframework.BaseActivity;
import com.quangnam.baseframework.BaseDataFragment;
import com.quangnam.baseframework.BaseFragmentInterface;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.fileExplorer.ListFileFragment;
import com.tqnam.filemanager.preference.PreferenceFragment;
import com.tqnam.filemanager.utils.UIUtils;

/**
 * Activity container
 * First design for file explorer, may be add setting, too.
 */

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, ExplorerBaseFragment.ExplorerBaseFunction {

    private Handler mHandler;
    private ViewHolder mViewHolder;
    private FragmentDataStorage mDataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(savedInstanceState);
    }

    @Override
    public BaseDataFragment getDataFragment() {
        return mDataFragment;
    }

    public String getLocalHomePath() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String key_pref_homepath = getString(R.string.pref_local_home_path);
        String homePath = pref.getString(key_pref_homepath, null);
        if (homePath == null) {
            homePath = "/";
            pref.edit().putString(key_pref_homepath, homePath)
                    .apply();
        }

        return homePath;
    }

    /**
     * Init function for activity.
     * Inflate view and Fragment
     */
    private void init(Bundle savedInstanceState) {
        mHandler = new Handler(Looper.getMainLooper());

        mViewHolder = new ViewHolder();

        mViewHolder.mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewHolder.mPager = (ViewPager) findViewById(R.id.pager);
        mViewHolder.mTab = (TabLayout) findViewById(R.id.appbar_tab);
        mViewHolder.mBtnAddFile = (FloatingActionButton) findViewById(R.id.btn_add);
        mViewHolder.mAdapter = new PageAdapter(getSupportFragmentManager());
        setSupportActionBar(mViewHolder.mToolbar);
        mViewHolder.mPager.setAdapter(mViewHolder.mAdapter);
        mViewHolder.mTab.setupWithViewPager(mViewHolder.mPager);
        mViewHolder.mPager.addOnPageChangeListener(this);
        mViewHolder.mBtnAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuAddItemFragment fragment = new MenuAddItemFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.rootview, fragment, MenuAddItemFragment.TAG)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Init data fragment
        mDataFragment = (FragmentDataStorage) getSupportFragmentManager()
                .findFragmentByTag(FragmentDataStorage.TAG);

        if (mDataFragment == null) {
            mDataFragment = new FragmentDataStorage();
            getSupportFragmentManager().beginTransaction()
                    .add(mDataFragment, FragmentDataStorage.TAG)
                    .commit();
        }
        //

        //        Observable<Boolean> obsMenuVisibility = RxView.layoutChanges(mViewHolder.mMenuAddItem)
        //                .flatMap(new Func1<Void, Observable<Boolean>>() {
        //                    @Override
        //                    public Observable<Boolean> call(Void aVoid) {
        //                        return Observable.just(View.VISIBLE == mViewHolder.mMenuAddItem.getVisibility());
        //                    }
        //                })
        //                .distinctUntilChanged();
        //        obsMenuVisibility.subscribe(new Action1<Boolean>() {
        //            @Override
        //            public void call(Boolean aBoolean) {
        //                if (aBoolean) {
        //                    mViewHolder.mBtnAddFile.animate().rotation(45)
        //                            .setDuration(100)
        //                            .start();
        //                } else {
        //                    mViewHolder.mBtnAddFile.animate().rotation(0)
        //                            .setDuration(100)
        //                            .start();
        //                }
        //            }
        //        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        String tag = UIUtils.getViewPagerTag(mViewHolder.mPager.getId(),
                mViewHolder.mAdapter.getItemId(position));

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);

        if (fragment != null &&
                fragment instanceof BaseFragmentInterface) {
            //            requestFocusFragment((BaseFragmentInterface) fragment);
            ((BaseFragmentInterface) fragment).requestFocusFragment(this);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void showAddButton() {
        mViewHolder.mBtnAddFile.show();
    }

    public void showAddButtonDirect() {
        mViewHolder.mBtnAddFile.setVisibility(View.VISIBLE);
    }

    public void hideAddButton() {
        mViewHolder.mBtnAddFile.setVisibility(View.GONE);
    }

    private class PageAdapter extends FragmentPagerAdapter {

        static final int INDEX_LOCAL_FILE_FRAGMENT = 0;
        static final int INDEX_PREF_FRAGMENT = 1;

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            final Fragment fragment;

            switch (position) {
                case INDEX_LOCAL_FILE_FRAGMENT:
                    fragment = HostFragment.newInstance();

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ListFileFragment list = ListFileFragment.newInstance(getLocalHomePath());
                            mViewHolder.mFragmentListFile = list;

                            ((HostFragment) fragment).addFragmentPage(list, ListFileFragment.TAG);
                        }
                    });

                    break;
                case INDEX_PREF_FRAGMENT:
                    fragment = new PreferenceFragment();
                    break;
                default:
                    fragment = null;
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
                case INDEX_LOCAL_FILE_FRAGMENT:
                    return "LOCAL";
                case INDEX_PREF_FRAGMENT:
                    return "PREFERENCE";
                default:
                    return null;
            }
        }
    }

    private class ViewHolder {
        ListFileFragment mFragmentListFile;
        Toolbar mToolbar;
        TabLayout mTab;
        ViewPager mPager;
        PageAdapter mAdapter;
        FloatingActionButton mBtnAddFile;
    }
}
