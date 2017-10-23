package com.pugfish1992.progress;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

/**
 * Created by daichi on 10/23/17.
 */

public interface PartialAnimatableAdapter {

    @NonNull
    ViewGroup getRootOfTransition();
    void enablePartialItemAnimation();
    void disablePartialItemAnimation();
}
