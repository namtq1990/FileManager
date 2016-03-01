package com.tqnam.filemanager.view;

import android.animation.Animator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.tqnam.filemanager.Application;
import com.tqnam.filemanager.Common;
import com.tqnam.filemanager.R;

/**
 * Created by quangnam on 2/4/16.
 * Preview view class that support multiple touch action to minimize and maximum size.
 * It depend on state variable, have some state is {@link #STATE_MAXIMUM}, {@link #STATE_MINIUM}
 */
public class Preview extends RelativeLayout implements View.OnClickListener, View.OnTouchListener {

    public static final int STATE_MINIUM   = 0;
    public static final int STATE_MAXIMUM  = 1;
    public static final int STATE_REMOVING = 2;

    public static final int   MIN_TOUCH = 10;
    public static final float MIN_ALPHA = 0.3f;
    public static final float MAX_ALPHA = 1;
    public static final int LINE_UP    = 1;
    public static final int LINE_DOWN  = 2;
    public static final int LINE_LEFT  = 3;
    public static final int LINE_RIGHT = 4;
    public static final double ANGLE_45  = Math.PI / 4;
    public static final double ANGLE_135 = 3 * ANGLE_45;
    public static final double ANGLE_225 = -ANGLE_135;
    public static final double ANGLE_315 = -ANGLE_45;
    private int        mState;
    private PointF mFirstTouch;
    private PointF     mLastTouch;
    private Long       mFirstTouchTime;
    private Long       mLastTouchUpTime;
    private float      mRatio;
    private int        mMaxWidth;
    private int        mMaxHeight;
    private int        mMinWidth;
    private int        mMinHeight;
    private float      mMaxScale;
    private float      mMinScale;
    private float      mMaxScroll;
    private float      mMinScroll;
    private float      mMinScrollDetect;
    private int      mDisplayWidth;
    private int      mDisplayHeight;
    private boolean    mCanceled;
    private ViewHolder mHolder;
    private OnRemoveListener      mRemoveListener;
    private OnStateChangeListener mStateChangeListener;

    public Preview(Context context) {
        this(context, null);
    }

    public Preview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Preview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public static int getWidthFromHeight(int height) {
//        Get best scale for screen 16:9
        return height * 16 / 9;
    }

    /**
     * Match with {@link #getWidthFromHeight(int)}
     */
    public static int getHeightFromWidth(int width) {
        return width * 9 / 16;
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preview_layout, this, true);
        mHolder = new ViewHolder();
        mHolder.mContainer = (RelativeLayout) findViewById(R.id.preview_container);
        mHolder.mMenu = (RelativeLayout) findViewById(R.id.preview_menu);
        mHolder.mExtraInfo = (ScrollView) findViewById(R.id.preview_info);
        View btnMinium = mHolder.mMenu.findViewById(R.id.btn_minium);
        btnMinium.setOnClickListener(this);
        btnMinium.setOnTouchListener(this);

        mDisplayWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mDisplayHeight = getContext().getResources().getDisplayMetrics().heightPixels -
                Application.getInstance().getGlobalData().mStatusBarHeight;
        mMinHeight = getContext().getResources().getDimensionPixelSize(R.dimen.c_preview_mini_height);
        mMinWidth = getWidthFromHeight(mMinHeight);
        mMaxWidth = mDisplayWidth;
        mMaxHeight = mDisplayHeight;
        mMaxScale = 1.0f;
        mMinScale = mMinWidth / (float) mMaxWidth;
        mMinScroll = 0;
        mMinScrollDetect = getContext().getResources().getDisplayMetrics().density * 2;     // If touch is max 2dp then execute scroll

        if (mDisplayWidth > mDisplayHeight) {
//            Hide panel when device is in lanscape
            mHolder.mExtraInfo.setVisibility(View.GONE);
        }

        mHolder.mContainer.setOnTouchListener(this);

        internalSetState(STATE_MAXIMUM);
    }

    public View getContentView() {
        return mHolder.mContainer.getChildCount() > 0 ? mHolder.mContainer.getChildAt(0) : null;
    }

    public void setContentView(View contentView) {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mHolder.mContainer.removeAllViews();
        mHolder.mContainer.addView(contentView, params);
    }

    public void setState(int state) {
        internalSetState(state);
        invalidate();
    }

    public void setOnRemoveListener(OnRemoveListener listener) {
        mRemoveListener = listener;
    }

    public void setOnStateChangeListener(OnStateChangeListener listener) {
        mStateChangeListener = listener;
    }

    private void internalSetState(int state) {
        if (mState != state) {
            int oldState = mState;
            mState = state;

            if (mState == STATE_MINIUM) {
                hideMenu();
            }

            if (mStateChangeListener != null) {
                mStateChangeListener.onStateChanged(oldState, mState);
            }
        }
    }

    public void minimize() {
        mHolder.mContainer.animate()
                .scaleX(mMinScale)
                .scaleY(mMinScale)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setState(STATE_MINIUM);
                        mHolder.mContainer.animate().setListener(null);
                        mHolder.mMenu.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                })
                .start();
        animate().translationY(mHolder.mExtraInfo.getMeasuredHeight())
                .setListener(null)
                .start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;

