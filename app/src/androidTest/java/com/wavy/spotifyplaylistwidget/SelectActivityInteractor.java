package com.wavy.spotifyplaylistwidget;

import android.os.SystemClock;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;


import org.hamcrest.Description;
import org.hamcrest.Matcher;


import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class SelectActivityInteractor {

    private SelectActivity mActivity;

    public SelectActivityInteractor(SelectActivity activity) {
        mActivity = activity;
    }

    public void clickRow(int position) {
        ViewInteraction recyclerView = GetSelectionRecyclerview();
        recyclerView.perform(actionOnItemAtPosition(position, click()));
    }

    public CheckBox getCheckBoxAtPosition(int position) {

        GetSelectionRecyclerview().perform(scrollToPosition(position));
        waitForRow(position, 1000);
        View row = mActivity.mPlaylistsSelectionView.getLayoutManager().findViewByPosition(position);
        return (CheckBox) row.findViewById(R.id.playlist_checkbox);
    }

    public void selectAll() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.select_all)).perform(click());
    }

    public void removeSelections() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.remove_selections)).perform(click());
    }

    public void refresh() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.refresh_playlists)).perform(click());
    }

    public int getRowCount() {
       return mActivity.mPlaylistsSelectionView.getAdapter().getItemCount();
    }

    public void clickNext() {
        onView(withId(R.id.selection_next_button)).perform(click());
    }

    /**
     * Serves two purposes. You can wait for playlists data to be available, or assert that it comes
     * available within some timerange. Or Both.
     */
    public Boolean waitForPlaylistsDataLoaded(long timeoutMillis) {
        long timeout = System.currentTimeMillis() + timeoutMillis;
        RecyclerView.Adapter adapter = mActivity.mPlaylistsSelectionView.getAdapter();
        while (System.currentTimeMillis() < timeout) {
            if (adapter.getItemCount() > 0)
                return true;
            SystemClock.sleep(16);
        }
        return false;
    }

    private Boolean waitForRow(int position, long timeoutMillis) {
        RecyclerView.LayoutManager m = mActivity.mPlaylistsSelectionView.getLayoutManager();
        long timeout = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < timeout) {
            if (m.findViewByPosition(position) != null)
                return true;
            SystemClock.sleep(100);
        }
        return false;
    }

    public Matcher<RecyclerView.ViewHolder> matchPlaylistRow(int position)
    {
        return new BoundedMatcher<RecyclerView.ViewHolder, RecyclerView.ViewHolder>(RecyclerView.ViewHolder.class)
        {
            @Override
            protected boolean matchesSafely(RecyclerView.ViewHolder item) {
                return item.getLayoutPosition() == position;
            }
            @Override
            public void describeTo(Description description)
            {
                description.appendText("view holder with position: " + position);
            }
        };
    }

    private ViewInteraction GetSelectionRecyclerview() {
        return onView(
                allOf(withId(R.id.playlist_selection_list),
                        withParent(withId(R.id.swiperefresh)),
                        isDisplayed()));
    }

}
