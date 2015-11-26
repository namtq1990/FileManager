package com.tqnam.filemanager.explorer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.tqnam.filemanager.R;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.utils.UIUtils;
import com.tqnam.filemanager.view.GridViewItem;

import java.util.List;

/**
 * Adapter for explorer, must be combined with custom GridView and GridViewItem
 */
public class ExplorerItemAdapter extends ArrayAdapter<ItemExplorer>{

    public static final int STATE_NORMAL = 0;
    public static final int STATE_EDIT = 1;
	public static final int STATE_MULTI_SELECT = 2;
	int m_resID;
	int mState = STATE_NORMAL;
	private OnLongClickListener mTextViewLongClick = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			if (v instanceof EditText) {
                updateUI(v, STATE_EDIT);
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
		ViewHolder holder;

		if (convertView == null) {
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li = (LayoutInflater)getContext().getSystemService(inflater);
			convertView = li.inflate(m_resID, parent, false);

			holder = new ViewHolder();
			holder.label = (EditText) convertView.findViewById(R.id.title_item);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon_item);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.item_check);

			holder.label.setOnLongClickListener(mTextViewLongClick);

//            tag.checkBox.setVisibility(View.GONE);          //TODO hide this view because it may be reason gridview blank when scroll

			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}

        if (mState == STATE_MULTI_SELECT) {
			holder.checkBox.setVisibility(View.VISIBLE);
        } else {
			holder.checkBox.setVisibility(View.GONE);
		}

		ItemExplorer item = getItem(position);
		((GridViewItem) convertView).setPosition(position);
		if (item != null) {
			holder.label.setText(item.getDisplayName());

			if (item.isDirectory()) {
				holder.icon.setImageResource(R.drawable.folder_icon);
			}
			else holder.icon.setImageResource(R.drawable.file_icon);
		}

		return convertView;
	}

    public void updateUI(View view, int state) {
		if (mState == state)
			return;

        switch (state) {
            case STATE_NORMAL:
                view.setEnabled(false);
                view.setEnabled(true);
                UIUtils.hideKeyboard((Activity) getContext());
                        ((ViewGroup) view.getParent()).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                view.clearFocus();

                break;
            case STATE_EDIT:
                ViewGroup parent = (ViewGroup) view.getParent();

                parent.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                view.requestFocus();

                break;
			case STATE_MULTI_SELECT:
				break;
        }

		mState = state;
    }

	private class ViewHolder {
        CheckBox checkBox;
		EditText label;
		ImageView icon;
	}
}
