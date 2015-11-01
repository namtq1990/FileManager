package com.tqnam.filemanager.explorer;

/**
 * Created by tqnam on 10/28/2015.
 */
public interface ItemExplorer {

    String getDisplayName();
    String getPath();
    boolean isDirectory();
}
