/*
 * MIT License
 *
 * Copyright (c) 2017 Tran Quang Nam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tqnam.filemanager.utils;

import android.text.TextUtils;

import com.quangnam.base.exception.SystemException;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.model.ErrorCode;
import com.tqnam.filemanager.model.ItemExplorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by quangnam on 10/6/16.
 */
public class FileUtil {
    public static final int BUFF_SIZE = 1024 * 16;

    public static List<FileItem> open(String path) {
        FileItem folder = new FileItem(path);
        return open(folder);
    }

    public static List<FileItem> open(FileItem file) {
        if (file.isDirectory()) {
            return file.getChild();

        } else {
            // User wrong function to open item, error may be in openFile() function
            throw new SystemException(ErrorCode.RK_EXPLORER_OPEN_WRONG_FUNCTION,
                    "Wrong function to open");
        }
    }

    public static File createFile(String path, String filename) {
        File file = new File(path, filename);

        try {
            boolean isCreated = file.createNewFile();

            if (!isCreated) {
                if (file.exists()) {
                    throw new SystemException(ErrorCode.RK_EXPLORER_FILE_EXISTED,
                            "File with this name existed");
                } else if (!file.canWrite()) {
                    throw new SystemException(ErrorCode.RK_EXPLORER_PERMISSION,
                            "Don't have permission");
                }

                throw new SystemException(ErrorCode.RK_UNKNOWN,
                        "Couldn't create file " + filename
                                + " at " + path);
            }
        } catch (IOException e) {
            if (file.exists()) {
                throw new SystemException(ErrorCode.RK_EXPLORER_FILE_EXISTED,
                        "File with this name existed", e);
            } else if (!file.canWrite()) {
                throw new SystemException(ErrorCode.RK_EXPLORER_PERMISSION,
                        "Don't have permission", e);
            } else throw new SystemException(ErrorCode.RK_UNKNOWN, "Can't write file", e);
        } catch (Throwable e) {
            if (e instanceof SystemException) {
                throw e;
            } else {
                throw new SystemException(ErrorCode.RK_UNKNOWN,
                        "Couldn't create file " + filename
                                + " at " + path,
                        e);
            }
        }

        return file;
    }

    public static File createFolder(String path, String filename) {
        File file = new File(path, filename);

        try {
            boolean isCreated = file.mkdir();

            if (!isCreated) {
                if (file.exists()) {
                    throw new SystemException(ErrorCode.RK_EXPLORER_FILE_EXISTED,
                            "File with this name existed");
                } else if (!file.canWrite()) {
                    throw new SystemException(ErrorCode.RK_EXPLORER_PERMISSION,
                            "Don't have permission");
                }
            }
        } catch (Throwable e) {
            if (e instanceof SystemException) {
                throw e;
            } else {
                throw new SystemException(ErrorCode.RK_UNKNOWN,
                        "Couldn't create folder " + filename
                                + " at " + path,
                        e);
            }
        }

        return file;
    }

    public static List<? extends ItemExplorer> filter(List<? extends ItemExplorer> list, String query) {
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

        List<? extends ItemExplorer> childs = file.getChild();
        List<ItemExplorer> result = new ArrayList<>(childs);

        filter(result, query);

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

    /**
     * Copy file source to destination file
     *
     * @param source File to copy
     * @param destination File to copy
     */
    public static void copy(FileItem source, FileItem destination, boolean isOverwrite) {


    }

    public static String formatListTitle(List<? extends ItemExplorer> list) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            sb.append(" - ")
                    .append(list.get(i).getDisplayName())
                    .append('\n');
        }

        return sb.toString();
    }

    public static String getExtension(ItemExplorer file) {
        String name = file.getDisplayName();
        int index = name.lastIndexOf('.');

        return index > 0 ? name.substring(index + 1) : "";
    }

    public static String createNameWithSuffix(FileItem file) {
        String extension = file.getExtension();
        if (!TextUtils.isEmpty(extension)) {
            extension = "." + extension;
        }
        String nameWithoutExtension = file.getDisplayName().replace(extension, "");

        int i = 1;
        String nameFormat = "%s (%d)%s";
        FileItem newFile;

        while(true) {
            String name = String.format(Locale.ENGLISH,
                    nameFormat,
                    nameWithoutExtension,
                    i++,
                    extension);

            newFile = new FileItem(file.getParentPath(), name);
            if (!newFile.exists()) {
                break;
            }
        }

        return newFile.getDisplayName();
    }

}
