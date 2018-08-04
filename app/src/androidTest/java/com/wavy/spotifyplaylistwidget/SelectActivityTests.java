package com.wavy.spotifyplaylistwidget;


import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Handler;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
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
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class SelectActivityTests extends ActivityTestBase {

    @Rule
    public ActivityTestRule<SelectActivity> mActivityTestRule = new ActivityTestRule<>(SelectActivity.class, true, false);

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private CountingIdlingResource networkIdlingResource = new CountingIdlingResource("apiCall");
    private SelectActivityInteractor interactor;

    @Before
    public void setup() {
        super.initialize();

        IdlingRegistry.getInstance().register(networkIdlingResource);

        when(mockSpotifyApi.isAccessTokenValid()).thenReturn(true);
        setSpotifyApiResult(50);
    }

    @Test
    public void clickingRowTogglesCheckboxStatus() {
        openActivity();

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
        openActivity();

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
        openActivity();

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
        openActivity();

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

    @Test
    public void transitionToArrangeActivityWorksWith100kPlaylists() {

        setSpotifyApiResult(100000);
        openActivity();

        interactor.selectAll();

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

    private void openActivity() {
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
        mActivityTestRule.launchActivity(intent);
        interactor = new SelectActivityInteractor(mActivityTestRule.getActivity());

        Assert.assertTrue(interactor.waitForPlaylistsDataLoaded(5000));
    }

    /**
     * Set the mock api to return a default set of playlists
     */
    private void setSpotifyApiResult(int playlistCount) {

        doAnswer(invocationOnMock -> Observable.fromArray(getTestPlaylists(playlistCount))
                                            .doOnEach(e -> networkIdlingResource.increment())
                                            .delay(1000, TimeUnit.MILLISECONDS)
                                            .doOnEach(e -> networkIdlingResource.decrement())).when(mockSpotifyApi).getPlaylists();
    }

    /**
     * Set the mock api to return specific set of playlists
     */
    private void setSpotifyApiResult(ArrayList<PlaylistViewModel> models) {
        doAnswer(invocationOnMock -> Observable.fromArray(models)
                                            .doOnEach(e -> networkIdlingResource.increment())
                                            .delay(1000, TimeUnit.MILLISECONDS)
                                            .doOnEach(e -> networkIdlingResource.decrement())).when(mockSpotifyApi).getPlaylists();
    }

    private ArrayList<PlaylistViewModel> getTestPlaylists(int count) {
        ArrayList<PlaylistViewModel> models = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            models.add(new PlaylistViewModel("Playlist"+i, "id"+i, "uri"+i, null, i, "user"+1));
        }
        return models;
    }
}