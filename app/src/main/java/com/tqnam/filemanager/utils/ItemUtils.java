package com.tqnam.filemanager.utils;

import com.tqnam.filemanager.model.ItemExplorer;

public class ItemUtils {

    public static int getFileType(String extension) {
        if (extension == null || extension.isEmpty())
            return ItemExplorer.FILE_TYPE_NORMAL;

        for (int i = 0;i <ItemExplorer.EXT_MAPPER.length;i++) {
            String[] type = ItemExplorer.EXT_MAPPER[i];

            for (String ext : type) {
                if (ext.equalsIgnoreCase(extension))
                    return i;
            }
        }

        return ItemExplorer.FILE_TYPE_NORMAL;
    }
}
