/*
 * MIT License
 *
 * Copyright (c) 2017 Tran Quang Nam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tqnam.filemanager.explorer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quangnam.base.BaseActivity;
import com.quangnam.base.BaseDataFragment;
import com.quangnam.base.BaseFragmentInterface;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.adapter.DrawerMenuAdapter;
import com.tqnam.filemanager.explorer.dialog.AboutDialogFragment;
import com.tqnam.filemanager.explorer.dialog.EnterTextDialogFragment;
import com.tqnam.filemanager.explorer.fileExplorer.ListFileFragment;
import com.tqnam.filemanager.explorer.fragment.ExplorerBaseFragment;
import com.tqnam.filemanager.explorer.fragment.FragmentDataStorage;
import com.tqnam.filemanager.explorer.fragment.HostFragment;
import com.tqnam.filemanager.explorer.fragment.MenuAddItemFragment;
import com.tqnam.filemanager.explorer.fragment.OperatorManagerFragment;
import com.tqnam.filemanager.utils.ViewUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity container
 * First design for file explorer, may be add setting, too.
 */

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener,
        ExplorerBaseFragment.ExplorerBaseFunction, MenuAddItemFragment.MenuFABListener,
        EnterTextDialogFragment.EnterTextDialogListener, DrawerMenuAdapter.DrawerMenuListener
{

    private static final String KEY_FILE = "file";
    private static final String KEY_FOLDER = "folder";

    private Handler mHandler;
    private ViewHolder mViewHolder;
    private FragmentDataStorage mDataFragment;
    private SharedPreferences mPref;
    private ArrayList<String> mShortcutList;
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.pref_show_ads))) {
                if (sharedPreferences.getBoolean(key, false)) {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mViewHolder.mBottomAdView.loadAd(adRequest);
                    mViewHolder.mBottomAdView.setVisibility(View.VISIBLE);
                } else {
                    mViewHolder.mBottomAdView.destroy();
                    mViewHolder.mBottomAdView.setVisibility(View.GONE);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(savedInstanceState);

//        final RxCacheWithoutError<Object> mapper = new RxCacheWithoutError<>(1);
//        mapper.setForceReplay(true);
//        final Observable<Object> observable = Observable.create(
//                new Observable.OnSubscribe<Object>() {
//                    @Override
//                    public void call(Subscriber<? super Object> subscriber) {
//
//                        for (int i = 0; i < 10; i++) {
//                            Log.d("Producing: " + i);
//                            subscriber.onNext(i);
//
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                        subscriber.onCompleted();
//                    }
//                }
//        ).subscribeOn(Schedulers.computation())
//                .doOnSubscribe(new Action0() {
//                    @Override
//                    public void call() {
//                        Log.d("Subscribed");
//                    }
//                })
//                .doOnUnsubscribe(new Action0() {
//                    @Override
//                    public void call() {
//                        Log.d("Unsubscribed");
//                    }
//                })
//                .doOnError(new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        throwable.printStackTrace();
//                    }
//                })
//                .doOnTerminate(new Action0() {
//                    @Override
//                    public void call() {
//                        Log.d("Terminated");
//                    }
//                })
//                .compose(mapper);
//
//        final Action1<Object> action = new Action1<Object>() {
//            @Override
//            public void call(Object o) {
//                Log.d("onNext: " + o);
//            }
//        };
//        final Action1<Throwable> error = new Action1<Throwable>() {
//            @Override
//            public void call(Throwable throwable) {
//                Log.d("Error happened");
//            }
//        };
//        final Subscription subscription = observable.subscribe(action, error);
//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                subscription.unsubscribe();
//            }
//        }, 5000);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                observable.subscribe(action, error);
//            }
//        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPref.unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        mViewHolder.mBottomAdView.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewHolder.mBottomAdView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewHolder.mBottomAdView.pause();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mViewHolder.mDrawerToggle.syncState();
    }

    @Override
    public BaseDataFragment getDataFragment() {
        return mDataFragment != null ? mDataFragment : (BaseDataFragment) getSupportFragmentManager()
                .findFragmentByTag(BaseDataFragment.TAG);
    }

    public String getLocalHomePath() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String key_pref_homepath = getString(R.string.pref_local_home_path);
        String homePath = pref.getString(key_pref_homepath, null);
        if (homePath == null) {
            homePath = Environment.getExternalStorageDirectory().getPath();
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
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mPref.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        initData();

        mViewHolder = new ViewHolder();

        ButterKnife.bind(mViewHolder, this);
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

        initDrawerMenu();
        initAds();

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

    private void initDrawerMenu() {
        mViewHolder.mDrawerToggle = new ActionBarDrawerToggle(this,
                mViewHolder.mDrawerLayout,
                mViewHolder.mToolbar,
                R.string.drawer_action_open,
                R.string.drawer_action_close
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mViewHolder.mDrawerLayout.addDrawerListener(mViewHolder.mDrawerToggle);
        mViewHolder.mDrawerMenuAdapter = new DrawerMenuAdapter(mShortcutList);
        mViewHolder.mDrawerMenuList.setAdapter(mViewHolder.mDrawerMenuAdapter);
        mViewHolder.mDrawerMenuList.setLayoutManager(new LinearLayoutManager(this));
        mViewHolder.mDrawerMenuList.setHasFixedSize(true);
        mViewHolder.mDrawerMenuAdapter.setListener(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void initAds() {
        mPreferenceChangeListener.onSharedPreferenceChanged(mPref, getString(R.string.pref_show_ads));
    }

    private void initData() {
        mDataFragment = (FragmentDataStorage) getSupportFragmentManager()
                .findFragmentByTag(BaseDataFragment.TAG);

        if (mDataFragment == null) {
            mDataFragment = new FragmentDataStorage();
            getSupportFragmentManager().beginTransaction()
                    .add(mDataFragment, BaseDataFragment.TAG)
                    .commit();
        }

        mShortcutList = new ArrayList<>();
        if (Environment.getExternalStorageState() != null) {
            String storagePath = Environment.getExternalStorageDirectory().getPath();
            mShortcutList.add(storagePath);

            String[] paths = {
                    Environment.DIRECTORY_ALARMS,
                    Environment.DIRECTORY_DCIM,
                    Environment.DIRECTORY_DOWNLOADS,
                    Environment.DIRECTORY_MOVIES,
                    Environment.DIRECTORY_MUSIC,
                    Environment.DIRECTORY_NOTIFICATIONS,
                    Environment.DIRECTORY_PICTURES
            };
            for (String path : paths) {
//                File file = getExternalFilesDir(path);
//                if (file != null) {
//                    mShortcutList.add(file.getPath());
//                }
                mShortcutList.add(storagePath + "/" + path);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Fragment fragment = getCurSelectedPage();

        if (fragment != null) {
            //            requestFocusFragment((BaseFragmentInterface) fragment);
            ((BaseFragmentInterface) fragment).requestFocusFragment();
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

    public void showAddFileDialog() {
        EnterTextDialogFragment fragment = EnterTextDialogFragment.newInstance("New File",
                "Enter file name",
                KEY_FILE);
        fragment.show(getSupportFragmentManager(), EnterTextDialogFragment.TAG);
    }

    public void showAddFolderDialog() {
        EnterTextDialogFragment fragment = EnterTextDialogFragment.newInstance("New Folder",
                "Enter folder name",
                KEY_FOLDER);
        fragment.show(getSupportFragmentManager(), EnterTextDialogFragment.TAG);
    }

    @Override
    public void onAddFileSelected() {
        showAddFileDialog();
    }

    @Override
    public void onAddFolderSelected() {
        showAddFolderDialog();
    }

    public Fragment getCurSelectedPage() {
        int curID = mViewHolder.mPager.getCurrentItem();
        return getSupportFragmentManager()
                .findFragmentByTag(ViewUtils.getViewPagerTag(R.id.pager, curID));
    }

    @Nullable
    public Fragment getCurFragmentInHostIfExist() {
        Fragment hostFragment = getCurSelectedPage();

        if (hostFragment instanceof HostFragment) {
            return ((HostFragment) hostFragment).getCurPage();
        } else {
            return null;
        }
    }

    @Override
    public void onSubmit(String key, String content) {
        Fragment fragment = getCurFragmentInHostIfExist();

        if (fragment instanceof ExplorerBaseFragment) {
            if (key.equals(KEY_FILE)) {
                ((ExplorerBaseFragment) fragment).createFile(content);
            } else if (key.equals(KEY_FOLDER)) {
                ((ExplorerBaseFragment) fragment).createFolder(content);
            }
        }
    }

    @Override
    public void onCancel(String key, String content) {
        // Nothing to do
    }

    @Override
    public void onOpenDirectory(final String path) {
        mViewHolder.mDrawerLayout.closeDrawers();

        // In current script, we must to clear all fragment explorer in backstack.
        clearCurrentExplorerBackstack();

        // Open folder after cleared backstack
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getCurFragmentInHostIfExist();
                if (fragment != null && fragment instanceof ExplorerBaseFragment) {
                    ((ExplorerBaseFragment) fragment).openFolder(path);
                }
            }
        });
    }

    private void clearCurrentExplorerBackstack() {
        Fragment fragment = getCurFragmentInHostIfExist();

        if (fragment != null && fragment instanceof ExplorerBaseFragment) {
            final FragmentManager fm = fragment.getFragmentManager();
//            for (int i = fm.getBackStackEntryCount();i >= 0;i--) {
//                fm.popBack
//            }
            fm.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private class PageAdapter extends FragmentPagerAdapter {

        static final int INDEX_LOCAL_FILE_FRAGMENT = 0;
//        static final int INDEX_PREF_FRAGMENT = 1;
        static final int INDEX_OPERATOR_FRAGMENT = 1;

        PageAdapter(FragmentManager fm) {
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
//                case INDEX_PREF_FRAGMENT:
//                    fragment = new PreferenceFragment();
//                    break;
                case INDEX_OPERATOR_FRAGMENT:
                    fragment = OperatorManagerFragment.newInstance();
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
//                case INDEX_PREF_FRAGMENT:
//                    return "PREFERENCE";
                default:
                    return null;
            }
        }
    }

    class ViewHolder {
        ListFileFragment mFragmentListFile;
        @BindView(R.id.drawer_layout)
        DrawerLayout mDrawerLayout;
        ActionBarDrawerToggle mDrawerToggle;
        @BindView(R.id.drawer_menu)
        RecyclerView mDrawerMenuList;
        DrawerMenuAdapter mDrawerMenuAdapter;
        @BindView(R.id.toolbar)
        Toolbar mToolbar;
        @BindView(R.id.appbar_tab)
        TabLayout mTab;
        @BindView(R.id.pager)
        ViewPager mPager;
        PageAdapter mAdapter;
        @BindView(R.id.btn_add)
        FloatingActionButton mBtnAddFile;
        @BindView(R.id.adView)
        AdView mBottomAdView;

        @OnClick(R.id.btn_setting)
        void onSettingClick() {
            mDrawerLayout.closeDrawers();
//            PreferenceFragment fragment = (PreferenceFragment) getSupportFragmentManager()
//                    .findFragmentByTag(PreferenceFragment.TAG);
//
//            if (fragment == null) {
//                fragment = new PreferenceFragment();
//
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.rootview, fragment, PreferenceFragment.TAG)
//                        .addToBackStack(null)
//                        .commit();
//            }
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        }

        @OnClick(R.id.img_logo)
        void onAboutClick() {
            AboutDialogFragment dialog = new AboutDialogFragment();
            dialog.show(getSupportFragmentManager(), AboutDialogFragment.TAG);
        }
    }
}
