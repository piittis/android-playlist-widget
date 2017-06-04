package com.wavy.spotifyplaylistwidget;


import android.support.test.espresso.action.ViewActions;
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
                .perform(ViewActions.swipeDown());
    }

    public void moveUp(String name) {
        onView(allOf(withId(R.id.playlist_drag_handle), withParent(hasDescendant(withText(name)))))
                .perform(ViewActions.swipeUp());
    }

}
