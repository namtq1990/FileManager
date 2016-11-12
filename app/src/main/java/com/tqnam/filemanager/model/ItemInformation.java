package com.tqnam.filemanager.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by quangnam on 11/11/16.
 */
public class ItemInformation implements Serializable {

    private ItemExplorer[] mItems;

    public ItemInformation(ItemExplorer[] listItem) {
        mItems = listItem;
    }

    public String[] getName() {
        String[] names = new String[mItems.length];
        for (int i = 0; i < mItems.length; i++) {
            names[i] = mItems[i].getDisplayName();
        }

        return names;
    }

    public String getPath() {
//        String[] paths = new String[mItems.length];
//        for (int i = 0; i < mItems.length; i++) {
//            paths[i] = mItems[i].getPath();
//        }

        return mItems[0].getPath();
    }

    public long getFileSize() {
        long totalSize = 0;
        for (ItemExplorer item : mItems) {
            totalSize += item.getSize();
        }

        return totalSize;
    }

    public Date getModifiedTime() {
        return (mItems.length == 1) ? mItems[0].getModifiedTime() : null;
    }

    public Boolean canWrite() {
        return (mItems.length == 1) ? mItems[0].canWrite() : null;
    }

    public Boolean canRead() {
        return (mItems.length == 1) ? mItems[0].canRead() : null;
    }

    public Boolean canExecute() {
        return (mItems.length == 1) ? mItems[0].canExecute() : null;
    }

    public String getFileType() {
        return (mItems.length == 1) ? "" + mItems[0].getFileType() : null;
    }
}
