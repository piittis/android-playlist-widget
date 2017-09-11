package com.wavy.spotifyplaylistwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.PositionAssertions.isAbove;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class ArrangeActivityTests extends ActivityTestBase {

    @Rule
    public ActivityTestRule<ArrangeActivity> mActivityTestRule = new ActivityTestRule<>(ArrangeActivity.class, true, false);

    private ArrangeActivityInteractor interactor;

    @Before
    public void setup() {
        super.initialize();
    }

    @Test
    public void dragArrangeWorks() {

        setupSelectedPlaylists(5);
        setupAndOpenActivity();

        interactor.moveDown("Playlist0");

        onView(withText("Playlist1")).check(isAbove(withText("Playlist0")));
        Assert.assertEquals("id1", playlistsContainer.getSelectedPlaylists().get(0).id);

        interactor.moveUp("Playlist0");

        Assert.assertEquals("id0", playlistsContainer.getSelectedPlaylists().get(0).id);
        onView(withText("Playlist0")).check(isAbove(withText("Playlist1")));
    }

    @Test
    public void dragArrangeWorksWith10kPlaylists() {

        setupSelectedPlaylists(10000);
        setupAndOpenActivity();

        interactor.moveDown("Playlist0");

        onView(withText("Playlist1")).check(isAbove(withText("Playlist0")));
        Assert.assertEquals("id1", playlistsContainer.getSelectedPlaylists().get(0).id);

        interactor.moveUp("Playlist0");

        Assert.assertEquals("id0", playlistsContainer.getSelectedPlaylists().get(0).id);
        onView(withText("Playlist0")).check(isAbove(withText("Playlist1")));
    }

    private void setupAndOpenActivity() {
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
        mActivityTestRule.launchActivity(intent);
        interactor = new ArrangeActivityInteractor(mActivityTestRule.getActivity());
        SystemClock.sleep(200);
    }

    private void setupSelectedPlaylists(int count) {
        ArrayList<PlaylistViewModel> models = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            PlaylistViewModel model = new PlaylistViewModel("Playlist"+i, "id"+i, "uri"+i, null, i, "user"+1);
            model.selected = true;
            models.add(model);
        }
        playlistsContainer.initializePlaylists(models);
        playlistsContainer.updateSelectedPlaylists();
    }
}