package com.wavy.spotifyplaylistwidget;


import android.appwidget.AppWidgetManager;
import android.content.Intent;

import com.wavy.spotifyplaylistwidget.interaction.SelectActivityInteractor;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import io.reactivex.Observable;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

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
        setSpotifyApiResult(60);
    }

    @Test
    public void initializeFromDbWorks() {

        ArrayList<PlaylistViewModel> testPlaylists = getTestPlaylists(60);
        ArrayList<PlaylistViewModel> playlistsInWidget = new ArrayList<>();
        for (int i = 0; i < testPlaylists.size(); i+=2)
            playlistsInWidget.add((testPlaylists.get(i)));

        insertMockWidget(playlistsInWidget);
        // the playlists in the pre-existing widget should be selected.
        openActivity();
        for (int i = 0; i < 60; i+=2)
            Assert.assertTrue(interactor.getCheckBoxAtPosition(i).isChecked());
        for (int i = 1; i < 60; i+=2)
            Assert.assertFalse(interactor.getCheckBoxAtPosition(i).isChecked());
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

        setSpotifyApiResult(getTestPlaylists(60));
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
    public void transitionToArrangeActivityWorksWith10kPlaylists() {

        setSpotifyApiResult(10000);
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

        Assert.assertTrue(interactor.waitForPlaylistsDataLoaded(50000));
    }

    /**
     * Set the mock api to return a default set of playlists
     */
    private void setSpotifyApiResult(int playlistCount) {
        doAnswer(invocationOnMock -> Observable.fromIterable(getTestPlaylists(playlistCount))
                                            .window(50, 50)
                                            .flatMapSingle(w -> w.toList())
                                            .doOnEach(e -> networkIdlingResource.increment())
                                            .zipWith(Observable.interval(50, TimeUnit.MILLISECONDS), (batch, interval) -> batch)
                                            .doOnEach(e -> networkIdlingResource.decrement())).when(mockSpotifyApi).getPlaylists();
    }

    /**
     * Set the mock api to return specific set of playlists
     */
    private void setSpotifyApiResult(ArrayList<PlaylistViewModel> models) {
        doAnswer(invocationOnMock -> Observable.fromIterable(models)
                                            .window(50, 50)
                                            .flatMapSingle(w -> w.toList())
                                            .doOnEach(e -> networkIdlingResource.increment())
                                            .zipWith(Observable.interval(50, TimeUnit.MILLISECONDS), (batch, interval) -> batch)
                                            .doOnEach(e -> networkIdlingResource.decrement())).when(mockSpotifyApi).getPlaylists();
    }

}