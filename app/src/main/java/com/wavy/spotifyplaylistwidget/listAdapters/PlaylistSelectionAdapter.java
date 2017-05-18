package com.wavy.spotifyplaylistwidget.listAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
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

    private final ArrayList<PlaylistViewModel> mPlaylists;
    private View mView;
    private View.OnClickListener mClickListener;
    private Context mContext;
    private int mImageSize;

    public PlaylistSelectionAdapter(ArrayList<PlaylistViewModel> playlists, Context context) {
        mPlaylists = playlists;
        mContext = context;
        mImageSize = mContext.getResources().getDimensionPixelSize(R.dimen.playlist_image_size);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectable_playlist, parent, false);
        return new ViewHolder(mView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(PlaylistSelectionAdapter.ViewHolder holder, int position) {

        PlaylistViewModel playlist = mPlaylists.get(position);
        //todo use resources and stuff
        holder.playlistName.setText(playlist.name);
        holder.playlistInfo.setText(playlist.tracks + " kappaletta");
        holder.checkBox.setChecked(playlist.selected);

        if (playlist.imageUrl != null) {

            Picasso.with(mContext.getApplicationContext())
                    .load(playlist.imageUrl)
                    .tag(PicassoOnScrollListener.RECYCLVIEW_TAG)
                    .resize(mImageSize,
                            mImageSize)
                    .centerCrop()
                    //todo placeholder get stretched to 50dp... it should be 24dp
                    .placeholder(R.drawable.ic_music_note_white_24dp)
                    .into(holder.mImageView);
        }

        holder.setOnClickListener((v) -> {
            playlist.selected = !playlist.selected;
            holder.checkBox.setChecked(playlist.selected);
            notifyItemChanged(position);
            mClickListener.onClick(mView);
        });
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
            playlistName = (TextView) view.findViewById(R.id.playlist_name);
            playlistInfo = (TextView) view.findViewById(R.id.playlist_info);
            checkBox = (CheckBox) view.findViewById(R.id.playlist_checkbox);
            mImageView = (ImageView) view.findViewById(R.id.playlist_image);
        }
    }
}
