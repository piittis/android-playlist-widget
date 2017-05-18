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
    private int mImageSize;

    public PlaylistArrangeAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<PlaylistViewModel> playlists) {
        super(context, resource, playlists);
        mContext = context;
        mLayoutResourceId = resource;
        mPlaylists = playlists;
        mImageSize = mContext.getResources().getDimensionPixelSize(R.dimen.playlist_image_size);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (row == null) {
            row = LayoutInflater.from(mContext).inflate(mLayoutResourceId, parent, false);
        }

        // Get the data item for this position
        PlaylistViewModel playlist = mPlaylists.get(position);

        ((TextView) row.findViewById(R.id.playlist_name)).setText(playlist.name);
        ((TextView) row.findViewById(R.id.playlist_info)).setText(playlist.tracks + " kappaletta");

        if (playlist.imageUrl != null) {
            Picasso.with(mContext)
                    .load(playlist.imageUrl)
                    .resize(mImageSize,
                            mImageSize)
                    .centerCrop()
                    .placeholder(R.drawable.ic_music_note_white_24dp)
                    .into(((ImageView) row.findViewById(R.id.playlist_image)));
        }


        return row;
    }

}
