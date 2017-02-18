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

import android.net.Uri;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * Created by tqnam on 10/28/2015.
 */
public interface ItemExplorer extends Parcelable {

    int FILE_TYPE_IMAGE  = 0;
    int FILE_TYPE_VIDEO  = 1;
    int FILE_TYPE_TEXT   = 2;
    int FILE_TYPE_ZIP    = 3;
    int FILE_TYPE_AUDIO  = 4;
    int FILE_TYPE_PDF    = 5;
    int FILE_TYPE_WORD   = 6;
    int FILE_TYPE_FOLDER = 20;
    int FILE_TYPE_NORMAL = -1;

    /**
     * Need to keep value of {@link ItemExplorer#FILE_TYPE_IMAGE}, {@link ItemExplorer#FILE_TYPE_VIDEO}, ...
     * as continuous index of this map, and last is {@link ItemExplorer#FILE_TYPE_NORMAL}, equal to map length
     * So when get file type, if it's not equal to all of type, it'll be set to {@link ItemExplorer#FILE_TYPE_NORMAL}
     * <br>
     */
    String[][] EXT_MAPPER = {
            {"jpg", "png"},         // index of {@FILE_TYPE_IMAGE}
            {"mp4", "mov", "mkv"},   // index of {@FILE_TYPE_VIDEO}
            {"txt"},                 // index of {@FILE_TYPE_TEXT}
            {"rar", "zip", "7z"},   // index of {@FILE_TYPE_ZIP}
            {"mp3"},                 // index of {@FILE_TYPE_AUDIO}
            {"pdf"},                     // index of {@FILE_TYPE_PDF}
            {"doc", "docx"}            // index of {@FILE_TYPE_WORD}
    };

    String getDisplayName();

    String getPath();

    boolean isDirectory();

    String getExtension();

    String getParentPath();

    ItemExplorer getParentItem();

    long getSize();

    Date getModifiedTime();

    boolean canRead();

    boolean canWrite();

    boolean canExecute();

    boolean exists();

    Uri getUri();

    int getFileType();

    List<? extends ItemExplorer> getChild();
}
