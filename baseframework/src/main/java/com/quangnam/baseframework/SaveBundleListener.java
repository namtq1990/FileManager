package com.quangnam.baseframework;

import android.os.Bundle;

/**
 * Created by quangnam on 11/13/16.
 */
public interface SaveBundleListener {
    void onSaveInstanceState(Bundle state);
    void onRestoreInstanceState(Bundle savedInstanceState);
}