//        Use only one size for all state. It's same size
        width = getContext().getResources().getDisplayMetrics().widthPixels;
        height = ((View) getParent()).getMeasuredHeight();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHolder.mContainer.setPivotX(mHolder.mContainer.getMeasuredWidth());
        mHolder.mContainer.setPivotY(mHolder.mContainer.getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        System.out.println("OnInterceptTouchEvent " + ev.getAction() + ": " + ev.getX() + " " + ev.getY());
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mFirstTouch = new PointF(ev.getRawX(), ev.getRawY());
            mFirstTouchTime = SystemClock.uptimeMillis();
        }

        if (mState == STATE_MINIUM || mState == STATE_REMOVING) {
//            Handle all touch action here if preview is minium
            return true;
        } else if (mState == STATE_MAXIMUM) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {

                if (!mHolder.mMenu.isShown()) {
                    mHolder.mMenu.setVisibility(View.VISIBLE);
                    showMenu();
                } else {
                    if (!isTouchMenu(ev)) {
                        mHolder.mMenu.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }

        mLastTouchUpTime = SystemClock.uptimeMillis();

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return onTouchEvent(this, event);
    }

    private boolean onTouchEvent(View view, MotionEvent event) {
        System.out.println("OnTouchEvent " + event.getAction() + ": " + event.getX() + " " + event.getY());
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!isTouchContainer(event)) {
//                    Don't handle action if user don't first touch in container
                    return false;
                }
                mLastTouch = new PointF(event.getRawX(), event.getRawY());

                break;
            case MotionEvent.ACTION_MOVE:
                mCanceled = true;
//                Check last touch == null because ACTION_CANCEL fired. So cancel all action touched
                if (mLastTouch != null) {
                    float distance = Math.abs(event.getRawX() - mLastTouch.x) + Math.abs(event.getRawY() - mLastTouch.y);
                    if (distance < MIN_TOUCH) {
//                        Don't handle when single touch but too small
                        return true;
                    }
                    int slideType = findAngleSlide(event);

                    if (slideType == LINE_LEFT || slideType == LINE_RIGHT) {
//                    if (mState == STATE_MINIUM)
                        slide(event);
                    } else if (slideType == LINE_UP || slideType == LINE_DOWN) {
                        scroll(event);
                    }

                    mLastTouch = new PointF(event.getRawX(), event.getRawY());
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mLastTouchUpTime = SystemClock.uptimeMillis();
                cancel();
                break;
        }

        return true;
    }

    private boolean isTouchMenu(MotionEvent event) {
        Rect menuRect = new Rect();
        mHolder.mMenu.getHitRect(menuRect);

        return menuRect.contains((int) event.getX(), (int) event.getY());
    }

    private boolean isTouchContainer(MotionEvent event) {
        Rect containerRect = new Rect();
        mHolder.mContainer.getHitRect(containerRect);

        return containerRect.contains((int) event.getX(), (int) event.getY());
    }

    public void cancel() {

        if (!isTapped()) {   // Stop action cancel() that set state multiple time
            ViewPropertyAnimator anim = animate();
            anim.setListener(null);
            mHolder.mContainer.animate().setListener(null);

            Common.Log("Cancelling with state " + mState);

            if (mState == STATE_REMOVING) {
                anim = anim.alpha(MIN_ALPHA);

                if (getTranslationX() < 0) {
                    int swidth = getContext().getResources().getDisplayMetrics().widthPixels;
                    anim = anim.translationX(-swidth);
                } else {
                    anim = anim.translationX(mMinWidth);
                }
                anim.setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setVisibility(View.GONE);
                        if (mRemoveListener != null) {
                            mRemoveListener.onRemovePreview();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
            } else if (getTranslationX() != 0) {
//            Cancel slide anim
                anim.translationX(0)
                        .alpha(1);
            }

            if (mHolder.mContainer.getScaleX() != 0) {
//            Cancel scroll anim
                if (mState == STATE_MAXIMUM) {
                    anim.translationY(0)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    mHolder.mContainer.animate()
                                            .scaleX(mMaxScale)
                                            .scaleY(mMaxScale);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mHolder.mMenu.setVisibility(View.VISIBLE);
                                    showMenu();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            });
                } else {
//                Other state should be scale to minium
                    Common.Log("Cancel to minimize");
                    anim.translationY(mHolder.mExtraInfo.getMeasuredHeight());
                    mHolder.mContainer.animate()
                            .scaleX(mMinScale)
                            .scaleY(mMinScale)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mHolder.mMenu.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            })
                            .start();
                }
            }

            Common.Log("Animation before");
            anim.start();
        }

        mFirstTouch = null;
        mLastTouch = null;
        mCanceled = true;
    }

    private void slide(MotionEvent lastEvent) {
        float distanceX = lastEvent.getRawX() - mLastTouch.x;
        float curTranslation = getTranslationX();
        float newTranslation = curTranslation + distanceX;
        this.setTranslationX(newTranslation);

        float percent = distanceX / mMinWidth;
        float curAlpha = getAlpha();
        if (curTranslation < 0) {
//            Current in left
            curAlpha = Math.min(MAX_ALPHA, Math.max(MIN_ALPHA, curAlpha + (MAX_ALPHA - MIN_ALPHA) * percent));
        } else if (curTranslation > 0) {
//            Current in right
            curAlpha = Math.min(MAX_ALPHA, Math.max(MIN_ALPHA, curAlpha - (MAX_ALPHA - MIN_ALPHA) * percent));
        } else {
//            Current start
            curAlpha = Math.min(MAX_ALPHA, Math.max(MIN_ALPHA, curAlpha - (MAX_ALPHA - MIN_ALPHA) * Math.abs(percent)));
        }
        setAlpha(curAlpha);

        if (Math.abs(newTranslation) > mMinWidth * 2 / 3) {
            internalSetState(STATE_REMOVING);
        } else {
            internalSetState(STATE_MINIUM);
        }
    }

    private void scroll(MotionEvent lastEvent) {
        float distance = mLastTouch.y - lastEvent.getRawY();
        float maxDistance = mDisplayHeight - mMinHeight;
        float curScale = mHolder.mContainer.getScaleX();
        float newScale = curScale + mMaxScale * distance / maxDistance;

        if (newScale > (mMaxScale + mMinScale) / 2.0f) {
            internalSetState(STATE_MAXIMUM);
        } else {
            internalSetState(STATE_MINIUM);
        }

        if (newScale < mMinScale || newScale > mMaxScale) {
            return;
        } else {
            float curTranslateY = getTranslationY();
            mHolder.mContainer.setScaleX(newScale);
            mHolder.mContainer.setScaleY(newScale);

            setTranslationY(curTranslateY - distance);
            if (mState == STATE_MAXIMUM) {
                if (getTranslationY() > mMinScrollDetect) {
//                    User start scroll in vertical
                    hideMenu();
                }
            }
        }
    }

    private boolean isTapped() {
        if (mLastTouchUpTime - mFirstTouchTime > ViewConfiguration.getTapTimeout()) {
            return false;
        } else {
            return Math.max(Math.abs(mLastTouch.x - mFirstTouch.x), Math.abs(mLastTouch.y - mFirstTouch.y))
                    < mMinScrollDetect;
        }
    }

    private int findAngleSlide(MotionEvent lastEvent) {
        float oldX = mLastTouch.x;
        float oldY = mLastTouch.y;
        float lstX = lastEvent.getRawX();
        float lstY = lastEvent.getRawY();

        System.out.println("oX: " + oldX + " oY:" + oldY + " lX: " + lstX + " lY: " + lstY);
        double angle = Math.atan2(-lstY + oldY, lstX - oldX);
        System.out.println("angle: " + angle);

        if (angle < ANGLE_45 && angle > ANGLE_315) {
            if (mHolder.mContainer.getScaleX() != mMinScale) {
//                If view is scroll and scaling, action continue like scroll
                return LINE_UP;
            }

            return LINE_RIGHT;
        } else if (angle >= ANGLE_45 && angle <= ANGLE_135) {
            if (getTranslationX() != 0) {
//                If view is sliding, action continue like slide
                return LINE_LEFT;
            }

            return LINE_UP;
        } else if (angle >= ANGLE_135 || angle <= ANGLE_225) {
            if (mHolder.mContainer.getScaleX() != mMinScale) {
//                If view is scroll and scaling, action continue like scroll
                return LINE_UP;
            }
            return LINE_LEFT;
        } else {
            if (getTranslationX() != 0) {
//                If view is sliding, action continue like slide
                return LINE_LEFT;
            }
            return LINE_DOWN;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_minium:
                if (mLastTouchUpTime - mFirstTouchTime <= ViewConfiguration.getTapTimeout()) {
                    minimize();
                    break;
                }
        }
    }

    public void showMenu() {
        mHolder.mMenu.setAlpha(1);
    }

    public void hideMenu() {
        mHolder.mMenu.setAlpha(0.5f);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.btn_minium) {
            System.out.println("OnTouch in " + v + " at " + event.getRawX() + "," + event.getRawY());

            if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                mCanceled = false;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Cancel animation start if not cancel function don't be called
                        if (!mCanceled) {
                            cancel();
                        }
                    }
                }, 50);
            } else {
                onTouchEvent(v, event);
            }
        } else if (v.getId() == R.id.preview_container) {
            return true;
        }

        return false;
    }

    public interface OnRemoveListener {
        void onRemovePreview();
    }

    public interface OnStateChangeListener {
        void onStateChanged(int oldState, int newState);
    }

    private class ViewHolder {
        RelativeLayout mMenu;
        RelativeLayout mContainer;
        ScrollView     mExtraInfo;
    }
}
