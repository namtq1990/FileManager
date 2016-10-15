package com.tqnam.filemanager.explorer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quangnam.baseframework.BaseActivity;
import com.quangnam.baseframework.BaseFragment;
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
            requestFocusFragment((BaseActivity) getActivity());
        }
    }

    public void addFragmentPage(Fragment page, String tag) {
        if (isHostEmpty()) {
            getChildFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, page, tag)
                    .commit();
        } else {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, page, tag)
                    .addToBackStack(null)
                    .commit();
        }
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
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(fragment instanceof BaseActivity.OnBackPressedListener)
                || !((BaseActivity.OnBackPressedListener) fragment).onBackPressed()) {
            getChildFragmentManager().popBackStack();
        }

        return true;
    }
}
