package com.wavy.spotifyplaylistwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.SystemClock;

import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetOptions;
import com.wavy.spotifyplaylistwidget.interaction.CustomizeActivityInteractor;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.Instant;

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

    @Test
    public void InitializeFromDbWorks() {

        // Make sure options are different than the defaults.
        mockDatabase.widgetDao().upsert(new WidgetEntity(1, Instant.now(), new WidgetOptions(
                "#ffffff",
                50,
                "#ffffff",
                "#ffffff",
                false,
                false
        )));

        setupSelectedPlaylists(10);
        setupAndOpenActivity();

        interactor.assertOpacityPercentageText("50 %");
        interactor.assertOpacitySeekbarProgress(50);
        interactor.assertShowEditButtonNotChecked();
        interactor.assertShowTrackCountNotChecked();
    }

    @Test
    public void PlaylistPreviewIsInitilizedCorrectly() {

        setupSelectedPlaylists(10);
        setupAndOpenActivity();

        SystemClock.sleep(2000);
        interactor.assertPreviewPlaylistName("Playlist0");
        interactor.assertPreviewTrackCount(0);
    }


    private void setupAndOpenActivity() {
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
        mActivityTestRule.launchActivity(intent);
        interactor = new CustomizeActivityInteractor(mActivityTestRule.getActivity());
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
