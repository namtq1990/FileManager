package com.tqnam.filemanager.explorer.fileExplorer;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.webkit.MimeTypeMap;

import com.tqnam.filemanager.model.ItemExplorer;

import java.io.File;

/**
 * Created by tqnam on 10/28/2015.
 */
public class FileItem extends File implements ItemExplorer, Parcelable {

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
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        String curPath = getPath();
        parcel.writeString(curPath);
    }
}
