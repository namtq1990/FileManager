package com.tqnam.filemanager.explorer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tqnam.filemanager.BaseFragment;
import com.tqnam.filemanager.R;

/**
 * Created by quangnam on 1/17/16.
 */
public class MenuAddItemFragment extends BaseFragment {

    public static final String TAG = "MenuAddItem";

    private ViewHolder mHolder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHolder = new ViewHolder();
        View rootView = inflater.inflate(R.layout.fragment_menu_add_item, container, false);
        mHolder.mBtnAdd = (FloatingActionButton) rootView.findViewById(R.id.btn_add);

        showMenu();
        mHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getFragmentManager().beginTransaction()
//                        .remove(MenuAddItemFragment.this)
//                        .commit();
                getFragmentManager().popBackStack();
            }
        });

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getFragmentManager().popBackStack();
                return true;
            }
        });

        return rootView;
    }

    private void showMenu() {
        mHolder.mBtnAdd.animate()
                .setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .rotation(135)
                .start();
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (!enter) {
            return ObjectAnimator.ofFloat(mHolder.mBtnAdd, "rotation", 135, 0)
                    .setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        }

        return super.onCreateAnimator(transit, enter, nextAnim);
    }

    private class ViewHolder {
        FloatingActionButton mBtnAdd;
    }
}
