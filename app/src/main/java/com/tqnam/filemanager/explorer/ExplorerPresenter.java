package com.tqnam.filemanager.explorer;

import com.quangnam.baseframework.AutoUnsubscribe;
import com.quangnam.baseframework.SaveBundleListener;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.Operator;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public interface ExplorerPresenter extends SaveBundleListener {
    Observable<ItemExplorer> onBackPressed();

    /**
     * Open an item at position in list
     * @param position position to open
     * @return Observable with result a ItemExplorer've just opened (Folder if it's folder)
     */
    Observable<ItemExplorer> openItem(int position);
    Observable<ItemExplorer> openDirectory(ItemExplorer path);
    Observable<Void> renameItem(ItemExplorer item, String newLabel);
    Observable<ItemExplorer> reload();
    Observable<Void> createFile(String filename);
    Observable<Void> createFolder(String filename);

    Observable<List<? extends ItemExplorer>> quickQueryFile(String query);
    Observable<List<? extends ItemExplorer>> quickQueryFile(String query, String path);
    Observable<List<ItemExplorer>> queryFile(String path, String query);
    Operator<?> deleteOperator(List<ItemExplorer> list);
    Operator<?> copyCurFolderOperator(List<ItemExplorer> listSelected);
    void setValidated(Operator operator);

    void saveClipboard(List<ItemExplorer> clipboard);
    ArrayList<ItemExplorer> getClipboard();

    String getCurLocation();
    void setCurLocation(String path);
    ItemExplorer getCurFolder();
    int getItemDisplayCount();
    ItemExplorer getItemDisplayedAt(int position);

    OpenType getOpenType();

    void setOpenType(OpenType openType);

    OpenOption getOpenOption();

    void setOpenOption(OpenOption openOption);

    public enum OpenType {
        LOCAL
    }

    public enum OpenOption {
        EXPLORER,
        SEARCH
    }

    public interface View extends AutoUnsubscribe {
        void onOpenItem(ItemExplorer item);
        ExplorerPresenter getPresenter();

        String getQuery();
        void setQuery(String query);

        String getRootPath();

        void setRootPath(String path);

        void refreshView();

        void showError(String message);

        void showError(int message);

        void showMessage(String message);

        void showMessage(int message);

        ExplorerPresenter.OpenType getOpenType();

        void setOpenType(ExplorerPresenter.OpenType openType);

        void onQueryFile(final String query);
    }
}
