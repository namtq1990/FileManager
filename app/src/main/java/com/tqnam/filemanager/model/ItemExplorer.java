package com.tqnam.filemanager.model;

import android.net.Uri;

/**
 * Created by tqnam on 10/28/2015.
 */
public interface ItemExplorer {

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

    Uri getUri();

    int getFileType();
}
