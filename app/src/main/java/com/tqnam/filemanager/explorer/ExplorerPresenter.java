package com.tqnam.filemanager.explorer;

import android.os.Bundle;

import com.tqnam.filemanager.model.ItemExplorer;

import rx.Observable;

/**
 * Created by tqnam on 10/28/2015.
 */
public interface ExplorerPresenter {

    void onRestoreInstanceState(Bundle savedInstanceState);
    void onSaveInstanceState(Bundle bundle);
    Observable<ItemExplorer> onBackPressed();

    /**
     * Open an item at position in list
     * @param position position to open
     * @return Observable with result a ItemExplorer've just opened (Folder if it's folder)
     */
    Observable<ItemExplorer> openItem(int position);
    Observable<ItemExplorer> openDirectory(ItemExplorer path);
    int getItemCount();
    ItemExplorer getItemAt(int position);
}
