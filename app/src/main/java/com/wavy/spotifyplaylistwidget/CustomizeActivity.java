package com.wavy.spotifyplaylistwidget;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetOptions;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomizeActivity extends PlaylistWidgetConfigureActivityBase {

    private String bgColor;
    private String text1Color;
    private String text2Color;

    @BindView(R.id.selection_next_button) Button mNextButton;
    @BindView(R.id.playlist_preview) View mPlaylistPreview;
    @BindView(R.id.opacityPercentage) TextView mOpacityPercentage;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customize);
        ButterKnife.bind(this);

        bgColor = this.getString(R.color.dark_bg);
        text1Color = this.getString(R.color.dark_text1);
        text2Color = this.getString(R.color.dark_text2);

        initializePreview();

        ((RadioGroup)this.findViewById(R.id.styleRadios)).setOnCheckedChangeListener((arg1, id) -> this.onStyleChange(id));
        ((SeekBar)this.findViewById(R.id.opacitySeek)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                onOpacityChange(i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mNextButton.setOnClickListener((v) -> onAddWidgetClick());
    }

    void initializeFromDb() {
        dToast("init from db");
    }

    void onAddWidgetClick() {
        findViewById(R.id.customize_content).setVisibility(View.GONE);
        findViewById(R.id.processing_indicator).setVisibility(View.VISIBLE);

        addWidget(new WidgetOptions(bgColor, text1Color, text2Color));
    }

    @SuppressLint("ResourceType")
    void onStyleChange(int id) {
        if (id == R.id.lightStyle) {
            bgColor = this.getString(R.color.light_bg);
            text1Color = this.getString(R.color.light_text1);
            text2Color = this.getString(R.color.light_text2);
        } else {
            bgColor = this.getString(R.color.dark_bg);
            text1Color = this.getString(R.color.dark_text1);
            text2Color = this.getString(R.color.dark_text2);
        }

        mPlaylistPreview.setBackgroundColor(Color.parseColor(bgColor));
        ((TextView)mPlaylistPreview.findViewById(R.id.playlist_name)).setTextColor(Color.parseColor(text1Color));
        ((TextView)mPlaylistPreview.findViewById(R.id.playlist_info)).setTextColor(Color.parseColor(text2Color));

    }

    @SuppressLint("SetTextI18n")
    void onOpacityChange(int progress) {
        //dToast(progress);
        ((TextView)this.findViewById(R.id.opacityPercentage)).setText(Integer.toString(progress) + " %");
    }


    void initializePreview() {
        View view = mPlaylistPreview;
        TextView playlistName = view.findViewById(R.id.playlist_name);
        TextView playlistInfo = view.findViewById(R.id.playlist_info);
        ImageView playlistImage = view.findViewById(R.id.playlist_image);
        int imgSize = this.getResources().getDimensionPixelSize(R.dimen.playlist_image_size);

        String imgUrl = "foo";
        if (mPlaylists.getPlaylistsCount() > 0) {
            PlaylistViewModel pl = mPlaylists.getPlaylists().get(0);

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

}
