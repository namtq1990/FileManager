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

package com.quangnam.baseframework;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by quangnam on 1/31/16.
 * Interface to active with {@link (BaseActivity)}
 */
public interface BaseFragmentInterface extends AutoUnsubscribe {

    void onAttach(Context context);
    void onCreate(Bundle savedState);
    void onSaveInstanceState(Bundle outState);
    void onDestroy();
    void onResume();
    void onPause();

    Context getAppContext();

    int getSavedHashcode();

    /**
     * Safety get activity of Fragment to stop something stupid from {@link Fragment#getActivity()}
     *
     * @return Activity of Fragment
     */
    FragmentActivity getActivitySafe();

    /**
     * Use this function to deal with object animator in support fragment.
     * Can add any animation to start in this anim
     * Time of all animator must be smaller than {@link #getTimeAnimate()}
     */
    void onCreateAnimator(int transit, boolean enter, int nextAnim);

    /**
     * Use this function to define max time to animate, setup with default animation.
     */
    long getTimeAnimate();

    void requestFocusFragment();

    void popupFocusFragment();

    void removeFocusRequest();

    int getPriorityFocusIndex();

    void requestAtPriority(int priority);

}
