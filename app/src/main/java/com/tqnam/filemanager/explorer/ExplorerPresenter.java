package com.tqnam.filemanager.explorer;

import com.quangnam.baseframework.AutoUnsubscribe;
import com.quangnam.baseframework.SaveBundleListener;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.Operator;

import java.util.ArrayList;
import java.util.List;

public interface ExplorerPresenter extends SaveBundleListener {

    void bind(View view);
    void unbind(View view);
    void onBackPressed();

    /**
     * Open an item at position in list
     * @param position position to open
     * @return Observable with result a ItemExplorer've just opened (Folder if it's folder)
     */
    void openItem(int position);
    void openDirectory(ItemExplorer path);
    void renameItem(ItemExplorer item, String newLabel);
    void reload();
    void createFile(String filename);
    void createFolder(String filename);

    void quickQueryFile(String query);
    void quickQueryFile(String query, String path);
    void queryFile(String path, String query);
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

    interface View extends AutoUnsubscribe {
        ExplorerPresenter getPresenter();

        void replaceExplorerAtItem(ItemExplorer root);

        void openPreview(ItemExplorer item);

        void showLoading(boolean isLoading);

        String getQuery();

        void setQuery(String query);

        String getRootPath();

        void setRootPath(String path);

        void refreshView();

        void showError(String message);

        void showError(int message);

        void showMessage(String message);

        void showMessage(int message);

        void onQueryFile(final String query);
    }
}
