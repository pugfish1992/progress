package com.pugfish1992.progress;

import android.support.annotation.NonNull;
import android.support.transition.Transition;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pugfish1992.progress.component.WorkCardView;
import com.pugfish1992.progress.utils.TransitionUtils;

/**
 * Created by daichi on 10/23/17.
 */

public class WorkCardViewHolder extends RecyclerView.ViewHolder {

    private WorkCardView mWorkCardView;
    private PartialAnimatableAdapter mPartialAnimatableAdapter;

    public static WorkCardViewHolder createWithView(ViewGroup parent, PartialAnimatableAdapter partialAnimatableAdapter) {
        WorkCardView view = (WorkCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work_card, parent, false);
        return new WorkCardViewHolder(view, partialAnimatableAdapter);
    }

    public WorkCardViewHolder(WorkCardView view, PartialAnimatableAdapter partialAnimatableAdapter) {
        super(view);
        mWorkCardView = view;
        mPartialAnimatableAdapter = partialAnimatableAdapter;
        init();
    }

    private void init() {
        mWorkCardView.setRootOfTransition(mPartialAnimatableAdapter.getRootOfTransition());
        mWorkCardView.addExpandedContentView(R.layout.item_comment);

        mWorkCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWorkCardView.beginBatchedStateChanges();
                mWorkCardView.setExpanded(!mWorkCardView.isExpanded());
                mWorkCardView.setCircleActive(!mWorkCardView.isCircleActive());
                mWorkCardView.setAreTitleAndSubTitleActive(!mWorkCardView.areTitleAndSubTitleActive());
                mWorkCardView.endBatchedStateChangesWithAnimation(400, 0,
                        new TransitionUtils.TransitionListenerAdapter() {
                            @Override
                            public void onTransitionStart(@NonNull Transition transition) {
                                mPartialAnimatableAdapter.enablePartialItemAnimation();
                            }

                            @Override
                            public void onTransitionEnd(@NonNull Transition transition) {
                                mPartialAnimatableAdapter.disablePartialItemAnimation();
                            }

                            @Override
                            public void onTransitionCancel(@NonNull Transition transition) {
                                mPartialAnimatableAdapter.disablePartialItemAnimation();
                            }
                        });
            }
        });
    }

    public void bindData(String title, String sub, int position) {
        mWorkCardView.setTitle(title);
        mWorkCardView.setSubTitle(sub);
        mWorkCardView.setCircleNumber(position + 1);
    }
}
