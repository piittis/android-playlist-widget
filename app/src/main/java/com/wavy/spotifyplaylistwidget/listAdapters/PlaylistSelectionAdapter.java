package com.wavy.spotifyplaylistwidget.listAdapters;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wavy.spotifyplaylistwidget.R;
import com.wavy.spotifyplaylistwidget.models.Playlist;

import java.util.ArrayList;

public class PlaylistSelectionAdapter
        extends RecyclerView.Adapter<PlaylistSelectionAdapter.ViewHolder>{

    private final ArrayList<Playlist> playlists;
    private View view;
    private View.OnClickListener clickListener;

    public PlaylistSelectionAdapter(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectable_playlist, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(PlaylistSelectionAdapter.ViewHolder holder, int position) {

        Playlist list = playlists.get(position);
        //todo use resources and stuff
        holder.playlistName.setText(list.name);
        holder.playlistInfo.setText(list.tracks + " kappaletta");
        holder.checkBox.setChecked(list.selected);

        holder.setOnClickListener((v) -> {
            list.selected = !list.selected;
            holder.checkBox.setChecked(list.selected);
            notifyItemChanged(position);
            clickListener.onClick(view);
        });
    }

    public void setOnClickListener(View.OnClickListener listener) {
        clickListener = listener;
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView playlistName;
        final TextView playlistInfo;
        final CheckBox checkBox;
        final View view;

        public void setOnClickListener (View.OnClickListener listener) {
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
        }
    }
}
