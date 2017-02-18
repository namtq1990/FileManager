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
