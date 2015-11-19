package com.tqnam.filemanager.explorer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.tqnam.filemanager.R;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.view.GridViewItem;

import java.util.List;

/**
 * Adapter for explorer, must be combined with custom GridView and GridViewItem
 */
public class ExplorerItemAdapter extends ArrayAdapter<ItemExplorer>{

	int m_resID;

	private OnLongClickListener mTextViewLongClick = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			if (v instanceof EditText) {
				ViewGroup parent = (ViewGroup) v.getParent();

				parent.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
				v.requestFocus();
			}

			return false;
		}
	};

	private OnTouchListener mOnItemTouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v instanceof EditText) {
				return false;
			}

			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				Activity context = (Activity) getContext();
				if (context.getCurrentFocus() instanceof EditText) {
					EditText et = (EditText) ((Activity) getContext()).getCurrentFocus();

					if (et != null) {
						et.clearFocus();
						((ViewGroup) et.getParent()).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

						return true;
					}
				}
			}

			return false;
		}
	};

	public ExplorerItemAdapter(Context context, int resid, List<? extends ItemExplorer> items) {
		super(context, resid, (List<ItemExplorer>) items);
		m_resID = resid;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GridViewItem newView;
		EditText label;
		ImageView icon;

		if (convertView == null) {
			newView = new GridViewItem(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li = (LayoutInflater)getContext().getSystemService(inflater);
			li.inflate(m_resID, newView);

			label = (EditText) newView.findViewById(R.id.title_item);
			icon = (ImageView) newView.findViewById(R.id.icon_item);
			ViewHolder tag = new ViewHolder();

			newView.setOnTouchListener(mOnItemTouch);
			label.setOnLongClickListener(mTextViewLongClick);
			label.setOnTouchListener(mOnItemTouch);

			tag.label = label;
			tag.icon = icon;

			newView.setTag(tag);
		}
		else {
			newView = (GridViewItem) convertView;
			ViewHolder tag = (ViewHolder)newView.getTag();

			label = tag.label;
			icon = tag.icon;
		}

		ItemExplorer item = getItem(position);
		newView.setPosition(position);
		if (item != null) {
			label.setText(item.getDisplayName());

			if (item.isDirectory()) {
				icon.setImageResource(R.drawable.folder_icon);
			}
			else icon.setImageResource(R.drawable.file_icon);
		}

		return newView;
	}

	private class ViewHolder {
		EditText label;
		ImageView icon;
	}
}
