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
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import java.util.ArrayList;

public class PlaylistArrangeAdapter extends ArrayAdapter<PlaylistViewModel> {

    private final ArrayList<PlaylistViewModel> mPlaylists;
    private int mLayoutResourceId;
    private Context mContext;

    public PlaylistArrangeAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<PlaylistViewModel> playlists) {
        super(context, resource, playlists);
        mContext = context;
        mLayoutResourceId = resource;
        mPlaylists = playlists;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (row == null) {
            row = LayoutInflater.from(mContext).inflate(mLayoutResourceId, parent, false);
        }

        // Get the data item for this position
        PlaylistViewModel pl = mPlaylists.get(position);

        ((TextView) row.findViewById(R.id.playlist_name)).setText(pl.name);
        ((TextView) row.findViewById(R.id.playlist_info)).setText(pl.tracks + " kappaletta");

        Picasso.with(mContext)
                .load(pl.imageUrl)
                .resize(mContext.getResources().getDimensionPixelSize(R.dimen.playlist_image_size),
                        mContext.getResources().getDimensionPixelSize(R.dimen.playlist_image_size))
                .centerCrop()
                .placeholder(R.drawable.ic_music_note_white_24dp)
                .into(((ImageView) row.findViewById(R.id.playlist_image)));

        return row;
    }

}
