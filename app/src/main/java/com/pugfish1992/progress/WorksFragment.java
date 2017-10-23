package com.pugfish1992.progress;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


public class WorksFragment extends Fragment {

    private RecyclerView mWorksListView;

    public WorksFragment() {
        // Required empty public constructor
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

    private class WorkAdapter extends RecyclerView.Adapter<WorkCardViewHolder>
            implements PartialAnimatableAdapter {

        @Override
        public WorkCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return WorkCardViewHolder.createWithView(parent, this);
        }

        @Override
        public void onBindViewHolder(WorkCardViewHolder holder, int position) {
            holder.bindData("pugfish1992", "pugfish.on.github@gmail.com", position);
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
}
