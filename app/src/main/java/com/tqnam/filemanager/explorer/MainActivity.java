package com.tqnam.filemanager.explorer;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.fileExplorer.ListFileFragment;

/**
 * 	Activity container
 * 	First design for file explorer, may be add setting, too.
 */

public class MainActivity extends Activity {

	private ViewHolder mViewHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}

	/**
	 * 	Init function for activity.
	 * 	Inflate view and Fragment
	 */
	private void init() {
		mViewHolder = new ViewHolder();

		// Init Fragment
		ListFileFragment listFile = (ListFileFragment) getFragmentManager()
				.findFragmentByTag(ListFileFragment.TAG);

		if (listFile == null) {
			listFile = new ListFileFragment();
			getFragmentManager().beginTransaction()
					.add(R.id.fragment_list_container, listFile, ListFileFragment.TAG)
					.commit();
		}

		mViewHolder.mFragmentListFile = listFile;
	}

	@Override
	public void onBackPressed() {
		if (mViewHolder.mFragmentListFile != null
				&& mViewHolder.mFragmentListFile.isResumed()) {
			mViewHolder.mFragmentListFile.onBackPressed();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// TODO Implement back keycode

		}

		return super.onKeyDown(keyCode, event);
	}

	private class ViewHolder {
		ListFileFragment mFragmentListFile;
	}
}
