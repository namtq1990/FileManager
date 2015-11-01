package com.tqnam.filemanager.explorer;

import java.util.ArrayList;

/**
 * Created by tqnam on 10/28/2015.
 */
public interface ExplorerView {

    void init(ExplorerPresenter presenter, ArrayList<? extends  ItemExplorer> listItem);
    void updateList(ArrayList<? extends ItemExplorer> listItem);
    void onErrorPermission();
}
