package com.wavy.spotifyplaylistwidget;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

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
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
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

@RunWith(AndroidJUnit4.class)
public class SelectActivityTests {

    @Rule
    public ActivityTestRule<SelectActivity> mActivityTestRule = new ActivityTestRule<>(SelectActivity.class, true, false);

    @Mock
    SpotifyApi spotifyApiMock;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private CountingIdlingResource networkIdlingResource = new CountingIdlingResource("apiCall");

    private SelectActivityInteractor interactor;

    @Before
    public void setup() {
        Espresso.registerIdlingResources(networkIdlingResource);
        spotifyApiMock = mock(SpotifyApi.class);
        SpotifyApi.setInstance(spotifyApiMock);

        when(spotifyApiMock.isAccessTokenSet()).thenReturn(true);
        setSpotifyApiResultDefault();

        mActivityTestRule.launchActivity(new Intent());
        interactor = new SelectActivityInteractor(mActivityTestRule.getActivity());

        Assert.assertTrue(interactor.waitForPlaylistsDataLoaded(5000));
    }

    @Test
    public void clickingRowTogglesCheckboxStatus() {

        interactor.clickRow(0);
        interactor.clickRow(2);
        interactor.clickRow(4);

        Assert.assertTrue(interactor.getCheckBoxAtPosition(0).isChecked());
        Assert.assertTrue(interactor.getCheckBoxAtPosition(2).isChecked());
        Assert.assertTrue(interactor.getCheckBoxAtPosition(4).isChecked());

        interactor.clickRow(0);
        interactor.clickRow(2);
        interactor.clickRow(4);

        Assert.assertFalse(interactor.getCheckBoxAtPosition(0).isChecked());
        Assert.assertFalse(interactor.getCheckBoxAtPosition(2).isChecked());
        Assert.assertFalse(interactor.getCheckBoxAtPosition(4).isChecked());
    }

    @Test
    public void selectAllAndRemoveSelectionMenuActionsWork() {

        int itemCount = interactor.getRowCount();

        interactor.selectAll();

        for (int i = 0; i < itemCount; i++) {
            Assert.assertTrue(interactor.getCheckBoxAtPosition(i).isChecked());
        }

        interactor.removeSelections();

        for (int i = 0; i < itemCount; i++) {
            Assert.assertFalse(interactor.getCheckBoxAtPosition(i).isChecked());
        }
    }

    @Test
    public void newDataLoadDoesNotClearSelections() {

        interactor.clickRow(0);
        interactor.clickRow(2);
        interactor.clickRow(4);

        setSpotifyApiResult(getTestPlaylists(50));
        interactor.refresh();

        Assert.assertTrue(interactor.getCheckBoxAtPosition(0).isChecked());
        Assert.assertTrue(interactor.getCheckBoxAtPosition(2).isChecked());
        Assert.assertTrue(interactor.getCheckBoxAtPosition(4).isChecked());
    }

    @Test
    public void transitionToArrangeActivityWorks() {
        interactor.clickRow(0);
        interactor.clickRow(2);
        interactor.clickRow(4);

        interactor.clickNext();

        onView(withId(R.id.playlist_arrange_list))
                .check(ViewAssertions.matches(
                        allOf(
                                hasDescendant(withText("Playlist0")),
                                hasDescendant(withText("Playlist2")),
                                hasDescendant(withText("Playlist4"))
                        )
                ));
    }

    /**
     * Set the mock api to return a default set of playlists
     */
    private void setSpotifyApiResultDefault() {
        doAnswer(invocationOnMock -> {
            SpotifyApi.playlistsLoadedCallbackListener listener = invocationOnMock.getArgument(1);
            networkIdlingResource.increment();

            // Simulate some network delay before calling callback.
            new Handler(getTargetContext().getMainLooper()).postDelayed(() -> {
                networkIdlingResource.decrement();
                listener.onPlaylistsLoaded(0, getTestPlaylists(50));
            }, 1000);

            return null;
        }).when(spotifyApiMock).getPlaylists(anyInt(), any());
    }

    /**
     * Set the mock api to return specific set of playlists
     */
    private void setSpotifyApiResult(ArrayList<PlaylistViewModel> models) {
        doAnswer(invocationOnMock -> {
            SpotifyApi.playlistsLoadedCallbackListener listener = invocationOnMock.getArgument(1);
            networkIdlingResource.increment();

            // Simulate some network delay before calling callback.
            new Handler(getTargetContext().getMainLooper()).postDelayed(() -> {
                networkIdlingResource.decrement();
                listener.onPlaylistsLoaded(0, models);
            }, 1000);

            return null;
        }).when(spotifyApiMock).getPlaylists(anyInt(), any());
    }

    /**
     * Set the mock api to return a specific playlist
     */
    private void setSpotifyApiResult(PlaylistViewModel model) {
        ArrayList<PlaylistViewModel> models = new ArrayList<>();
        models.add(model);
        setSpotifyApiResult(models);
    }

    private ArrayList<PlaylistViewModel> getTestPlaylists(int count) {
        ArrayList<PlaylistViewModel> models = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            models.add(new PlaylistViewModel("Playlist"+i, "id"+i, "uri"+i, null, i, "user"+1));
        }
        return models;
    }
}