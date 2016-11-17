package com.tqnam.filemanager.explorer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quangnam.baseframework.BaseFragment;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.adapter.OperatorAdapter;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.model.CopyFileOperator;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.Operator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by quangnam on 11/16/16.
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
        List<ItemExplorer> files = new ArrayList<>();
        files.add(new FileItem("/Sdcard/Movies"));
        files.add(new FileItem("/Sdcard/Music"));
        Operator testOperator = new Operator.MultipleItemOperator<ItemExplorer>(files) {
            @Override
            public Observable execute(Object... arg) {
                return Observable.just("Test");
            }

            @Override
            public String getSourcePath() {
                return "/Sdcard";
            }

            @Override
            public String getDestinationPath() {
                return "/Sdcard";
            }

            @Override
            public boolean isUpdatable() {
                return true;
            }
        };

        ArrayList<Operator> operators = new ArrayList<>();
        operators.add(testOperator);
        operators.add(testOperator);

        List<FileItem> input = new ArrayList<>();
        input.add(new FileItem("/storage/emulated/0/Download"));
        testOperator = new CopyFileOperator(input, "/storage/emulated/0/Subtitles");
        operators.add(testOperator);

        OperatorAdapter.OperatorList childList = new OperatorAdapter.OperatorList(operators);
        ArrayList<OperatorAdapter.OperatorList> list = new ArrayList<>();
        list.add(childList);
//        list.add(childList);

        mViewHolder.listAdapter = new OperatorAdapter(list);
    }

    class ViewHolder {
        @BindView(R.id.list_operator) RecyclerView listOperator;
        OperatorAdapter listAdapter;
    }
}
