package com.tqnam.filemanager.explorer;

import com.quangnam.baseframework.AutoUnsubscribe;
import com.quangnam.baseframework.SaveBundleListener;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.operation.Operation;

import java.util.ArrayList;
import java.util.List;

public interface ExplorerPresenter extends SaveBundleListener {

    void bind(View view);
    void unbind(View view);
    void onBackPressed();
    List<ItemExplorer> getListData();

    void openItem(ItemExplorer item);
    void openDirectory(ItemExplorer path);
    void renameItem(ItemExplorer item, String newLabel);
    void reload();
    void createFile(String filename);
    void createFolder(String filename);

    void queryFile(String path, String query);
    Operation<?> deleteOperation(List<ItemExplorer> list);
    Operation<?> copyCurFolderOperation(List<ItemExplorer> listSelected);
    Operation<?> moveCurFolderOperation(List<ItemExplorer> listSelected);
    Operation<?> doPasteAction();
    void trySetValidated(Operation operation);

    /**
     * Save to clipboard with category (Copy or Move)
     */
    void saveClipboard(List<ItemExplorer> clipboard, int category);
    ArrayList<ItemExplorer> getClipboard();

    Operation getValidatingOperation();

    void setValidatingOperation(Operation operation);

    String getCurLocation();
    void setCurLocation(String path);
    ItemExplorer getCurFolder();

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

        void showValidate(Operation operation);
    }
}
