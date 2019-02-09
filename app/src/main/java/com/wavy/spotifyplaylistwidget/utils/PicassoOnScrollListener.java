package com.wavy.spotifyplaylistwidget.utils;


import com.squareup.picasso.Picasso;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Helper to pause picasso image loading when scrolling a RecyclerView.
 */
public class PicassoOnScrollListener extends RecyclerView.OnScrollListener {

    public static final Object RECYCLVIEW_TAG = new Object();
    private static final int RESUME_DELAY = 333;
    private static final int RESUME_SPEED_THRESHOLD = 50;

    private Picasso sPicasso;
    private Runnable mSettlingResumeRunnable = null;
    private Boolean mPaused = false;

    public PicassoOnScrollListener() {
        sPicasso = Picasso.get();
    }

    private void resumeDelayed(RecyclerView recyclerView) {

        // Set a callback to resume image loading soon.
        mSettlingResumeRunnable = () -> sPicasso.resumeTag(RECYCLVIEW_TAG);
        recyclerView.postDelayed(mSettlingResumeRunnable, RESUME_DELAY);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {

        // Make sure to resume when scrolling stops.
        if(scrollState == RecyclerView.SCROLL_STATE_IDLE) {
            if (mPaused) {
                mPaused = false;
                recyclerView.removeCallbacks(mSettlingResumeRunnable);
                sPicasso.resumeTag(RECYCLVIEW_TAG);
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        if (Math.abs(dy) <= RESUME_SPEED_THRESHOLD) {
            // Scrolling is settling down... resume loading images soon.
            if (mPaused) {
                mPaused = false;
                resumeDelayed(recyclerView);
            }
        } else {
            // Scrolling fast, pause if not already.
            if (!mPaused) {
                mPaused = true;
                sPicasso.pauseTag(RECYCLVIEW_TAG);
                recyclerView.removeCallbacks(mSettlingResumeRunnable);
            }
        }
    }
}