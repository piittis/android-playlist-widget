package com.wavy.spotifyplaylistwidget;


import android.os.SystemClock;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistSelectionAdapter;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToHolder;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;



@RunWith(AndroidJUnit4.class)
public class SelectActivityTest {




    @Rule
    public ActivityTestRule<SelectActivity> mActivityTestRule = new ActivityTestRule<>(SelectActivity.class);


    @Test
    public void clickingRowTogglesCheckboxStatus() {

        Assert.assertTrue(waitForPlaylistsDataLoaded(5000));

        clickAtPosition(0);
        clickAtPosition(2);
        clickAtPosition(4);

        Assert.assertTrue(getCheckBoxAtPosition(0).isChecked());
        Assert.assertTrue(getCheckBoxAtPosition(2).isChecked());
        Assert.assertTrue(getCheckBoxAtPosition(4).isChecked());

        clickAtPosition(0);
        clickAtPosition(2);
        clickAtPosition(4);

        Assert.assertFalse(getCheckBoxAtPosition(0).isChecked());
        Assert.assertFalse(getCheckBoxAtPosition(2).isChecked());
        Assert.assertFalse(getCheckBoxAtPosition(4).isChecked());
    }

    @Test
    public void selectAllAndRemoveSelectionMenuActionsWork() {

        Assert.assertTrue(waitForPlaylistsDataLoaded(5000));

        int itemCount = mActivityTestRule.getActivity().mPlaylistsSelectionView.getAdapter().getItemCount();

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.select_all)).perform(click());

        for (int i = 0; i < itemCount; i++) {
            Assert.assertTrue(getCheckBoxAtPosition(i).isChecked());
        }

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.remove_selections)).perform(click());

        for (int i = 0; i < itemCount; i++) {
            Assert.assertFalse(getCheckBoxAtPosition(i).isChecked());
        }
    }

    // Todo put private methods and complex things in some other class so test cases are readable

    private void clickAtPosition(int position) {
        ViewInteraction recyclerView = GetSelectionRecyclerview();
        recyclerView.perform(actionOnItemAtPosition(position, click()));
    }

    private CheckBox getCheckBoxAtPosition(int position) {

        GetSelectionRecyclerview().perform(scrollToHolder(matchPlaylistRow(position)));
        View row = mActivityTestRule.getActivity().mPlaylistsSelectionView.getLayoutManager().findViewByPosition(position);
        return (CheckBox) row.findViewById(R.id.playlist_checkbox);
    }

    private ViewInteraction GetSelectionRecyclerview() {
        return onView(
                allOf(withId(R.id.playlist_selection_list),
                        withParent(withId(R.id.swiperefresh)),
                        isDisplayed()));
    }

    /**
     * Serves two purposes. You can wait for playlists data to be available, or assert that it comes
     * available within some timerange. Or Both.
     */
    private Boolean waitForPlaylistsDataLoaded(long timeoutMillis) {
        // todo this can probably be a matcher
        long timeout = System.currentTimeMillis() + timeoutMillis;
        RecyclerView.Adapter adapter = mActivityTestRule.getActivity().mPlaylistsSelectionView.getAdapter();
        while (System.currentTimeMillis() < timeout) {
            if (adapter.getItemCount() > 0)
                return true;
            SystemClock.sleep(100);
        }
        return false;
    }

    public static Matcher<RecyclerView.ViewHolder> matchPlaylistRow(int position)
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
}