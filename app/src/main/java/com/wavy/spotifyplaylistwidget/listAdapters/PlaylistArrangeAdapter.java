package com.wavy.spotifyplaylistwidget.listAdapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wavy.spotifyplaylistwidget.R;
import com.wavy.spotifyplaylistwidget.models.Playlist;

import java.util.ArrayList;

public class PlaylistArrangeAdapter extends ArrayAdapter<Playlist> {

    private final ArrayList<Playlist> playlists;
    private int layoutResourceId;
    private Context context;

    public PlaylistArrangeAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Playlist> playlists) {
        super(context, resource, playlists);
        this.context = context;
        this.layoutResourceId = resource;
        this.playlists = playlists;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (row == null) {
            row = LayoutInflater.from(context).inflate(layoutResourceId, parent, false);
        }

        // Get the data item for this position
        Playlist pl = playlists.get(position);

        ((TextView)row.findViewById(R.id.playlist_name)).setText(pl.name);
        ((TextView)row.findViewById(R.id.playlist_info)).setText(pl.tracks + " kappaletta");

        Picasso.with(context)
                .load(pl.mImageUrl)
                .resize(context.getResources().getDimensionPixelSize(R.dimen.playlist_image_size),
                        context.getResources().getDimensionPixelSize(R.dimen.playlist_image_size))
                .centerCrop()
                .placeholder(R.drawable.ic_music_note_white_24dp)
                .into(((ImageView)row.findViewById(R.id.playlist_image)));

        return row;
    }

}
