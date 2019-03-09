package com.wavy.spotifyplaylistwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.SystemClock;

import com.wavy.spotifyplaylistwidget.interaction.CustomizeActivityInteractor;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
public class CustomizeActivityTests extends ActivityTestBase {

    @Rule
    public ActivityTestRule<CustomizeActivity> mActivityTestRule = new ActivityTestRule<>(CustomizeActivity.class, true, false);

    private CustomizeActivityInteractor interactor;

    @Before
    public void setup() {
        super.initialize();
    }

    @Test
    public void ActivityOpens() {
        setupSelectedPlaylists(10);
        setupAndOpenActivity();
    }


    private void setupAndOpenActivity() {
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
        mActivityTestRule.launchActivity(intent);
        interactor = new CustomizeActivityInteractor();
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
