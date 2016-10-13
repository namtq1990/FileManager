package com.tqnam.filemanager.utils;

import android.text.TextUtils;

import com.quangnam.baseframework.exception.SystemException;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.model.ErrorCode;
import com.tqnam.filemanager.model.ItemExplorer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quangnam on 10/6/16.
 */
public class FileUtil {

    public static List<ItemExplorer> open(String path) {
        FileItem folder = new FileItem(path);
        return open(folder);
    }

    public static List<ItemExplorer> open(FileItem file) {
        ArrayList<ItemExplorer> result = new ArrayList<>();

        if (file.isDirectory()) {
            return file.getChild();

        } else {
            // User wrong function to open item, error may be in openFile() function
            throw new SystemException(ErrorCode.RK_EXPLORER_OPEN_WRONG_FUNCTION,
                    "Wrong function to open");
        }
    }

    public static List<ItemExplorer> filter(List<ItemExplorer> list, String query) {
        if (TextUtils.isEmpty(query))
            return list;

        for (int i = 0;i < list.size();i++) {
            ItemExplorer item = list.get(i);
            if (!item.getDisplayName().toLowerCase().contains(query.toLowerCase())) {
                list.remove(i);
                i--;
            }
        }

        return list;
    }

    public static List<ItemExplorer> search(ItemExplorer file, String query) {
        if (!file.isDirectory())
            return null;

        List<ItemExplorer> childs = file.getChild();
        List<ItemExplorer> result = new ArrayList<>(childs);

        result = filter(result, query);

        for (ItemExplorer child : childs) {
            List<ItemExplorer> childFilteredList = search(child, query);
            if (childFilteredList != null)
                result.addAll(childFilteredList);
        }

        return result;
    }

    public static List<ItemExplorer> search(String path, String query) {
        return search(new FileItem(path), query);
    }
}
