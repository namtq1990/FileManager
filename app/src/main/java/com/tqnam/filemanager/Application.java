package com.tqnam.filemanager;

import android.app.Activity;
import android.os.Bundle;

import java.lang.ref.WeakReference;

public class Application extends android.app.Application implements android.app.Application.ActivityLifecycleCallbacks {

	private static Application msInstance;

	public GlobalData				mGlobalData;

	public static Application getInstance() {
		return msInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		msInstance = this;

		mGlobalData = new GlobalData();
	}

	public GlobalData getGlobalData() {
		return mGlobalData;
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle bundle) {
		mGlobalData.mCurActivity = new WeakReference<Activity>(activity);
	}

	@Override
	public void onActivityStarted(Activity activity) {
		mGlobalData.mCurActivity = new WeakReference<Activity>(activity);
	}

	@Override
	public void onActivityResumed(Activity activity) {
		mGlobalData.mCurActivity = new WeakReference<Activity>(activity);
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

	public static class GlobalData {
		private WeakReference<Activity> mCurActivity;

		public GlobalData() {
		}

		public Activity getCurActivity() {
			return mCurActivity == null ? null : mCurActivity.get();
		}

	}
}
