package com.tqnam.filemanager.explorer.fileExplorer;

import android.os.Bundle;

import com.quangnam.baseframework.BaseActivity;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.ExplorerPresenter;
import com.tqnam.filemanager.explorer.fragment.ExplorerBaseFragment;
import com.tqnam.filemanager.model.ExplorerModel;
import com.tqnam.filemanager.model.ItemExplorer;

import java.util.List;

import rx.functions.Action1;
import rx.functions.Func1;

public class ListFileFragment extends ExplorerBaseFragment {
    public static final String TAG = "ListFileFragment";

    public static ListFileFragment newInstance(String path) {
        ListFileFragment fragment = new ListFileFragment();

        Bundle bundle = new Bundle();
        if (path == null) {
            path = "/";
        }
        bundle.putString(ARG_ROOT_PATH, path);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static ListFileFragment newInstance(String path, String query) {
        ListFileFragment fragment = new ListFileFragment();

        Bundle bundle = new Bundle();
        if (path == null) {
            path = "/";
        }
        bundle.putString(ARG_ROOT_PATH, path);
        bundle.putString(ARG_QUERY, query);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void openFolder(String path) {
        getPresenter().openDirectory(new FileItem(path))
                .subscribe(mActionOpen, mActionError);
    }

    @Override
    public ExplorerPresenter getPresenter() {
        if (mPresenter == null) {
            mPresenter = new FileExplorerPresenter(this,
                    new ExplorerModel(((BaseActivity) getActivity()).getDataFragment()));
            mPresenter.setOpenType(ExplorerPresenter.OpenType.LOCAL);
        }

        return mPresenter;
    }

    @Override
    public void onQueryFile(final String query) {
        if (getOpenOption() == ExplorerPresenter.OpenOption.EXPLORER) {
            ListFileFragment fragment = ListFileFragment.newInstance(getPresenter().getCurLocation(), query);

            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, ListFileFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        } else if (getOpenOption() == ExplorerPresenter.OpenOption.SEARCH) {
            setQuery(query);
            getPresenter().queryFile(getRootPath(), query)
                    .map(new Func1<List<ItemExplorer>, ItemExplorer>() {
                        @Override
                        public ItemExplorer call(List<ItemExplorer> list) {
                            return getPresenter().getCurFolder();
                        }
                    })
                    .subscribe(new Action1<ItemExplorer>() {
                        @Override
                        public void call(ItemExplorer itemExplorer) {
                            refreshView();
                        }
                    }, mActionError);
        }
    }
}
