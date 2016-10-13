package com.tqnam.filemanager.explorer;

import android.content.Context;

import com.tqnam.filemanager.model.ItemExplorer;

/**
 * Created by tqnam on 10/28/2015.
 */
public interface ExplorerView {
    void onOpenItem(ItemExplorer item);
    Context getContext();
    ExplorerPresenter getPresenter();

    String getQuery();
    void setQuery(String query);

    String getRootPath();

    void setRootPath(String path);

    ExplorerPresenter.OpenType getOpenType();

    void setOpenType(ExplorerPresenter.OpenType openType);

    void onQueryFile(final String query);
}
