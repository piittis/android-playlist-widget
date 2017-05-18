package com.wavy.spotifyplaylistwidget.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.squareup.picasso.Picasso;

/**
 * Helper to pause picasso image loading when scrolling a RecyclerView.
 */
public class PicassoOnScrollListener extends RecyclerView.OnScrollListener {

    public static final Object RECYCLVIEW_TAG = new Object();
    private static final int RESUME_DELAY = 333;
    private static final int RESUME_SPEED_THRESHOLD = 50;

    private Picasso sPicasso = null;
    private Runnable mSettlingResumeRunnable = null;
    private Boolean mPaused = false;

    public PicassoOnScrollListener(Context context) {
        sPicasso = Picasso.with(context.getApplicationContext());
    }

    private void resumeDelayed(RecyclerView recyclerView) {

        // Set a callback to resume image loading soon.
        mSettlingResumeRunnable = new Runnable() {
            @Override
            public void run() {
                sPicasso.resumeTag(RECYCLVIEW_TAG);
            }
        };
        recyclerView.postDelayed(mSettlingResumeRunnable, RESUME_DELAY);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {

        // Make sure we resume when scrolling stops
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
            // Scrolling is settling down... resume loading images soom
            if (mPaused) {
                mPaused = false;
                resumeDelayed(recyclerView);
            }
        } else {
            // Scrolling fast, pause if not already
            if (!mPaused) {
                mPaused = true;
                sPicasso.pauseTag(RECYCLVIEW_TAG);
                recyclerView.removeCallbacks(mSettlingResumeRunnable);
            }
        }
    }
}