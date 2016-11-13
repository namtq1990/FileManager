package com.tqnam.filemanager.explorer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quangnam.baseframework.BaseActivity;
import com.quangnam.baseframework.BaseFragment;
import com.quangnam.baseframework.BaseFragmentInterface;
import com.quangnam.baseframework.exception.SystemException;
import com.tqnam.filemanager.Common;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.explorer.fileExplorer.ListFileFragment;
import com.tqnam.filemanager.model.ErrorCode;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.ItemInformation;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by quangnam on 11/12/15.
 * Base fragment for explorer view, may be file explorer, ftp explorer, ...
 */
public abstract class ExplorerBaseFragment extends BaseFragment implements ExplorerView,
        MenuItemCompat.OnActionExpandListener, ExplorerItemAdapter.ExplorerItemAdapterListener,
        BaseActivity.OnBackPressedListener,
        DialogRenameFragment.RenameDialogListener, BaseActivity.OnFocusFragmentChanged
{
    public static final String ARG_QUERY = "query";
    public static final String ARG_ROOT_PATH = "root_path";
    private static final String ARG_QUICK_QUERY = "query_text";
    //    private Animator               mOpenAnimType;
    protected Action1<Throwable> mActionError = new Action1<Throwable>() {
        @Override
        public void call(Throwable e) {
            SystemException exception;
            if (!(e instanceof SystemException)) {
                exception = new SystemException(ErrorCode.RK_UNKNOWN, "", e);
            } else {
                exception = (SystemException) e;
            }

            exception.printStackTrace();
            int errcode = exception.mErrorcode;
            Activity curActivity = getActivitySafe();

            switch (errcode) {
                case ErrorCode.RK_EXPLORER_OPEN_ERROR:
                    Toast.makeText(curActivity, curActivity.getString(R.string.explorer_err_permission), Toast.LENGTH_LONG)
                            .show();
                    break;
                case ErrorCode.RK_RENAME_ERR:
                    Toast.makeText(curActivity, "Cann't rename file, check permission", Toast.LENGTH_LONG)
                            .show();
                    break;
                case ErrorCode.RK_EXPLORER_OPEN_NOTHING:
                case ErrorCode.RK_EXPLORER_OPEN_WRONG_FUNCTION:
                    Common.Log("Calling wrong function");
                case ErrorCode.RK_UNKNOWN:
                    Toast.makeText(curActivity, R.string.err_unknown, Toast.LENGTH_LONG)
                            .show();
                    break;
            }
        }
    };
    private boolean mIsShownMenu;
    private ExplorerPresenter   mPresenter;
    private FragmentDataStorage mDataFragment;
    private ViewHolder mViewHolder;
    protected Action1<ItemExplorer> mActionOpen = new Action1<ItemExplorer>() {
        @Override
        public void call(ItemExplorer item) {
            onOpenItem(item);
        }
    };

    protected abstract ExplorerPresenter genPresenter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mIsShownMenu = true;
//        mDataFragment = (FragmentDataStorage) getFragmentManager().findFragmentByTag(FragmentDataStorage.TAG);

        mPresenter = genPresenter();
        if (savedInstanceState != null) {
            mPresenter.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);

        initializeData(savedInstanceState);
        initializeView(rootView, savedInstanceState);

        return rootView;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);

        if (childFragment instanceof DialogRenameFragment) {
            ((DialogRenameFragment) childFragment).setListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        BaseActivity activity = (BaseActivity) getActivity();

//        if (activity.getFocusFragment() == null) {
//            requestFocus();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        clearFocus();
    }

    @Override
    public void onDestroyView() {
        ((BaseActivity) getActivity()).removeFocusListener(this);
        mViewHolder.mSearchView.setOnQueryTextFocusChangeListener(null);
        mViewHolder.mSearchView.setOnQueryTextListener(null);
        mViewHolder = null;

        super.onDestroyView();
    }

    /**
     * Init data for fragment
     *
     * @param savedInstanceState saved state of fragment
     */
    private void initializeData(Bundle savedInstanceState) {
//        ExplorerModel model = genModel();
//        mPresenter = genPresenter();
        BaseActivity activity = (BaseActivity) getActivity();
        activity.addFocusListener(this);
        mDataFragment = (FragmentDataStorage) activity.getDataFragment();

        if (savedInstanceState == null) {
//            mPresenter.onRestoreInstanceState(savedInstanceState);
//        } else {
//            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getAppContext());
//            String key_pref_homepath = getAppContext().getString(R.string.pref_local_home_path);
//            String homePath = PreferenceManager.getDefaultSharedPreferences(getAppContext())
//                    .getString(key_pref_homepath, null);
//            if (homePath == null) {
//                homePath = "/";
//                pref.edit().putString(key_pref_homepath, homePath)
//                        .apply();
//            }
            String homePath = getRootPath();

            Observable<ItemExplorer> observable;
            if (getOpenOption() == ExplorerPresenter.OpenOption.EXPLORER) {
                observable = mPresenter.openDirectory(new FileItem(homePath)).cache();
            } else {
                mPresenter.setCurLocation(homePath);
                observable = mPresenter.queryFile(homePath, getQuery())
                        .map(new Func1<List<ItemExplorer>, ItemExplorer>() {
                            @Override
                            public ItemExplorer call(List<ItemExplorer> list) {
                                return getPresenter().getCurFolder();
                            }
                        })
                        .cache();
            }
            Subscription subscription = observable
                    .subscribe(new Action1<ItemExplorer>() {
                        @Override
                        public void call(ItemExplorer itemExplorer) {
                            // Init function, so don't do anything here
                        }
                    }, mActionError);

            activity.getLocalSubscription().add(subscription);
        }
    }

    /**
     * Setup UI for fragment
     */
    private void initializeView(View rootView, Bundle savedState) {
        mViewHolder = new ViewHolder();
        mViewHolder.mAdapter = new ExplorerItemAdapter(rootView.getContext(), mPresenter);

        GridLayoutManager layoutManager = new GridLayoutManager(rootView.getContext(), 2);

        mViewHolder.mList = (RecyclerView) rootView.findViewById(R.id.grid_view_list);
        mViewHolder.mList.setAdapter(mViewHolder.mAdapter);
        mViewHolder.mList.setLayoutManager(layoutManager);
        mViewHolder.mList.setHasFixedSize(true);

        mViewHolder.mAdapter.setListener(this);

        if (savedState != null) {
            mViewHolder.mAdapter.onRestoreInstanceState(savedState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveInstanceState(outState);
        mViewHolder.mAdapter.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_explorer, menu);
        addActionSearch(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void addActionSearch(Menu menu) {
        Activity activity = getActivitySafe();
        SearchManager searchManager = (SearchManager) activity
                .getSystemService(Context.SEARCH_SERVICE);
        mViewHolder.mSearchMenu = menu.findItem(R.id.action_search);
        mViewHolder.mSearchView = (SearchView) mViewHolder.mSearchMenu.getActionView();
        LinearLayout searchBar = (LinearLayout) mViewHolder.mSearchView
                .findViewById(R.id.search_bar);
        MenuItemCompat.setOnActionExpandListener(mViewHolder.mSearchMenu, this);
        mViewHolder.mSearchView
                .setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));

        mViewHolder.mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (!isFocused) {
                    mViewHolder.mSearchMenu.collapseActionView();
                }
            }
        });
        mViewHolder.mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    onQueryTextChange("");  // Reset displaylist before execute
                    onQueryFile(query);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(final String query) {
                String oldQuery = getQuickQuery();
                if (oldQuery.equals(query)) {
                    return false;
                }

                Observable<List<ItemExplorer>> observable = mPresenter.quickQueryFile(query);
                observable.subscribe(new Action1<List<ItemExplorer>>() {
                    @Override
                    public void call(List<ItemExplorer> aVoid) {
                        refreshView();
                        setQuickQuery(query);
                    }
                }, mActionError);

                return false;
            }
        });

        mViewHolder.mSearchMenu.setVisible(mIsShownMenu);
        //region Add animation to search field, consider default fadeIn and translate
        // -----------------------------------------------------------------------------------------

        LayoutTransition searchBarTransition = new LayoutTransition();

        //If use translation animation
