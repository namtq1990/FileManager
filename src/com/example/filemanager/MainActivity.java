package com.example.filemanager;

import java.io.File;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListAdapter;
import android.widget.Toast;

public class MainActivity extends Activity implements ListItemInterface{

	/*
	 * 
	 */
	private	FileListAdapter 	mAdapter;			//Adapter to the list view
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new FileListAdapter(this, R.layout.item_file, getMyApp().mFileList);
		openDirectory(getMyApp().mCurrentFolder);
		
		setContentView(R.layout.activity_main);
	}

	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}
	
	@Override
	public void onClickItem(int position) {
		File f = getMyApp().mFileList.get(position);
		openDirectory(f);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			openDirectory(getMyApp().mCurrentFolder.getParentFile());
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public MyApplication getMyApp() {
		return (MyApplication)getApplication();
	}
	
	public void openDirectory(File location) {
		if (location != null && location.isDirectory()) {
			File[] list = location.listFiles();
			if (list != null) {
				getMyApp().mCurrentFolder = location;
				getMyApp().mFileList.clear();
				Arrays.sort(list, getMyApp().mComparator);
				getMyApp().mFileList.addAll(Arrays.asList(list));
				mAdapter.notifyDataSetChanged();
			}
			else {
				if (!location.canRead())
					Toast.makeText(this, "Don't have permission to access file", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
