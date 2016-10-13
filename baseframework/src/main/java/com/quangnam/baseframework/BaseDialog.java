package com.quangnam.baseframework;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class BaseDialog extends DialogFragment implements BaseFragmentInterface {

    private Context mAppContext;

    @Override
    public FragmentActivity getActivitySafe() {
        BaseApplication application = (BaseApplication) getAppContext();
        return getActivity() != null ? getActivity() : (FragmentActivity) application.getCurActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAppContext = context.getApplicationContext();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation anim = super.onCreateAnimation(transit, enter, nextAnim);

        long timeAnim = getTimeAnimate();
        if (timeAnim != 0) {

            if (anim == null) {
                anim = AnimationUtils.loadAnimation(getActivitySafe(), R.anim.anim_base_time);
            }
            anim.setDuration(timeAnim);
            onCreateAnimator(transit, enter, nextAnim);
        }

        return anim;
    }

    public void onCreateAnimator(int transit, boolean enter, int nextAnim) {
    }

    public long getTimeAnimate() {
        return 0;
    }

    public Context getAppContext() {
        return mAppContext;
    }
}
