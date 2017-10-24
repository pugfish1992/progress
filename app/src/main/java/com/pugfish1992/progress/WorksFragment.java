package com.pugfish1992.progress;


import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.pugfish1992.progress.component.WorkCardView;
import com.pugfish1992.progress.utils.CheapIdGenerator;


public class WorksFragment extends Fragment {

    private RecyclerView mWorksListView;

    public WorksFragment() {
        // Required empty public construct
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_works, container, false);

        mWorksListView = view.findViewById(R.id.recycle_work_list);
        mWorksListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWorksListView.setAdapter(new WorkAdapter());
        return view;
    }

    /* ------------------------------------------------------------------------------- *
     * ADAPTER
     * ------------------------------------------------------------------------------- */

    private class WorkAdapter extends RecyclerView.Adapter<WorkCardViewHolder>
            implements PartialAnimatableAdapter {

        private Recycler mCommentViewRecycler;

        WorkAdapter() {
            mCommentViewRecycler = new Recycler(WorkCardViewHolder.CommentVh.LAYOUT_RES_ID);
        }

        @Override
        public WorkCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return WorkCardViewHolder.createWithView(parent, this);
        }

        @Override
        public void onBindViewHolder(WorkCardViewHolder holder, int position) {
            holder.bindData("pugfish1992", "pugfish.on.github@gmail.com", position);
            // Comment
            int commentCount = position % 10 + 1;
            mCommentViewRecycler.manageCommetViews(holder.workCardView, commentCount);
            for (int i = 0; i < commentCount; ++i) {
                WorkCardViewHolder.CommentVh.getCommentVhFromView(
                        holder.workCardView.getExpandedContentViewAt(i)).bindData(i);
            }
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        private final View.OnTouchListener TOUCH_EATER = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        };

        private RecyclerView.ItemAnimator mPresetAnimator;

        @Override
        public void enablePartialItemAnimation() {
            mWorksListView.setOnTouchListener(TOUCH_EATER);
            mPresetAnimator = mWorksListView.getItemAnimator();
            mWorksListView.setItemAnimator(null);
        }

        @Override
        public void disablePartialItemAnimation() {
            mWorksListView.setOnTouchListener(null);
            mWorksListView.setItemAnimator(mPresetAnimator);
            mPresetAnimator = null;
        }

        @NonNull
        @Override
        public ViewGroup getRootOfTransition() {
            return mWorksListView;
        }
    }

    /* ------------------------------------------------------------------------------- *
     * RECYCLER FOR COMMENT VIEWS
     * ------------------------------------------------------------------------------- */

    private static class Recycler {

        private static final int POSITION_TO_ATTACH_OR_REMOVE = 0;

        private final CheapIdGenerator ID_GENERATOR;
        @LayoutRes
        private final int COMMENT_VIEW_LAYOUT;
        private SparseArray<View> mAvailableViews;
        private SparseArray<View> mInUseViews;

        Recycler(@LayoutRes int commentViewLayoutRes) {
            ID_GENERATOR = new CheapIdGenerator();
            COMMENT_VIEW_LAYOUT = commentViewLayoutRes;
            mAvailableViews = new SparseArray<>();
            mInUseViews = new SparseArray<>();
        }

        void manageCommetViews(WorkCardView workCardView, int requestViewCount) {
            final int childCount = workCardView.getExpandedContentViewCount();
            if (childCount == requestViewCount) return;

            if (childCount < requestViewCount) {
                for (int i = 0; i < requestViewCount - childCount; ++i) {
                    View view = getAvailableViewIfExist();
                    if (view == null) {
                        view = createAndAttachNewView(workCardView);
                    } else {
                        workCardView.addExpandedContentView(view, POSITION_TO_ATTACH_OR_REMOVE);
                    }

                    makeViewInUse(view);
                }

            } else {
                for (int i = 0; i < childCount - requestViewCount; ++i) {
                    View view = workCardView.getExpandedContentViewAt(POSITION_TO_ATTACH_OR_REMOVE);
                    workCardView.removeExpandedContentViewAt(POSITION_TO_ATTACH_OR_REMOVE);
                    makeViewAvailable(view);
                }
            }

            // TODO; REMOVE (for debug)
            int available = mAvailableViews.size();
            int inUse = mInUseViews.size();
            int cached = available + inUse;
            Log.d(Recycler.class.getName(), "cached=" + cached + "(" + "av:" + available + "/iu:" + inUse + ") / 110 [max comments]");
        }

        private View createAndAttachNewView(WorkCardView parent) {
            View view = parent.addExpandedContentView(COMMENT_VIEW_LAYOUT, POSITION_TO_ATTACH_OR_REMOVE);
            WorkCardViewHolder.CommentVh vh = new WorkCardViewHolder.CommentVh(view);
            vh.id = ID_GENERATOR.generate();
            return vh.commentView;
        }

        @Nullable
        private View getAvailableViewIfExist() {
            if (mAvailableViews.size() != 0) {
                int key  = mAvailableViews.keyAt(mAvailableViews.size() - 1);
                return mAvailableViews.get(key);
            }
            return null;
        }

        private void makeViewInUse(WorkCardViewHolder.CommentVh vh) {
            if (BuildConfig.DEBUG && mInUseViews.get(vh.id, null) != null) {
                throw new AssertionError("specified vh has been already marked as a 'in used view'");
            }
            mInUseViews.put(vh.id, vh.commentView);
            mAvailableViews.remove(vh.id);
        }

        private void makeViewInUse(View view) {
            makeViewInUse(WorkCardViewHolder.CommentVh.getCommentVhFromView(view));
        }

        private void makeViewAvailable(View view) {
            if (view.getParent() != null) {
                throw new IllegalStateException("specified view already has a parent view");
            }

            WorkCardViewHolder.CommentVh vh = WorkCardViewHolder.CommentVh.getCommentVhFromView(view);

            if (BuildConfig.DEBUG && mAvailableViews.get(vh.id, null) != null) {
                throw new AssertionError("specified vh has been already marked as a 'available view'");
            }

            mInUseViews.remove(vh.id);
            mAvailableViews.put(vh.id, view);
        }
    }
}
