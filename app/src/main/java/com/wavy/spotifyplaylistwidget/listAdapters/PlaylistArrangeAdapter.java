package com.wavy.spotifyplaylistwidget.listAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wavy.spotifyplaylistwidget.R;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistArrangeAdapter extends RecyclerView.Adapter<PlaylistArrangeAdapter.ViewHolder>  {

    private final ArrayList<PlaylistViewModel> mPlaylists;
    // private View mView;

    private int mImageSize;
    private String mTrackCountString;
    private ItemTouchHelper mItemTouchHelper;

    @SuppressLint("CheckResult")
    public PlaylistArrangeAdapter(RecyclerView view, @NonNull ArrayList<PlaylistViewModel> playlists, @NonNull Context context) {
        mPlaylists = playlists;
        mTrackCountString = context.getString(R.string.track_count);
        mImageSize = context.getResources().getDimensionPixelSize(R.dimen.playlist_image_size);

        // ItemTouchHelper handles item dragging.
        ArrangeTouchHelperCallback callback = new ArrangeTouchHelperCallback();
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(view);

        callback.getItemMoves().subscribe(onMoveArgs -> {
            PlaylistViewModel item = mPlaylists.get(onMoveArgs.from);
            mPlaylists.remove(onMoveArgs.from);
            mPlaylists.add(onMoveArgs.to, item);

            notifyItemMoved(onMoveArgs.from, onMoveArgs.to);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public PlaylistArrangeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.arrangeable_playlist, parent, false);
        PlaylistArrangeAdapter.ViewHolder viewHolder = new PlaylistArrangeAdapter.ViewHolder(view);

        // Inform ItemTouchHelper to start dragging when user presses down on a handle
        viewHolder.mDragHandle.setOnTouchListener((View v, MotionEvent event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mItemTouchHelper.startDrag(viewHolder);
            }
            return false;
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlaylistArrangeAdapter.ViewHolder holder, int position) {

        PlaylistViewModel playlist = mPlaylists.get(position);

        holder.playlistName.setText(playlist.name);
        holder.playlistInfo.setText(String.format(mTrackCountString, playlist.tracks));

        if (playlist.imageUrl != null) {
            Picasso.get()
                    .load(playlist.imageUrl)
                    .resize(mImageSize,
                            mImageSize)
                    .placeholder(R.drawable.ic_music_note_white_48dp)
                    .error(R.drawable.ic_music_note_white_48dp)
                    .into(holder.mImageView);
        } else {
            holder.mImageView.setImageResource(R.drawable.ic_music_note_white_48dp);
        }
    }

    @Override
    public int getItemCount() { return mPlaylists.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView playlistName;
        final TextView playlistInfo;
        final View view;
        final ImageView mImageView;
        final ImageView mDragHandle;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            playlistName = view.findViewById(R.id.playlist_name);
            playlistInfo = view.findViewById(R.id.playlist_info);
            mImageView = view.findViewById(R.id.playlist_image);
            mDragHandle = view.findViewById(R.id.playlist_drag_handle);
        }
    }

}
