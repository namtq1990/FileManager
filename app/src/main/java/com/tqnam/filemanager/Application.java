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

package com.tqnam.filemanager;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.quangnam.baseframework.BaseApplication;
import com.quangnam.baseframework.Config;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

public class Application extends BaseApplication implements android.app.Application.ActivityLifecycleCallbacks {

    private static Application msInstance;

    public GlobalData mGlobalData;

    public static Application getInstance() {
        return msInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Config.USE_FABRIC) {
            Fabric.with(this, new Crashlytics());
        }

        msInstance = this;

        mGlobalData = new GlobalData();
    }

    public GlobalData getGlobalData() {
        return mGlobalData;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

        // tqnam: set color of status_bar if request
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    View v = getCurActivity().findViewById(R.id.status_padding);

                    if (v != null) {
                        v.getLayoutParams().height = mGlobalData.mStatusBarHeight;
                    }
                }
            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // tqnam: for some reason, need to set flag FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS to true to
            // enable system bar background in api >= 21
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    public class GlobalData {
        public int mStatusBarHeight;

        /**
         * icon for item in list
         */
        public Integer mIconSize;

        public Picasso mImage;

        public GlobalData() {
            int statusBarRes;
            statusBarRes = getResources().getIdentifier("status_bar_height", "dimen", "android");

            if (statusBarRes > 0) {
                mStatusBarHeight = getResources().getDimensionPixelSize(statusBarRes);
            }

            mIconSize = getResources().getDimensionPixelSize(R.dimen.design_fab_size_normal);

            mImage = Picasso.with(Application.this);
            //TODO Remove logging here
            mImage.setIndicatorsEnabled(true);
            mImage.setLoggingEnabled(true);
        }


    }
}
