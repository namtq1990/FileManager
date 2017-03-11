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

package com.quangnam.base.service;

import android.app.Activity;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.quangnam.base.BaseApplication;
import com.quangnam.base.Log;
import com.quangnam.base.PermissionDebugActivity;
import com.quangnam.base.R;
import com.quangnam.base.TrackingTime;
import com.quangnam.base.exception.SystemException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by quangnam on 2/18/17.
 * Project FileManager-master
 */
public class FloatingViewService extends Service {
    private static final String TAG = FloatingViewService.class.getName();
    private static final String EXTEND_TAG_CLICK_EXPAND = "click_expand";
    private static final String EXTEND_TAG_KEY_BACK = "_onBackPress";
    private static final long IDENTIFY_CLICK_TIME = 300;
    private static final int[] MENU_ID = {
            R.id.btn_reload,
            R.id.btn_copy,
            R.id.btn_collapse,
            R.id.btn_up,
            R.id.btn_down,
            R.id.btn_exception,
            R.id.btn_screenshot
    };
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private View mFloatingView;
    private View mExpandedView;
    private View mCollapsedView;
    private TextView mTvLog;
    private CharSequence mLog;
    private View.OnClickListener mMenuOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_collapse) {
                collapseView();
            } else if (id == R.id.btn_reload) {
                refresh();
            } else if (id == R.id.btn_copy) {
                copyAll();
            } else if (id == R.id.btn_up) {
                scrollUp();
            } else if (id == R.id.btn_down) {
                scrollDown();
            } else if (id == R.id.btn_exception) {
                throw new SystemException(SystemException.RK_UNKNOWN, "Test");
            } else if (id == R.id.btn_screenshot) {
                takeScreenshot();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(this)) {
            startActivity(new Intent(this, PermissionDebugActivity.class));
            stopSelf();
        } else {
            initView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) {
            mWindowManager.removeView(mFloatingView);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Collapse the notification bar
        Intent collapseNotificationIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(collapseNotificationIntent);
        //

        return super.onStartCommand(intent, flags, startId);
    }

    private void initView() {
        mFloatingView = LayoutInflater.from(new ContextThemeWrapper(this, R.style.TemplateTheme))
                .inflate(R.layout.layout_floating_debug, null);
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        mLayoutParams.gravity = Gravity.TOP | Gravity.START;
        mLayoutParams.x = 0;
        mLayoutParams.y = 100;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, mLayoutParams);

        ImageView collapsedIcon = (ImageView) mFloatingView.findViewById(R.id.img_collapse);

        try {
            Drawable icon = getPackageManager().getApplicationIcon(getPackageName());
            collapsedIcon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        collapsedIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        View btnClose = mFloatingView.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });

        mExpandedView = mFloatingView.findViewById(R.id.layout_expand);
        mCollapsedView = mFloatingView.findViewById(R.id.layout_collapse);

        collapseView();

        for (int id : MENU_ID) {
            View btn = mExpandedView.findViewById(id);
            btn.setOnClickListener(mMenuOnClickListener);
        }

        collapsedIcon.setOnTouchListener(new View.OnTouchListener() {
            private int initializeX;
            private int initializeY;

            private float initializeTouchX;
            private float initializeTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        TrackingTime.beginTracking(TAG + EXTEND_TAG_CLICK_EXPAND);
                        initializeX = mLayoutParams.x;
                        initializeY = mLayoutParams.y;

                        initializeTouchX = event.getRawX();
                        initializeTouchY = event.getRawY();

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        mLayoutParams.x = (int) (initializeX + (event.getRawX() - initializeTouchX));
                        mLayoutParams.y = (int) (initializeY + (event.getRawY() - initializeTouchY));

                        mWindowManager.updateViewLayout(mFloatingView, mLayoutParams);

                        return true;
                    case MotionEvent.ACTION_UP:
                        long touchedTime = TrackingTime.endTracking(TAG + EXTEND_TAG_CLICK_EXPAND);
                        if (touchedTime <= IDENTIFY_CLICK_TIME) {
                            // It's a click, not a move
                            int xDiff = (int) (event.getRawX() - initializeTouchX);
                            int yDiff = (int) (event.getRawY() - initializeTouchY);

                            if (xDiff < 10 && yDiff < 10) {
                                v.performClick();
                            }
                        }

                        return true;
                }

                return false;
            }
        });

        mExpandedView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disable send touch action to #mFloatingView if touch in view expanded
                return true;
            }
        });

        mFloatingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isExpanded()) {
                    collapseView();
                }
            }
        });

        mTvLog = (TextView) mExpandedView.findViewById(R.id.tv_log);
//        mTvLog.setMovementMethod(new AccelerationMovement(mTvLog));
        mTvLog.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_DOWN:
                            TrackingTime.beginTracking(TAG + EXTEND_TAG_KEY_BACK);
                            return true;
                        case KeyEvent.ACTION_UP:
                            long longClickTime = TrackingTime.endTracking(TAG + EXTEND_TAG_KEY_BACK);
                            if (longClickTime < IDENTIFY_CLICK_TIME) {
                                collapseView();
                            }

                            return true;
                    }
                }

                return false;
            }
        });
    }

    public void toggle() {
        if (isExpanded()) {
            collapseView();
        } else {
            expandView();
        }
    }

    public boolean isExpanded() {
        return mExpandedView.getVisibility() == View.VISIBLE;
    }

    public void collapseView() {
        mExpandedView.setVisibility(View.GONE);
        mCollapsedView.setVisibility(View.VISIBLE);
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.dimAmount = 0.5f;

        mWindowManager.updateViewLayout(mFloatingView, mLayoutParams);
    }

    public void expandView() {
        mExpandedView.setVisibility(View.VISIBLE);
        mCollapsedView.setVisibility(View.GONE);
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        mLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        mWindowManager.updateViewLayout(mFloatingView, mLayoutParams);
        mTvLog.requestFocus();

        refresh();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void loadLog() {
        mLog = new StringBuilder();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader reader = Log.loadLog();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        addLineToLog(line);
                    }


                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mTvLog.setText(mLog);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.run();
    }

    public void addLineToLog(String line) {
        Spannable text = new SpannableString("\n" + line);
        text.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), android.R.color.holo_green_light, getTheme())),
                0,
                19,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        mLog = TextUtils.concat(mLog, text);
    }

    public void refresh() {
        mTvLog.setText(null);
        loadLog();
    }

    private void copyAll() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Text", mTvLog.getText());
        clipboardManager.setPrimaryClip(clip);

        Toast.makeText(FloatingViewService.this, "Log copied", Toast.LENGTH_SHORT).show();
    }

    public void scrollUp() {
        ScrollView scrollView = (ScrollView) mTvLog.getParent();
        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    public void scrollDown() {
        ScrollView scrollView = (ScrollView) mTvLog.getParent();
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    public void takeScreenshot() {

        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            BaseApplication application = (BaseApplication) getApplicationContext();
            if (application.getCurActivity() != null) {
                Activity activity = application.getCurActivity();
                // image naming and path  to include sd card  appending name you choose for file
                String mPath = Environment.getExternalStorageDirectory().toString()
                        + "/"
                        + getPackageName()
                        + "_"
                        + now
                        + ".jpg";

                // create bitmap screen capture
                View v1 = activity.getWindow().getDecorView().getRootView();
                v1.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(false);

                File imageFile = new File(mPath);

                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();
            }

            Toast.makeText(FloatingViewService.this, "Screenshot captured", Toast.LENGTH_SHORT).show();

        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }
}
