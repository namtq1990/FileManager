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

package com.tqnam.filemanager.explorer.behavior;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.ads.AdView;

import java.util.List;

/**
 * Created by tqnam on 12/22/2015.
 */
public class FABScrollBehavior extends FloatingActionButton.Behavior {

    private ObjectAnimator mFabTranslationYAnimator;
    private float mFabTranslationY;

    public FABScrollBehavior(Context context, AttributeSet attributeSet) {
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency) || (dependency instanceof AdView);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if ((dyConsumed > 0 || dyUnconsumed > 0 ) && child.getVisibility() == View.VISIBLE) {
            child.hide();
        } else if ((dyConsumed < 0 || dyUnconsumed < 0) && child.getVisibility() == View.GONE) {
            child.show();
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL);
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if(isDependencyUpdateTranslationY(dependency)) {
            this.updateFabTranslation(parent, child, true);
            return false;
        } else {
            return super.onDependentViewChanged(parent, child, dependency);
        }
    }

    public void onDependentViewRemoved(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if(isDependencyUpdateTranslationY(dependency)) {
            this.updateFabTranslation(parent, child, true);
        }

    }

    private void updateFabTranslation(CoordinatorLayout parent, final FloatingActionButton fab, boolean animationAllowed) {
        float targetTransY = this.getFabTranslationY(parent, fab);
        if(this.mFabTranslationY != targetTransY) {
            float currentTransY = ViewCompat.getTranslationY(fab);
            if(this.mFabTranslationYAnimator != null && this.mFabTranslationYAnimator.isRunning()) {
                this.mFabTranslationYAnimator.cancel();
            }

            if(animationAllowed && fab.isShown() && Math.abs(currentTransY - targetTransY) > (float)fab.getHeight() * 0.667F) {
                if(this.mFabTranslationYAnimator == null) {
                    this.mFabTranslationYAnimator = ObjectAnimator.ofFloat(fab, "translationY", targetTransY);
                    this.mFabTranslationYAnimator.setInterpolator(new FastOutSlowInInterpolator());
                }

//                this.mFabTranslationYAnimator.setFloatValues(currentTransY, targetTransY);
                this.mFabTranslationYAnimator.start();
            } else {
                ViewCompat.setTranslationY(fab, targetTransY);
            }

            this.mFabTranslationY = targetTransY;
        }
    }

    private float getFabTranslationY(CoordinatorLayout parent, FloatingActionButton fab) {
        float minOffset = 0.0F;
        List dependencies = parent.getDependencies(fab);
        int i = 0;

        for(int z = dependencies.size(); i < z; ++i) {
            View view = (View)dependencies.get(i);
            if(isDependencyUpdateTranslationY(view) && parent.doViewsOverlap(fab, view)) {
                minOffset = Math.min(minOffset, ViewCompat.getTranslationY(view) - (float)view.getHeight());
            }
        }

        return minOffset;
    }

    private boolean isDependencyUpdateTranslationY(View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout || dependency instanceof AdView;
    }

}
