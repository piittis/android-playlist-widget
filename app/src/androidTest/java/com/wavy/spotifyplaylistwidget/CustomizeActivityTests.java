package com.wavy.spotifyplaylistwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.SystemClock;

import com.wavy.spotifyplaylistwidget.db.entity.PlayableEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetOptions;
import com.wavy.spotifyplaylistwidget.interaction.CustomizeActivityInteractor;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.List;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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
                80,
                "#ff0000",
                "#00ff00",
                false,
                false
        )));

        setupSelectedPlaylists(10);
        setupAndOpenActivity();

        WidgetOptions options = mActivityTestRule.getActivity().mWidgetOptions;

        interactor.assertOpacityPercentageText("80 %");
        interactor.assertOpacitySeekbarProgress(80);
        Assert.assertEquals(80, options.backgroundOpacity);

        interactor.assertShowEditButtonNotChecked();
        Assert.assertEquals(false, options.showEditButton);

        interactor.assertShowTrackCountNotChecked();
        Assert.assertEquals(false, options.showTrackCount);

        interactor.assertPlaylistNameColor("#ff0000");
        Assert.assertEquals("#ffffff", options.backgroundColor);
        Assert.assertEquals("#ff0000", options.primaryTextColor);
        Assert.assertEquals("#00ff00", options.secondaryTextColor);
    }

    @Test
    public void PlaylistPreviewIsInitilizedCorrectly() {

        setupSelectedPlaylists(10);
        setupAndOpenActivity();

        interactor.assertPreviewPlaylistName("Playlist0");
        interactor.assertPreviewTrackCount(0);
    }

    @Test
    public void opacitySeekbarWorks() {
        setupSelectedPlaylists(10);
        setupAndOpenActivity();

        WidgetOptions options = mActivityTestRule.getActivity().mWidgetOptions;

        SystemClock.sleep(500);

        interactor.setOpacitySeekbarValue(10);
        interactor.assertOpacityPercentageText("10 %");
        interactor.assertOpacitySeekbarProgress(10);
        Assert.assertEquals(10, options.backgroundOpacity);

        SystemClock.sleep(500);

        interactor.setOpacitySeekbarValue(50);
        interactor.assertOpacityPercentageText("50 %");
        interactor.assertOpacitySeekbarProgress(50);
        Assert.assertEquals(50, options.backgroundOpacity);
    }

    @Test
    public void styleSelectionWorks() {
        //todo
    }

    @Test
    public void showTrackCountCheckboxWorks() {
        setupSelectedPlaylists(10);
        setupAndOpenActivity();

        WidgetOptions options = mActivityTestRule.getActivity().mWidgetOptions;
        onView(withId(R.id.playlist_info)).check(matches(isDisplayed()));
        Assert.assertEquals(true, options.showTrackCount);

        SystemClock.sleep(500);

        interactor.toggleShowTrackCountCheckbox();

        SystemClock.sleep(500);

        onView(withId(R.id.playlist_info)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        Assert.assertEquals(false, options.showTrackCount);

        interactor.toggleShowTrackCountCheckbox();

        SystemClock.sleep(500);

        onView(withId(R.id.playlist_info)).check(matches(isDisplayed()));
        Assert.assertEquals(true, options.showTrackCount);
    }

    @Test
    public void addWidgetWorks() {
        setupSelectedPlaylists(10);
        setupAndOpenActivity();

        SystemClock.sleep(500);

        interactor.clickAddWidget();

        SystemClock.sleep(500);

        WidgetEntity createdWidget = mockDatabase.widgetDao().getById(1);
        List<PlayableEntity> createdPlaylists = mockDatabase.widgetPlayablesDao().getWidgetPlayables(1);

        Assert.assertNotNull(createdWidget);
        Assert.assertNotNull(createdPlaylists);
        Assert.assertEquals(10, createdPlaylists.size());
        // TODO could do some asserts on the values.
    }

    private void setupAndOpenActivity() {
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
        mActivityTestRule.launchActivity(intent);
        interactor = new CustomizeActivityInteractor(mActivityTestRule.getActivity());
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
