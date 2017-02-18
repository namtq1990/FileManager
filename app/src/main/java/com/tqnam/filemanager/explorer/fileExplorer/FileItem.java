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

package com.tqnam.filemanager.explorer.fileExplorer;

import android.net.Uri;
import android.os.Parcel;
import android.webkit.MimeTypeMap;

import com.quangnam.baseframework.exception.SystemException;
import com.tqnam.filemanager.model.ErrorCode;
import com.tqnam.filemanager.model.ItemExplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tqnam on 10/28/2015.
 */
public class FileItem extends File implements ItemExplorer {

    /**
     * Creator for parcelable
     */
    public static final Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel parcel) {
            String curPath = parcel.readString();
            return new FileItem(curPath);
        }

        @Override
        public Object[] newArray(int i) {
            return new FileItem[i];
        }
    };

    public FileItem(String path) {
        super(path);
    }

    public FileItem(String path, String name) {
        super(path, name);
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getExtension() {
        return MimeTypeMap.getFileExtensionFromUrl(getDisplayName());
    }

    @Override
    public String getParentPath() {
        return getParent();
    }

    @Override
    public ItemExplorer getParentItem() {
        return new FileItem(getParentPath());
    }

    @Override
    public long getSize() {
        if (!isDirectory()) {
            return super.length();
        }

        // get Size if it's directory
        final List<File> dirs=new ArrayList<>();
        dirs.add(this);

        long result=0;
        while(!dirs.isEmpty())
        {
            final File dir=dirs.remove(0);
            if(!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if(listFiles == null || listFiles.length == 0)
                continue;
            for(final File child : listFiles)
            {
                result += child.length();
                if(child.isDirectory())
                    dirs.add(child);
            }
        }

        return result;
    }

    @Override
    public Date getModifiedTime() {
        return new Date(super.lastModified());
    }

    @Override
    public Uri getUri() {
        return Uri.fromFile(this);
    }

    @Override
    public int getFileType() {
        if (isDirectory())
            return FILE_TYPE_FOLDER;

        String extension = getExtension();

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

    @Override
    public List<FileItem> getChild() {
        File[] list = listFiles();
        List<FileItem> childs = new ArrayList<>(list == null ? 0 : list.length);

        if (list != null) {
            for (File item : list) {
                childs.add(new FileItem(item.getAbsolutePath()));
            }
        } else {
            throw new SystemException(ErrorCode.RK_EXPLORER_OPEN_ERROR,
                    "Cannot open folder " + getPath() + ", check permission");
        }

        return childs;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        String curPath = getPath();
        parcel.writeString(curPath);
    }
}
