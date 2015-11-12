package com.tqnam.filemanager.explorer.fileExplorer;

import com.tqnam.filemanager.explorer.ExplorerBaseFragment;
import com.tqnam.filemanager.explorer.ExplorerPresenter;
import com.tqnam.filemanager.model.ExplorerModel;

public class ListFileFragment extends ExplorerBaseFragment {
    public static final String TAG = "ListFileFragment";


    @Override
    protected ExplorerModel genModel() {
        return new FileModel();
    }

    @Override
    protected ExplorerPresenter genPresenter(ExplorerModel model) {
        return new FileExplorerPresenter(this, model);
    }
}
