package com.example.filemanager;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;

public class ListItemFragment extends Fragment {
	private ListItemInterface mActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_item_list, container, false);

		GridView view = (GridView)root.findViewById(R.id.grid_view_list);
		view.setAdapter(mActivity.getAdapter());
		
		view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				mActivity.onClickItem(position);
			}
		});
		
		return root;
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mActivity = (ListItemInterface)activity;
	}
}

interface ListItemInterface {
	public ListAdapter getAdapter();
	public void onClickItem(int position);
}
