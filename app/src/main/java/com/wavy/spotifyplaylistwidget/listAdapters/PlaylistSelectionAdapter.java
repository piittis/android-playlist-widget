package com.wavy.spotifyplaylistwidget.listAdapters;

import android.annotation.SuppressLint;
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
import com.wavy.spotifyplaylistwidget.models.Playlist;

import java.util.ArrayList;

public class PlaylistSelectionAdapter
        extends RecyclerView.Adapter<PlaylistSelectionAdapter.ViewHolder> {

    private final ArrayList<Playlist> mPlaylists;
    private View mView;
    private View.OnClickListener mClickListener;
    private Context mContext;

    public PlaylistSelectionAdapter(ArrayList<Playlist> playlists, Context context) {
        mPlaylists = playlists;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectable_playlist, parent, false);
        return new ViewHolder(mView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(PlaylistSelectionAdapter.ViewHolder holder, int position) {

        Playlist list = mPlaylists.get(position);
        //todo use resources and stuff
        holder.playlistName.setText(list.name);
        holder.playlistInfo.setText(list.tracks + " kappaletta");
        holder.checkBox.setChecked(list.selected);

        Picasso.with(mContext)
                .load(list.imageUrl)
                .resize(mContext.getResources().getDimensionPixelSize(R.dimen.playlist_image_size),
                        mContext.getResources().getDimensionPixelSize(R.dimen.playlist_image_size))
                .centerCrop()
                .placeholder(R.drawable.ic_music_note_white_24dp)
                .into(holder.mImageView);


        holder.setOnClickListener((v) -> {
            list.selected = !list.selected;
            holder.checkBox.setChecked(list.selected);
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
