package com.tqnam.filemanager.explorer;

import android.graphics.Bitmap;

import com.quangnam.baseframework.SaveBundleListener;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.Operator;

import java.util.List;

import rx.Observable;

/**
 * Created by tqnam on 10/28/2015.
 */
public interface ExplorerPresenter extends SaveBundleListener {
    Observable<ItemExplorer> onBackPressed();

    /**
     * Open an item at position in list
     * @param position position to open
     * @return Observable with result a ItemExplorer've just opened (Folder if it's folder)
     */
    Observable<ItemExplorer> openItem(int position);
    Observable<ItemExplorer> openDirectory(ItemExplorer path);
    Observable<Bitmap> loadImage(ItemExplorer item);
    Observable<Void> renameItem(ItemExplorer item, String newLabel);
    Observable<ItemExplorer> reload();
    Observable<Void> createFile(String filename);
    Observable<Void> createFolder(String filename);

    Observable<List<? extends ItemExplorer>> quickQueryFile(String query);
    Observable<List<? extends ItemExplorer>> quickQueryFile(String query, String path);
    Observable<List<ItemExplorer>> queryFile(String path, String query);
    Operator<?> deleteOperator(List<ItemExplorer> list);
    void setValidated(Operator operator);

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
}
