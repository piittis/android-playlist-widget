package com.wavy.spotifyplaylistwidget;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetOptions;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomizeActivity extends PlaylistWidgetConfigureActivityBase {

    public WidgetOptions mWidgetOptions;

    @BindView(R.id.customize_next_button) Button mNextButton;
    @BindView(R.id.playlist_preview) View mPlaylistPreview;
    @BindView(R.id.opacityPercentage) TextView mOpacityPercentage;
    @BindView(R.id.show_edit_checkbox) CheckBox mShowEditCheckbox;
    @BindView(R.id.show_track_count) CheckBox mShowTrackCountCheckbox;
    @BindView(R.id.preview_bg) ImageView mPreviewBg;
    @BindView(R.id.styleRadios) RadioGroup mStyleRadioGroup;
    @BindView(R.id.opacitySeek) SeekBar mOpacitySeek;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customize);
        ButterKnife.bind(this);

        initializePreview();
        initializeFromDb();

        mStyleRadioGroup.setOnCheckedChangeListener((arg1, id) -> this.onStyleChange(id));

        mOpacitySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                onOpacityChange(i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mShowEditCheckbox.setOnCheckedChangeListener((e, checked) -> mWidgetOptions.showEditButton = checked);
        mShowTrackCountCheckbox.setOnCheckedChangeListener((e, checked) -> {
            mWidgetOptions.showTrackCount = checked;
            updatePreviewTrackCount();
        });

        mNextButton.setOnClickListener((v) -> onAddWidgetClick());
    }

    @SuppressLint("ResourceType")
    void initializeFromDb() {

        if (mWidgetOptions != null)
            return;

        WidgetEntity existing = mAppDatabase.widgetDao().getById(mAppWidgetId);
        if (existing != null) {
            this.mWidgetOptions = existing.options;
            ((Button)this.findViewById(R.id.customize_next_button)).setText(R.string.update_widget);
        } else {
            this.mWidgetOptions = new WidgetOptions(
                    this.getString(R.color.dark_bg),
                    100,
                    this.getString(R.color.dark_text1),
                    this.getString(R.color.dark_text2),
                    true,
                    true
            );
        }

        mShowEditCheckbox.setChecked(mWidgetOptions.showEditButton);
        updatePreviewColors();
        onOpacityChange(mWidgetOptions.backgroundOpacity);
        updatePreviewTrackCount();
        mShowTrackCountCheckbox.setChecked(mWidgetOptions.showTrackCount);

        if (mWidgetOptions.backgroundColor.equals(this.getString(R.color.dark_bg))) {
            mStyleRadioGroup.check(R.id.darkStyle);
        } else {
            mStyleRadioGroup.check(R.id.lightStyle);
        }

        mOpacitySeek.setProgress(mWidgetOptions.backgroundOpacity);
    }

    void onAddWidgetClick() {
        findViewById(R.id.customize_content).setVisibility(View.GONE);
        findViewById(R.id.processing_indicator).setVisibility(View.VISIBLE);
        addWidget(mWidgetOptions);
    }

    @SuppressLint("ResourceType")
    void onStyleChange(int id) {
        if (id == R.id.lightStyle) {
            mWidgetOptions.backgroundColor = this.getString(R.color.light_bg);
            mWidgetOptions.primaryTextColor = this.getString(R.color.light_text1);
            mWidgetOptions.secondaryTextColor = this.getString(R.color.light_text2);
        } else {
            mWidgetOptions.backgroundColor = this.getString(R.color.dark_bg);
            mWidgetOptions.primaryTextColor = this.getString(R.color.dark_text1);
            mWidgetOptions.secondaryTextColor = this.getString(R.color.dark_text2);
        }
        updatePreviewColors();
    }

    @SuppressLint("SetTextI18n")
    void onOpacityChange(int newOpacity) {
        mWidgetOptions.backgroundOpacity = newOpacity;
        ((TextView)this.findViewById(R.id.opacityPercentage)).setText(Integer.toString(newOpacity) + " %");
        updatePreviewBgOpacity();
    }

    void initializePreview() {
        View view = mPlaylistPreview;
        TextView playlistName = view.findViewById(R.id.playlist_name);
        TextView playlistInfo = view.findViewById(R.id.playlist_info);
        ImageView playlistImage = view.findViewById(R.id.playlist_image);
        int imgSize = this.getResources().getDimensionPixelSize(R.dimen.playlist_image_size);

        String imgUrl = "foo";
        if (mPlaylists.getSelectedPlaylistsCount() > 0) {
            PlaylistViewModel pl = mPlaylists.getSelectedPlaylists().get(0);

            playlistName.setText(pl.name);
            playlistInfo.setText(String.format(this.getString(R.string.track_count), pl.tracks));
            imgUrl = pl.imageUrl;
        }

        Picasso.get()
                .load(imgUrl)
                .resize(imgSize,
                        imgSize)
                .placeholder(R.drawable.ic_music_note_white_48dp)
                .error(R.drawable.ic_music_note_white_48dp)
                .into(playlistImage);
    }

    void updatePreviewColors() {
        mPreviewBg.setColorFilter(Color.parseColor(mWidgetOptions.backgroundColor));
        ((TextView)mPlaylistPreview.findViewById(R.id.playlist_name)).setTextColor(Color.parseColor(mWidgetOptions.primaryTextColor));
        ((TextView)mPlaylistPreview.findViewById(R.id.playlist_info)).setTextColor(Color.parseColor(mWidgetOptions.secondaryTextColor));
    }

    void updatePreviewBgOpacity() {
        mPreviewBg.setAlpha((float)mWidgetOptions.backgroundOpacity / 100f);
    }

    void updatePreviewTrackCount() {
        mPlaylistPreview.findViewById(R.id.playlist_info).setVisibility(mWidgetOptions.showTrackCount ? View.VISIBLE : View.GONE);
    }

}
