package com.tqnam.filemanager.preference;

import android.os.Bundle;
import android.view.View;

import com.quangnam.baseframework.BasePreferenceFragment;
import com.tqnam.filemanager.R;

public class PreferenceFragment extends BasePreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_app);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}
