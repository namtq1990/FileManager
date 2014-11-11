package com.example.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import android.app.Application;

public class MyApplication extends Application{
	
	private static MyApplication	msInstance;
	public	static boolean			msIsCreated;		//stop activity recreate many time
	public ArrayList<File>			mFileList;			//List file in the folder to display
	public	File					mCurrentFolder;
	public	Comparator<File>		mComparator;		//compare between file and file to sort
	
	public static MyApplication getInstance() {
		return msInstance;
	}
	
	@Override
	public final void onCreate() {
		super.onCreate();
		msInstance = this;
		msIsCreated = true;
		
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
