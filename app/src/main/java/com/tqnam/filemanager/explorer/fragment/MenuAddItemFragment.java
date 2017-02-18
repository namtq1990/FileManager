/*
 * MIT License
 *
 * Copyright (c) 2017 Tran Quang Nam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tqnam.filemanager.explorer.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.quangnam.baseframework.BaseFragment;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.explorer.MainActivity;

/**
 * Created by quangnam on 1/17/16.
 *
 */
public class MenuAddItemFragment extends BaseFragment {

    public static final String TAG = "MenuAddItem";

    private ViewHolder mHolder;
    private MenuFABListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHolder = new ViewHolder();
        RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_menu_add_item, container, false);
        mHolder.mBtnAdd = (FloatingActionButton) rootView.findViewById(R.id.btn_add);
        mHolder.mBtnAddFile = (FloatingActionButton) rootView.findViewById(R.id.btn_add_file);
        mHolder.mBtnAddFolder = (FloatingActionButton) rootView.findViewById(R.id.btn_add_folder);
        mHolder.mBackground = rootView.findViewById(R.id.background);
        mHolder.mMenu = (ViewGroup) rootView.findViewById(R.id.fab_menu);

        mHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              dismiss();
            }
        });

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return true;
            }
        });

        mHolder.mBtnAddFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                if (mListener != null) {
                    mListener.onAddFolderSelected();
                }
            }
        });
        mHolder.mBtnAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                if (mListener != null) {
                    mListener.onAddFileSelected();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MenuFABListener) {
            mListener = (MenuFABListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        showMenu();
//        BaseActivity activity = (BaseActivity) getActivity();
//        activity.requestFocusFragment(this);
        requestFocusFragment();
    }

    @Override
    public void onPause() {
        super.onPause();

//        BaseActivity activity = (BaseActivity) getActivity();
//        activity.removeFocusRequest(this);
        removeFocusRequest();
    }

    @Override
    public void onDestroy() {
        if (!getActivity().isChangingConfigurations()) {
            ((MainActivity) getActivity()).showAddButtonDirect();

            // Restore the focus of list fragment

        }

        super.onDestroy();
    }

    public void dismiss() {
        getFragmentManager().popBackStack();
    }

    private void showMenu() {
        mHolder.mBtnAdd.animate()
                .setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .rotation(135)
                .start();
        mHolder.mBackground.animate()
                .setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .alphaBy(0.5f)
                .alpha(1)
                .start();
        for (int i = 0;i < mHolder.mMenu.getChildCount();i++) {
            mHolder.mMenu.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    private void hideMenu() {
        mHolder.mBackground.animate()
                .setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .alpha(0.0f)
                .start();
        ObjectAnimator.ofFloat(mHolder.mBtnAdd, "rotation", 135, 0)
                .setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();

        for (int i = 0;i < mHolder.mMenu.getChildCount();i++) {
            mHolder.mMenu.getChildAt(i).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public long getTimeAnimate() {
        return getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public void onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (!enter) {
            hideMenu();
        }

    }

    public interface MenuFABListener {
        void onAddFileSelected();
        void onAddFolderSelected();
    }

    private class ViewHolder {
        View mBackground;
        FloatingActionButton mBtnAdd;
        FloatingActionButton mBtnAddFile;
        FloatingActionButton mBtnAddFolder;
        ViewGroup mMenu;
    }
}
