package com.tqnam.filemanager.explorer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import com.quangnam.baseframework.exception.SystemException;
import com.tqnam.filemanager.Common;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.model.ErrorCode;
import com.tqnam.filemanager.model.ItemExplorer;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by quangnam on 11/12/15.
 * Base fragment for explorer view, may be file explorer, ftp explorer, ...
 */
public abstract class ExplorerBaseFragment extends BaseFragment implements ExplorerView,
        MenuItemCompat.OnActionExpandListener, ExplorerItemAdapter.OpenRenameDialogListnener,
        ExplorerItemAdapter.OnOpenItemActionListener, BaseActivity.OnBackPressedListener,
        DialogRenameFragment.RenameDialogListener
{
    private static final String ARG_QUERIED_TEXT = "query_text";

    //    private Animator               mOpenAnimType;

    private ExplorerPresenter   mPresenter;
    private FragmentDataStorage mDataFragment;
    private ViewHolder mViewHolder = new ViewHolder();

    private Action1<ItemExplorer> mActionOpen = new Action1<ItemExplorer>() {
        @Override
        public void call(ItemExplorer item) {
            onOpenItem(item);
        }
    };
    private Action1<Throwable> mActionError = new Action1<Throwable>() {
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

    protected abstract ExplorerPresenter genPresenter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDataFragment = (FragmentDataStorage) getFragmentManager().findFragmentByTag(FragmentDataStorage.TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_item_list, container, false);

        initializeData(savedInstanceState);
        initializeView(root);

        return root;
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
        BaseActivity activity = (BaseActivity) getActivity();

        if (activity.getFocusFragment() == null) {
            requestFocus();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewHolder = null;
    }

    /**
     * Init data for fragment
     *
     * @param savedInstanceState saved state of fragment
     */
    private void initializeData(Bundle savedInstanceState) {
//        ExplorerModel model = genModel();
        mPresenter = genPresenter();
        BaseActivity activity = (BaseActivity) getActivity();

        if (savedInstanceState != null) {
            mPresenter.onRestoreInstanceState(savedInstanceState);
        } else {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getAppContext());
            String key_pref_homepath = getAppContext().getString(R.string.pref_local_home_path);
            String homePath = PreferenceManager.getDefaultSharedPreferences(getAppContext())
                    .getString(key_pref_homepath, null);
            if (homePath == null) {
                homePath = "/";
                pref.edit().putString(key_pref_homepath, homePath)
                        .apply();
            }

            Observable<ItemExplorer> observable = mPresenter.openDirectory(new FileItem(homePath)).cache();
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
    private void initializeView(View rootView) {
        mViewHolder.mAdapter = new ExplorerItemAdapter(rootView.getContext(), mPresenter);

        GridLayoutManager layoutManager = new GridLayoutManager(rootView.getContext(), 2);

        mViewHolder.mList = (RecyclerView) rootView.findViewById(R.id.grid_view_list);
        mViewHolder.mList.setAdapter(mViewHolder.mAdapter);
        mViewHolder.mList.setLayoutManager(layoutManager);
        mViewHolder.mList.setHasFixedSize(true);

        mViewHolder.mAdapter.setRenameListener(this);
        mViewHolder.mAdapter.setOpenItemListener(this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveInstanceState(outState);
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String query) {
                String oldQuery = getQueryText();
                if (oldQuery.equals(query)) {
                    return false;
                }

                Observable<Void> observable = mPresenter.quickQueryFile(query);
                observable.subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mViewHolder.mAdapter.notifyDataSetChanged();
                        setQueryText(query);
                    }
                }, mActionError);

                return false;
            }
        });


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

    @Override
    public void requestFocus() {
        super.requestFocus();
        ((ExplorerBaseFunction) getActivity()).showAddButton();
    }

    @Override
    public void clearFocus() {
        super.clearFocus();
        ((ExplorerBaseFunction) getActivity()).hideAddButton();
    }

    public void onBackPressed() {
//        mOpenAnimType = animActionOpenUp();
        Observable<ItemExplorer> observable = mPresenter.onBackPressed();
        if (observable != null) {
            // Can back-able so back to parent folder
            BaseActivity activity = (BaseActivity) getActivity();
            activity.getLocalSubscription().add(observable.subscribe(mActionOpen,
                    mActionError));
        } else {
            //TODO implement function to finish app
        }
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
            mViewHolder.mAdapter.notifyDataSetChanged();
            return;
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

                    getFragmentManager().beginTransaction()
                            .add(R.id.rootview, previewFragment, PreviewFragment.TAG)
                            .commit();
                }

                previewFragment.setItem(item);
                previewFragment.loadPreview();
                break;
        }
    }

    protected String getQueryText() {
        return mDataFragment.getData().containsKey(ARG_QUERIED_TEXT)
                ? mDataFragment.getData().getString(ARG_QUERIED_TEXT) : "";
    }

    protected void setQueryText(String query) {
        mDataFragment.getData().putString(ARG_QUERIED_TEXT, query);
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
