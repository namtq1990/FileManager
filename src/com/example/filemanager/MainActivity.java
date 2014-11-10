package com.example.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListAdapter;
import android.widget.Toast;

public class MainActivity extends Activity implements ListItemInterface{

	/*
	 * 
	 */
	FileListAdapter mAdapter;			//Adapter to the list view
	ArrayList<File> mFileList;			//List file in the folder to display
	File			mCurrentFolder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCurrentFolder = new File("/");
		mFileList = new ArrayList<File>(Arrays.asList(mCurrentFolder.listFiles()));
		mAdapter = new FileListAdapter(this, R.layout.item_file, mFileList);
		
		setContentView(R.layout.activity_main);
	}

	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}
	
	@Override
	public void onClickItem(int position) {
		File f = mFileList.get(position);
		openDirectory(f);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			openDirectory(mCurrentFolder.getParentFile());
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void openDirectory(File location) {
		if (location != null && location.isDirectory()) {
			File[] list = location.listFiles();
			if (list != null) {
				mCurrentFolder = location;
				mFileList.clear();
				mFileList.addAll(Arrays.asList(location.listFiles()));
				mAdapter.notifyDataSetChanged();
			}
			else {
				if (!location.canRead())
					Toast.makeText(this, "Don't have permission to access file", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