//        int curWidth = getResources().getDisplayMetrics().widthPixels;
//        ObjectAnimator animSlide = ObjectAnimator.ofFloat(searchBar, "translationX", curWidth, 0);
//        searchBarTransition.setAnimator(LayoutTransition.APPEARING, animSlide);
//        searchBarTransition.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
//        searchBarTransition.setStartDelay(LayoutTransition.APPEARING, 0);
        searchBar.setLayoutTransition(searchBarTransition);
        // -----------------------------------------------------------------------------------------
        //endregion

    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        AppCompatActivity activity = (AppCompatActivity) getActivitySafe();
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setDisplayShowHomeEnabled(false);

        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        AppCompatActivity activity = (AppCompatActivity) getActivitySafe();
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void requestFocus() {
//        BaseActivity activity = (BaseActivity) getActivity();
//        activity.requestFocusFragment(this);
        requestFocusFragment((BaseActivity) getActivity());
        ((ExplorerBaseFunction) getActivity()).showAddButton();
    }

    public void clearFocus() {
//        BaseActivity activity = (BaseActivity) getActivity();
//        activity.removeFocusRequest(this);
        removeFocusRequest((BaseActivity) getActivity());
        ((ExplorerBaseFunction) getActivity()).hideAddButton();
    }

    public boolean onBackPressed() {
//        mOpenAnimType = animActionOpenUp();

        if (isCloseFragment()) {
            getFragmentManager().popBackStack();
        } else {
            Observable<ItemExplorer> observable = mPresenter.onBackPressed();
            if (observable != null) {
                // Can back-able so back to parent folder
                BaseActivity activity = (BaseActivity) getActivity();
                activity.getLocalSubscription().add(observable.subscribe(mActionOpen,
                        mActionError));
            }
        }

        return true;
    }

    private Animator animActionOpenUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && getView() != null) {
            View rootView = getView();
            Context context = rootView.getContext();
            int startColor = ContextCompat.getColor(context, R.color.accent_material_dark);
            int endColor = ContextCompat.getColor(context, R.color.white);
            int width = rootView.getWidth();
            int height = rootView.getHeight();

            int startRadius = (int) Math.sqrt(width * width + height * height);
            AnimatorSet set = new AnimatorSet();
            Animator anim = ViewAnimationUtils.createCircularReveal(rootView, width, height, startRadius, 0);
            ObjectAnimator colorAnim = ObjectAnimator.ofObject(rootView, "backgroundColor",
                    new ArgbEvaluator(), startColor, endColor).setDuration(anim.getDuration());
            set.play(anim).with(colorAnim);
            rootView.setVisibility(View.VISIBLE);

            return set;
        } else {
            return null;
        }
    }

    private Animator animActionOpenIn(View itemView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && getView() != null) {
            final View rootView = getView();
            // TODO need iconview to be child directly of itemView
            View iconView = itemView.findViewById(R.id.icon_item);
            int left = itemView.getLeft() + iconView.getLeft();
            int right = itemView.getLeft() + iconView.getRight();
            int top = itemView.getTop() + iconView.getTop();
            int bottom = itemView.getTop() + iconView.getBottom();
            Context context = rootView.getContext();
            rootView.setVisibility(View.INVISIBLE);
            int centerX = (left + right) / 2;
            int centerY = (top + bottom) / 2;
            int width = rootView.getWidth();
            int height = rootView.getHeight();
            int startRadius = 0;
            int startColor = ContextCompat.getColor(context, R.color.accent_material_dark);
            final int endColor = ContextCompat.getColor(context, R.color.white);

            int finalRadius = (int) (Math.sqrt(width * width
                    + height * height));
            AnimatorSet set = new AnimatorSet();
            Animator anim = ViewAnimationUtils.createCircularReveal(rootView, centerX, centerY,
                    startRadius, finalRadius);
            ObjectAnimator colorAnim = ObjectAnimator.ofObject(rootView, "backgroundColor",
                    new ArgbEvaluator(), startColor, endColor).setDuration(anim.getDuration());
            set.play(anim).with(colorAnim);
            rootView.setVisibility(View.VISIBLE);

            return set;
        } else {
            return null;
        }
    }

    protected boolean isCloseFragment() {
        if (getOpenOption() == ExplorerPresenter.OpenOption.SEARCH) {
            return true;
        } else {
            return mPresenter.getCurLocation() == null
                    || mPresenter.getCurLocation().equals(getRootPath());
        }
    }

    protected boolean isFragmentFocusing(BaseFragmentInterface curFocus) {
        if (curFocus instanceof HostFragment) {
            Fragment curPage = ((HostFragment) curFocus).getChildFragmentManager()
                    .findFragmentById(R.id.fragment_container);
            if (curPage == this)
                return true;
        }

        return false;
    }

    @Override
    public void onFocusFragmentChange(BaseFragmentInterface oldFragment, BaseFragmentInterface newFragment) {
        if (isFragmentFocusing(newFragment)) {
            ((MainActivity) getActivity()).showAddButton();
            mIsShownMenu = true;
            if (mViewHolder.mSearchMenu != null) mViewHolder.mSearchMenu.setVisible(true);
        } else {
            ((MainActivity) getActivity()).hideAddButton();
            mIsShownMenu = false;
            if (mViewHolder.mSearchView != null) mViewHolder.mSearchMenu.setVisible(false);
        }
    }

    @Override
    public void openRenameDialog(String label, int position) {
        ItemExplorer item = mPresenter.getItemDisplayedAt(position);
        DialogRenameFragment dialog = DialogRenameFragment.newInstance(item, label);
        dialog.show(getChildFragmentManager(), DialogRenameFragment.TAG);
    }

    public void onRename(ItemExplorer item, String label) {
        BaseActivity activity = (BaseActivity) getActivity();
        Observable<Void> observable = mPresenter.renameItem(item, label);
        activity.getLocalSubscription().add(observable.subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                ((BaseActivity) getActivity()).getLocalSubscription()
                        .add(mPresenter.reload().subscribe(mActionOpen, mActionError));
            }
        }, mActionError));
    }

    @Override
    public void onViewProperty(ItemExplorer[] listSelected) {
        ItemInformation information = new ItemInformation(listSelected);
        InformationDialogFragment fragment = InformationDialogFragment.newInstance(information);
        fragment.show(getFragmentManager(), InformationDialogFragment.TAG);
    }

    @Override
    public void onOpenAction(int position) {
//        mOpenAnimType = animActionOpenIn(mViewHolder.mList.findViewHolderForAdapterPosition(position).itemView);
        BaseActivity activity = (BaseActivity) getActivity();
        activity.getLocalSubscription().add(mPresenter.openItem(position)
                .subscribe(mActionOpen, mActionError));
    }

    @Override
    public void onOpenItem(ItemExplorer item) {
        int fileType = item.getFileType();

        if (fileType == ItemExplorer.FILE_TYPE_FOLDER) {
            if (getOpenOption() == ExplorerPresenter.OpenOption.EXPLORER) {
                refreshView();
                return;
            } else if (getOpenOption() == ExplorerPresenter.OpenOption.SEARCH) {
                ListFileFragment fragment = ListFileFragment.newInstance(item.getPath());
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment, ListFileFragment.TAG)
                        .addToBackStack(null)
                        .commit();

                return;
            }
        }

        PreviewFragment previewFragment = (PreviewFragment) getFragmentManager()
                .findFragmentByTag(PreviewFragment.TAG);

        switch (fileType) {
            case ItemExplorer.FILE_TYPE_IMAGE:
                Observable<Bitmap> observable = mPresenter.loadImage(item);
                mDataFragment.getObservableManager().updateLoaderObservable(observable);

//                break;
            default:

                if (previewFragment == null) {
                    previewFragment = new PreviewFragment();

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.rootview, previewFragment, PreviewFragment.TAG)
                            .commit();
                }

                previewFragment.setItem(item);
                previewFragment.loadPreview((BaseActivity) getActivity());
                break;
        }
    }

    protected String getQuickQuery() {
        return mDataFragment.getData().containsKey(ARG_QUICK_QUERY)
                ? mDataFragment.getData().getString(ARG_QUICK_QUERY) : "";
    }

    protected void setQuickQuery(String query) {
        mDataFragment.getData().putString(ARG_QUICK_QUERY, query);
    }

    public abstract void openFolder(String path);

    @Override
    public String getQuery() {
        return getArguments().getString(ARG_QUERY);
    }

    @Override
    public void setQuery(String query) {
        getArguments().putString(ARG_QUERY, query);
    }

    @Override
    public String getRootPath() {
        return getArguments().getString(ARG_ROOT_PATH);
    }

    @Override
    public void setRootPath(String path) {
        getArguments().putString(ARG_ROOT_PATH, path);
    }

    public void createFile(String name) {
        mPresenter.createFile(name).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                refreshView();
            }
        }, mActionError);
    }

    public void createFolder(String name) {
        mPresenter.createFolder(name)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        refreshView();
                    }
                }, mActionError);
    }

//    public void setOpenOption(ExplorerPresenter.OpenOption openOption) {
//        mPresenter.setOpenOption(openOption);
//    }

    public void refreshView() {
        mViewHolder.mAdapter.notifyDataSetChanged();
    }

    @Override
    public ExplorerPresenter.OpenType getOpenType() {
        return mPresenter.getOpenType();
    }

    @Override
    public void setOpenType(ExplorerPresenter.OpenType openType) {
        mPresenter.setOpenType(openType);
    }

    @Override
    public ExplorerPresenter getPresenter() {
        return mPresenter;
    }

    public ExplorerPresenter.OpenOption getOpenOption() {
        return mPresenter.getOpenOption();
    }

    public interface ExplorerBaseFunction {
        void showAddButton();

        void hideAddButton();
    }

    private class ViewHolder {
        ExplorerItemAdapter mAdapter;
        RecyclerView        mList;
        MenuItem            mSearchMenu;
        SearchView          mSearchView;
    }
}
