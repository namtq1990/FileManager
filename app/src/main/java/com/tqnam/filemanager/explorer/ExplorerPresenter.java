package com.tqnam.filemanager.explorer;

import android.os.Bundle;

import com.tqnam.filemanager.model.ItemExplorer;

/**
 * Created by tqnam on 10/28/2015.
 */
public interface ExplorerPresenter {

    void onRestoreInstanceState(Bundle savedInstanceState);
    void onSaveInstanceState(Bundle bundle);
    void onBackPressed();
    void openItem(int position);
    void openDirectory(ItemExplorer path);
    int getItemCount();
    ItemExplorer getItemAt(int position);
}
