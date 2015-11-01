package com.tqnam.filemanager.explorer;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.tqnam.filemanager.Application;
import com.tqnam.filemanager.R;

import java.util.ArrayList;

public class MainActivity extends Activity implements ListItemInterface, ExplorerView {

	/*
	 * 
	 */
	private	FileListAdapter 	mAdapter;			//Adapter to the list view
	private ExplorerPresenter	mPresenter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPresenter = new FileExplorerPresenter(this, ((Application) getApplicationContext()).getGlobalData());
	}

	@Override
	public void init(ExplorerPresenter presenter, ArrayList<? extends ItemExplorer> listItem) {
		// Init Fragment
		ListItemFragment fragmentList = (ListItemFragment) getFragmentManager()
				.findFragmentByTag(ListItemFragment.TAG);

		if (fragmentList == null) {
			fragmentList = new ListItemFragment();
			getFragmentManager().beginTransaction()
					.add(R.id.fragment_list_container, fragmentList, ListItemFragment.TAG)
					.commit();
		}

		mAdapter = new FileListAdapter(this, R.layout.item_file, listItem);
		mPresenter = presenter;
		mPresenter.openDirectory(new FileItem(((Application) getApplicationContext()).getGlobalData().mCurFolder));

	}

	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}
	
	@Override
	public void onClickItem(int position) {
		ItemExplorer item = mAdapter.getItem(position);
		mPresenter.openDirectory(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			FileItem folder = new FileItem(Application.getInstance().getGlobalData().mCurFolder);
			mPresenter.openDirectory(folder);

			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	public Application getMyApp() {
		return (Application) getApplication();
	}
	
	@Override
	public void updateList(ArrayList<? extends  ItemExplorer> listItem) {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onErrorPermission() {
		Toast.makeText(this, "Don't have permission to access file", Toast.LENGTH_SHORT).show();
	}

}
