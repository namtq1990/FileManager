package com.tqnam.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class Application extends android.app.Application {

	private static Application msInstance;
	
	public ArrayList<File>			mFileList;			//List file in the folder to display
	public	File					mCurrentFolder;
	public	Comparator<File>		mComparator;		//compare between file and file to sort

	public static Application getInstance() {
		return msInstance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		msInstance = this;

		mCurrentFolder = new File("/");
		mFileList = new ArrayList<File>();
		mComparator = new Comparator<File>() {
			
			@Override
			public int compare(File a, File b) {
				if (a.isDirectory() ^ b.isDirectory()) {
					return a.isDirectory() ? -1 : 1;
				}
				
				return (a.getName().compareToIgnoreCase(b.getName()) <= 0) ? -1 : 1;
			}
		};
	}
}
