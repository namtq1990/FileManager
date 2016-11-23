package com.tqnam.filemanager.explorer.fragment;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quangnam.baseframework.BaseActivity;
import com.quangnam.baseframework.BaseFragment;
import com.quangnam.baseframework.BaseFragmentInterface;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.ExplorerPresenter;
import com.tqnam.filemanager.explorer.adapter.ExplorerItemAdapter;
import com.tqnam.filemanager.explorer.dialog.AlertDialogFragment;
import com.tqnam.filemanager.explorer.dialog.DialogRenameFragment;
import com.tqnam.filemanager.explorer.dialog.InformationDialogFragment;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.explorer.fileExplorer.ListFileFragment;
import com.tqnam.filemanager.model.CopyFileOperator;
import com.tqnam.filemanager.model.DeleteOperator;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.ItemInformation;
import com.tqnam.filemanager.model.Operator;
import com.tqnam.filemanager.utils.FileUtil;
import com.tqnam.filemanager.utils.SparseBooleanArrayParcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quangnam on 11/12/15.
 * Base fragment for explorer view, may be file explorer, ftp explorer, ...
 */
public abstract class ExplorerBaseFragment extends BaseFragment implements ExplorerPresenter.View,
        MenuItemCompat.OnActionExpandListener, ExplorerItemAdapter.ExplorerItemAdapterListener,
        BaseActivity.OnBackPressedListener,
        DialogRenameFragment.RenameDialogListener, BaseActivity.OnFocusFragmentChanged,
        AlertDialogFragment.AlertDialogListener
{
    public static final String ARG_QUERY = "query";
    public static final String ARG_ROOT_PATH = "root_path";
    public static final String ARG_PRESENTER = "presenter";
    public static final int ACTION_DELETE_VALIDATE = 0;
    public static final int ACTION_COPY_VALIDATE = 1;
    public static final int ACTION_MOVE_VALIDATE = 2;
    private static final String ARG_SELECTED_LIST = "selected_list";
    private static final String ARG_QUICK_QUERY = "query_text";
    private static final String ARG_HASHCODE = "hashcode";
    //    private Animator               mOpenAnimType;
    protected ExplorerPresenter mPresenter;
    private boolean mIsShownMenu;
    private FragmentDataStorage mDataFragment;
    private ViewHolder mViewHolder;
    private Parcelable mSelectedList;
    private ActionMode.Callback mActionCallback        = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.action_select_item, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mViewHolder.mActionMode = mode;
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            List<ItemExplorer> selectedList = mViewHolder.mAdapter.getSelectedList();

            switch (item.getItemId()) {
                case R.id.action_property: {
                    ItemInformation information = new ItemInformation(
                            selectedList.toArray(new ItemExplorer[selectedList.size()])
                    );
                    InformationDialogFragment fragment = InformationDialogFragment.newInstance(information);
                    fragment.show(getFragmentManager(), InformationDialogFragment.TAG);
                    break;
                }
                case R.id.action_del: {
                    String message = "Do you want to remove these files?"
                            + "\n"
                            + FileUtil.formatListTitle(selectedList);
                    AlertDialogFragment.newInstance(ACTION_DELETE_VALIDATE,
                            message)
                            .show(getChildFragmentManager(), AlertDialogFragment.TAG);

                    break;
                }
                case R.id.action_copy: {
                    mPresenter.saveClipboard(new ArrayList<>(selectedList));
                    getActivity().supportInvalidateOptionsMenu();
                    hideContextMenu();

                    break;
                }
                default:
                    break;
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mViewHolder.mActionMode = null;
            mViewHolder.mAdapter.setEnableMultiSelect(false);
        }
    };

    public ExplorerBaseFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mIsShownMenu = true;

        mPresenter = getPresenter();
        initializeData(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);
        initializeView(rootView, savedInstanceState);

        return rootView;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);

        if (childFragment instanceof DialogRenameFragment) {
            ((DialogRenameFragment) childFragment).setListener(this);
        } else if (childFragment instanceof AlertDialogFragment) {
            ((AlertDialogFragment) childFragment).setListener(this);
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
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unbind(this);
    }

    @Override
    public void onDestroyView() {
        ((BaseActivity) getActivity()).removeFocusListener(this);

        if (mViewHolder.mSearchView != null) {
            mViewHolder.mSearchView.setOnQueryTextFocusChangeListener(null);
            mViewHolder.mSearchView.setOnQueryTextListener(null);
        }

        super.onDestroyView();
    }

    /**
     * Init data for fragment
     *
     * @param savedInstanceState saved state of fragment
     */
    private void initializeData(Bundle savedInstanceState) {
        BaseActivity activity = (BaseActivity) getActivity();
        mDataFragment = (FragmentDataStorage) activity.getDataFragment();

        if (savedInstanceState == null) {
            String homePath = getRootPath();

            if (mPresenter.getOpenOption() == ExplorerPresenter.OpenOption.EXPLORER) {
                mPresenter.openDirectory(new FileItem(homePath));
            } else if (mPresenter.getOpenOption() == ExplorerPresenter.OpenOption.SEARCH) {
                mPresenter.setCurLocation(homePath);
                mPresenter.queryFile(homePath, getQuery());
            }
        } else {
            mPresenter.onRestoreInstanceState(savedInstanceState);
            mSelectedList = savedInstanceState.getParcelable(ARG_SELECTED_LIST);
        }
    }

    /**
     * Setup UI for fragment
     */
    private void initializeView(View rootView, Bundle savedState) {
        mViewHolder = new ViewHolder();
        ((BaseActivity) getActivity()).addFocusListener(this);
        mViewHolder.mAdapter = new ExplorerItemAdapter(rootView.getContext(), mPresenter);

        GridLayoutManager layoutManager = new GridLayoutManager(rootView.getContext(), 2);

        mViewHolder.mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        mViewHolder.mRefreshLayout.setColorSchemeResources(R.color.green);
        mViewHolder.mList = (RecyclerView) rootView.findViewById(R.id.grid_view_list);
        mViewHolder.mList.setAdapter(mViewHolder.mAdapter);
        mViewHolder.mList.setLayoutManager(layoutManager);
        mViewHolder.mList.setHasFixedSize(true);

        mViewHolder.mAdapter.setListener(this);
        if (mSelectedList != null)
            mViewHolder.mAdapter.setSelectedList((SparseBooleanArrayParcelable) mSelectedList);

        mViewHolder.mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.reload();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String presenterTag = getDataTag(hashCode(), ARG_PRESENTER);
        getArguments().putInt(ARG_HASHCODE, hashCode());
        mDataFragment.getOtherData().put(presenterTag, mPresenter);

        mPresenter.onSaveInstanceState(outState);
        if (mViewHolder != null) {
            mSelectedList = mViewHolder.mAdapter.getSelectedItem();
        }
        if (mSelectedList != null) {
            outState.putParcelable(ARG_SELECTED_LIST, mSelectedList);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_explorer, menu);
        addActionSearch(menu);
        addActionPaste(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void addActionPaste(Menu menu) {
        menu.findItem(R.id.action_paste).setVisible(mPresenter.getClipboard() != null);
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

                mPresenter.quickQueryFile(query);
                setQuickQuery(query);

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
            case R.id.action_paste:
                List<ItemExplorer> listSelected = mPresenter.getClipboard();
                CopyFileOperator operator = (CopyFileOperator) mPresenter.copyCurFolderOperator(listSelected);
                mPresenter.saveClipboard(null);

                doValidate(operator);

                item.setVisible(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    private Animator animActionOpenUp() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
//                && getView() != null) {
//            View rootView = getView();
//            Context context = rootView.getContext();
//            int startColor = ContextCompat.getColor(context, R.color.accent_material_dark);
//            int endColor = ContextCompat.getColor(context, R.color.white);
//            int width = rootView.getWidth();
//            int height = rootView.getHeight();
//
//            int startRadius = (int) Math.sqrt(width * width + height * height);
//            AnimatorSet set = new AnimatorSet();
//            Animator anim = ViewAnimationUtils.createCircularReveal(rootView, width, height, startRadius, 0);
//            ObjectAnimator colorAnim = ObjectAnimator.ofObject(rootView, "backgroundColor",
//                    new ArgbEvaluator(), startColor, endColor).setDuration(anim.getDuration());
//            set.play(anim).with(colorAnim);
//            rootView.setVisibility(View.VISIBLE);
//
//            return set;
//        } else {
//            return null;
//        }
//    }
//
//    private Animator animActionOpenIn(View itemView) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
//                && getView() != null) {
//            final View rootView = getView();
//            // TODO need iconview to be child directly of itemView
//            View iconView = itemView.findViewById(R.id.icon_item);
//            int left = itemView.getLeft() + iconView.getLeft();
//            int right = itemView.getLeft() + iconView.getRight();
//            int top = itemView.getTop() + iconView.getTop();
//            int bottom = itemView.getTop() + iconView.getBottom();
//            Context context = rootView.getContext();
//            rootView.setVisibility(View.INVISIBLE);
//            int centerX = (left + right) / 2;
//            int centerY = (top + bottom) / 2;
//            int width = rootView.getWidth();
//            int height = rootView.getHeight();
//            int startRadius = 0;
//            int startColor = ContextCompat.getColor(context, R.color.accent_material_dark);
//            final int endColor = ContextCompat.getColor(context, R.color.white);
//
//            int finalRadius = (int) (Math.sqrt(width * width
//                    + height * height));
//            AnimatorSet set = new AnimatorSet();
//            Animator anim = ViewAnimationUtils.createCircularReveal(rootView, centerX, centerY,
//                    startRadius, finalRadius);
//            ObjectAnimator colorAnim = ObjectAnimator.ofObject(rootView, "backgroundColor",
//                    new ArgbEvaluator(), startColor, endColor).setDuration(anim.getDuration());
//            set.play(anim).with(colorAnim);
//            rootView.setVisibility(View.VISIBLE);
//
//            return set;
//        } else {
//            return null;
//        }
//    }

    public boolean onBackPressed() {
//        mOpenAnimType = animActionOpenUp();

        if (isCloseFragment()) {
            getFragmentManager().popBackStack();
        } else {
            mPresenter.onBackPressed();
        }

        return true;
    }

    protected boolean isCloseFragment() {
        return mPresenter.getOpenOption() == ExplorerPresenter.OpenOption.SEARCH
                || mPresenter.getCurLocation() == null
                || mPresenter.getCurLocation().equals(getRootPath());
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
            ((ExplorerBaseFunction) getActivity()).showAddButton();
            mIsShownMenu = true;
            getActivity().supportInvalidateOptionsMenu();
        } else {
            ((ExplorerBaseFunction) getActivity()).hideAddButton();
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
        mPresenter.renameItem(item, label);
    }

    @Override
    public void showContextMenu() {
        ((AppCompatActivity) getActivity()).startSupportActionMode(mActionCallback);
    }

    @Override
    public void hideContextMenu() {
        if (mViewHolder.mActionMode != null) {
            mViewHolder.mActionMode.finish();
        }
    }

    @Override
    public void onDialogClick(DialogInterface dialog, int which, int action, Bundle args) {
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            mViewHolder.mAdapter.setEnableMultiSelect(false);
            return;
        }

        List<ItemExplorer> selectedList = mViewHolder.mAdapter.getSelectedList();

        switch (action) {
            case ACTION_DELETE_VALIDATE: {
                DeleteOperator operation = (DeleteOperator) mPresenter.deleteOperator(selectedList);
                doValidate(operation);
                hideContextMenu();
                break;
            }
            case ACTION_COPY_VALIDATE:
            case ACTION_MOVE_VALIDATE:
                break;
            default:
                break;
        }
    }

    public void doValidate(Operator operation) {
        //TODO Show dialog to validate action
        mPresenter.setValidated(operation);
    }

    @Override
    public void replaceExplorerAtItem(ItemExplorer root) {
        ListFileFragment fragment = ListFileFragment.newInstance(root.getPath());
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, ListFileFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openPreview(ItemExplorer item) {
        int fileType = item.getFileType();

        PreviewFragment previewFragment = (PreviewFragment) getFragmentManager()
                .findFragmentByTag(PreviewFragment.TAG);

        switch (fileType) {
            case ItemExplorer.FILE_TYPE_IMAGE:

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

    @Override
    public void showLoading(boolean isLoading) {
        if (mViewHolder != null && mViewHolder.mRefreshLayout.isRefreshing() != isLoading) {
            mViewHolder.mRefreshLayout.setRefreshing(isLoading);
        }
    }

    protected String getQuickQuery() {
        return mDataFragment.getData().containsKey(ARG_QUICK_QUERY)
                ? mDataFragment.getData().getString(ARG_QUICK_QUERY) : "";
    }

    protected void setQuickQuery(String query) {
        mDataFragment.getData().putString(ARG_QUICK_QUERY, query);
    }

    public void openFolder(String path) {
        mPresenter.openDirectory(new FileItem(path));
    }

    @Override
    public String getQuery() {
        return getArguments().getString(ARG_QUERY);
    }

    @Override
    public void setQuery(String query) {
        getArguments().putString(ARG_QUERY, query);
    }

    public int getSavedHashcode() {
        return getArguments().getInt(ARG_HASHCODE, hashCode());
    }

    public String getDataTag(int hashcode, String dataTag) {
        return getTag() + "_" + hashcode + "_" + dataTag;
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
        mPresenter.createFile(name);
    }

//    public void setOpenOption(ExplorerPresenter.OpenOption openOption) {
//        mPresenter.setOpenOption(openOption);
//    }

    public void createFolder(String name) {
        mPresenter.createFolder(name);
    }

    @Override
    public void refreshView() {
        if (mViewHolder != null
                && mViewHolder.mAdapter != null)
            mViewHolder.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivitySafe(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(int message) {
        Toast.makeText(getActivitySafe(), getAppContext().getString(message), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(int message) {
        Toast.makeText(getActivitySafe(), getAppContext().getString(message), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivitySafe(), message, Toast.LENGTH_SHORT).show();
    }

    public interface ExplorerBaseFunction {
        void showAddButton();
        void hideAddButton();
    }

    private class ViewHolder {
        SwipeRefreshLayout  mRefreshLayout;
        ExplorerItemAdapter mAdapter;
        RecyclerView        mList;
        MenuItem            mSearchMenu;
        SearchView          mSearchView;
        ActionMode          mActionMode;
    }
}
