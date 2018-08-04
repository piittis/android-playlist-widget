package com.wavy.spotifyplaylistwidget.listAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wavy.spotifyplaylistwidget.R;
import com.wavy.spotifyplaylistwidget.utils.PicassoOnScrollListener;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import java.util.ArrayList;

public class PlaylistSelectionAdapter
        extends RecyclerView.Adapter<PlaylistSelectionAdapter.ViewHolder> {

    private ArrayList<PlaylistViewModel> mPlaylists;
    private View mView;
    private View.OnClickListener mClickListener;
    private int mImageSize;
    private String mTrackCountString;

    public PlaylistSelectionAdapter(ArrayList<PlaylistViewModel> playlists, Context context) {
        mPlaylists = playlists;
        mTrackCountString = context.getString(R.string.track_count);
        mImageSize = context.getResources().getDimensionPixelSize(R.dimen.playlist_image_size);
    }

    public void setPlaylists(ArrayList<PlaylistViewModel> playlists) {
        mPlaylists = playlists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectable_playlist, parent, false);
        ViewHolder viewHolder = new ViewHolder(mView);

        viewHolder.setOnClickListener((v) -> {
            int pos = viewHolder.getAdapterPosition();
            PlaylistViewModel playlist = mPlaylists.get(pos);
            playlist.selected = !playlist.selected;
            viewHolder.checkBox.setChecked(playlist.selected);
            mClickListener.onClick(mView);
            notifyItemChanged(pos);
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlaylistSelectionAdapter.ViewHolder holder, int position) {

        PlaylistViewModel playlist = mPlaylists.get(position);

        holder.playlistName.setText(playlist.name);
        holder.playlistInfo.setText(String.format(mTrackCountString, playlist.tracks));
        holder.checkBox.setChecked(playlist.selected);

        if (playlist.imageUrl != null) {
            Picasso.get()
                    .load(playlist.imageUrl)
                    .tag(PicassoOnScrollListener.RECYCLVIEW_TAG)
                    .resize(mImageSize,
                            mImageSize)
                    .placeholder(R.drawable.ic_music_note_white_48dp)
                    .error(R.drawable.ic_music_note_white_48dp)
                    .into(holder.mImageView);
        } else {
            holder.mImageView.setImageResource(R.drawable.ic_music_note_white_48dp);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mPlaylists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView playlistName;
        final TextView playlistInfo;
        final CheckBox checkBox;
        final View view;
        final ImageView mImageView;

        public void setOnClickListener(View.OnClickListener listener) {
            view.setOnClickListener(listener);
            // Checkbox needs it's own listener
            checkBox.setOnClickListener(listener);
        }

        ViewHolder(View view) {

            super(view);
            this.view = view;
            playlistName = view.findViewById(R.id.playlist_name);
            playlistInfo = view.findViewById(R.id.playlist_info);
            checkBox = view.findViewById(R.id.playlist_checkbox);
            mImageView = view.findViewById(R.id.playlist_image);
        }
    }
}
