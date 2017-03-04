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

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.quangnam.baseframework.service.FloatingViewService;

/**
 * Created by quangnam on 1/31/16.
 * Base Application
 */
public class BaseApplication extends InternalBaseApplication {
    public static final int NOTIFICATION_ID = 1000;
    public static final int REQUEST_CODE_DEBUG_MENU = 1;
    public static final int REQUEST_CODE_SETTING = 2;
    public static final int REQUEST_CODE_APP = 3;

    private NotificationManagerCompat mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private RemoteViews mBigNotificationView;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Running in debug mode");

        if (Config.DEBUG) {
            showDebugNotification();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);

        if (Config.DEBUG) {
            Intent appIntent;
            appIntent = new Intent(this, activity.getClass());
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(activity.getClass());
            stackBuilder.addNextIntent(appIntent);
            PendingIntent pendingIntent;
            pendingIntent = stackBuilder.getPendingIntent(REQUEST_CODE_APP, PendingIntent.FLAG_UPDATE_CURRENT);

//            PendingIntent.getActivity(this, REQUEST_CODE_APP, );

            // Add cur activity to notification setting
            mBigNotificationView.setOnClickPendingIntent(R.id.btn_open_app, pendingIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    public void showDebugNotification() {
        Intent serviceIntent = new Intent(this, FloatingViewService.class);
        Intent settingIntent = new Intent();
        settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        settingIntent.setData(Uri.parse("package:" + getPackageName()));

        mBigNotificationView = new RemoteViews(getPackageName(), R.layout.layout_notification_debug);
        mBigNotificationView.setOnClickPendingIntent(R.id.btn_open_debug, PendingIntent.getService(this, REQUEST_CODE_DEBUG_MENU, serviceIntent, 0));
        mBigNotificationView.setOnClickPendingIntent(R.id.btn_setting, PendingIntent.getActivity(this, REQUEST_CODE_SETTING, settingIntent, 0));

        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.round_corner_selector)
                .setCustomBigContentView(mBigNotificationView)
                .setContentTitle(getPackageName())
                .setContentText(getPackageName() + " is Debugging");

        try {
            BitmapDrawable icon = (BitmapDrawable) getPackageManager().getApplicationIcon(getPackageName());
            mBigNotificationView.setImageViewBitmap(R.id.img_noti_logo, icon.getBitmap());

            mBuilder.setLargeIcon(icon.getBitmap());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mNotificationManager = NotificationManagerCompat.from(this);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
