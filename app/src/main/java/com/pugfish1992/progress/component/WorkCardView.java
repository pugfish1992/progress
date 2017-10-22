package com.pugfish1992.progress.component;

/**
 * Created by daichi on 10/22/17.
 */

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pugfish1992.progress.R;
import com.pugfish1992.progress.utils.BackgroundTintAnimation;
import com.pugfish1992.progress.utils.CrossFadeAnimation;
import com.pugfish1992.progress.utils.TransitionUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class WorkCardView extends RelativeLayout {

    // # UI

    // Circle (bg)
    private ViewGroup mCircleBgLayout;
    @ColorInt private int mActiveCircleBgColor;
    @ColorInt private int mInactiveCircleBgColor;
    // Circle (text label)
    private TextView mCircleTextView;
    // Circle (icon label)
    private ImageView mCircleIconView;
    // Title
    private TextView mActiveTitleView;
    private TextView mInactiveTitleView;
    // Sub Title
    private TextView mActiveSubTitleView;
    private TextView mInactiveSubTitleView;
    // Expandable Contents Container
    private FrameLayout mExpandableContentsContainer;
    // Expanded content
    private View mExpandedContent;
    private LinearLayout mExpandedChildContentsContainer;
    // Collapsed content
    private TextView mCollapsedContent;

    // # DRAWING

    // Connection Line
    private Paint mConnectionLinePaint;
    private int mConnectionLineTopSpace;
    private int mConnectionLineBottomSpace;

    public WorkCardView(Context context) {
        super(context);
        init(null, 0);
    }

    public WorkCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public WorkCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load the layout file & attributes
        inflate(getContext(), R.layout.component_work_card_view, this);

        // # UI

        // Circle (bg)
        mCircleBgLayout = findViewById(R.id.fl_circle_bg);
        mActiveCircleBgColor = ContextCompat.getColor(getContext(),
                R.color.work_card_view_active_circle_background);
        mInactiveCircleBgColor = ContextCompat.getColor(getContext(),
                R.color.work_card_view_inactive_circle_background);
        // Circle (text label)
        mCircleTextView = findViewById(R.id.txt_circle_text);
        // Circle (text icon)
        mCircleIconView = findViewById(R.id.img_circle_icon);
        // Title
        mActiveTitleView = findViewById(R.id.txt_active_title);
        mInactiveTitleView = findViewById(R.id.txt_inactive_title);
        // Sub Title
        mActiveSubTitleView = findViewById(R.id.txt_active_sub_title);
        mInactiveSubTitleView = findViewById(R.id.txt_inactive_sub_title);
        // Expandable Contents Container
        mExpandableContentsContainer = findViewById(R.id.fl_expandable_contents_container);
        // Expanded content
        mExpandedContent = findViewById(R.id.cv_expanded_content);
        mExpandedChildContentsContainer = findViewById(R.id.ll_expanded_contents_container);
        // Collapsed content
        mCollapsedContent = findViewById(R.id.txt_collapsed_content);

        // # DRAWING

        // Connection Line

        mConnectionLinePaint = new Paint();
        mConnectionLinePaint.setColor(ContextCompat.getColor(
                getContext(), R.color.work_card_view_connection_line));
        mConnectionLinePaint.setStrokeWidth(getResources().getDimensionPixelSize(
                R.dimen.work_card_view_connection_line_width));

        mConnectionLineTopSpace = getResources().getDimensionPixelSize(
                R.dimen.work_card_view_connection_line_top_space);
        mConnectionLineBottomSpace = getResources().getDimensionPixelSize(
                R.dimen.work_card_view_connection_line_bottom_space);

        // # Add ripple-effect to myself
        // # See -> https://stackoverflow.com/questions/37987732/programatically-set-selectableitembackground-on-android-view

        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        this.setBackgroundResource(outValue.resourceId);
        this.setClickable(true);

        // # INITIALIZE STATUS

        setExpanded(false, false, 0, 0, true);
        setCircleActive(false, false, 0, 0, true);
        setShowCircleIcon(false, false, 0, 0, true);
        setAreTitleAndSubTitleActive(false, false, 0, 0, true);
    }

    /**
     * TITLE
     * ---------- */

    public void setTitle(String title) {
        mActiveTitleView.setText(title);
        mInactiveTitleView.setText(title);
    }

    /**
     * SUB TITLE
     * ---------- */

    public void setSubTitle(String subTitle) {
        mActiveSubTitleView.setText(subTitle);
        mInactiveSubTitleView.setText(subTitle);
//        if (subTitle == null || subTitle.length() == 0) {
//            mActiveSubTitleView.setVisibility(GONE);
//            mInactiveSubTitleView.setVisibility(GONE);
//        } else {
//            mActiveSubTitleView.setVisibility(VISIBLE);
//            mInactiveSubTitleView.setVisibility(VISIBLE);
//        }
    }

    /**
     * CIRCLE
     * ---------- */

    public void setCircleText(String text) {
        mCircleTextView.setText(text);
    }

    public void setCircleNumber(int number) {
        mCircleTextView.setText(String.valueOf(number));
    }

    public void setCircleIcon(@DrawableRes int resId) {
        mCircleIconView.setImageResource(resId);
    }

    /**
     * CONTENT (WHEN EXPANDED)
     * ---------- */

    public View addExpandedContentView(@LayoutRes int layoutResId) {
        return addExpandedContentView(layoutResId, 0);
    }

    public View addExpandedContentView(@LayoutRes int layoutResId, int position) {
        View view = LayoutInflater.from(getContext())
                .inflate(layoutResId, mExpandedChildContentsContainer, false);
        addExpandedContentView(view, position);
        return view;
    }

    public void addExpandedContentView(View view) {
        addExpandedContentView(view, 0);
    }

    public void addExpandedContentView(View view, int position) {
        if (view == null) return;
        mExpandedChildContentsContainer.addView(view, position);
    }

    public void removeExpandedContentView(View view) {
        if (view == null) return;
        mExpandedChildContentsContainer.removeView(view);
    }

    public void removeExpandedContentViewAt(int position) {
        mExpandedChildContentsContainer.removeViewAt(position);
    }

    public View getExpandedContentViewAt(int position) {
        return mExpandedChildContentsContainer.getChildAt(position);
    }

    public int getExpandedContentViewCount() {
        return mExpandedChildContentsContainer.getChildCount();
    }

    /**
     * CONTENT (WHEN COLLAPSED)
     * ---------- */

    public void setMessageOnCollapsed(String message) {
        mCollapsedContent.setText(message);
    }

    /**
     * CONNECTION LINE
     * ---------- */

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Connection Line
        int connectionLineX = mCircleBgLayout.getLeft() + mCircleBgLayout.getWidth() / 2;
        int connectionLineStartY = mCircleBgLayout.getBottom() + mConnectionLineTopSpace;
        int contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int connectionLineEndY = contentHeight - mConnectionLineBottomSpace;

        canvas.drawLine(
                connectionLineX, connectionLineStartY,
                connectionLineX, connectionLineEndY,
                mConnectionLinePaint);
    }

    /**
     * CARD STATUS
     * ---------- */

    private boolean mIsExpanded = false;
    private boolean mIsCircleActive = false;
    private boolean mShowCircleIcon = false;
    private boolean mAreTitleAndSubTitleActive = false;

    public boolean isExpanded() {
        return mIsExpanded;
    }

    public boolean isCircleActive() {
        return mIsCircleActive;
    }

    public boolean isShowCircleIcon() {
        return mShowCircleIcon;
    }

    public boolean areTitleAndSubTitleActive() {
        return mAreTitleAndSubTitleActive;
    }

    /**
     * Needed when call {@link TransitionManager#beginDelayedTransition(ViewGroup)}
     */
    private ViewGroup mRootOfTransition;

    private boolean mHasDelayedTransitionAlreadyBegun = false;

    public void setRootOfTransition(ViewGroup rootOfTransition) {
        mRootOfTransition = rootOfTransition;
    }

    private void beginDelayedAutoTransition(long duration, long startDelay) {
        if (mHasDelayedTransitionAlreadyBegun) return;
        Transition transition = new AutoTransition();
        transition.setDuration(duration);
        transition.setStartDelay(startDelay);
        transition.addListener(new TransitionUtils.TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(@NonNull Transition transition) {
                mHasDelayedTransitionAlreadyBegun = false;
            }
        });
        TransitionManager.beginDelayedTransition(mRootOfTransition, transition);
        mHasDelayedTransitionAlreadyBegun = true;
    }

    public void setExpanded(boolean expanded, boolean animate, long duration, long startDelay, boolean enforceUpdate) {
        if (mIsInBatchedStateChanges) {
            mReservedIsExpanded = expanded;
            return;
        }

        if (mIsExpanded == expanded && !enforceUpdate) return;
        mIsExpanded = expanded;

        if (animate) {
            if (mRootOfTransition != null) {
                beginDelayedAutoTransition(duration, startDelay);
            } else {
                throw new IllegalStateException(
                        "Specify a root view group before call setExpanded()");
            }
        }

        // Hide Content-Container if there is no contents to be shown
        if (mIsExpanded) {
            // Expand
            if (mExpandedChildContentsContainer.getChildCount() == 0) {
                mExpandableContentsContainer.setVisibility(GONE);
            } else {
                mExpandableContentsContainer.setVisibility(VISIBLE);
            }
            mCollapsedContent.setVisibility(GONE);
            mExpandedContent.setVisibility(VISIBLE);

        } else {
            // Collapse
            if (mCollapsedContent.getText() == null ||
                    mCollapsedContent.getText().length() == 0) {
                mExpandableContentsContainer.setVisibility(GONE);
            } else {
                mExpandableContentsContainer.setVisibility(VISIBLE);
            }
            mCollapsedContent.setVisibility(VISIBLE);
            mExpandedContent.setVisibility(GONE);
        }
    }

    public void setExpanded(boolean expanded, boolean animate, long duration, long startDelay) {
        setExpanded(expanded, animate, duration, startDelay, false);
    }

    public void setExpanded(boolean expanded) {
        setExpanded(expanded, false, 0, 0, false);
    }

    public void setShowCircleIcon(boolean showCircleIcon, boolean animate, long duration, long startDelay, boolean enforceUpdate) {
        if (mIsInBatchedStateChanges) {
            mReservedShowCircleIcon = showCircleIcon;
            return;
        }

        if (mShowCircleIcon == showCircleIcon && !enforceUpdate) return;
        mShowCircleIcon = showCircleIcon;

        if (animate) {
            if (mRootOfTransition != null) {
                beginDelayedAutoTransition(duration, startDelay);
            } else {
                throw new IllegalStateException(
                        "Specify a root view group before call setExpanded()");
            }
        }

        mCircleTextView.setVisibility(mShowCircleIcon ? INVISIBLE : VISIBLE);
        mCircleIconView.setVisibility(mShowCircleIcon ? VISIBLE : INVISIBLE);
    }

    public void setShowCircleIcon(boolean showCircleIcon, boolean animate, long duration, long startDelay) {
        setShowCircleIcon(showCircleIcon, animate, duration, startDelay, false);
    }

    public void setShowCircleIcon(boolean showCircleIcon) {
        setShowCircleIcon(showCircleIcon, false, 0, 0, false);
    }

    public void setCircleActive(boolean circleActive, boolean animate, long duration, long startDelay, boolean enforceUpdate) {
        if (mIsInBatchedStateChanges) {
            mReservedIsCircleActive = circleActive;
            return;
        }

        if (mIsCircleActive == circleActive && !enforceUpdate) return;

        int currentColor = mIsCircleActive ? mActiveCircleBgColor : mInactiveCircleBgColor;
        int nextColor = circleActive ? mActiveCircleBgColor : mInactiveCircleBgColor;
        mIsCircleActive = circleActive;

        if (animate) {
            ValueAnimator animator = BackgroundTintAnimation.create(mCircleBgLayout, currentColor, nextColor);
            animator.setDuration(duration);
            animator.setStartDelay(startDelay);
            animator.start();
        } else {
            mCircleBgLayout.getBackground().setColorFilter(
                    nextColor,
                    PorterDuff.Mode.SRC_IN);
        }
    }

    public void setCircleActive(boolean circleActive, boolean animate, long duration, long startDelay) {
        setCircleActive(circleActive, animate, duration, startDelay, false);
    }

    public void setCircleActive(boolean circleActive) {
        setCircleActive(circleActive, false, 0, 0, false);
    }

    public void setAreTitleAndSubTitleActive(boolean areTitleAndSubTitleActive, boolean animate, long duration, long startDelay, boolean enforceUpdate) {
        if (mIsInBatchedStateChanges) {
            mReservedAreTitleAndSubTitleActive = areTitleAndSubTitleActive;
            return;
        }

        if (mAreTitleAndSubTitleActive == areTitleAndSubTitleActive && !enforceUpdate) return;
        mAreTitleAndSubTitleActive = areTitleAndSubTitleActive;

        if (animate) {
            if (mAreTitleAndSubTitleActive) {
                CrossFadeAnimation.animate(mInactiveTitleView, mActiveTitleView, duration, startDelay);
                CrossFadeAnimation.animate(mInactiveSubTitleView, mActiveSubTitleView, duration, startDelay);
            } else {
                CrossFadeAnimation.animate(mActiveTitleView, mInactiveTitleView, duration, startDelay);
                CrossFadeAnimation.animate(mActiveSubTitleView, mInactiveSubTitleView, duration, startDelay);
            }

        } else {
            if (mAreTitleAndSubTitleActive) {
                mActiveTitleView.setVisibility(VISIBLE);
                mActiveSubTitleView.setVisibility(VISIBLE);
                mInactiveTitleView.setVisibility(INVISIBLE);
                mInactiveSubTitleView.setVisibility(INVISIBLE);
            } else {
                mActiveTitleView.setVisibility(INVISIBLE);
                mActiveSubTitleView.setVisibility(INVISIBLE);
                mInactiveTitleView.setVisibility(VISIBLE);
                mInactiveSubTitleView.setVisibility(VISIBLE);
            }
        }
    }

    public void setAreTitleAndSubTitleActive(boolean areTitleAndSubTitleActive, boolean animate, long duration, long startDelay) {
        setAreTitleAndSubTitleActive(areTitleAndSubTitleActive, animate, duration, startDelay, false);
    }

    public void setAreTitleAndSubTitleActive(boolean areTitleAndSubTitleActive) {
        setAreTitleAndSubTitleActive(areTitleAndSubTitleActive, false, 0, 0, false);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(
            flag = true,
            value = {ANIM_EXPAND, ANIM_COLLAPSE,
                    ANIM_ACTIVATE_CIRCLE, ANIM_INACTIVATE_CIRCLE,
                    ANIM_SHOW_CIRCLE_ICON, ANIM_HIDE_CIRCLE_ICON,
                    ANIM_ACTIVATE_TITLE_SUBTITLE, ANIM_INACTIVATE_TITLE_SUBTITLE})
    public @interface AnimationFlag{}
    public static final int ANIM_EXPAND = 1;
    public static final int ANIM_COLLAPSE = 1 << 1;
    public static final int ANIM_ACTIVATE_CIRCLE = 1 << 2;
    public static final int ANIM_INACTIVATE_CIRCLE = 1 << 3;
    public static final int ANIM_SHOW_CIRCLE_ICON = 1 << 4;
    public static final int ANIM_HIDE_CIRCLE_ICON = 1 << 5;
    public static final int ANIM_ACTIVATE_TITLE_SUBTITLE = 1 << 6;
    public static final int ANIM_INACTIVATE_TITLE_SUBTITLE = 1 << 7;

    /**
     * Use this method for complexity animation instead of calling some certain methods (
     * {@link WorkCardView#setExpanded(boolean, boolean, long, long)},
     * {@link WorkCardView#setCircleActive(boolean, boolean, long, long)}, etc...) individually.
     */
    public void applyStateChangesWithAnimation(@AnimationFlag int flags, long duration, long startDelay) {
        if ((flags & ANIM_ACTIVATE_CIRCLE) != 0) {
            setCircleActive(true, true, duration, startDelay, false);
        }
        if ((flags & ANIM_INACTIVATE_CIRCLE) != 0) {
            setCircleActive(false, true, duration, startDelay, false);
        }
        if ((flags & ANIM_ACTIVATE_TITLE_SUBTITLE) != 0) {
            setAreTitleAndSubTitleActive(true, true, duration, startDelay, false);
        }
        if ((flags & ANIM_INACTIVATE_TITLE_SUBTITLE) != 0) {
            setAreTitleAndSubTitleActive(false, true, duration, startDelay, false);
        }
        // Animation which use TransitionManager must be executed
        // after execute the other kinds of animation
        if ((flags & ANIM_EXPAND) != 0) {
            setExpanded(true, true, duration, startDelay, false);
        }
        if ((flags & ANIM_COLLAPSE) != 0) {
            setExpanded(false, true, duration, startDelay, false);
        }
        if ((flags & ANIM_SHOW_CIRCLE_ICON) != 0) {
            setShowCircleIcon(true, true, duration, startDelay, false);
        }
        if ((flags & ANIM_HIDE_CIRCLE_ICON) != 0) {
            setShowCircleIcon(false, true, duration, startDelay, false);
        }
    }

    private boolean mReservedIsExpanded;
    private boolean mReservedIsCircleActive;
    private boolean mReservedShowCircleIcon;
    private boolean mReservedAreTitleAndSubTitleActive;
    private boolean mIsInBatchedStateChanges = false;

    /**
     * Make sure to call {@link WorkCardView#endBatchedStateChangesWithAnimation(long, long)} after call this method.
     */
    public void beginBatchedStateChanges() {
        mReservedIsExpanded = isExpanded();
        mReservedIsCircleActive = isCircleActive();
        mReservedShowCircleIcon = isShowCircleIcon();
        mReservedAreTitleAndSubTitleActive = areTitleAndSubTitleActive();
        mIsInBatchedStateChanges = true;
    }

    /**
     * Convenience method for creating a flag used as a first parameter of
     * {@link WorkCardView#applyStateChangesWithAnimation(int, long, long)} automatically.
     * Make sure to call {@link WorkCardView#beginBatchedStateChanges()} before call this method.
     * Use this method for complexity animation instead of calling some certain methods (
     * {@link WorkCardView#setExpanded(boolean, boolean, long, long)},
     * {@link WorkCardView#setCircleActive(boolean, boolean, long, long)}, etc...) individually.
     */
    public void endBatchedStateChangesWithAnimation(long animationDuration, long startAnimationDelay) {
        @AnimationFlag int flag = 0;
        flag |= mReservedIsExpanded ? WorkCardView.ANIM_EXPAND : WorkCardView.ANIM_COLLAPSE;
        flag |= mReservedIsCircleActive ? WorkCardView.ANIM_ACTIVATE_CIRCLE : WorkCardView.ANIM_INACTIVATE_CIRCLE;
        flag |= mReservedShowCircleIcon ? WorkCardView.ANIM_SHOW_CIRCLE_ICON : WorkCardView.ANIM_HIDE_CIRCLE_ICON;
        flag |= mReservedAreTitleAndSubTitleActive ? WorkCardView.ANIM_ACTIVATE_TITLE_SUBTITLE : WorkCardView.ANIM_INACTIVATE_TITLE_SUBTITLE;
        mIsInBatchedStateChanges = false;
        applyStateChangesWithAnimation(flag, animationDuration, startAnimationDelay);
    }
}
