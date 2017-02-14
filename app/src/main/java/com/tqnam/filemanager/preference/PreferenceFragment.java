package com.tqnam.filemanager.preference;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.quangnam.baseframework.BasePreferenceFragment;
import com.tqnam.filemanager.R;

public class PreferenceFragment extends BasePreferenceFragment {
    public static final String TAG = PreferenceFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_app);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestFocusFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeFocusRequest();
    }

}
