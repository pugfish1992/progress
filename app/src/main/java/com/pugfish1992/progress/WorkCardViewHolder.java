package com.pugfish1992.progress;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.transition.Transition;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pugfish1992.progress.component.WorkCardView;
import com.pugfish1992.progress.utils.TransitionUtils;

/**
 * Created by daichi on 10/23/17.
 */

public class WorkCardViewHolder extends RecyclerView.ViewHolder {

    WorkCardView workCardView;
    private PartialAnimatableAdapter mPartialAnimatableAdapter;

    public static WorkCardViewHolder createWithView(ViewGroup parent, PartialAnimatableAdapter partialAnimatableAdapter) {
        WorkCardView view = (WorkCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work_card, parent, false);
        return new WorkCardViewHolder(view, partialAnimatableAdapter);
    }

    public WorkCardViewHolder(WorkCardView view, PartialAnimatableAdapter partialAnimatableAdapter) {
        super(view);
        workCardView = view;
        mPartialAnimatableAdapter = partialAnimatableAdapter;
        init();
    }

    private void init() {
        workCardView.setRootOfTransition(mPartialAnimatableAdapter.getRootOfTransition());
        workCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workCardView.beginBatchedStateChanges();
                workCardView.setExpanded(!workCardView.isExpanded());
                workCardView.setCircleActive(!workCardView.isCircleActive());
                workCardView.setAreTitleAndSubTitleActive(!workCardView.areTitleAndSubTitleActive());
                workCardView.endBatchedStateChangesWithAnimation(400, 0,
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
        workCardView.setTitle(title);
        workCardView.setSubTitle(sub);
        workCardView.setCircleNumber(position + 1);
    }

    /* ------------------------------------------------------------------------------- *
     * VH ATTACHED TO A COMMENT VIEW (NOT FOR RECYCLER VIEW)
     * ------------------------------------------------------------------------------- */

    public static class CommentVh {

        static final int KEY_VH = 210497576;
        @LayoutRes static final int LAYOUT_RES_ID = R.layout.item_comment;

        final View commentView;
        int id;
        TextView comment;

        public static CommentVh getCommentVhFromView(View view) {
            Object tag = view.getTag(KEY_VH);
            if (tag == null) {
                throw new IllegalArgumentException("specified view does not have a CommentVh");
            }
            return (CommentVh) tag;
        }

        public CommentVh(View view) {
            this.commentView = view;
            view.setTag(KEY_VH, this);

            comment = view.findViewById(R.id.comment);
        }

        public void bindData(int position) {

        }
    }
}
