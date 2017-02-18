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

package com.tqnam.filemanager.explorer.fileExplorer;

import android.os.Bundle;

import com.quangnam.baseframework.BaseActivity;
import com.quangnam.baseframework.BaseDataFragment;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.ExplorerPresenter;
import com.tqnam.filemanager.explorer.fragment.ExplorerBaseFragment;
import com.tqnam.filemanager.model.ExplorerModel;

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
    public ExplorerPresenter getPresenter() {
        if (mPresenter == null) {
            BaseDataFragment dataFragment = ((BaseActivity) getActivity())
                    .getDataFragment();
            String tag = getDataTag(getSavedHashcode(), ARG_PRESENTER);

            if (dataFragment != null
                    && dataFragment.getOtherData().containsKey(tag)) {
                // Presenter saved, so restore it
                mPresenter = (ExplorerPresenter) dataFragment.getOtherData().get(tag);
                dataFragment.getOtherData().remove(tag);
            } else {
                mPresenter = new FileExplorerPresenter(
                        new ExplorerModel(((BaseActivity) getActivity()).getDataFragment()));
                mPresenter.setOpenType(ExplorerPresenter.OpenType.LOCAL);
            }

            mPresenter.bind(this);
        }

        return mPresenter;
    }

    @Override
    public void onQueryFile(final String query) {
        if (mPresenter.getOpenOption() == ExplorerPresenter.OpenOption.EXPLORER) {
            ListFileFragment fragment = ListFileFragment.newInstance(getPresenter().getCurLocation(), query);

            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, ListFileFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        } else if (mPresenter.getOpenOption() == ExplorerPresenter.OpenOption.SEARCH) {
            setQuery(query);
            getPresenter().queryFile(getRootPath(), query);
        }
    }
}
