package com.tqnam.filemanager.explorer;

import com.tqnam.filemanager.model.ItemExplorer;

import java.util.ArrayList;

/**
 * Created by tqnam on 10/28/2015.
 */
public interface ExplorerView {

    /**
     * Update list item to UI
     * @param listItem List to show in UI
     */
    void updateList(ArrayList<? extends ItemExplorer> listItem);

    /**
     * Handle when don't have permission to execute UI
     */
    void onErrorPermission();
}
