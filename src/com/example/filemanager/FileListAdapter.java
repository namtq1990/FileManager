package com.example.filemanager;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FileListAdapter extends ArrayAdapter<File>{

	int m_resID;
	
	public FileListAdapter(Context context, int resid, List<File> items) {
		super(context, resid, items);
		m_resID = resid;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout newView;
		if (convertView == null) {
			newView = new RelativeLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li = (LayoutInflater)getContext().getSystemService(inflater);
			li.inflate(m_resID, newView);
		}
		else {
			newView = (RelativeLayout)convertView;
		}
		File item = getItem(position);
		if (item != null) {
			TextView tv = (TextView)newView.findViewById(R.id.title_item);
			tv.setText(item.getName());
		}
		
		return newView;
	}
}
