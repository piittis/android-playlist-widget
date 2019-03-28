package com.wavy.spotifyplaylistwidget.utils;

import android.content.Context;


/**
 * Helper class to get correct size for the app window.
 * Only use if app is not fullscreen. Not currently used but kepts for future reference.
 */
public class WindowSizeHelper {

    private final static int maxWidthDp = 500;
    private final static int maxHeightDp = 1000;
    private final static int paddingDp = 14;

    public static int getAppWindowWidth(Context context) {
        int screenWidthDp = context.getResources().getConfiguration().screenWidthDp;
        return DpToPx(context, Math.min(screenWidthDp, maxWidthDp) - paddingDp * 2);
    }

    public static int getAppWindowHeight(Context context) {
        int screenHeightDp = context.getResources().getConfiguration().screenHeightDp;
        return DpToPx(context, Math.min(screenHeightDp, maxHeightDp) - paddingDp * 2);
    }

    private static int DpToPx(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
