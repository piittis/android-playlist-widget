package com.wavy.spotifyplaylistwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.wavy.spotifyplaylistwidget.network.SpotifyApi;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.PositionAssertions.isAbove;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.*;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ArrangeActivityTests {


    @Rule
    public ActivityTestRule<ArrangeActivity> mActivityTestRule = new ActivityTestRule<>(ArrangeActivity.class, true, false);


    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();


    private ArrangeActivityInteractor interactor;

    @Before
    public void setup() {


        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
        intent.putParcelableArrayListExtra("mPlaylists", getTestPlaylists(5));

        mActivityTestRule.launchActivity(intent);
        interactor = new ArrangeActivityInteractor(mActivityTestRule.getActivity());

    }

    @Test
    public void dragArrangeWorks() {
        interactor.moveDown("Playlist0");
        onView(withText("Playlist1")).check(isAbove(withText("Playlist0")));
        interactor.moveUp("Playlist0");
        onView(withText("Playlist0")).check(isAbove(withText("Playlist1")));
    }

    private ArrayList<PlaylistViewModel> getTestPlaylists(int count) {
        ArrayList<PlaylistViewModel> models = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            models.add(new PlaylistViewModel("Playlist"+i, "id"+i, "uri"+i, null, i, "user"+1));
        }
        return models;
    }
}
