package com.wavy.spotifyplaylistwidget.interaction;

import android.widget.SeekBar;

import com.wavy.spotifyplaylistwidget.CustomizeActivity;
import com.wavy.spotifyplaylistwidget.R;

import org.junit.Assert;

import androidx.test.espresso.matcher.ViewMatchers;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class CustomizeActivityInteractor {

    private CustomizeActivity mActivity;

    public CustomizeActivityInteractor(CustomizeActivity activity) {
        mActivity = activity;
    }

    public void assertOpacityPercentageText(String text) {
        onView(withId(R.id.opacityPercentage)).check(matches(withText(text)));
    }

    public void assertOpacitySeekbarProgress(int progress) {
        Assert.assertEquals(((SeekBar)mActivity.findViewById(R.id.opacitySeek)).getProgress(), progress);
    }

    public void assertPreviewPlaylistName(String name) {
        onView(withId(R.id.playlist_name)).check(matches(withText(name)));
    }

    public void assertPreviewTrackCount(int count) {
        onView(withId(R.id.playlist_info)).check(matches(withText(String.format(mActivity.getString(R.string.track_count), count))));
    }

    public void assertShowEditButtonNotChecked() {
        onView(withId(R.id.show_edit_checkbox)).check(matches(ViewMatchers.isNotChecked()));
    }

    public void assertShowTrackCountNotChecked() {
        onView(withId(R.id.show_edit_checkbox)).check(matches(ViewMatchers.isNotChecked()));
    }
}
