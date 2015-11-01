package com.tqnam.filemanager;

import com.tqnam.filemanager.explorer.FileItem;
import com.tqnam.filemanager.explorer.ItemExplorer;

import java.util.ArrayList;
import java.util.Comparator;

public class Application extends android.app.Application {

	private static Application msInstance;

	public GlobalData				mGlobalData;

	public static Application getInstance() {
		return msInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		msInstance = this;

		mGlobalData = new GlobalData();
	}

	public GlobalData getGlobalData() {
		return mGlobalData;
	}

	public static class GlobalData {
		public ArrayList<FileItem> mListFile;		// List File to Display
		public String mCurFolder;						// Path of current Folder
		public Comparator<ItemExplorer> mCurCompare;		// To compare a list of file
		public Option mOption;						// List Option to select in app

		public GlobalData() {
			mListFile = new ArrayList<FileItem>();
			mCurFolder = "/";
			mOption = new Option();
			mCurCompare = mOption.mSortName;		// Default is sort by name
		}

	}

	public static class Option {
		public final Comparator<ItemExplorer> mSortName = new Comparator<ItemExplorer>() {
			@Override
			public int compare(ItemExplorer a, ItemExplorer b) {
				if (a.isDirectory() ^ b.isDirectory()) {
					return a.isDirectory() ? -1 : 1;
				}

				return (a.getDisplayName().compareToIgnoreCase(b.getDisplayName()) <= 0) ? -1 : 1;
			}
		};
	}
}
