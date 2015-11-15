package com.tqnam.filemanager.explorer;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tqnam.filemanager.Application;
import com.tqnam.filemanager.BaseFragment;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.model.ExplorerModel;
import com.tqnam.filemanager.model.ItemExplorer;

import java.util.ArrayList;

/**
 * Created by quangnam on 11/12/15.
 * Base fragment for explorer view, may be file explorer, ftp explorer, ...
 */
public abstract class ExplorerBaseFragment extends BaseFragment implements ExplorerView {

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
        mViewHolder.mAdapter = new ExplorerItemAdapter(getActivity(), R.layout.item_file, mPresenter.getCurList());

        mViewHolder.mList = (GridView) rootView.findViewById(R.id.grid_view_list);
        mViewHolder.mList.setAdapter(mViewHolder.mAdapter);

        mViewHolder.mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mPresenter.openDirectory(mViewHolder.mAdapter.getItem(position));
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Activity activity = getActivity();
        inflater.inflate(R.menu.menu_explorer, menu);
        SearchManager searchManager = (SearchManager) activity
                .getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        LinearLayout searchBar = (LinearLayout) searchView.findViewById(R.id.search_bar);
//        LayoutTransition slideTransition = new LayoutTransition();
//        slideTransition.setAnimator(LayoutTransition.APPEARING, );

        searchView.post(new Runnable() {
            @Override
            public void run() {
                System.out.println(searchView.getWidth());
            }
        });

        searchBar.setLayoutTransition(new LayoutTransition());
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                try {
                    AppCompatActivity activity = (AppCompatActivity) getActivity();
                    activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    return true;
                }
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                try {
                    AppCompatActivity activity = (AppCompatActivity) getActivity();
                    activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    return true;
                }
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
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
    public void updateList(ArrayList<? extends ItemExplorer> listItem) {
        if (mViewHolder.mAdapter != null)
            mViewHolder.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onErrorPermission() {
        Activity curActivity = getActivity() != null ? getActivity() : Application.getInstance()
                .getGlobalData().getCurActivity();
        Toast.makeText(curActivity, curActivity.getString(R.string.explorer_err_permission), Toast.LENGTH_LONG)
                .show();
    }

    private class ViewHolder {
        ExplorerItemAdapter mAdapter;
        GridView mList;
    }


}
