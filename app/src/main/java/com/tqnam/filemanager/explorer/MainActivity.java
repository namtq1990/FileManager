package com.tqnam.filemanager.explorer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.quangnam.baseframework.BaseActivity;
import com.quangnam.baseframework.BaseDataFragment;
import com.quangnam.baseframework.BaseFragmentInterface;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.fileExplorer.ListFileFragment;
import com.tqnam.filemanager.preference.PreferenceFragment;
import com.tqnam.filemanager.utils.ViewUtils;

import java.util.ArrayList;

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
    private ArrayList<String> mShortcutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(savedInstanceState);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mViewHolder.mDrawerToggle.syncState();
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
        initData();

        mViewHolder = new ViewHolder();

        mViewHolder.mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewHolder.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        initDrawerMenu();

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
        mViewHolder.mDrawerMenuList = (RecyclerView) findViewById(R.id.drawer_menu);
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

    private void initData() {
        mDataFragment = (FragmentDataStorage) getSupportFragmentManager()
                .findFragmentByTag(FragmentDataStorage.TAG);

        if (mDataFragment == null) {
            mDataFragment = new FragmentDataStorage();
            getSupportFragmentManager().beginTransaction()
                    .add(mDataFragment, FragmentDataStorage.TAG)
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
        DrawerLayout mDrawerLayout;
        ActionBarDrawerToggle mDrawerToggle;
        RecyclerView mDrawerMenuList;
        DrawerMenuAdapter mDrawerMenuAdapter;
        Toolbar mToolbar;
        TabLayout mTab;
        ViewPager mPager;
        PageAdapter mAdapter;
        FloatingActionButton mBtnAddFile;
    }
}
