package com.tqnam.filemanager;

import android.animation.Animator;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by quangnam on 11/12/15.
 * <p/>
 * Base Class Fragment to use in this app
 * Add it to handle lifecycle and can quickly modify for all fragment
 */
public class BaseFragment extends android.support.v4.app.Fragment {

    private Context mAppContext;

    /**
     * Safety get activity of Fragment to stop something stupid from {@link #getActivity()}
     *
     * @return Activity of Fragment
     */
    public FragmentActivity getActivitySafe() {
        return getActivity() != null ? getActivity() : (FragmentActivity) Application.getInstance().getGlobalData().getCurActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAppContext = context.getApplicationContext();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation anim = super.onCreateAnimation(transit, enter, nextAnim);

        Animator animator = onCreateAnimator(transit, enter, nextAnim);
        if (animator != null) {

            if (anim == null) {
                anim = AnimationUtils.loadAnimation(getActivitySafe(), R.anim.anim_base_time);
            }
            anim.setDuration(animator.getDuration());
            animator.start();
        }

        return anim;
    }

    /**
     * Use this function to deal with object animator in support fragment.
     * Can add any animation to start in this anim
     */
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        return null;
    }

    public Context getAppContext() {
        return mAppContext;
    }
}
