package com.tqnam.filemanager.explorer.fileExplorer;

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
            FileItem file = new FileItem(curPath);

            return file;
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
    public String getPath() {
        return getAbsolutePath();
    }

    @Override
    public String getExtension() {
        return MimeTypeMap.getSingleton().getFileExtensionFromUrl(getDisplayName());
    }

    @Override
    public String getParentPath() {
        return getParent();
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
