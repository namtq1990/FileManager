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

package com.tqnam.filemanager.explorer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quangnam.base.BaseFragment;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.adapter.OperationAdapter;
import com.tqnam.filemanager.utils.OperationManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by quangnam on 11/16/16.
 * Project FileManager-master
 */
public class OperatorManagerFragment extends BaseFragment {
    public static final String TAG = OperatorManagerFragment.class.getCanonicalName();
    private ViewHolder mViewHolder;

    public static OperatorManagerFragment newInstance() {
        return new OperatorManagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_operator, container, false);
        mViewHolder = new ViewHolder();
        initData();
        ButterKnife.bind(mViewHolder, rootView);
        mViewHolder.listOperator.setAdapter(mViewHolder.listAdapter);
        mViewHolder.listOperator.setLayoutManager(mViewHolder.listLayout);
        mViewHolder.listOperator.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        mViewHolder.listLayout.removeAllViews();    // Force remove view here to let adapter free all resource
        super.onDestroyView();
    }

    private void initData() {
        ArrayList<OperationAdapter.OperatorList> list = new ArrayList<>();
        for (int category : OperationManager.CATEGORIES) {
            OperationAdapter.OperatorList childList = new OperationAdapter.OperatorList(
                    OperationManager.getInstance().getOperatorList(category)
            );
            list.add(childList);
        }

        mViewHolder.listAdapter = new OperationAdapter(list, getActivity());
        mViewHolder.listLayout = new LinearLayoutManager(getActivity());
    }

    class ViewHolder {
        @BindView(R.id.list_operator) RecyclerView listOperator;
        OperationAdapter listAdapter;
        LinearLayoutManager listLayout;
    }
}
