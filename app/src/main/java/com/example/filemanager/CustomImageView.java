package com.example.filemanager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CustomImageView extends ImageView{
	private boolean mBlockLayout;

	public CustomImageView(Context context) {
		super(context);
	}
	
	public CustomImageView(Context context, AttributeSet attrib) {
		super(context, attrib);
	}
	
	public CustomImageView(Context context, AttributeSet attrib, int defStyle) {
		super(context, attrib, defStyle);
	}
	
	@Override
	public void requestLayout() {
		if (!mBlockLayout)
			super.requestLayout();
	}
	
	@Override
	public void setImageURI(Uri uri) {
		mBlockLayout = true;
		super.setImageURI(uri);
		mBlockLayout = false;
	}
	
	@Override
	public void setImageDrawable(Drawable drawable) {
		mBlockLayout = true;
		super.setImageDrawable(drawable);
		mBlockLayout = false;
	}
}
