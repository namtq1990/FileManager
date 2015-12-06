package com.tqnam.filemanager.explorer;

/**
 * Created by tqnam on 10/28/2015.
 */
public interface ExplorerView {

    /**
     * Update list item to UI
     */
    void updateList();

    /**
     * Handle when don't have permission to execute UI
     */
    void onErrorPermission();
}
