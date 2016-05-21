package com.quangnam.baseframework;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * Created by quangnam on 1/31/16.
 * Base Application
 */
public class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private WeakReference<Activity> mCurActivity;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.init(this);
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mCurActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mCurActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mCurActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    public Activity getCurActivity() {
        return mCurActivity == null ? null : mCurActivity.get();
    }
}
