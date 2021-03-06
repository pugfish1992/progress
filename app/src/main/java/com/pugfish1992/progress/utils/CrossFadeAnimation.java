package com.pugfish1992.progress.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/**
 * Created by daichi on 10/23/17.
 */

public class CrossFadeAnimation {

    /* Intentional private */
    private CrossFadeAnimation() {}

    public static void animate(final View willHide, View willReveal, long duration, long startDelay) {
        willReveal.setAlpha(0f);
        willReveal.setVisibility(View.VISIBLE);
        willReveal.animate()
                .alpha(1f)
                .setDuration(duration)
                .setStartDelay(startDelay)
                .setListener(null)
                .start();

        willHide.animate()
                .alpha(0f)
                .setDuration(duration)
                .setStartDelay(startDelay)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        willHide.setVisibility(View.GONE);
                    }
                })
                .start();
    }
}
