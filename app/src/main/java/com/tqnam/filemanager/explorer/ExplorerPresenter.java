package com.tqnam.filemanager.explorer;

import android.os.Bundle;

import com.tqnam.filemanager.model.ItemExplorer;

import java.util.ArrayList;

/**
 * Created by tqnam on 10/28/2015.
 */
public interface ExplorerPresenter {

    void onRestoreInstanceState(Bundle savedInstanceState);
    void onSaveInstanceState(Bundle bundle);
    void onBackPressed();
    void openDirectory(ItemExplorer path);
    ArrayList<? extends ItemExplorer> getCurList();
}
