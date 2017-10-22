package com.pugfish1992.progress.utils;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by daichi on 10/22/17.
 */

public class BackgroundTintAnimation {

    /* Intentional private */
    private BackgroundTintAnimation() {}

    public static ValueAnimator create(@NonNull final View target, int startColor, int endColor) {
        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                target.getBackground().setColorFilter(
                        (int) valueAnimator.getAnimatedValue(), PorterDuff.Mode.SRC_IN);
            }
        });

        return animator;
    }

    public static ValueAnimator setColors(ValueAnimator animator, int startColor, int endColor) {
        animator.setIntValues(startColor, endColor);
        return animator;
    }
}
