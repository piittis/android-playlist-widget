package com.wavy.spotifyplaylistwidget;


import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.ViewActions;
import android.view.View;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class ArrangeActivityInteractor {

    private ArrangeActivity mActivity;

    public ArrangeActivityInteractor(ArrangeActivity activity) {
        mActivity = activity;
    }

    public void moveDown(String name) {
        onView(allOf(withId(R.id.playlist_drag_handle), withParent(hasDescendant(withText(name)))))
                .perform(down());
    }

    public void moveUp(String name) {
        onView(allOf(withId(R.id.playlist_drag_handle), withParent(hasDescendant(withText(name)))))
                .perform(up());
    }

    private static ViewAction up() {
        return new GeneralSwipeAction(Swipe.SLOW, GeneralLocation.CENTER,
                view -> new float[] {screenX(view), screenY(view) - 500 }, Press.FINGER);
    }

    private static ViewAction down() {
        return new GeneralSwipeAction(Swipe.SLOW, GeneralLocation.CENTER,
                view -> new float[] {screenX(view), screenY(view) + 500 }, Press.FINGER);
    }

    private static int screenX(View view) {
        int [] xy = new int[2];
        view.getLocationOnScreen(xy);
        return xy[0];
    }
    private static int screenY(View view) {
        int [] xy = new int[2];
        view.getLocationOnScreen(xy);
        return xy[1];
    }

}
