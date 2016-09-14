package com.tqnam.filemanager.explorer.fileExplorer;

import com.tqnam.filemanager.explorer.ExplorerBaseFragment;
import com.tqnam.filemanager.explorer.ExplorerPresenter;
import com.tqnam.filemanager.model.ExplorerModel;

public class ListFileFragment extends ExplorerBaseFragment {
    public static final String TAG = "ListFileFragment";

    @Override
    protected ExplorerPresenter genPresenter() {
        return new FileExplorerPresenter(this, new ExplorerModel());
    }
}
