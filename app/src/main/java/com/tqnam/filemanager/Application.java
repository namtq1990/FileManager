package com.tqnam.filemanager;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

public class Application extends android.app.Application implements android.app.Application.ActivityLifecycleCallbacks {

    private static Application msInstance;

    public GlobalData mGlobalData;

    public static Application getInstance() {
        return msInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());
        msInstance = this;

        mGlobalData = new GlobalData();
        registerActivityLifecycleCallbacks(this);
    }

    public GlobalData getGlobalData() {
        return mGlobalData;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        mGlobalData.mCurActivity = new WeakReference<>(activity);

        // tqnam: set color of status_bar if request
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    View v = mGlobalData.getCurActivity().findViewById(R.id.status_padding);

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

    @Override
    public void onActivityStarted(Activity activity) {
        mGlobalData.mCurActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mGlobalData.mCurActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public class GlobalData {
        public int mStatusBarHeight;

        /**
         * icon for item in list
         */
        public Integer mIconSize;
        private WeakReference<Activity> mCurActivity;

        public GlobalData() {
            int statusBarRes;
            statusBarRes = getResources().getIdentifier("status_bar_height", "dimen", "android");

            if (statusBarRes > 0) {
                mStatusBarHeight = getResources().getDimensionPixelSize(statusBarRes);
            }

            mIconSize = getResources().getDimensionPixelSize(R.dimen.design_fab_size_normal);
        }

        public Activity getCurActivity() {
            return mCurActivity == null ? null : mCurActivity.get();
        }

    }
}
