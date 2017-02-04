package com.tqnam.filemanager.explorer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quangnam.baseframework.BaseFragment;
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
        mViewHolder.listOperator.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        mViewHolder.listOperator.setHasFixedSize(true);

        return rootView;
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
    }

    class ViewHolder {
        @BindView(R.id.list_operator) RecyclerView listOperator;
        OperationAdapter listAdapter;
    }
}
