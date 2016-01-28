package com.tqnam.filemanager.explorer;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
        RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_menu_add_item, container, false);
        mHolder.mBtnAdd = (FloatingActionButton) rootView.findViewById(R.id.btn_add);
        mHolder.mBackground = rootView.findViewById(R.id.background);
        mHolder.mMenu = (ViewGroup) rootView.findViewById(R.id.menu_add_item);

        LayoutTransition transition = new LayoutTransition();
//        transition.setDuration(2000);
        mHolder.mMenu.setLayoutTransition(transition);

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

    @Override
    public void onResume() {
        super.onResume();

        showMenu();
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

    private class ViewHolder {
        View mBackground;
        FloatingActionButton mBtnAdd;
        ViewGroup mMenu;
    }
}
