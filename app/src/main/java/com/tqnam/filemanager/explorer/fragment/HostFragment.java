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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quangnam.base.BaseActivity;
import com.quangnam.base.BaseFragment;
import com.tqnam.filemanager.R;

/**
 * Created by quangnam on 9/27/16.
 * Empty fragment to store child fragment explorer in backstack
 */
public class HostFragment extends BaseFragment implements BaseActivity.OnBackPressedListener {
    public static final String TAG = "HostFragment";

    public static HostFragment newInstance() {

        return new HostFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_host, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (needRequestFocus()) {
//            BaseActivity activity = (BaseActivity) getActivity();
//            activity.requestFocusFragment(this);
            requestFocusFragment();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeFocusRequest();
    }

    public void addFragmentPage(Fragment page, String tag) {
        if (isHostEmpty()) {
            getChildFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, page, tag)
                    .commitAllowingStateLoss();
        } else {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, page, tag)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }

    public Fragment getCurPage() {
        return getChildFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private boolean isHostEmpty() {
        return getChildFragmentManager().getFragments() == null || getChildFragmentManager().getFragments().isEmpty();
    }

    private boolean needRequestFocus() {
        BaseActivity activity = (BaseActivity) getActivity();
        return activity.getFocusFragment() == null;
    }

    @Override
    public boolean onBackPressed() {
        Fragment fragment = getCurPage();
        if (!(fragment instanceof BaseActivity.OnBackPressedListener)
                || !((BaseActivity.OnBackPressedListener) fragment).onBackPressed()) {
            getChildFragmentManager().popBackStack();
        }

        return true;
    }
}
