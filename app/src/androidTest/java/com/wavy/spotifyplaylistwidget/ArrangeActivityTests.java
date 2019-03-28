package com.wavy.spotifyplaylistwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.SystemClock;

import com.wavy.spotifyplaylistwidget.db.entity.PlaylistEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetOptions;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetPlaylist;
import com.wavy.spotifyplaylistwidget.interaction.ArrangeActivityInteractor;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.Instant;

import java.util.ArrayList;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.PositionAssertions.isAbove;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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
    public void initialzeFromDbWorks() {
        // If we are modifying a pre-existing widget, then the order of playlists should be preserved.

        setupSelectedPlaylists(5);

        mockDatabase.widgetDao().upsert(new WidgetEntity(1, Instant.now(), WidgetOptions.getDefaultOptions()));

        ArrayList<PlaylistEntity> widgetPlaylists = new ArrayList<>();
        widgetPlaylists.add(new PlaylistEntity("id0", "Playlist0", "", "", 5));
        widgetPlaylists.add(new PlaylistEntity("id1", "Playlist1", "", "", 5));
        widgetPlaylists.add(new PlaylistEntity("id2", "Playlist2", "", "", 5));
        mockDatabase.playlistDao().upsertAll(widgetPlaylists);

        ArrayList<WidgetPlaylist> widgetPlaylistsMapping = new ArrayList<>();
        widgetPlaylistsMapping.add(new WidgetPlaylist(1, "id2", 1));
        widgetPlaylistsMapping.add(new WidgetPlaylist(1, "id1", 2));
        widgetPlaylistsMapping.add(new WidgetPlaylist(1, "id0", 3));
        mockDatabase.widgetPlaylistDao().setWidgetsPlaylists(1, widgetPlaylistsMapping);

        setupAndOpenActivity();

        // order should be playlist2, playlist1, playlist0, playlist3, playlist4
        onView(withText("Playlist2")).check(isAbove(withText("Playlist1")));
        Assert.assertEquals("id2", mockPlaylistContainer.getSelectedPlaylists().get(0).id);

        onView(withText("Playlist1")).check(isAbove(withText("Playlist0")));
        Assert.assertEquals("id1", mockPlaylistContainer.getSelectedPlaylists().get(1).id);

        onView(withText("Playlist0")).check(isAbove(withText("Playlist3")));
        Assert.assertEquals("id0", mockPlaylistContainer.getSelectedPlaylists().get(2).id);

        onView(withText("Playlist3")).check(isAbove(withText("Playlist4")));
        Assert.assertEquals("id3", mockPlaylistContainer.getSelectedPlaylists().get(3).id);

        Assert.assertEquals("id4", mockPlaylistContainer.getSelectedPlaylists().get(4).id);

    }

    @Test
    public void dragArrangeWorks() {

        setupSelectedPlaylists(5);
        setupAndOpenActivity();

        interactor.moveDown("Playlist0");

        onView(withText("Playlist1")).check(isAbove(withText("Playlist0")));
        Assert.assertEquals("id1", mockPlaylistContainer.getSelectedPlaylists().get(0).id);

        interactor.moveUp("Playlist0");

        Assert.assertEquals("id0", mockPlaylistContainer.getSelectedPlaylists().get(0).id);
        onView(withText("Playlist0")).check(isAbove(withText("Playlist1")));
    }

    @Test
    public void dragArrangeWorksWith10kPlaylists() {

        setupSelectedPlaylists(10000);
        setupAndOpenActivity();

        interactor.moveDown("Playlist0");

        onView(withText("Playlist1")).check(isAbove(withText("Playlist0")));
        Assert.assertEquals("id1", mockPlaylistContainer.getSelectedPlaylists().get(0).id);

        interactor.moveUp("Playlist0");

        Assert.assertEquals("id0", mockPlaylistContainer.getSelectedPlaylists().get(0).id);
        onView(withText("Playlist0")).check(isAbove(withText("Playlist1")));
    }

    @Test
    public void transitionToCustomizeActicityWorks() {
        setupSelectedPlaylists(10000);
        setupAndOpenActivity();
        interactor.clickNext();
        onView(withText("Customize")).check(matches(isDisplayed()));
    }

    private void setupAndOpenActivity() {
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
        mActivityTestRule.launchActivity(intent);
        interactor = new ArrangeActivityInteractor();
        SystemClock.sleep(200);
    }

    private void setupSelectedPlaylists(int count) {
        ArrayList<PlaylistViewModel> models = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            PlaylistViewModel model = new PlaylistViewModel("Playlist"+i, "id"+i, "uri"+i, null, i, "user"+1);
            model.selected = true;
            models.add(model);
        }
        mockPlaylistContainer.initializePlaylists(models);
        mockPlaylistContainer.updateSelectedPlaylists();
    }
}