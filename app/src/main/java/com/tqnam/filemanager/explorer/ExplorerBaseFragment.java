package com.tqnam.filemanager.explorer;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tqnam.filemanager.BaseFragment;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.model.ExplorerModel;

/**
 * Created by quangnam on 11/12/15.
 * Base fragment for explorer view, may be file explorer, ftp explorer, ...
 */
public abstract class ExplorerBaseFragment extends BaseFragment implements ExplorerView,
        MenuItemCompat.OnActionExpandListener, ExplorerItemAdapter.OnRenameActionListener,
        ExplorerItemAdapter.OnOpenItemActionListener {

    private ActionMode        mActionMode;
    private ExplorerPresenter mPresenter;
    private ViewHolder mViewHolder = new ViewHolder();

    protected abstract ExplorerModel genModel();

    protected abstract ExplorerPresenter genPresenter(ExplorerModel model);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_item_list, container, false);

        initData(savedInstanceState);
        initView(root);

        return root;
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
    private void initData(Bundle savedInstanceState) {
        ExplorerModel model = genModel();
        mPresenter = genPresenter(model);

        if (savedInstanceState != null) {
            mPresenter.onRestoreInstanceState(savedInstanceState);
        } else {
            mPresenter.openDirectory(new FileItem("/"));
        }
    }

    /**
     * Setup UI for fragment
     */
    private void initView(View rootView) {
        mViewHolder.mAdapter = new ExplorerItemAdapter(mPresenter);

        mViewHolder.mList = (RecyclerView) rootView.findViewById(R.id.grid_view_list);
        mViewHolder.mList.setAdapter(mViewHolder.mAdapter);
        mViewHolder.mList.setLayoutManager(new GridLayoutManager(rootView.getContext(), 2));
        mViewHolder.mList.setHasFixedSize(true);

        mViewHolder.mAdapter.setRenameListener(this);
        mViewHolder.mAdapter.setOpenItemListener(this);
//        mViewHolder.mList.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    Activity context = getActivitySafe();
//                    View curFocus = context.getCurrentFocus();
//
//                    if (curFocus != null) {
//                        MenuItemCompat.collapseActionView(mViewHolder.mSearchMenu);
//
//                        if (curFocus.getId() == R.id.title_item) {
//                            EditText et = (EditText) context.getCurrentFocus();
//                            mViewHolder.mAdapter.updateUI(et, ExplorerItemAdapter.STATE_NORMAL);
//                            return true;
//                        }
//                    }
//                }
//
//                return false;
//            }
//        });

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

    public void onBackPressed() {
        mPresenter.onBackPressed();
    }

    @Override
    public void onRenameAction(String label, int position) {
        DialogRenameFragment dialog = new DialogRenameFragment();
        Bundle args = new Bundle();
        args.putString(DialogRenameFragment.ARG_LABEL, label);

        dialog.setArguments(args);
        dialog.show(getFragmentManager(), DialogRenameFragment.TAG);
    }

    @Override
    public void onOpenAction(int position) {
        mPresenter.openDirectory(mPresenter.getItemAt(position));
    }

    @Override
    public void updateList() {
        if (mViewHolder.mAdapter != null)
            mViewHolder.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onErrorPermission() {
        Activity curActivity = getActivitySafe();
        Toast.makeText(curActivity, curActivity.getString(R.string.explorer_err_permission), Toast.LENGTH_LONG)
                .show();
    }

    private class ViewHolder {
        ExplorerItemAdapter mAdapter;
        RecyclerView        mList;
        MenuItem            mSearchMenu;
        SearchView          mSearchView;
    }


}
