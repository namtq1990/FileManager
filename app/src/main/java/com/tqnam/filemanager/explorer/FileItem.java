package com.tqnam.filemanager.explorer;

import java.io.File;

/**
 * Created by tqnam on 10/28/2015.
 */
public class FileItem extends File implements ItemExplorer {

    public FileItem(String path) {
        super(path);
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getPath() {
        return getAbsolutePath();
    }

}
