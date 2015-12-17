package com.tqnam.filemanager.preference;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;

import com.tqnam.filemanager.R;

public class PreferenceFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_app);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}
